package com.chat.crazy.base.config;

import com.chat.crazy.base.constant.ApplicationConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import javax.servlet.FilterConfig;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.UUID;


@WebFilter(urlPatterns = "/*", filterName = "mdcFilter")
public class MdcFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig){}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException{
        try{
            MDC.put(ApplicationConstant.TRACE_ID, UUID.randomUUID().toString().replace("-", ""));
            chain.doFilter(request, response);
        }finally {
            MDC.remove(ApplicationConstant.TRACE_ID);
        }
    }

    @Override
    public void destroy(){}

    public static String fetchCurrTraceId() {
        return StringUtils.trimToEmpty(MDC.get(ApplicationConstant.TRACE_ID));
    }

    public static void refreshMDCTraceId() {
        MDC.put(ApplicationConstant.TRACE_ID, UUID.randomUUID().toString().replace("-", ""));
    }

    public static void  overWriteMDCTraceId(String currTraceId) {
        currTraceId = StringUtils.trimToEmpty(currTraceId);
        MDC.put(ApplicationConstant.TRACE_ID, currTraceId);
    }

    public static void cleanCurrMDCTraceId() {
        MDC.remove(ApplicationConstant.TRACE_ID);
    }
}
