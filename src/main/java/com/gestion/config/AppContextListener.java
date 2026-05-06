package com.gestion.config;

import com.gestion.utils.JPAUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Listener du cycle de vie Tomcat.
 * - Démarrage : initialise JPA (connexion DB)
 * - Arrêt     : ferme proprement l'EntityManagerFactory
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("=== Application démarrée — JPA initialisé ===");
        JPAUtil.getEntityManager().close(); // force l'initialisation du EMF
        DataInitializer.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        JPAUtil.close();
        System.out.println("=== Application arrêtée — JPA fermé ===");
    }
}