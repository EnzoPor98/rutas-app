package com.rutas.repository;

import com.rutas.models.Viaje;
import com.rutas.models.Enums.EstadoViaje;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

public class ViajeRepository extends GenericDAO<Viaje, Long> {

    public ViajeRepository() {
        super(Viaje.class);
    }

    // CONTROL: Validar solapamiento de un vehículo
    public boolean existeSolapamientoVehiculo(Long vehiculoId, LocalDateTime inicio, LocalDateTime fin,
            Long viajeIdExcluir) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(v) FROM Viaje v " +
                    "WHERE v.vehiculo.id = :vehiculoId " +
                    "AND v.estado <> :estadoCancelado " +
                    "AND (:viajeIdExcluir IS NULL OR v.id <> :viajeIdExcluir) " +
                    "AND (v.fechaHoraSalida < :fin AND v.fechaHoraLlegada > :inicio)";

            Long count = em.createQuery(jpql, Long.class)
                    .setParameter("vehiculoId", vehiculoId)
                    .setParameter("estadoCancelado", EstadoViaje.CANCELADO)
                    .setParameter("viajeIdExcluir", viajeIdExcluir)
                    .setParameter("inicio", inicio)
                    .setParameter("fin", fin)
                    .getSingleResult();

            return count > 0;
        } finally {
            em.close();
        }
    }

    // CONTROL: Validar solapamiento de un chofer (evalúa si pertenece a la
    // colección del viaje)
    public boolean existeSolapamientoChofer(Long choferId, LocalDateTime inicio, LocalDateTime fin,
            Long viajeIdExcluir) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(v) FROM Viaje v JOIN v.choferes c " +
                    "WHERE c.id = :choferId " +
                    "AND v.estado <> :estadoCancelado " +
                    "AND (:viajeIdExcluir IS NULL OR v.id <> :viajeIdExcluir) " +
                    "AND (v.fechaHoraSalida < :fin AND v.fechaHoraLlegada > :inicio)";

            Long count = em.createQuery(jpql, Long.class)
                    .setParameter("choferId", choferId)
                    .setParameter("estadoCancelado", EstadoViaje.CANCELADO)
                    .setParameter("viajeIdExcluir", viajeIdExcluir)
                    .setParameter("inicio", inicio)
                    .setParameter("fin", fin)
                    .getSingleResult();

            return count > 0;
        } finally {
            em.close();
        }
    }

    // INFORME 1: Mostrar los viajes programados con información detallada
    public List<Viaje> listarViajesProgramados() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT DISTINCT v FROM Viaje v " +
                    "JOIN FETCH v.origen JOIN FETCH v.destino " +
                    "JOIN FETCH v.vehiculo LEFT JOIN FETCH v.choferes " +
                    "WHERE v.estado = :estado " +
                    "ORDER BY v.fechaHoraSalida ASC";

            return em.createQuery(jpql, Viaje.class)
                    .setParameter("estado", EstadoViaje.PROGRAMADO)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // INFORME 2: Informe detallado de viajes para un colectivo/vehículo determinado
    public List<Viaje> buscarPorVehiculo(Long vehiculoId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT DISTINCT v FROM Viaje v " +
                    "JOIN FETCH v.origen JOIN FETCH v.destino " +
                    "LEFT JOIN FETCH v.choferes " +
                    "WHERE v.vehiculo.id = :vehiculoId " +
                    "ORDER BY v.fechaHoraSalida DESC";

            return em.createQuery(jpql, Viaje.class)
                    .setParameter("vehiculoId", vehiculoId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // INFORME 3: Cantidad de viajes ya realizados por un chofer
    public Long contarViajesRealizadosPorChofer(Long choferId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(v) FROM Viaje v JOIN v.choferes c " +
                    "WHERE c.id = :choferId AND v.estado = :estadoCompletado";

            return em.createQuery(jpql, Long.class)
                    .setParameter("choferId", choferId)
                    .setParameter("estadoCompletado", EstadoViaje.COMPLETADO)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
}