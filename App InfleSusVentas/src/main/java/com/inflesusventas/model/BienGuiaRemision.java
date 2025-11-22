package com.inflesusventas.model;

/**
 * Modelo para representar un bien/ítem en la Guía de Remisión
 * Ruta: src/main/java/com/inflesusventas/model/BienGuiaRemision.java
 */
public class BienGuiaRemision {

    private String codigoBien;
    private String descripcionDetallada;
    private String unidadMedida; // UNIDAD (NIU), KILOGRAMO, etc.
    private int cantidad;
    private String unidadMedidaPeso; // KILOGRAMO (KGM)
    private double pesoBrutoTotal;

    // Constructor vacío
    public BienGuiaRemision() {
        this.unidadMedida = "NIU"; // UNIDAD por defecto (código SUNAT)
        this.unidadMedidaPeso = "KGM"; // KILOGRAMO por defecto
    }

    // Constructor completo
    public BienGuiaRemision(String codigoBien, String descripcionDetallada,
                            int cantidad, double pesoBrutoTotal) {
        this();
        this.codigoBien = codigoBien;
        this.descripcionDetallada = descripcionDetallada;
        this.cantidad = cantidad;
        this.pesoBrutoTotal = pesoBrutoTotal;
    }

    // Getters y Setters
    public String getCodigoBien() {
        return codigoBien;
    }

    public void setCodigoBien(String codigoBien) {
        this.codigoBien = codigoBien;
    }

    public String getDescripcionDetallada() {
        return descripcionDetallada;
    }

    public void setDescripcionDetallada(String descripcionDetallada) {
        this.descripcionDetallada = descripcionDetallada;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getUnidadMedidaPeso() {
        return unidadMedidaPeso;
    }

    public void setUnidadMedidaPeso(String unidadMedidaPeso) {
        this.unidadMedidaPeso = unidadMedidaPeso;
    }

    public double getPesoBrutoTotal() {
        return pesoBrutoTotal;
    }

    public void setPesoBrutoTotal(double pesoBrutoTotal) {
        this.pesoBrutoTotal = pesoBrutoTotal;
    }

    @Override
    public String toString() {
        return cantidad + "x " + descripcionDetallada + " (" + pesoBrutoTotal + " KG)";
    }
}