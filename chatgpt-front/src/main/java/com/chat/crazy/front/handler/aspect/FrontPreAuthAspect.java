package com.chat.crazy.front.handler.aspect;

import cn.hutool.extra.servlet.ServletUtil;
import com.chat.crazy.base.annotation.FrontPreAuth;
import com.chat.crazy.base.domain.entity.UserDO;
import com.chat.crazy.base.domain.entity.VirtualUserDO;
import com.chat.crazy.base.domain.query.ChatRequest;
import com.chat.crazy.base.enums.UserTypeEnum;
import com.chat.crazy.base.exception.AuthException;
import com.chat.crazy.base.exception.ServiceException;
import com.chat.crazy.base.util.TokenUtil;
import com.chat.crazy.front.service.AuthService;
import com.chat.crazy.base.util.WebUtil;
import com.chat.crazy.front.service.UserService;
import com.chat.crazy.front.service.VirtualService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

/**
 * @author hncboy
 * @date 2023/3/23 00:19
 * FrontPreAuth 用户端切面
 */
@Order(0)
@Aspect
@Component
@Slf4j
public class FrontPreAuthAspect {
    @Resource
    AuthService authService;
    @Resource
    VirtualService virtualService;
    @Resource
    UserService userService;

    @Pointcut("@annotation(com.chat.crazy.base.annotation.FrontPreAuth) || @within(com.chat.crazy.base.annotation.FrontPreAuth)")
    public void pointcut() {

    }

    /**
     * 切 方法 和 类上的 @PreAuth 注解
     *
     * @param point 切点
     * @return Object
     * @throws Throwable 没有权限的异常
     */
    @Around("pointcut()")
    public Object checkAuth(ProceedingJoinPoint point) throws Throwable {
        HttpServletRequest request = WebUtil.getRequest();
        HttpServletResponse response = WebUtil.getResponse();
        //方法签名
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        //获取方法
        Method aimMethod = methodSignature.getMethod();
        //获取 @Permissions 的值
        FrontPreAuth preAuth = aimMethod.getAnnotation(FrontPreAuth.class);
        Object result = null;
        String virId = ServletUtil.getHeader(request, "virId", StandardCharsets.UTF_8);
        String token = ServletUtil.getHeader(request, "token", StandardCharsets.UTF_8);
//        if (preAuth.customize() == 0) {
        if (preAuth.virIdAuth() && StringUtils.isEmpty(virId)) {
            log.error("virId 不能为空");
            throw new AuthException("virId不能为空");
        }

        if (preAuth.tokenAuth() && StringUtils.isEmpty(token)) {
            log.error("token 不能为空");
            throw new AuthException("token不能为空");
        }

        if (preAuth.tokenAuth() || StringUtils.isNotEmpty(token)) {
            boolean isValid = authService.verifyWebToken(token, response);
            if (!isValid) {
                throw new AuthException("Error: 无访问权限 | No access rights");
            }
        }
        ChatRequest globalArg = null;
        for (Object arg : point.getArgs()) {
            if (ChatRequest.class.isAssignableFrom(arg.getClass())) {
                globalArg = (ChatRequest) arg;
                break;
            }
        }
        if (globalArg != null) {
            // 填充user信息
            Integer userId = TokenUtil.getUserId();
            if (userId != -1) {
                UserDO user = userService.getById(userId);
                if (user == null) {
                    throw new ServiceException("用户不存在");
                }
                globalArg.setUser(user);
                globalArg.setUserId(userId);
                globalArg.setUserType(UserTypeEnum.COMMON.getType());
            }
            
            if (preAuth.virIdAuth() || StringUtils.isNotEmpty(virId)) {
                VirtualUserDO virtualUser = virtualService.getByVirId(virId);
                // 需要检查virId填充 && 查不到虚拟用户 时，报异常。
                if (virtualUser == null) {
                    throw new ServiceException("virId不存在");
                }
                globalArg.setVirtualUser(virtualUser);
                if (userId == -1) {
                    globalArg.setUserId(virtualUser.getId());
                    globalArg.setUserType(UserTypeEnum.NOT_LOGIN.getType());
                }
            }
        }
        result = point.proceed();
//        } else {
//            // 特殊流程
////            if ("getUserInfo".equals(aimMethod.getName())) {
//                if (StringUtils.isEmpty(virId)) {
//                    log.error("virId 不能为空");
//                    throw new AuthException("virId不能为空");
//                }
//                // 未登录时忽略校验
//                if (StringUtils.isNotEmpty(token)) {
//                    boolean isValid = authService.verifyWebToken(token, response);
//                    if (!isValid) {
//                        throw new AuthException("Error: 无访问权限 | No access rights");
//                    }
//                }
//                result = point.proceed();
////            }
//        }

        return result;
    }
}
