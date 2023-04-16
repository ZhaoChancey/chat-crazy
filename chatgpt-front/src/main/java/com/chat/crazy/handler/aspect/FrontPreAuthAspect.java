package com.chat.crazy.handler.aspect;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.ObjectMapper;
import com.chat.crazy.annotation.FrontPreAuth;
import com.chat.crazy.exception.AuthException;
import com.chat.crazy.service.AuthService;
import com.chat.crazy.util.WebUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
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
@Aspect
@Component
@Slf4j
public class FrontPreAuthAspect {
    @Resource
    AuthService authService;

    @Pointcut("@annotation(com.chat.crazy.annotation.FrontPreAuth) || @within(com.chat.crazy.annotation.FrontPreAuth)")
    public void pointcut() {

    }

    private final Gson gson = new Gson();
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
        log.info("checkAuth begin");
        //方法签名
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        //获取方法
        Method aimMethod = methodSignature.getMethod();
        //获取 @Permissions 的值
        FrontPreAuth preAuth = aimMethod.getAnnotation(FrontPreAuth.class);
        Object result = null;
        if (preAuth.customize() == 0) {
            if (preAuth.virIdAuth() && StringUtils.isEmpty(ServletUtil.getHeader(request, "virId", StandardCharsets.UTF_8))) {
                log.error("virId 不能为空");
                throw new AuthException("virId不能为空");
            }

            if (preAuth.tokenAuth() && StringUtils.isEmpty(ServletUtil.getHeader(request, "token", StandardCharsets.UTF_8))) {
                log.error("token 不能为空");
                throw new AuthException("token不能为空");
            }

            if (preAuth.tokenAuth()) {
                boolean isValid = authService.verifyWebToken(request.getHeader("token"), response);
                if (!isValid) {
                    throw new AuthException("Error: 无访问权限 | No access rights");
                }
            }

            if (StringUtils.isNotEmpty(response.getHeader("token"))) {
                response.setHeader("token", ServletUtil.getHeader(request, "token", StandardCharsets.UTF_8));
            }
            result = point.proceed();
//            response.setContentType("application/json; charset=utf-8");
        } else {
            if ("getUserInfo".equals(aimMethod.getName())) {
                if (StringUtils.isEmpty(ServletUtil.getHeader(request, "virId", StandardCharsets.UTF_8))) {
                    log.error("virId 不能为空");
                    throw new AuthException("virId不能为空");
                }
                // 未登录时忽略校验
                if (StringUtils.isNotEmpty(ServletUtil.getHeader(request, "token", StandardCharsets.UTF_8))) {
                    boolean isValid = authService.verifyWebToken(request.getHeader("token"), response);
                    if (!isValid) {
                        throw new AuthException("Error: 无访问权限 | No access rights");
                    }
                    result = point.proceed();
                    if (StringUtils.isNotEmpty(response.getHeader("token"))) {
                        response.setHeader("token", ServletUtil.getHeader(request, "token", StandardCharsets.UTF_8));
                    }
                } else {
                    result = point.proceed();
                }
            }
//            response.setContentType("application/json; charset=utf-8");
//            response.getWriter().write(gson.toJson(result));
//            response.getWriter().write(
//            );
//            ObjectMapper objectMapper = new ObjectMapper();
//            resp.getWriter().write(objectMapper.writeValueAsString(restResult))
        }

        return result;
    }

//    private void writeResponse(HttpServletResponse resp, Object restResult) throws IOException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        resp.setStatus(status.value());
//        resp.setContentType("application/json; charset=utf-8");
//        resp.getWriter().write(objectMapper.writeValueAsString(restResult));
//    }
}
