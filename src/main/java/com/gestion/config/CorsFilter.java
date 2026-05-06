package com.gestion.config;

import jakarta.servlet.*;
import java.io.IOException;

// Ce filtre est remplacé par org.apache.catalina.filters.CorsFilter dans web.xml
public class CorsFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(req, res);
    }
    @Override public void init(FilterConfig filterConfig) {}
    @Override public void destroy() {}
}