package com.upnbiblioteca.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "prestamo")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "libro_id", nullable = false)
    private Libro libro;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDate fechaPrestamo;

    @Column(name = "fecha_devolucion", nullable = false)
    private LocalDate fechaDevolucion;

    @Column(name = "renovado")
    private int renovado;

    @Transient
    private String fechaPrestamoStr;

    @Transient
    private String fechaDevolucionStr;

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public int getRenovado() {
        return renovado;
    }

    public void setRenovado(int renovado) {
        this.renovado = renovado;
    }

    public String getFechaPrestamoStr() {
        return fechaPrestamoStr;
    }

    public void setFechaPrestamoStr(String fechaPrestamoStr) {
        this.fechaPrestamoStr = fechaPrestamoStr;
    }

    public String getFechaDevolucionStr() {
        return fechaDevolucionStr;
    }

    public void setFechaDevolucionStr(String fechaDevolucionStr) {
        this.fechaDevolucionStr = fechaDevolucionStr;
    }
}
