package com.gestion.controllers;

import com.gestion.dto.AffectationDTO;
import com.gestion.services.AffectationService;
import com.gestion.utils.JsonUtil;
import com.google.gson.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Servlet Affectation.
 *
 * GET    /api/affectations              → toutes les affectations  (ADMIN)
 * GET    /api/affectations/projet/{id}  → par projet               (ADMIN + EMPLOYE)
 * GET    /api/affectations/employe/{id} → par employé              (ADMIN + EMPLOYE)
 * POST   /api/affectations              → créer une affectation    (ADMIN)
 * DELETE /api/affectations/{id}         → supprimer                (ADMIN)
 */
@WebServlet("/api/affectations/*")
public class AffectationServlet extends HttpServlet {

    private final AffectationService affectationService = new AffectationService();

    // ── GET ────────────────────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        String pathInfo = request.getPathInfo();

        // GET /api/affectations → toutes (ADMIN uniquement)
        if (pathInfo == null || pathInfo.equals("/")) {
            if (!isAdmin(request)) {
                JsonUtil.sendError(response, 403, "Accès refusé");
                return;
            }
            List<AffectationDTO> list = affectationService.getAllAffectations();
            JsonUtil.sendResponse(response, 200, list);
            return;
        }

        // GET /api/affectations/projet/{id}
        if (pathInfo.startsWith("/projet/")) {
            try {
                Long projetId = Long.parseLong(pathInfo.substring(8));
                List<AffectationDTO> list = affectationService.getByProjet(projetId);
                JsonUtil.sendResponse(response, 200, list);
            } catch (NumberFormatException e) {
                JsonUtil.sendError(response, 400, "ID projet invalide");
            }
            return;
        }

        // GET /api/affectations/employe/{id}
        if (pathInfo.startsWith("/employe/")) {
            try {
                Long employeId = Long.parseLong(pathInfo.substring(9));
                List<AffectationDTO> list = affectationService.getByEmploye(employeId);
                JsonUtil.sendResponse(response, 200, list);
            } catch (NumberFormatException e) {
                JsonUtil.sendError(response, 400, "ID employé invalide");
            }
            return;
        }

        JsonUtil.sendError(response, 404, "Route introuvable");
    }

    // ── POST ───────────────────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws IOException {

        if (!isAdmin(request)) {
            JsonUtil.sendError(response, 403, "Accès refusé");
            return;
        }

        String body = JsonUtil.readBody(request);
        JsonObject json = JsonUtil.fromJson(body, JsonObject.class);

        // Validation des champs obligatoires
        if (json == null
                || !json.has("employeId")
                || !json.has("projetId")
                || !json.has("dateDebut")) {
            JsonUtil.sendError(response, 400,
                    "Champs requis : employeId, projetId, dateDebut");
            return;
        }

        try {
            Long      employeId = json.get("employeId").getAsLong();
            Long      projetId  = json.get("projetId").getAsLong();
            LocalDate dateDebut = LocalDate.parse(json.get("dateDebut").getAsString());
            LocalDate dateFin   = json.has("dateFin") && !json.get("dateFin").isJsonNull()
                    ? LocalDate.parse(json.get("dateFin").getAsString())
                    : null;

            AffectationDTO created = affectationService.createAffectation(
                    employeId, projetId, dateDebut, dateFin
            );

            JsonUtil.sendResponse(response, 201, created);

        } catch (IllegalArgumentException e) {
            JsonUtil.sendError(response, 409, e.getMessage());
        } catch (Exception e) {
            JsonUtil.sendError(response, 500, "Erreur serveur : " + e.getMessage());
        }
    }

    // ── DELETE ─────────────────────────────────────────────────────
    @Override
    protected void doDelete(HttpServletRequest request,
                            HttpServletResponse response) throws IOException {

        if (!isAdmin(request)) {
            JsonUtil.sendError(response, 403, "Accès refusé");
            return;
        }

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            JsonUtil.sendError(response, 400, "ID requis dans l'URL");
            return;
        }

        try {
            Long id      = Long.parseLong(pathInfo.substring(1));
            boolean done = affectationService.deleteAffectation(id);

            if (!done) {
                JsonUtil.sendError(response, 404, "Affectation introuvable");
                return;
            }

            response.setStatus(204);

        } catch (NumberFormatException e) {
            JsonUtil.sendError(response, 400, "ID invalide");
        }
    }

    // ── Helper ─────────────────────────────────────────────────────
    private boolean isAdmin(HttpServletRequest req) {
        return "ADMIN".equals(req.getAttribute("role"));
    }
}