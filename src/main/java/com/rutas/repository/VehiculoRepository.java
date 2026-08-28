package com.rutas.repository;

import com.rutas.models.Colectivo;
import com.rutas.models.Vehiculo;
import com.rutas.models.Enums.EstadoVehiculo;
import jakarta.persistence.EntityManager;
import java.util.List;

public class VehiculoRepository extends GenericDAO<Vehiculo, Long> {
    public VehiculoRepository() {
        super(Vehiculo.class);
    }

    public List<Vehiculo> listarDisponibles() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT v FROM Vehiculo v WHERE v.estado = :estado";
            return em.createQuery(jpql, Vehiculo.class)
                    .setParameter("estado", EstadoVehiculo.DISPONIBLE)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Colectivo> listarColectivos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT c FROM Colectivo c";
            return em.createQuery(jpql, Colectivo.class).getResultList();
        } finally {
            em.close();
        }
    }
}
