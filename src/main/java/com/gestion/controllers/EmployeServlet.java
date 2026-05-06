package com.gestion.controllers;

import com.gestion.dto.EmployeDTO;
import com.gestion.dto.EmployeInputDTO;
import com.gestion.models.Employe;
import com.gestion.services.EmployeService;
import com.gestion.utils.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * Servlet Employé — CRUD complet.
 *
 * GET    /api/employes          → liste tous les employés
 * GET    /api/employes/{id}     → un employé par ID
 * POST   /api/employes          → créer un employé
 * PUT    /api/employes/{id}     → modifier un employé
 * DELETE /api/employes/{id}     → supprimer un employé
 */
@WebServlet("/api/employes/*")
public class EmployeServlet extends HttpServlet {

    private final EmployeService employeService = new EmployeService();

    // ── GET ────────────────────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        String role = (String) request.getAttribute("role");

        // Seul l'ADMIN peut lister les employés
        if (!"ADMIN".equals(role)) {
            JsonUtil.sendError(response, 403, "Accès refusé");
            return;
        }

        String pathInfo = request.getPathInfo(); // null ou "/{id}"

        // GET /api/employes → liste complète
        if (pathInfo == null || pathInfo.equals("/")) {
            List<EmployeDTO> employes = employeService.getAllEmployes();
            JsonUtil.sendResponse(response, 200, employes);
            return;
        }

        // GET /api/employes/{id} → un seul employé
        try {
            Long id = extractId(pathInfo);
            EmployeDTO employe = employeService.getEmployeById(id);

            if (employe == null) {
                JsonUtil.sendError(response, 404, "Employé introuvable");
                return;
            }

            JsonUtil.sendResponse(response, 200, employe);

        } catch (NumberFormatException e) {
            JsonUtil.sendError(response, 400, "ID invalide");
        }
    }

    // ── POST ───────────────────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {

        String role = (String) request.getAttribute("role");

        if (!"ADMIN".equals(role)) {
            JsonUtil.sendError(response, 403, "Accès refusé");
            return;
        }

        // Lire et désérialiser le body JSON
        String body = JsonUtil.readBody(request);
        EmployeInputDTO input = JsonUtil.fromJson(body, EmployeInputDTO.class);

        // Validation des champs obligatoires
        if (input == null
                || isBlank(input.getNom())
                || isBlank(input.getPrenom())
                || isBlank(input.getEmail())
                || isBlank(input.getPassword())) {
            JsonUtil.sendError(response, 400,
                    "Champs obligatoires : nom, prenom, email, password");
            return;
        }

        try {
            Employe employe    = employeService.toEntity(input);
            EmployeDTO created = employeService.createEmploye(employe);
            JsonUtil.sendResponse(response, 201, created);

        } catch (IllegalArgumentException e) {
            // Email déjà utilisé
            JsonUtil.sendError(response, 409, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(response, 500, "Erreur serveur : " + e.getMessage());
        }
    }

    // ── PUT ────────────────────────────────────────────────────────
    @Override
    protected void doPut(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        String role = (String) request.getAttribute("role");

        if (!"ADMIN".equals(role)) {
            JsonUtil.sendError(response, 403, "Accès refusé");
            return;
        }

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            JsonUtil.sendError(response, 400, "ID requis dans l'URL");
            return;
        }

        try {
            Long id = extractId(pathInfo);

            String body = JsonUtil.readBody(request);
            EmployeInputDTO input = JsonUtil.fromJson(body, EmployeInputDTO.class);

            if (input == null) {
                JsonUtil.sendError(response, 400, "Body JSON invalide");
                return;
            }

            Employe employe    = employeService.toEntity(input);
            EmployeDTO updated = employeService.updateEmploye(id, employe);

            if (updated == null) {
                JsonUtil.sendError(response, 404, "Employé introuvable");
                return;
            }

            JsonUtil.sendResponse(response, 200, updated);

        } catch (NumberFormatException e) {
            JsonUtil.sendError(response, 400, "ID invalide");
        } catch (Exception e) {
            JsonUtil.sendError(response, 500, "Erreur serveur : " + e.getMessage());
        }
    }

    // ── DELETE ─────────────────────────────────────────────────────
    @Override
    protected void doDelete(HttpServletRequest request,
                            HttpServletResponse response) throws IOException {

        String role = (String) request.getAttribute("role");

        if (!"ADMIN".equals(role)) {
            JsonUtil.sendError(response, 403, "Accès refusé");
            return;
        }

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            JsonUtil.sendError(response, 400, "ID requis dans l'URL");
            return;
        }

        try {
            Long id      = extractId(pathInfo);
            boolean done = employeService.deleteEmploye(id);

            if (!done) {
                JsonUtil.sendError(response, 404, "Employé introuvable");
                return;
            }

            // 204 No Content → suppression réussie sans body
            response.setStatus(204);

        } catch (NumberFormatException e) {
            JsonUtil.sendError(response, 400, "ID invalide");
        } catch (Exception e) {
            JsonUtil.sendError(response, 500, "Erreur serveur : " + e.getMessage());
        }
    }

    // ── Helpers ────────────────────────────────────────────────────

    /** Extrait l'ID numérique depuis le pathInfo "/123" */
    private Long extractId(String pathInfo) {
        return Long.parseLong(pathInfo.substring(1));
    }

    /** Vérifie si une chaîne est null ou vide */
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}