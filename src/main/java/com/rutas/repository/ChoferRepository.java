package com.rutas.repository;

import com.rutas.models.Chofer;
import jakarta.persistence.EntityManager;
import java.util.List;

public class ChoferRepository extends GenericDAO<Chofer, Long> {
    public ChoferRepository() {
        super(Chofer.class);
    }

    public List<Chofer> listarActivos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT c FROM Chofer c WHERE c.activo = true";
            return em.createQuery(jpql, Chofer.class).getResultList();
        } finally {
            em.close();
        }
    }
}
