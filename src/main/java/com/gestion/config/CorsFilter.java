package com.gestion.config;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtre CORS global — intercepte TOUTES les requêtes HTTP.
 * Indispensable pour que Angular (port 4200) puisse appeler
 * le backend (port 8080) sans être bloqué par le navigateur.
 */
@WebFilter("/*")
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest)  req;
        HttpServletResponse response = (HttpServletResponse) res;

        // ✅ Origine Angular autorisée
response.setHeader("Access-Control-Allow-Origin", 
    "https://frontend-project-mgmt.vercel.app");
        // ✅ Méthodes HTTP autorisées
        response.setHeader("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS");

        // ✅ Headers autorisés (Authorization pour le JWT)
        response.setHeader("Access-Control-Allow-Headers",
                "Content-Type, Authorization, Accept");

        // ✅ Autorise Angular à lire les headers de réponse
        response.setHeader("Access-Control-Expose-Headers", "Authorization");

        // ✅ Durée du cache preflight (en secondes)
        response.setHeader("Access-Control-Max-Age", "3600");

        // Les requêtes OPTIONS sont des "preflight" envoyées par le navigateur
        // avant chaque requête cross-origin → on répond 200 directement
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // Sinon, on laisse passer la requête vers la Servlet
        chain.doFilter(req, res);
    }

    @Override public void init(FilterConfig filterConfig) {}
    @Override public void destroy() {}
}