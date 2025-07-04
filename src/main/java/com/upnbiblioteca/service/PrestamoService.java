package com.upnbiblioteca.service;

import com.upnbiblioteca.model.Prestamo;
import com.upnbiblioteca.repository.PrestamoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrestamoService {

    @Autowired
    private PrestamoRepository prestamoRepository;

    public List<Prestamo> obtenerTodosLosPrestamos() {
        return prestamoRepository.findAll();
    }

    public List<Prestamo> obtenerPrestamosPorUsuario(String username) {
        return prestamoRepository.findByUsuarioUsername(username);
    }

    public Prestamo obtenerPrestamoPorId(Long id) {
        return prestamoRepository.findById(id).orElse(null);
    }

    public void guardarPrestamo(Prestamo prestamo) {
        prestamoRepository.save(prestamo);
    }

    public void eliminarPrestamoPorId(Long id) {
        prestamoRepository.deleteById(id);
    }

    public void solicitarRenovacion(Long id) {
        // Lógica para renovar el préstamo
        Prestamo prestamo = obtenerPrestamoPorId(id);
        if (prestamo != null) {
            // Actualiza la fecha de devolución o cualquier otra lógica necesaria
            prestamo.setFechaDevolucion(prestamo.getFechaDevolucion().plusDays(7)); // Ejemplo: añadir 7 días
            guardarPrestamo(prestamo);
        }
    }
}

