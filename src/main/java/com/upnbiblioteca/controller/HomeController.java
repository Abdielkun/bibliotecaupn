package com.upnbiblioteca.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; 
import org.springframework.web.bind.annotation.GetMapping;

import com.upnbiblioteca.model.Prestamo;
import com.upnbiblioteca.model.Usuario;
import com.upnbiblioteca.model.Libro;
import com.upnbiblioteca.service.PrestamoService;
import com.upnbiblioteca.service.LibroService;
import com.upnbiblioteca.service.UsuarioService;

@Controller
public class HomeController {

    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private LibroService libroService;
    
    @Autowired
    private UsuarioService usuarioService;

    // Página de inicio del sistema (después de login) - REDIRIGE SEGÚN ROL
    @GetMapping("/")
    public String index(Model model) {
        // Obtener usuario actual
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            Usuario usuario = usuarioService.findByUsername(auth.getName());
            
            if (usuario != null) {
                // Si es ADMIN, mostrar panel administrativo
                if ("ROLE_ADMIN".equals(usuario.getRol())) {
                    return adminDashboard(model);
                } 
                // Si es USER, redirigir a dashboard de usuario
                else {
                    return "redirect:/usuario/dashboard";
                }
            }
        }
        
        // Si no está autenticado, redirigir al login
        return "redirect:/login";
    }
    
    // Panel administrativo (solo para admins)
    private String adminDashboard(Model model) {
        List<Libro> libros = libroService.obtenerTodosLosLibros();
        List<Prestamo> prestamos = prestamoService.obtenerTodosLosPrestamos();
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        
        // Estadísticas para el admin
        model.addAttribute("libros", libros);
        model.addAttribute("prestamos", prestamos);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalLibros", libros.size());
        model.addAttribute("prestamosActivos", prestamos.size());
        model.addAttribute("usuariosActivos", usuarios.size());
        model.addAttribute("librosDisponibles", libros.stream().filter(l -> l.isDisponible()).count());
        model.addAttribute("librosPrestados", libros.stream().filter(l -> !l.isDisponible()).count());
        
        return "index"; // Página de admin
    }

    @GetMapping("/buscar-libros")
    public String buscarLibros() {
        return "buscar-libros";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }

    @GetMapping("/mis-prestamos")
    public String misPrestamos(Model model) {
        List<Prestamo> prestamos = prestamoService.obtenerTodosLosPrestamos();
        model.addAttribute("prestamos", prestamos);
        return "misPrestamos";
    }
    
    // Ruta alternativa para dashboard (redirección inteligente)
    @GetMapping("/dashboard")
    public String redirectToDashboard() {
        return index(null); // Reutiliza la lógica del método index
    }
}
