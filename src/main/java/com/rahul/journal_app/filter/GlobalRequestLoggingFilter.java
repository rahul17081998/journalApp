package com.rahul.journal_app.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Slf4j
@Component
@WebFilter("/*") // Applies to all requests
public class GlobalRequestLoggingFilter implements Filter {
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_RESET = "\u001B[0m";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        String httpMethod = httpRequest.getMethod();
        String requestUri = httpRequest.getRequestURI().replaceFirst("^/journal", "");

        log.info("{}Incoming Request -> Method: [{}], URI: [{}]{}", ANSI_BLUE, httpMethod, requestUri, ANSI_RESET);
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
