package com.upnbiblioteca.service;

import com.upnbiblioteca.model.Usuario;
import com.upnbiblioteca.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario findById(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.orElse(null);
    }

    public Usuario findByUsername(String username) {
        System.out.println("Buscando usuario con nombre (ignorando mayúsculas): " + username);
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username);
        System.out.println("Usuario encontrado: " + usuario);
        return usuario;
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }
    public Usuario obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }
}
