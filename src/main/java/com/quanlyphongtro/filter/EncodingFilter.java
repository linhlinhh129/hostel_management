package com.quanlyphongtro.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.jsp.jstl.core.Config;

import java.io.IOException;
import java.util.TimeZone;

@WebFilter(filterName = "1EncodingFilter", urlPatterns = "/*")
public class EncodingFilter implements Filter {

    private static final TimeZone VN_TIMEZONE = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        TimeZone.setDefault(VN_TIMEZONE);
        System.setProperty("user.timezone", "Asia/Ho_Chi_Minh");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        TimeZone.setDefault(VN_TIMEZONE);
        Config.set(request, Config.FMT_TIME_ZONE, VN_TIMEZONE);
        chain.doFilter(request, response);
    }
}
