package com.upnbiblioteca.service;

import com.upnbiblioteca.model.Libro;
import com.upnbiblioteca.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;

    public Libro obtenerLibroPorId(Long id) {
        return libroRepository.findById(id).orElse(null);
    }

    public List<Libro> obtenerTodosLosLibros() {
        return libroRepository.findAll();
    }

    public List<Libro> buscarPorTituloOAutor(String keyword) {
        return libroRepository.findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCase(keyword, keyword);
    }

    public void guardarLibro(Libro libro) {
        libroRepository.save(libro);
    }

    public void eliminarLibroPorId(Long id) {
        libroRepository.deleteById(id);
    }
}
