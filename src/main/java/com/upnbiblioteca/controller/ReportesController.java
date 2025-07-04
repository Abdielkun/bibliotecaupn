package com.upnbiblioteca.controller;

import com.upnbiblioteca.service.LibroService;
import com.upnbiblioteca.service.PrestamoService;
import com.upnbiblioteca.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reportes")
public class ReportesController {

    @Autowired
    private LibroService libroService;

    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String reportes(Model model) {
        // Estadísticas para los reportes
        model.addAttribute("totalLibros", libroService.obtenerTodosLosLibros().size());
        model.addAttribute("totalPrestamos", prestamoService.obtenerTodosLosPrestamos().size());
        model.addAttribute("totalUsuarios", usuarioService.obtenerTodosLosUsuarios().size());
        
        // Libros más prestados (simulado)
        model.addAttribute("libros", libroService.obtenerTodosLosLibros());
        model.addAttribute("prestamos", prestamoService.obtenerTodosLosPrestamos());
        
        return "reportes";
    }
}
