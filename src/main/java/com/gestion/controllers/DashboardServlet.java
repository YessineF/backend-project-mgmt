package com.gestion.controllers;

import com.gestion.dao.AffectationDAO;
import com.gestion.dao.CategorieDAO;
import com.gestion.dao.EmployeDAO;
import com.gestion.dto.ProjetDTO;
import com.gestion.services.ProjetService;
import com.gestion.utils.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * GET /api/dashboard/stats
 * Retourne les statistiques agrégées pour le dashboard admin.
 */
@WebServlet("/api/dashboard/stats")
public class DashboardServlet extends HttpServlet {

    private final ProjetService   projetService   = new ProjetService();
    private final EmployeDAO      employeDAO      = new EmployeDAO();
    private final CategorieDAO    categorieDAO    = new CategorieDAO();
    private final AffectationDAO  affectationDAO  = new AffectationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        try {
            // ── 1. Récupérer les données via le Service (retourne List<ProjetDTO>) ──
            List<ProjetDTO> projets = projetService.getAllProjets();

            // ── 2. Compter par statut (getStatut() retourne un String dans ProjetDTO) ──
            long enCours   = projets.stream().filter(p -> "EN_COURS".equals(p.getStatut())).count();
            long termines  = projets.stream().filter(p -> "TERMINE".equals(p.getStatut())).count();
            long enAttente = projets.stream().filter(p -> "EN_ATTENTE".equals(p.getStatut())).count();

            // ── 3. Agrégation des budgets (BigDecimal → doubleValue) ─────────────────
            double budgetTotal = projets.stream()
                    .filter(p -> p.getBudget() != null)
                    .mapToDouble(p -> p.getBudget().doubleValue())
                    .sum();

            double budgetMoyen = projets.isEmpty() ? 0.0 : budgetTotal / projets.size();

            double budgetEnCours = projets.stream()
                    .filter(p -> "EN_COURS".equals(p.getStatut()) && p.getBudget() != null)
                    .mapToDouble(p -> p.getBudget().doubleValue())
                    .sum();

            double budgetTermines = projets.stream()
                    .filter(p -> "TERMINE".equals(p.getStatut()) && p.getBudget() != null)
                    .mapToDouble(p -> p.getBudget().doubleValue())
                    .sum();

            double budgetEnAttente = projets.stream()
                    .filter(p -> "EN_ATTENTE".equals(p.getStatut()) && p.getBudget() != null)
                    .mapToDouble(p -> p.getBudget().doubleValue())
                    .sum();

            // ── 4. Les 5 projets les plus récents ─────────────────────────────────────
            List<Map<String, Object>> recents = projets.stream()
                    .limit(5)
                    .map(p -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("id",            p.getId());
                        m.put("nom",           p.getNom());
                        m.put("description",   p.getDescription());
                        m.put("statut",        p.getStatut());
                        m.put("budget",        p.getBudget() != null ? p.getBudget().doubleValue() : 0.0);
                        m.put("nombreEmployes", p.getNombreEmployes());
                        return m;
                    })
                    .collect(Collectors.toList());

            // ── 5. Construire la réponse ──────────────────────────────────────────────
            Map<String, Object> stats = new HashMap<>();

            // Totaux globaux
            stats.put("totalEmployes",     employeDAO.findAll().size());
            stats.put("totalProjets",      projets.size());
            stats.put("totalCategories",   categorieDAO.findAll().size());
            stats.put("totalAffectations", affectationDAO.findAll().size());

            // Projets par statut
            stats.put("projetsEnCours",   enCours);
            stats.put("projetsTermines",  termines);
            stats.put("projetsEnAttente", enAttente);

            // Budgets (arrondis à l'entier)
            stats.put("budgetTotal",      Math.round(budgetTotal));
            stats.put("budgetMoyen",      Math.round(budgetMoyen));
            stats.put("budgetEnCours",    Math.round(budgetEnCours));
            stats.put("budgetTermines",   Math.round(budgetTermines));
            stats.put("budgetEnAttente",  Math.round(budgetEnAttente));

            // Projets récents
            stats.put("projetsRecents",   recents);

            // ── 6. Envoyer la réponse JSON via JsonUtil (cohérent avec le reste) ──────
            JsonUtil.sendResponse(resp, 200, stats);

        } catch (Exception e) {
            JsonUtil.sendError(resp, 500, "Erreur dashboard : " + e.getMessage());
        }
    }
}