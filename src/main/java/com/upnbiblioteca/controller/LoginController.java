package com.upnbiblioteca.controller;

import com.upnbiblioteca.model.Usuario;
import com.upnbiblioteca.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login(Model model) {
        return "login"; // Muestra la página de login
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("usuario", new Usuario()); // Objeto para el formulario
        return "register"; // Muestra el formulario de registro
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("usuario") Usuario usuario,
                           RedirectAttributes redirectAttributes) {

        if (usuarioService.existsByUsername(usuario.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "El usuario ya existe.");
            return "redirect:/register";
        }

        // Validar el rol desde el formulario, asignar ROLE_USER si el valor es inválido
        String rol = usuario.getRol();
        if (!"ROLE_ADMIN".equals(rol) && !"ROLE_USER".equals(rol)) {
            rol = "ROLE_USER";
        }
        usuario.setRol(rol);

        // Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Guardar el usuario en la base de datos
        usuarioService.save(usuario);

        redirectAttributes.addFlashAttribute("success", "Registro exitoso. Inicia sesión.");
        return "redirect:/login";
    }
}
