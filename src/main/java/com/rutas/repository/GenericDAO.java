package com.rutas.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;

public abstract class GenericDAO<T, ID> {
    private final Class<T> entityClass;

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public T guardar(T entity) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(entity);
            tx.commit();
            return entity;
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            throw new RuntimeException("Error al guardar " + entityClass.getSimpleName(), e);
        } finally {
            em.close();
        }
    }

    public T actualizar(T entity) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T merged = em.merge(entity);
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            throw new RuntimeException("Error al actualizar " + entityClass.getSimpleName(), e);
        } finally {
            em.close();
        }
    }

    public void eliminarPorId(ID id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T entity = em.find(entityClass, id);
            if (entity != null)
                em.remove(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            throw new RuntimeException("Error al eliminar " + entityClass.getSimpleName(), e);
        } finally {
            em.close();
        }
    }

    public Optional<T> buscarPorId(ID id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(entityClass, id));
        } finally {
            em.close();
        }
    }

    public List<T> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            return em.createQuery(jpql, entityClass).getResultList();
        } finally {
            em.close();
        }
    }
}
