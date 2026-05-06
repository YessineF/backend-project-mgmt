package com.gestion.utils;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// ✅ Pas d'annotation @WebFilter — déclaré dans web.xml uniquement
public class JwtFilter implements Filter {

    private static final List<String> PUBLIC_ROUTES = Arrays.asList(
            "/api/auth/login"
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest)  req;
        HttpServletResponse response = (HttpServletResponse) res;

        String pathInfo = request.getPathInfo();
        String path = request.getServletPath() + (pathInfo != null ? pathInfo : "");

        // ✅ Laisse passer les routes publiques et OPTIONS
        if (PUBLIC_ROUTES.contains(path) ||
                "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(req, res);
            return;
        }

        // 🔐 Vérification JWT
        String authHeader = request.getHeader("Authorization");
        String token      = JwtUtil.extractFromHeader(authHeader);

        if (token == null || !JwtUtil.validateToken(token)) {
            JsonUtil.sendError(response, 401, "Token invalide ou manquant");
            return;
        }

        request.setAttribute("email", JwtUtil.extractEmail(token));
        request.setAttribute("role",  JwtUtil.extractRole(token));

        chain.doFilter(req, res);
    }

    @Override public void init(FilterConfig filterConfig) {}
    @Override public void destroy() {}
}