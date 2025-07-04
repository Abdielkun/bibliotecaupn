package com.upnbiblioteca.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/configuracion")
public class ConfiguracionController {

    @GetMapping
    public String configuracion(Model model) {
        // Aquí puedes agregar configuraciones del sistema
        model.addAttribute("version", "1.0.0");
        model.addAttribute("ubicacion", "Chorrillos, Lima");
        return "configuracion";
    }
}
