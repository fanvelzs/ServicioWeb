package com.example.servicioweb.model;

public class Usuario {
    private int id;
    private String nombre;
    private String username;
    private String email;
    private String telefono;
    private String ciudad;
    private String empresa;

    public Usuario(int id, String nombre, String username, String email, String telefono, String ciudad, String empresa) {
        this.id = id;
        this.nombre = nombre;
        this.username = username;
        this.email = email;
        this.telefono = telefono;
        this.ciudad = ciudad;
        this.empresa = empresa;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getEmpresa() {
        return empresa;
    }
}
