package com.gestion.controllers;

import com.gestion.dto.EmployeInputDTO;
import com.gestion.models.Employe;
import com.gestion.services.EmployeService;
import com.gestion.utils.JsonUtil;
import com.gestion.utils.JwtUtil;
import com.google.gson.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Servlet Auth — gère l'authentification.
 *
 * POST /api/auth/login  → retourne un token JWT
 * GET  /api/auth/me     → retourne l'utilisateur connecté (token requis)
 */
@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {

    private final EmployeService employeService = new EmployeService();

    // ── POST /api/auth/login ───────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {

        String path = request.getPathInfo(); // ex: "/login"

        if ("/login".equals(path)) {
            handleLogin(request, response);
        } else {
            JsonUtil.sendError(response, 404, "Route introuvable");
        }
    }

    // ── GET /api/auth/me ───────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        String path = request.getPathInfo();

        if ("/me".equals(path)) {
            handleMe(request, response);
        } else {
            JsonUtil.sendError(response, 404, "Route introuvable");
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  Handlers privés
    // ══════════════════════════════════════════════════════════════

    private void handleLogin(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        // 1. Lire le body JSON
        String body = JsonUtil.readBody(request);
        EmployeInputDTO input = JsonUtil.fromJson(body, EmployeInputDTO.class);

        // 2. Validation basique
        if (input == null || input.getEmail() == null || input.getPassword() == null) {
            JsonUtil.sendError(response, 400, "Email et mot de passe requis");
            return;
        }

        // 3. Authentification
        Employe employe = employeService.authenticate(
                input.getEmail(), input.getPassword()
        );

        if (employe == null) {
            JsonUtil.sendError(response, 401, "Email ou mot de passe incorrect");
            return;
        }

        // 4. Générer le token JWT
        String token = JwtUtil.generateToken(
                employe.getEmail(),
                employe.getRole().name()
        );

        // 5. Construire la réponse JSON
        JsonObject data = new JsonObject();
        data.addProperty("id",     employe.getId());
        data.addProperty("token",  token);
        data.addProperty("email",  employe.getEmail());
        data.addProperty("nom",    employe.getNom());
        data.addProperty("prenom", employe.getPrenom());
        data.addProperty("role",   employe.getRole().name());

        JsonUtil.sendResponse(response, 200, data);
    }

    private void handleMe(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        // L'email est injecté par JwtFilter après validation du token
        String email = (String) request.getAttribute("email");
        String role  = (String) request.getAttribute("role");

        if (email == null) {
            JsonUtil.sendError(response, 401, "Non authentifié");
            return;
        }

        JsonObject data = new JsonObject();
        data.addProperty("email", email);
        data.addProperty("role",  role);

        JsonUtil.sendResponse(response, 200, data);
    }
}