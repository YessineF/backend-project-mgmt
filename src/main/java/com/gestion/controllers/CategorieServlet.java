package com.gestion.controllers;

import com.gestion.models.Categorie;
import com.gestion.services.CategorieService;
import com.gestion.utils.JsonUtil;
import com.google.gson.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * Servlet Catégorie — CRUD complet.
 *
 * GET    /api/categories        → liste toutes les catégories
 * GET    /api/categories/{id}   → une catégorie par ID
 * POST   /api/categories        → créer une catégorie
 * PUT    /api/categories/{id}   → modifier une catégorie
 * DELETE /api/categories/{id}   → supprimer une catégorie
 */
@WebServlet("/api/categories/*")
public class CategorieServlet extends HttpServlet {

    private final CategorieService categorieService = new CategorieService();

    // ── GET ────────────────────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        String pathInfo = request.getPathInfo();

        // GET /api/categories → liste complète
        // Accessible par ADMIN et EMPLOYE (pour le formulaire d'affectation)
        if (pathInfo == null || pathInfo.equals("/")) {
            List<Categorie> categories = categorieService.getAllCategories();
            JsonUtil.sendResponse(response, 200, categories);
            return;
        }

        // GET /api/categories/{id}
        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            Categorie cat = categorieService.getCategorieById(id);

            if (cat == null) {
                JsonUtil.sendError(response, 404, "Catégorie introuvable");
                return;
            }

            JsonUtil.sendResponse(response, 200, cat);

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

        String body  = JsonUtil.readBody(request);
        JsonObject json = JsonUtil.fromJson(body, JsonObject.class);

        if (json == null || !json.has("nom")) {
            JsonUtil.sendError(response, 400, "Champ 'nom' requis");
            return;
        }

        String nom = json.get("nom").getAsString().trim();

        if (nom.isEmpty()) {
            JsonUtil.sendError(response, 400, "Le nom ne peut pas être vide");
            return;
        }

        try {
            Categorie created = categorieService.createCategorie(nom);
            JsonUtil.sendResponse(response, 201, created);
        } catch (IllegalArgumentException e) {
            JsonUtil.sendError(response, 409, e.getMessage());
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
            Long id      = Long.parseLong(pathInfo.substring(1));
            String body  = JsonUtil.readBody(request);
            JsonObject json = JsonUtil.fromJson(body, JsonObject.class);

            if (json == null || !json.has("nom")) {
                JsonUtil.sendError(response, 400, "Champ 'nom' requis");
                return;
            }

            String nom = json.get("nom").getAsString().trim();
            Categorie updated = categorieService.updateCategorie(id, nom);

            if (updated == null) {
                JsonUtil.sendError(response, 404, "Catégorie introuvable");
                return;
            }

            JsonUtil.sendResponse(response, 200, updated);

        } catch (NumberFormatException e) {
            JsonUtil.sendError(response, 400, "ID invalide");
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
            boolean done = categorieService.deleteCategorie(id);

            if (!done) {
                JsonUtil.sendError(response, 404, "Catégorie introuvable");
                return;
            }

            response.setStatus(204);

        } catch (NumberFormatException e) {
            JsonUtil.sendError(response, 400, "ID invalide");
        } catch (Exception e) {
            // Contrainte FK : catégorie liée à des employés
            JsonUtil.sendError(response, 409,
                    "Impossible de supprimer : catégorie utilisée par des employés");
        }
    }

    // ── Helper ─────────────────────────────────────────────────────
    private boolean isAdmin(HttpServletRequest request) {
        return "ADMIN".equals(request.getAttribute("role"));
    }
}