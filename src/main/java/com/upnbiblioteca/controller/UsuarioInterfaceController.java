package com.upnbiblioteca.controller;

import com.upnbiblioteca.model.Libro;
import com.upnbiblioteca.model.Prestamo;
import com.upnbiblioteca.model.Usuario;
import com.upnbiblioteca.service.LibroService;
import com.upnbiblioteca.service.PrestamoService;
import com.upnbiblioteca.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/usuario")
public class UsuarioInterfaceController {

    @Autowired
    private LibroService libroService;

    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Usuario usuario = getCurrentUser ();
        if (usuario == null) {
            return "redirect:/login";
        }

        // Obtener préstamos del usuario actual
        List<Prestamo> misPrestamos = prestamoService.obtenerTodosLosPrestamos()
                .stream()
                .filter(p -> p.getUsuario().getId().equals(usuario.getId()))
                .collect(Collectors.toList());

        // Estadísticas del usuario
        model.addAttribute("usuario", usuario);
        model.addAttribute("misPrestamos", misPrestamos);
        model.addAttribute("prestamosActivos", misPrestamos.size());
        model.addAttribute("librosDisponibles", libroService.obtenerTodosLosLibros().stream().filter(Libro::isDisponible).count());
        model.addAttribute("totalLibros", libroService.obtenerTodosLosLibros().size());

        // Libros recomendados (últimos agregados)
        List<Libro> librosRecomendados = libroService.obtenerTodosLosLibros()
                .stream()
                .filter(Libro::isDisponible)
                .limit(6)
                .collect(Collectors.toList());
        model.addAttribute("librosRecomendados", librosRecomendados);

        return "usuario/dashboard";
    }

    @GetMapping("/catalogo")
    public String catalogo(@RequestParam(value = "buscar", required = false) String buscar, Model model) {
        Usuario usuario = getCurrentUser ();
        if (usuario == null) {
            return "redirect:/login";
        }

        List<Libro> libros = libroService.obtenerTodosLosLibros();

        // Filtrar libros si hay búsqueda
        if (buscar != null && !buscar.trim().isEmpty()) {
            String buscarLower = buscar.toLowerCase().trim();
            libros = libros.stream()
                    .filter(libro ->
                            libro.getTitulo().toLowerCase().contains(buscarLower) ||
                                    libro.getAutor().toLowerCase().contains(buscarLower) ||
                                    libro.getIsbn().toLowerCase().contains(buscarLower)
                    )
                    .collect(Collectors.toList());
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("libros", libros);
        model.addAttribute("buscar", buscar);
        model.addAttribute("totalLibros", libros.size());
        model.addAttribute("librosDisponibles", libros.stream().filter(Libro::isDisponible).count());

        return "usuario/catalogo";
    }

    @GetMapping("/mis-prestamos")
    public String misPrestamos(Model model) {
        Usuario usuario = getCurrentUser ();
        if (usuario == null) {
            return "redirect:/login";
        }

        List<Prestamo> misPrestamos = prestamoService.obtenerTodosLosPrestamos()
                .stream()
                .filter(p -> p.getUsuario().getId().equals(usuario.getId()))
                .collect(Collectors.toList());

        model.addAttribute("usuario", usuario);
        model.addAttribute("prestamos", misPrestamos);
        model.addAttribute("prestamosActivos", misPrestamos.size());

        return "usuario/mis-prestamos"; // Asegúrate de que esta vista esté configurada correctamente
    }

    @PostMapping("/renovar/{id}")
    public String renovarPrestamo(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            Prestamo prestamo = prestamoService.obtenerPrestamoPorId(id);
            if (prestamo == null) {
                redirectAttributes.addFlashAttribute("error", "Préstamo no encontrado");
                return "redirect:/usuario/mis-prestamos"; // Redirigir a la lista de préstamos
            }
            // Renovar préstamo
            prestamo.setFechaDevolucion(prestamo.getFechaDevolucion().plusDays(15)); // Renovar por 15 días
            prestamoService.guardarPrestamo(prestamo);
            redirectAttributes.addFlashAttribute("success", "Préstamo renovado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al renovar el préstamo.");
        }
        return "redirect:/usuario/mis-prestamos"; // Redirigir a la lista de préstamos
    }

    @PostMapping("/solicitar-prestamo/{libroId}")
    public String solicitarPrestamo(@PathVariable Long libroId, RedirectAttributes redirectAttributes) {
        Usuario usuario = getCurrentUser ();
        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            Libro libro = libroService.obtenerLibroPorId(libroId);
            if (libro == null) {
                redirectAttributes.addFlashAttribute("error", "El libro no existe.");
                return "redirect:/usuario/catalogo";
            }

            if (!libro.isDisponible()) {
                redirectAttributes.addFlashAttribute("error", "El libro no está disponible.");
                return "redirect:/usuario/catalogo";
            }

            // Verificar si el usuario ya tiene el máximo de préstamos
            long prestamosActivos = prestamoService.obtenerTodosLosPrestamos()
                    .stream()
                    .filter(p -> p.getUsuario().getId().equals(usuario.getId()))
                    .count();

            if (prestamosActivos >= 3) {
                redirectAttributes.addFlashAttribute("error", "Has alcanzado el límite máximo de 3 libros prestados.");
                return "redirect:/usuario/catalogo";
            }

            // Crear nuevo préstamo
            Prestamo prestamo = new Prestamo();
            prestamo.setLibro(libro);
            prestamo.setUsuario(usuario);
            prestamo.setFechaPrestamo(LocalDate.now());
            prestamo.setFechaDevolucion(LocalDate.now().plusDays(15)); // 15 días por defecto

            prestamoService.guardarPrestamo(prestamo);

            // Marcar libro como no disponible
            libro.setDisponible(false);
            libroService.guardarLibro(libro);

            redirectAttributes.addFlashAttribute("success", "Préstamo solicitado exitosamente. Tienes 15 días para devolver el libro.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar la solicitud de préstamo.");
        }

        return "redirect:/usuario/catalogo";
    }

    @GetMapping("/perfil")
    public String perfil(Model model) {
        Usuario usuario = getCurrentUser ();
        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        return "usuario/perfil"; // Asegúrate de que esta vista esté configurada correctamente
    }

    @PostMapping("/actualizar-perfil")
    public String actualizarPerfil(@ModelAttribute Usuario usuarioForm, RedirectAttributes redirectAttributes) {
        Usuario usuario = getCurrentUser ();
        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            usuario.setNombre(usuarioForm.getNombre());
            usuario.setEmail(usuarioForm.getEmail());
            usuarioService.save(usuario);
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil.");
        }

        return "redirect:/usuario/perfil";
    }

    private Usuario getCurrentUser () {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser ")) {
            return usuarioService.findByUsername(auth.getName());
        }
        return null;
    }
}
