package com.gestion.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class JPAUtil {

    private static final String PERSISTENCE_UNIT = "GestionPU";
    private static EntityManagerFactory emf;

    static {
        Map<String, Object> props = new HashMap<>();

        // Lire les variables d'environnement Railway (prioritaires sur persistence.xml)
        String dbUrl      = System.getenv("DB_URL");
        String dbUser     = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");

        if (dbUrl != null && !dbUrl.isEmpty()) {
            props.put("javax.persistence.jdbc.url",      dbUrl);
            props.put("javax.persistence.jdbc.user",     dbUser != null ? dbUser : "");
            props.put("javax.persistence.jdbc.password", dbPassword != null ? dbPassword : "");
            System.out.println("=== JPAUtil: connexion Railway DB ===");
        } else {
            System.out.println("=== JPAUtil: connexion locale (DB_URL non défini) ===");
        }

        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, props);
    }

    private JPAUtil() {}

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
