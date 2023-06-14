package com.chat.crazy.base.config;

import com.chat.crazy.base.annotation.ApiAdminRestController;
import com.chat.crazy.base.constant.ApplicationConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author:
 * @Description:
 * @Date: 2023/4/14 下午5:46
 */
@Configuration
public class WebConfig {
    
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            //重写这个方法，添加跨域设置
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                //定义哪些URL接受跨域
                registry.addMapping("/**")
                        //定义哪些origin可以跨域请求
                        .allowedOrigins("*")
                        //定义接受的跨域请求方法
                        .allowedMethods("POST", "GET", "PUT", "PATCH", "OPTIONS", "DELETE")
                        .exposedHeaders("token")
                        .allowCredentials(true)
                        .allowedHeaders("*")
                        .maxAge(3600);
            }
            
            //注册拦截器
//            @Override
//            public void addInterceptors(InterceptorRegistry registry) {
//                registry.addInterceptor(userSecurityHandlerInterceptor()).addPathPatterns("/**").excludePathPatterns("/login", "/dev/**");
//            }
//
            // 管理端接口添加前缀/admin
            @Override
            public void configurePathMatch(PathMatchConfigurer configurer) {
                configurer.addPathPrefix(ApplicationConstant.ADMIN_PATH_PREFIX, c -> c.isAnnotationPresent(ApiAdminRestController.class));
            }
        };
    }

}
