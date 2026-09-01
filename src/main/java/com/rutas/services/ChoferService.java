package com.rutas.services;

import java.time.LocalDate;
import java.util.List;

import com.rutas.exception.NegocioException;
import com.rutas.models.Chofer;
import com.rutas.repository.ChoferRepository;

public class ChoferService {
    private final ChoferRepository choferRepository;

    public ChoferService() {
        this.choferRepository = new ChoferRepository();
    }

    public Chofer registrarChofer(Chofer chofer) throws NegocioException {
        if (chofer.getDni() == null || chofer.getDni().isBlank()) {
            throw new NegocioException("El DNI del chofer es obligatorio.");
        }
        if (chofer.getVencimientoLicencia() == null || chofer.getVencimientoLicencia().isBefore(LocalDate.now())) {
            throw new NegocioException("La licencia del chofer no puede registrarse estando vencida.");
        }
        chofer.setActivo(true);
        return choferRepository.guardar(chofer);
    }

    public List<Chofer> listarTodos() {
        return choferRepository.listarTodos();
    }

    public List<Chofer> listarActivos() {
        return choferRepository.listarActivos();
    }
}
