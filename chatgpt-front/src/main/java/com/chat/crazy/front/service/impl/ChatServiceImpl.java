package com.chat.crazy.front.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chat.crazy.base.domain.entity.ChatMessageDO;
import com.chat.crazy.base.domain.entity.ChatRoomDO;
import com.chat.crazy.base.domain.query.PageQuery;
import com.chat.crazy.base.enums.*;
import com.chat.crazy.base.exception.ServiceException;
import com.chat.crazy.base.util.ObjectMapperUtil;
import com.chat.crazy.front.domain.request.chat.ChatProcessRequest;
import com.chat.crazy.front.domain.request.chat.ChatSessionHistoryReq;
import com.chat.crazy.front.domain.request.chat.ChatSessionOperateReq;
import com.chat.crazy.front.domain.vo.chat.ChatSessionHistoryVO;
import com.chat.crazy.front.domain.vo.chat.ChatSessionListVO;
import com.chat.crazy.front.domain.vo.chat.ChatSessionOperateVO;
import com.chat.crazy.front.handler.emitter.ChatMessageEmitterChain;
import com.chat.crazy.front.handler.emitter.RateLimiterEmitterChain;
import com.chat.crazy.front.handler.emitter.ResponseEmitterChain;
import com.chat.crazy.front.handler.emitter.SensitiveWordEmitterChain;
import com.chat.crazy.front.service.*;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author hncboy
 * @date 2023/3/22 19:41
 * 聊天相关业务实现类
 */
@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    @Resource
    private ChatRoomService chatRoomService;
    @Resource
    private UserService userService;
    @Resource
    private ChatMessageService chatMessageService;
    
    @Override
    public ChatSessionOperateVO operateSession(ChatSessionOperateReq req) {
        ChatSessionOperateVO vo = new ChatSessionOperateVO();
        Integer userId;
        int userType = UserTypeEnum.COMMON.getType();
        if (req.getUser() != null) {
            // 已登录
            userId = req.getUser().getId();
        } else {
            userId = req.getVirtualUser().getId();
            userType = UserTypeEnum.NOT_LOGIN.getType();
        }
        
        // 1. 创建：保存聊天室
        if (req.getType() == SessionOperateTypeEnum.CREATE.getStatus()) {
            ChatRoomDO session = chatRoomService.createEmptySession(req.getTitle(), userId, userType);
            vo.setSessionId(session.getId());
        } else {
            if (req.getSessionId() == null) {
                throw new ServiceException("聊天窗口id不能为空");
            }
            // 校验sessionId是否与该用户绑定
            ChatRoomDO validSession = chatRoomService.getValidSession(req.getSessionId(), userId, userType);
            if (validSession == null) {
                throw new ServiceException("聊天窗口不存在");
            }
            
            // 编辑
            if (req.getType() == SessionOperateTypeEnum.EDIT_TITLE.getStatus()) {
                if (StringUtils.isEmpty(req.getTitle())) {
                    throw new ServiceException("聊天窗口标题不能为空");
                }
                boolean isSuccess = chatRoomService.update(new LambdaUpdateWrapper<ChatRoomDO>().set(ChatRoomDO::getTitle, req.getTitle()).eq(ChatRoomDO::getId, req.getSessionId()).eq(ChatRoomDO::getStatus, ChatRoomStatusEnum.VALID.getStatus()));
                if (!isSuccess) throw new ServiceException("聊天窗口标题修改失败");
            } else if (req.getType() == SessionOperateTypeEnum.DELETE.getStatus()) {
                // 删除
                boolean isSuccess = chatRoomService.update(new LambdaUpdateWrapper<ChatRoomDO>().set(ChatRoomDO::getStatus, ChatRoomStatusEnum.INVALID.getStatus()).eq(ChatRoomDO::getId, req.getSessionId()));
                if (!isSuccess) throw new ServiceException("聊天窗口删除失败");
            }
            vo.setSessionId(req.getSessionId());
        }
        return vo;
    }

    @Override
    public ResponseBodyEmitter sendMessage(ChatProcessRequest chatProcessRequest) {
        boolean isValid = userService.verifySendMessage(chatProcessRequest);
        if (!isValid) {
            throw new ServiceException("对话功能已失效，请充值");
        }
        ChatRoomDO validSession = chatRoomService.getValidSession(chatProcessRequest.getSessionId(),
                chatProcessRequest.getUserId(), chatProcessRequest.getUserType());
        if (validSession == null) {
            throw new ServiceException("聊天窗口不存在");
        }
        chatProcessRequest.setChatRoomDO(validSession);
        if (UserTypeEnum.NOT_LOGIN.getType() == chatProcessRequest.getUserType()) {
            chatProcessRequest.setModel(ChatCompletion.Model.GPT_3_5_TURBO);
        } else {
            if (chatProcessRequest.getUser().getPackageType() != PackageTypeEnum.PLUS_PACKAGE.getId()) {
                chatProcessRequest.setModel(ChatCompletion.Model.GPT_3_5_TURBO);
            } else {
                chatProcessRequest.setModel(chatProcessRequest.getVersion() == 0 ? ChatCompletion.Model.GPT_3_5_TURBO : ChatCompletion.Model.GPT_4_0613);
            }
        }
        // 超时时间设置 3 分钟
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(3 * 60 * 1000L);
        emitter.onCompletion(() -> log.debug("请求参数：{}，Front-end closed the emitter connection.", ObjectMapperUtil.toJson(chatProcessRequest)));
        emitter.onTimeout(() -> log.error("请求参数：{}，Back-end closed the emitter connection.", ObjectMapperUtil.toJson(chatProcessRequest)));

        // 构建 emitter 处理链路
        ResponseEmitterChain ipRateLimiterEmitterChain = new RateLimiterEmitterChain();
        ResponseEmitterChain sensitiveWordEmitterChain = new SensitiveWordEmitterChain();
        ipRateLimiterEmitterChain.setNext(sensitiveWordEmitterChain);
        sensitiveWordEmitterChain.setNext(new ChatMessageEmitterChain());
//        sensitiveWordEmitterChain.doChain(chatProcessRequest, emitter);
//        ipRateLimiterEmitterChain.setNext(sensitiveWordEmitterChain);
        ipRateLimiterEmitterChain.doChain(chatProcessRequest, emitter);
        return emitter;
    }
    
    @Override
    public ChatSessionListVO getSessionList(PageQuery query) {
        ChatSessionListVO vo = new ChatSessionListVO();
        Page<ChatRoomDO> chatRoomsPage = chatRoomService.getSessionsByUser(query);
        List<ChatRoomDO> records = chatRoomsPage.getRecords();
        if (CollectionUtils.isNotEmpty(records)) {
            List<ChatSessionListVO.SessionInfo> list = records.stream().map(item -> {
                ChatSessionListVO.SessionInfo sessionInfo = new ChatSessionListVO.SessionInfo();
                sessionInfo.setId(String.valueOf(item.getId()));
                sessionInfo.setTitle(item.getTitle());
                sessionInfo.setLastTimeTs(item.getUpdateTime().toInstant(ZoneOffset.of("+8")).toEpochMilli());
                return sessionInfo;
            }).collect(Collectors.toList());
            vo.setList(list);
        }
        vo.setPageNum(query.getPageNum());
        vo.setPageSize(query.getPageSize());
        vo.setTotal(chatRoomsPage.getTotal());
        return vo;
    }

    @Override
    public ChatSessionHistoryVO getSessionHistory(ChatSessionHistoryReq req) {
        // 校验 page>1时，lastMessageId不为空
        if (req.getPageNum() > 1 && StringUtils.isEmpty(req.getLastMessageId())) {
            throw new ServiceException("上次分页list中最后一条消息的id不能为空");
        }
        Integer userId = req.getUserId();
        Integer userType = req.getUserType();
        // 校验sessionId是否与该用户绑定
        ChatRoomDO validSession = chatRoomService.getValidSession(req.getSessionId(), userId, userType);
        if (validSession == null) {
            throw new ServiceException("聊天窗口不存在");
        }
        
        ChatMessageDO lastChatMessage = null;
        if (StringUtils.isNotEmpty(req.getLastMessageId())) {
            lastChatMessage = chatMessageService.getOne(new LambdaQueryWrapper<ChatMessageDO>()
                    .eq(ChatMessageDO::getMessageId, req.getLastMessageId())
                    .eq(ChatMessageDO::getUserId, userId)
                    .eq(ChatMessageDO::getUserType, userType));
            if (lastChatMessage == null) {
                throw new ServiceException("上次分页的最后一条消息不存在");
            }
        }
        Page<ChatMessageDO> pagedMessages = chatMessageService.getPagedMessages(req, lastChatMessage);
        ChatSessionHistoryVO vo = new ChatSessionHistoryVO();
        vo.setPageNum(req.getPageNum());
        vo.setPageSize(req.getPageSize());
        vo.setTotal(pagedMessages.getTotal());
        if (CollectionUtils.isNotEmpty(pagedMessages.getRecords())) {
            List<ChatSessionHistoryVO.SessionHistoryInfo> list = pagedMessages.getRecords().stream().map(item -> {
                ChatSessionHistoryVO.SessionHistoryInfo sessionHistoryInfo = new ChatSessionHistoryVO.SessionHistoryInfo();
                sessionHistoryInfo.setMessageId(item.getMessageId());
                sessionHistoryInfo.setMessageType(item.getMessageType());
                sessionHistoryInfo.setDateTime(item.getCreateTime());
                sessionHistoryInfo.setText(item.getContent());
                sessionHistoryInfo.setError(Objects.equals(item.getStatus(), ChatMessageStatusEnum.ERROR.getCode()));
                ChatProcessRequest.Options options = new ChatProcessRequest.Options();
                options.setParentMessageId(item.getParentMessageId());
                options.setConversationId(item.getConversationId());
                sessionHistoryInfo.setOptions(options);
                return sessionHistoryInfo;
            }).collect(Collectors.toList());
            vo.setList(list);
        }
        return vo;
    }
}
