package com.inflesusventas.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Modelo básico para representar un comprobante electrónico.
 */
public class ComprobanteElectronico {

    private String id;
    private String tipoComprobante; // e.g. "Factura", "Boleta"
    private LocalDateTime fechaEmision;
    private String rucCliente;
    private String razonSocialCliente;
    private String moneda;
    private List<ProductoCotizacion> items;
    private double subtotal;
    private double igv;
    private double total;
    private String condicionPago; // CONTADO / CREDITO / 50-50

    // Campos para Nota de Crédito
    private String rucEmisor;
    private String serie;
    private int numero;

    public ComprobanteElectronico() {
    }

    // getters / setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getRucCliente() {
        return rucCliente;
    }

    public void setRucCliente(String rucCliente) {
        this.rucCliente = rucCliente;
    }

    public String getRazonSocialCliente() {
        return razonSocialCliente;
    }

    public void setRazonSocialCliente(String razonSocialCliente) {
        this.razonSocialCliente = razonSocialCliente;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public List<ProductoCotizacion> getItems() {
        return items;
    }

    public void setItems(List<ProductoCotizacion> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("La lista de ítems no puede estar vacía");
        }
        this.items = items;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getIgv() {
        return igv;
    }

    public void setIgv(double igv) {
        this.igv = igv;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getCondicionPago() {
        return condicionPago;
    }

    public void setCondicionPago(String condicionPago) {
        this.condicionPago = condicionPago;
    }

    public String getRucEmisor() {
        return rucEmisor;
    }

    public void setRucEmisor(String rucEmisor) {
        this.rucEmisor = rucEmisor;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    @Override
    public String toString() {
        return "ComprobanteElectronico{" +
                "id='" + id + '\'' +
                ", tipoComprobante='" + tipoComprobante + '\'' +
                ", fechaEmision=" + fechaEmision +
                ", rucCliente='" + rucCliente + '\'' +
                ", total=" + total +
                '}';
    }
}
