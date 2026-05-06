package com.gestion.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Singleton thread-safe pour gérer l'EntityManagerFactory JPA.
 * À utiliser dans tous les DAO.
 */
public class JPAUtil {

    private static final String PERSISTENCE_UNIT = "GestionPU";
    private static EntityManagerFactory emf;

    // Initialisation au premier appel
    static {
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    }

    // Empêche l'instanciation
    private JPAUtil() {}

    /** Retourne un nouvel EntityManager (à fermer après usage) */
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /** À appeler à l'arrêt de l'application */
    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}