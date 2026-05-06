package com.gestion.config;

import com.gestion.dao.EmployeDAO;
import com.gestion.models.Employe;
import org.mindrot.jbcrypt.BCrypt;


public class DataInitializer {

    private static final String ADMIN_EMAIL    = "admin@admin.com";
    private static final String ADMIN_PASSWORD = "Admin123";
    private static final String ADMIN_NOM      = "Admin";
    private static final String ADMIN_PRENOM   = "System";

    public static void init() {
        EmployeDAO dao = new EmployeDAO();

        if (dao.emailExists(ADMIN_EMAIL)) {
            System.out.println("=== Admin déjà existant — aucune création nécessaire ===");
            return;
        }

        Employe admin = new Employe();
        admin.setNom(ADMIN_NOM);
        admin.setPrenom(ADMIN_PRENOM);
        admin.setEmail(ADMIN_EMAIL);
        admin.setPassword(BCrypt.hashpw(ADMIN_PASSWORD, BCrypt.gensalt(10)));
        admin.setRole(Employe.Role.ADMIN);

        dao.save(admin);
        System.out.println("=== Admin créé : " + ADMIN_EMAIL + " / " + ADMIN_PASSWORD + " ===");
    }
}
