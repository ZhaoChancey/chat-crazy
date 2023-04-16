package com.chat.crazy.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<MdcFilter> registerFilter() {
    FilterRegistrationBean<MdcFilter> registration = new FilterRegistrationBean<>();
    registration.setFilter(new MdcFilter());
    registration.addUrlPatterns("/*");
    registration.setName("LogTraceIdFilter");
    registration.setOrder(1);
    return registration;
}

}
