package com.inflesusventas.model;

public class ProductoCotizacion {
    private String codigo;
    private String descripcion;
    private int cantidad;
    private double precioBase; // Precio unitario sin impuestos
    private String unidadMedida; 

    public ProductoCotizacion() {
    }

    public ProductoCotizacion(String codigo, String descripcion, int cantidad, String unidadMedida, double precioBase) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.unidadMedida = unidadMedida; // <-- Guardarlo
        this.precioBase = precioBase;
    }

    public double getSubtotal() {
        return cantidad * precioBase;
    }

    // Getters y Setters necesarios para el PDF
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public double getPrecioBase() { return precioBase; }
    public void setPrecioBase(double precioBase) { this.precioBase = precioBase; }
    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
}