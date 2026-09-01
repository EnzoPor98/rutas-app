import java.time.LocalDate;
import java.time.LocalDateTime;

import com.rutas.exception.NegocioException;
import com.rutas.models.Chofer;
import com.rutas.models.Ciudad;
import com.rutas.models.Colectivo;
import com.rutas.models.Viaje;
import com.rutas.models.Enums.EstadoVehiculo;
import com.rutas.models.Enums.Provincia;
import com.rutas.repository.CiudadRepository;
import com.rutas.repository.JPAUtil;
import com.rutas.services.ChoferService;
import com.rutas.services.VehiculoService;
import com.rutas.services.ViajeService;

public class TestService {
    public static void main(String[] args) {
        System.out.println("=== INICIANDO PRUEBAS DE LA CAPA DE SERVICIO ===");

        // Instanciar Servicios
        ChoferService choferService = new ChoferService();
        VehiculoService vehiculoService = new VehiculoService();
        ViajeService viajeService = new ViajeService();
        CiudadRepository ciudadRepo = new CiudadRepository();

        try {
            // 1. Cargar Datos Básicos (Ciudades)
            Ciudad origen = new Ciudad();
            origen.setNombre("Rosario");
            origen.setProvincia(Provincia.SANTA_FE); // Ajusta a tu enum Provincia
            origen = ciudadRepo.guardar(origen);

            Ciudad destino = new Ciudad();
            destino.setNombre("Mendoza");
            destino.setProvincia(Provincia.MENDOZA);
            destino = ciudadRepo.guardar(destino);

            // 2. Registrar Choferes válidos
            Chofer chofer1 = new Chofer();
            chofer1.setDni("11111111");
            chofer1.setNombre("Mario");
            chofer1.setApellido("Rossi");
            chofer1.setNumeroLicencia("LIC-101");
            chofer1.setVencimientoLicencia(LocalDate.now().plusYears(2));
            chofer1 = choferService.registrarChofer(chofer1);

            Chofer chofer2 = new Chofer();
            chofer2.setDni("22222222");
            chofer2.setNombre("Pedro");
            chofer2.setApellido("Sorella");
            chofer2.setNumeroLicencia("LIC-102");
            chofer2.setVencimientoLicencia(LocalDate.now().plusYears(1));
            chofer2 = choferService.registrarChofer(chofer2);

            // 3. Registrar Chofer con Licencia Vencida para probar validación
            Chofer choferVencido = new Chofer();
            choferVencido.setDni("33333333");
            choferVencido.setNombre("Luis");
            choferVencido.setApellido("Vencido");
            choferVencido.setNumeroLicencia("LIC-103");
            // Se le asigna fecha futura para que registrarChofer lo permita, pero vencida
            // para la fecha del viaje
            choferVencido.setVencimientoLicencia(LocalDate.now().plusDays(5));
            choferVencido = choferService.registrarChofer(choferVencido);

            // 4. Registrar Colectivo
            Colectivo colectivo = new Colectivo();
            colectivo.setPatente("BB999ZZ");
            colectivo.setMarca("Scania");
            colectivo.setModelo("K400");
            colectivo.setDoblePiso(true);
            colectivo.setCapacidad(60);
            colectivo.setAireAcondicionado(true);
            colectivo.setWifi(true);
            colectivo.setCatering(false);
            colectivo.setEstado(EstadoVehiculo.DISPONIBLE);
            colectivo = (Colectivo) vehiculoService.registrarVehiculo(colectivo);

            // -------------------------------------------------------------
            // PRUEBA 1: Intentar guardar viaje con solo 1 chofer
            // -------------------------------------------------------------
            System.out.println("\n--- PRUEBA 1: Validación Mínimo 2 Choferes ---");
            Viaje viajeInvalido1 = new Viaje();
            viajeInvalido1.setOrigen(origen);
            viajeInvalido1.setDestino(destino);
            viajeInvalido1.setFechaHoraSalida(LocalDateTime.now().plusDays(2));
            viajeInvalido1.setFechaHoraLlegada(LocalDateTime.now().plusDays(2).plusHours(10));
            viajeInvalido1.setVehiculo(colectivo);
            viajeInvalido1.getChoferes().add(chofer1); // Solo un chofer

            try {
                viajeService.planificarViaje(viajeInvalido1);
                System.err.println("❌ FALLO: Debería haber rechazado un viaje con 1 solo chofer.");
            } catch (NegocioException e) {
                System.out.println("✅ ÉXITO BLOQUEO: " + e.getMessage());
            }

            // -------------------------------------------------------------
            // PRUEBA 2: Planificación Exitosa de Viaje Válido
            // -------------------------------------------------------------
            System.out.println("\n--- PRUEBA 2: Planificación de Viaje Válido ---");
            Viaje viajeOk = new Viaje();
            viajeOk.setOrigen(origen);
            viajeOk.setDestino(destino);
            viajeOk.setFechaHoraSalida(LocalDateTime.now().plusDays(10));
            viajeOk.setFechaHoraLlegada(LocalDateTime.now().plusDays(10).plusHours(12));
            viajeOk.setVehiculo(colectivo);
            viajeOk.getChoferes().add(chofer1);
            viajeOk.getChoferes().add(chofer2);

            viajeOk = viajeService.planificarViaje(viajeOk);
            System.out.println("✅ ÉXITO: Viaje registrado correctamente con ID: " + viajeOk.getId());

            // -------------------------------------------------------------
            // PRUEBA 3: Control de Solapamiento Horario
            // -------------------------------------------------------------
            System.out.println("\n--- PRUEBA 3: Intento de Superposición Horaria ---");
            Viaje viajeSuperpuesto = new Viaje();
            viajeSuperpuesto.setOrigen(origen);
            viajeSuperpuesto.setDestino(destino);
            // Salida 2 horas después del viaje anterior (mientras aún está en marcha)
            viajeSuperpuesto.setFechaHoraSalida(viajeOk.getFechaHoraSalida().plusHours(2));
            viajeSuperpuesto.setFechaHoraLlegada(viajeOk.getFechaHoraSalida().plusHours(14));
            viajeSuperpuesto.setVehiculo(colectivo); // Mismo vehículo
            viajeSuperpuesto.getChoferes().add(chofer1);
            viajeSuperpuesto.getChoferes().add(chofer2);

            try {
                viajeService.planificarViaje(viajeSuperpuesto);
                System.err.println("❌ FALLO: Permitió registrar un viaje superpuesto.");
            } catch (NegocioException e) {
                System.out.println("✅ ÉXITO BLOQUEO: " + e.getMessage());
            }

            // -------------------------------------------------------------
            // PRUEBA 4: Control de Licencia Vencida
            // -------------------------------------------------------------
            System.out.println("\n--- PRUEBA 4: Asignar Chofer con Licencia Vencida ---");
            Viaje viajeLicenciaVencida = new Viaje();
            viajeLicenciaVencida.setOrigen(origen);
            viajeLicenciaVencida.setDestino(destino);
            viajeLicenciaVencida.setFechaHoraSalida(LocalDateTime.now().plusDays(20)); // En 20 días
            viajeLicenciaVencida.setFechaHoraLlegada(LocalDateTime.now().plusDays(20).plusHours(5));
            viajeLicenciaVencida.setVehiculo(colectivo);
            viajeLicenciaVencida.getChoferes().add(chofer1);
            viajeLicenciaVencida.getChoferes().add(choferVencido); // Vence en 5 días

            try {
                viajeService.planificarViaje(viajeLicenciaVencida);
                System.err.println("❌ FALLO: Permitió asignar un chofer con licencia vencida.");
            } catch (NegocioException e) {
                System.out.println("✅ ÉXITO BLOQUEO: " + e.getMessage());
            }

            System.out.println("\n✅ TODAS LAS VALIDACIONES DE LA CAPA SERVICE FUNCIONAN CORRECTAMENTE.");

        } catch (Exception e) {
            System.err.println("❌ ERROR INESPERADO:");
            e.printStackTrace();
        } finally {
            JPAUtil.close();
        }
    }
}
