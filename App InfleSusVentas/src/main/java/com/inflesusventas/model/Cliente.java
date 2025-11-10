package com.inflesusventas.model;

public class Cliente {
    // 1. Atributos (Donde se guardan los datos de CADA cliente)
    private String ruc;
    private String razonSocial;
    private String direccion;
    private String telefono;
    private String email;
    private String nombreContacto;

    // 2. Constructor Vacío (Necesario para algunas librerías)
    public Cliente() {
    }

    // 3. Constructor Completo (Para crear un cliente con todos sus datos de una sola vez)
    public Cliente(String ruc, String razonSocial, String direccion, String telefono, String email, String nombreContacto) {
        this.ruc = ruc;
        this.razonSocial = razonSocial;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.nombreContacto = nombreContacto;
    }

    // 4. Getters y Setters (Para leer y escribir los datos)
    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombreContacto() {
        return nombreContacto;
    }

    public void setNombreContacto(String nombreContacto) {
        this.nombreContacto = nombreContacto;
    }

    // Opcional: Para imprimir el cliente fácilmente en consola
    @Override
    public String toString() {
        return razonSocial + " (RUC: " + ruc + ")";
    }
}