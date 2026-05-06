package com.gestion.controllers;

import com.gestion.dto.ProjetDTO;
import com.gestion.dto.ProjetInputDTO;
import com.gestion.services.ProjetService;
import com.gestion.utils.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * Servlet Projet — CRUD complet.
 *
 * GET    /api/projets              → liste tous les projets  (ADMIN + EMPLOYE)
 * GET    /api/projets/{id}         → un projet par ID        (ADMIN + EMPLOYE)
 * GET    /api/projets/employe/{id} → projets d'un employé    (ADMIN + EMPLOYE)
 * POST   /api/projets              → créer un projet         (ADMIN)
 * PUT    /api/projets/{id}         → modifier un projet      (ADMIN)
 * DELETE /api/projets/{id}         → supprimer un projet     (ADMIN)
 */
@WebServlet("/api/projets/*")
public class ProjetServlet extends HttpServlet {

    private final ProjetService projetService = new ProjetService();

    // ── GET ────────────────────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        String pathInfo = request.getPathInfo(); // null | "/" | "/{id}" | "/employe/{id}"

        // GET /api/projets → liste complète
        if (pathInfo == null || pathInfo.equals("/")) {
            List<ProjetDTO> projets = projetService.getAllProjets();
            JsonUtil.sendResponse(response, 200, projets);
            return;
        }

        // GET /api/projets/employe/{id} → projets d'un employé
        if (pathInfo.startsWith("/employe/")) {
            try {
                Long employeId = Long.parseLong(pathInfo.substring(9));
                List<ProjetDTO> projets = projetService.getProjetsByEmploye(employeId);
                JsonUtil.sendResponse(response, 200, projets);
            } catch (NumberFormatException e) {
                JsonUtil.sendError(response, 400, "ID employé invalide");
            }
            return;
        }

        // GET /api/projets/{id}
        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            ProjetDTO projet = projetService.getProjetById(id);

            if (projet == null) {
                JsonUtil.sendError(response, 404, "Projet introuvable");
                return;
            }

            JsonUtil.sendResponse(response, 200, projet);

        } catch (NumberFormatException e) {
            JsonUtil.sendError(response, 400, "ID invalide");
        }
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
        ProjetInputDTO input = JsonUtil.fromJson(body, ProjetInputDTO.class);

        if (input == null || isBlank(input.getNom())) {
            JsonUtil.sendError(response, 400, "Champ 'nom' requis");
            return;
        }

        try {
            ProjetDTO created = projetService.createProjet(input);
            JsonUtil.sendResponse(response, 201, created);
        } catch (Exception e) {
            JsonUtil.sendError(response, 500, "Erreur serveur : " + e.getMessage());
        }
    }

    // ── PUT ────────────────────────────────────────────────────────
    @Override
    protected void doPut(HttpServletRequest request,
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
            Long id = Long.parseLong(pathInfo.substring(1));
            String body = JsonUtil.readBody(request);
            ProjetInputDTO input = JsonUtil.fromJson(body, ProjetInputDTO.class);

            if (input == null) {
                JsonUtil.sendError(response, 400, "Body JSON invalide");
                return;
            }

            ProjetDTO updated = projetService.updateProjet(id, input);

            if (updated == null) {
                JsonUtil.sendError(response, 404, "Projet introuvable");
                return;
            }

            JsonUtil.sendResponse(response, 200, updated);

        } catch (NumberFormatException e) {
            JsonUtil.sendError(response, 400, "ID invalide");
        } catch (IllegalArgumentException e) {
            JsonUtil.sendError(response, 400, "Statut invalide. Valeurs: EN_COURS, TERMINE, EN_ATTENTE");
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
            boolean done = projetService.deleteProjet(id);

            if (!done) {
                JsonUtil.sendError(response, 404, "Projet introuvable");
                return;
            }

            response.setStatus(204);

        } catch (NumberFormatException e) {
            JsonUtil.sendError(response, 400, "ID invalide");
        }
    }

    // ── Helpers ────────────────────────────────────────────────────
    private boolean isAdmin(HttpServletRequest req) {
        return "ADMIN".equals(req.getAttribute("role"));
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}