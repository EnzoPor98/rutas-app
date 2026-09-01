package com.rutas.services;

import java.util.List;

import com.rutas.exception.NegocioException;
import com.rutas.models.Colectivo;
import com.rutas.models.Vehiculo;
import com.rutas.repository.VehiculoRepository;

public class VehiculoService {
    private final VehiculoRepository vehiculoRepository;

    public VehiculoService() {
        this.vehiculoRepository = new VehiculoRepository();
    }

    public Vehiculo registrarVehiculo(Vehiculo vehiculo) throws NegocioException {
        if (vehiculo.getPatente() == null || vehiculo.getPatente().isBlank()) {
            throw new NegocioException("La patente del vehículo es obligatoria.");
        }
        if (vehiculo.getCapacidad() == null || vehiculo.getCapacidad() <= 0) {
            throw new NegocioException("La capacidad debe ser un número mayor a cero.");
        }
        return vehiculoRepository.guardar(vehiculo);
    }

    public List<Vehiculo> listarTodos() {
        return vehiculoRepository.listarTodos();
    }

    public List<Vehiculo> listarDisponibles() {
        return vehiculoRepository.listarDisponibles();
    }

    public List<Colectivo> listarColectivos() {
        return vehiculoRepository.listarColectivos();
    }
}
