package com.inflesusventas.model;

public class Cliente {
    private int id;
    private String ruc;
    private String razonSocial;
    private String direccion;
    private String telefono;
    private String email;
    private String nombreContacto;

    public Cliente() {}

    public Cliente(int id, String ruc, String razonSocial, String direccion, String telefono, String email, String nombreContacto) {
        this.id = id;
        setRuc(ruc); // Usamos el setter para validar al construir
        this.razonSocial = razonSocial;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.nombreContacto = nombreContacto;
    }

    public String getRuc() { return ruc; }

    // AQUÍ ESTÁ LA VALIDACIÓN PARA EL TEST CN02
    public void setRuc(String ruc) { 
        if (ruc != null && ruc.length() != 11) {
            throw new IllegalArgumentException("El RUC debe tener 11 dígitos");
        }
        this.ruc = ruc; 
    }

    // ... Resto de getters y setters iguales ...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNombreContacto() { return nombreContacto; }
    public void setNombreContacto(String nombreContacto) { this.nombreContacto = nombreContacto; }
    @Override public String toString() { return razonSocial; }
}