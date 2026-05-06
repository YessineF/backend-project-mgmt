package com.gestion.utils;



import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filtre JWT — vérifie le token sur toutes les routes /api/*
 * sauf /api/auth/login qui est publique.
 */
@WebFilter("/api/*")
public class JwtFilter implements Filter {

    // Routes publiques (pas besoin de token)
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

        // ✅ Route publique → on laisse passer sans vérification
        if (PUBLIC_ROUTES.contains(path) ||
                "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(req, res);
            return;
        }

        // 🔐 Routes protégées → vérification du token JWT
        String authHeader = request.getHeader("Authorization");
        String token      = JwtUtil.extractFromHeader(authHeader);

        if (token == null || !JwtUtil.validateToken(token)) {
            JsonUtil.sendError(response, 401, "Token invalide ou manquant");
            return;
        }

        // ✅ Token valide → on enrichit la requête avec les infos de l'utilisateur
        request.setAttribute("email", JwtUtil.extractEmail(token));
        request.setAttribute("role",  JwtUtil.extractRole(token));

        chain.doFilter(req, res);
    }

    @Override public void init(FilterConfig filterConfig) {}
    @Override public void destroy() {}
}