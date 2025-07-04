package com.upnbiblioteca.controller;

import com.upnbiblioteca.model.Usuario;
import com.upnbiblioteca.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuariosController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("usuarioForm", new Usuario());
        return "usuarios";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute("usuarioForm") Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            if (usuario.getId() == null) {
                // Nuevo usuario
                if (usuarioService.existsByUsername(usuario.getUsername())) {
                    redirectAttributes.addFlashAttribute("error", "El nombre de usuario ya existe.");
                    return "redirect:/usuarios";
                }

                // Validar y asignar rol
                String rol = usuario.getRol();
                if (!"ROLE_ADMIN".equals(rol) && !"ROLE_USER".equals(rol)) {
                    rol = "ROLE_USER"; // Valor por defecto
                }
                usuario.setRol(rol);

                // Encriptar contraseña
                usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
                usuarioService.save(usuario);
                redirectAttributes.addFlashAttribute("success", "Usuario creado correctamente.");
            } else {
                // Actualizar usuario existente
                Usuario existente = usuarioService.findById(usuario.getId());
                if (existente == null) {
                    redirectAttributes.addFlashAttribute("error", "El usuario que intentas editar no existe.");
                    return "redirect:/usuarios";
                }

                existente.setNombre(usuario.getNombre());
                existente.setEmail(usuario.getEmail());

                // Actualizar rol si es válido
                String rol = usuario.getRol();
                if ("ROLE_ADMIN".equals(rol) || "ROLE_USER".equals(rol)) {
                    existente.setRol(rol);
                }

                // Actualizar contraseña solo si se ingresó
                if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                    existente.setPassword(passwordEncoder.encode(usuario.getPassword()));
                }

                usuarioService.save(existente);
                redirectAttributes.addFlashAttribute("success", "Usuario actualizado correctamente.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el usuario.");
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioService.findById(id);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
            return "redirect:/usuarios";
        }
        model.addAttribute("usuarioForm", usuario);
        model.addAttribute("usuarios", usuarioService.obtenerTodosLosUsuarios());
        return "usuarios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Usuario eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar el usuario. Puede tener préstamos asociados.");
        }
        return "redirect:/usuarios";
    }
}
