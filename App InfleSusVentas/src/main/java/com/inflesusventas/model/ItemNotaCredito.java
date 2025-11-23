package com.inflesusventas.model;

/**
 * Ítem de Nota de Crédito (POJO simple)
 */
public class ItemNotaCredito {

    private String id;
    private String unidadMedida;
    private Integer cantidad;
    private String descripcionOriginal;
    private String descripcionCorregida;
    private Double valorUnitario;
    private Double subtotal;

    public ItemNotaCredito() {
        this.id = java.util.UUID.randomUUID().toString();
    }

    public ItemNotaCredito(String unidadMedida, Integer cantidad,
            String descripcionOriginal, Double valorUnitario) {
        this();
        this.unidadMedida = unidadMedida;
        this.cantidad = cantidad;
        this.descripcionOriginal = descripcionOriginal;
        this.valorUnitario = valorUnitario;
        this.subtotal = cantidad * valorUnitario;
    }

    /**
     * Formatea la descripción para el PDF en formato: DICE / DEBE DECIR
     */
    public String formatearDescripcionParaPDF() {
        if (descripcionCorregida != null && !descripcionCorregida.trim().isEmpty()) {
            return String.format("DICE: %s DEBE DECIR: %s",
                    descripcionOriginal, descripcionCorregida);
        }
        return descripcionOriginal;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescripcionOriginal() {
        return descripcionOriginal;
    }

    public void setDescripcionOriginal(String descripcionOriginal) {
        this.descripcionOriginal = descripcionOriginal;
    }

    public String getDescripcionCorregida() {
        return descripcionCorregida;
    }

    public void setDescripcionCorregida(String descripcionCorregida) {
        this.descripcionCorregida = descripcionCorregida;
    }

    public Double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(Double valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}
