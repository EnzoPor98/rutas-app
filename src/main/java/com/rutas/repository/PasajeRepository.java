package com.rutas.repository;

import java.util.List;

import com.rutas.models.Pasaje;

import jakarta.persistence.EntityManager;

public class PasajeRepository extends GenericDAO<Pasaje, Long> {
    public PasajeRepository() {
        super(Pasaje.class);
    }

    public List<Pasaje> buscarPorViaje(Long viajeId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT p FROM Pasaje p WHERE p.viaje.id = :viajeId";
            return em.createQuery(jpql, Pasaje.class)
                    .setParameter("viajeId", viajeId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public boolean estaAsientoOcupado(Long viajeId, Integer numeroAsiento) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(p) FROM Pasaje p WHERE p.viaje.id = :viajeId AND p.numeroAsiento = :asiento";
            Long count = em.createQuery(jpql, Long.class)
                    .setParameter("viajeId", viajeId)
                    .setParameter("asiento", numeroAsiento)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}