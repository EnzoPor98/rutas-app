package com.rutas.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.rutas.exception.NegocioException;
import com.rutas.models.Chofer;
import com.rutas.models.Viaje;
import com.rutas.models.Enums.EstadoVehiculo;
import com.rutas.repository.ViajeRepository;

public class ViajeService {
    private final ViajeRepository viajeRepository;

    public ViajeService() {
        this.viajeRepository = new ViajeRepository();
    }

    public Viaje planificarViaje(Viaje viaje) throws NegocioException {
        validarReglasDeNegocio(viaje);
        return viajeRepository.guardar(viaje);
    }

    public Viaje actualizarViaje(Viaje viaje) throws NegocioException {
        if (viaje.getId() == null) {
            throw new NegocioException("El viaje a actualizar debe tener un ID válido.");
        }
        validarReglasDeNegocio(viaje);
        return viajeRepository.actualizar(viaje);
    }

    private void validarReglasDeNegocio(Viaje viaje) throws NegocioException {
        // 1. Validar fechas de salida y llegada
        if (viaje.getFechaHoraSalida() == null || viaje.getFechaHoraLlegada() == null) {
            throw new NegocioException("Las fechas de salida y llegada son obligatorias.");
        }
        if (!viaje.getFechaHoraLlegada().isAfter(viaje.getFechaHoraSalida())) {
            throw new NegocioException("La fecha/hora de llegada debe ser posterior a la fecha/hora de salida.");
        }
        if (viaje.getFechaHoraSalida().isBefore(LocalDateTime.now())) {
            throw new NegocioException("No se puede programar un viaje en el pasado.");
        }

        // 2. Validar que origen y destino sean diferentes
        if (viaje.getOrigen() == null || viaje.getDestino() == null) {
            throw new NegocioException("Debe especificar una ciudad de origen y una de destino.");
        }
        if (viaje.getOrigen().getId().equals(viaje.getDestino().getId())) {
            throw new NegocioException("El origen y el destino no pueden ser la misma ciudad.");
        }

        // 3. Validar Vehículo
        if (viaje.getVehiculo() == null) {
            throw new NegocioException("Debe asignar un vehículo al viaje.");
        }
        if (viaje.getVehiculo().getEstado() != EstadoVehiculo.DISPONIBLE) {
            throw new NegocioException("El vehículo seleccionado no se encuentra en estado DISPONIBLE.");
        }

        // 4. Validar Mínimo de Choferes (Mínimo 2)
        if (viaje.getChoferes() == null || viaje.getChoferes().size() < 2) {
            throw new NegocioException("Se requieren al menos 2 choferes asignados por viaje.");
        }

        // 5. Validar estado y licencias de los Choferes para la fecha del viaje
        LocalDate fechaViaje = viaje.getFechaHoraSalida().toLocalDate();
        for (Chofer chofer : viaje.getChoferes()) {
            if (!Boolean.TRUE.equals(chofer.getActivo())) {
                throw new NegocioException(
                        "El chofer " + chofer.getNombre() + " " + chofer.getApellido() + " está inactivo.");
            }
            if (chofer.getVencimientoLicencia().isBefore(fechaViaje)) {
                throw new NegocioException("La licencia del chofer " + chofer.getNombre() + " " + chofer.getApellido() +
                        " estará vencida para la fecha del viaje (" + chofer.getVencimientoLicencia() + ").");
            }
        }

        // 6. Validar Solapamiento de Horario del Vehículo
        boolean vehiculoOcupado = viajeRepository.existeSolapamientoVehiculo(
                viaje.getVehiculo().getId(),
                viaje.getFechaHoraSalida(),
                viaje.getFechaHoraLlegada(),
                viaje.getId());
        if (vehiculoOcupado) {
            throw new NegocioException("El vehículo ya tiene asignado un viaje en el rango de horario seleccionado.");
        }

        // 7. Validar Solapamiento de Horario de cada Chofer
        for (Chofer chofer : viaje.getChoferes()) {
            boolean choferOcupado = viajeRepository.existeSolapamientoChofer(
                    chofer.getId(),
                    viaje.getFechaHoraSalida(),
                    viaje.getFechaHoraLlegada(),
                    viaje.getId());
            if (choferOcupado) {
                throw new NegocioException("El chofer " + chofer.getNombre() + " " + chofer.getApellido() +
                        " ya tiene un viaje programado que se superpone en este rango de horario.");
            }
        }
    }

    // --- MÉTODOS DE CONSULTA / INFORMES ---

    public List<Viaje> obtenerViajesProgramados() {
        return viajeRepository.listarViajesProgramados();
    }

    public List<Viaje> obtenerInformePorVehiculo(Long vehiculoId) throws NegocioException {
        if (vehiculoId == null) {
            throw new NegocioException("Debe proporcionar un ID de vehículo válido.");
        }
        return viajeRepository.buscarPorVehiculo(vehiculoId);
    }

    public Long obtenerCantidadViajesPorChofer(Long choferId) throws NegocioException {
        if (choferId == null) {
            throw new NegocioException("Debe proporcionar un ID de chofer válido.");
        }
        return viajeRepository.contarViajesRealizadosPorChofer(choferId);
    }
}
