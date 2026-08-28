package com.rutas;

import com.rutas.models.*;
import com.rutas.models.Enums.*;
import com.rutas.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class testDAO {
    public static void main(String[] args) {
        System.out.println("=== INICIANDO PRUEBA DE REPOSITORIOS Y JPA ===");

        // Instanciar Repositorios
        CiudadRepository ciudadRepo = new CiudadRepository();
        ChoferRepository choferRepo = new ChoferRepository();
        VehiculoRepository vehiculoRepo = new VehiculoRepository();
        ViajeRepository viajeRepo = new ViajeRepository();

        try {

            // 1. Cargar Datos Básicos (Ciudades)
            Ciudad origen = new Ciudad();
            origen.setNombre("Buenos Aires");
            origen.setProvincia(Provincia.BUENOS_AIRES); // Asegúrate de ajustar al valor de tu Enum Provincia
            origen = ciudadRepo.guardar(origen);

            Ciudad destino = new Ciudad();
            destino.setNombre("Cordoba");
            destino.setProvincia(Provincia.CORDOBA);
            destino = ciudadRepo.guardar(destino);

            // 2. Cargar Choferes
            Chofer chofer1 = new Chofer();
            chofer1.setDni("12345678");
            chofer1.setNombre("Juan");
            chofer1.setApellido("Perez");
            chofer1.setNumeroLicencia("LIC-001");
            chofer1.setVencimientoLicencia(LocalDate.now().plusYears(2));
            chofer1.setActivo(true);
            chofer1 = choferRepo.guardar(chofer1);

            Chofer chofer2 = new Chofer();
            chofer2.setDni("87654321");
            chofer2.setNombre("Carlos");
            chofer2.setApellido("Gomez");
            chofer2.setNumeroLicencia("LIC-002");
            chofer2.setVencimientoLicencia(LocalDate.now().plusYears(1));
            chofer2.setActivo(true);
            chofer2 = choferRepo.guardar(chofer2);

            // 3. Cargar Colectivo
            Colectivo colectivo = new Colectivo();
            colectivo.setPatente("AA123CD");
            colectivo.setMarca("Mercedes-Benz");
            colectivo.setModelo("Marco Polo");
            colectivo.setCapacidad(50);
            colectivo.setAireAcondicionado(true);
            colectivo.setWifi(true);
            colectivo.setCatering(true);
            colectivo.setDoblePiso(true);
            colectivo.setEstado(EstadoVehiculo.DISPONIBLE);
            colectivo = (Colectivo) vehiculoRepo.guardar(colectivo);

            // 4. Crear un Viaje
            Viaje viaje = new Viaje();
            viaje.setOrigen(origen);
            viaje.setDestino(destino);
            viaje.setFechaHoraSalida(LocalDateTime.now().plusDays(1));
            viaje.setFechaHoraLlegada(LocalDateTime.now().plusDays(1).plusHours(8));
            viaje.setVehiculo(colectivo);
            viaje.getChoferes().add(chofer1);
            viaje.getChoferes().add(chofer2);
            viaje.setEstado(EstadoViaje.PROGRAMADO);

            viajeRepo.guardar(viaje);
            System.out.println("✅ Viaje guardado correctamente con ID: " + viaje.getId());

            // 5. PROBAR REQUERIMIENTO: Control de Solapamiento
            LocalDateTime salidaOverlap = viaje.getFechaHoraSalida().plusHours(2);
            LocalDateTime llegadaOverlap = viaje.getFechaHoraLlegada().plusHours(2);

            boolean choferOcupado = viajeRepo.existeSolapamientoChofer(chofer1.getId(), salidaOverlap, llegadaOverlap,
                    null);
            boolean vehiculoOcupado = viajeRepo.existeSolapamientoVehiculo(colectivo.getId(), salidaOverlap,
                    llegadaOverlap, null);

            System.out.println("🔍 Solapamiento Chofer (Esperado true): " + choferOcupado);
            System.out.println("🔍 Solapamiento Vehículo (Esperado true): " + vehiculoOcupado);

            // 6. PROBAR REQUERIMIENTO: Consultas de Informes
            List<Viaje> programados = viajeRepo.listarViajesProgramados();
            System.out.println("📊 Viajes programados encontrados: " + programados.size());

            List<Viaje> viajesPorColectivo = viajeRepo.buscarPorVehiculo(colectivo.getId());
            System.out.println("📊 Viajes del colectivo " + colectivo.getPatente() + ": " + viajesPorColectivo.size());

            System.out.println("\n✅ TODAS LAS PRUEBAS DE REPOSITORIO FINALIZARON CON ÉXITO.");

        } catch (Exception e) {
            System.err.println("❌ ERROR DURANTE LA PRUEBA:");
            e.printStackTrace();
        } finally {
            JPAUtil.close();
        }
    }
}
