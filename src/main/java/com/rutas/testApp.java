package com.rutas;

import com.rutas.repository.JPAUtil;
import jakarta.persistence.EntityManager;

public class testApp {
    public static void main(String[] args) {
        System.out.println(">>> Intentando conectar a la base de datos PostgreSQL...");

        try {
            // 1. Solicita un EntityManager (esto carga persistence.xml y crea las tablas si
            // usas 'update')
            EntityManager em = JPAUtil.getEntityManager();

            // 2. Ejecuta una consulta nativa simple para validar respuesta de PostgreSQL
            Object resultado = em.createNativeQuery("SELECT version();").getSingleResult();

            System.out.println("\n✅ CONEXIÓN EXITOSA!");
            System.out.println("Versión de PostgreSQL: " + resultado);

            // 3. Cierra la conexión
            em.close();
            JPAUtil.close();

        } catch (Exception e) {
            System.err.println("\n❌ ERROR AL CONECTAR A LA BASE DE DATOS:");
            e.printStackTrace();
        }
    }
}
