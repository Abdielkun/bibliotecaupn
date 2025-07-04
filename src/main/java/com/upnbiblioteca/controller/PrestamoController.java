package com.upnbiblioteca.controller;

import com.upnbiblioteca.model.Libro;
import com.upnbiblioteca.model.Prestamo;
import com.upnbiblioteca.model.Usuario;
import com.upnbiblioteca.service.LibroService;
import com.upnbiblioteca.service.PrestamoService;
import com.upnbiblioteca.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/prestamos")
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private LibroService libroService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listarPrestamos(Model model) {
        List<Prestamo> prestamos = prestamoService.obtenerTodosLosPrestamos();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Prestamo prestamo : prestamos) {
            if (prestamo.getFechaDevolucion() != null)
                prestamo.setFechaDevolucionStr(prestamo.getFechaDevolucion().format(formatter));
            if (prestamo.getFechaPrestamo() != null)
                prestamo.setFechaPrestamoStr(prestamo.getFechaPrestamo().format(formatter));
        }
        model.addAttribute("prestamos", prestamos);
        model.addAttribute("libros", libroService.obtenerTodosLosLibros());
        
        // Filtrar usuarios para excluir administradores
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios().stream()
                .filter(usuario -> !usuario.getRol().equals("ROLE_ADMIN")) // Cambiado a "ROLE_ADMIN"
                .collect(Collectors.toList());
        model.addAttribute("usuarios", usuarios);
        return "misPrestamos"; // Cambiado para apuntar a la vista correcta
    }


    @PostMapping("/guardar")
    public String guardarPrestamo(
            @RequestParam("libroId") Long libroId,
            @RequestParam("usuarioId") Long usuarioId,
            @RequestParam("fechaPrestamo") String fechaPrestamoStr,
            @RequestParam("fechaDevolucion") String fechaDevolucionStr,
            RedirectAttributes redirectAttributes
    ) {
        try {
            LocalDate fechaPrestamo = LocalDate.parse(fechaPrestamoStr);
            LocalDate fechaDevolucion = LocalDate.parse(fechaDevolucionStr);

            Libro libro = libroService.obtenerLibroPorId(libroId);
            Usuario usuario = usuarioService.obtenerUsuarioPorId(usuarioId);

            if (libro == null || usuario == null) {
                redirectAttributes.addFlashAttribute("error", "El libro o usuario no existen");
                return "redirect:/prestamos";
            }

            Prestamo prestamo = new Prestamo();
            prestamo.setLibro(libro);
            prestamo.setUsuario(usuario);
            prestamo.setFechaPrestamo(fechaPrestamo);
            prestamo.setFechaDevolucion(fechaDevolucion);
            prestamoService.guardarPrestamo(prestamo);

            redirectAttributes.addFlashAttribute("success", "Préstamo agregado correctamente.");
            return "redirect:/prestamos";
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Error de integridad de datos: " + e.getMessage());
            return "redirect:/prestamos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error en formato de fecha o datos invalidos");
            return "redirect:/prestamos";
        }
    }

    @GetMapping("/editar/{id}")
    public String editarPrestamo(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Prestamo prestamo = prestamoService.obtenerPrestamoPorId(id);
        if (prestamo == null) {
            redirectAttributes.addFlashAttribute("error", "Préstamo no encontrado");
            return "redirect:/prestamos";
        }
        model.addAttribute("prestamo", prestamo);
        model.addAttribute("libros", libroService.obtenerTodosLosLibros());

        // Filtrar usuarios para excluir administradores
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios().stream()
                .filter(usuario -> !usuario.getRol().equals("ADMIN")) // Cambia "ADMIN" por el rol que uses para los administradores
                .collect(Collectors.toList());
        model.addAttribute("usuarios", usuarios);

        return "editarPrestamo"; // Asegúrate de que esta vista esté configurada correctamente
    }

    @PostMapping("/actualizar")
    public String actualizarPrestamo(
            @ModelAttribute Prestamo prestamoForm,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Prestamo prestamoExistente = prestamoService.obtenerPrestamoPorId(prestamoForm.getId());
            if (prestamoExistente == null) {
                redirectAttributes.addFlashAttribute("error", "Préstamo no encontrado");
                return "redirect:/prestamos";
            }

            prestamoExistente.setFechaPrestamo(prestamoForm.getFechaPrestamo());
            prestamoExistente.setFechaDevolucion(prestamoForm.getFechaDevolucion());

            prestamoService.guardarPrestamo(prestamoExistente);
            redirectAttributes.addFlashAttribute("success", "Préstamo actualizado correctamente");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error", "Error de integridad de datos: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el préstamo");
        }
        return "redirect:/prestamos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPrestamo(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        prestamoService.eliminarPrestamoPorId(id);
        redirectAttributes.addFlashAttribute("success", "Préstamo eliminado correctamente");
        return "redirect:/prestamos";
    }
}
