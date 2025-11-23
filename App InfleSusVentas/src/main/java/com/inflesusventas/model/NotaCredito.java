package com.inflesusventas.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotaCredito {
    private String serie;
    private int numero;
    private LocalDate fechaEmision;

    // Datos del cliente
    private String rucCliente;
    private String razonSocialCliente;

    // Referencia
    private String numeroFacturaRef;
    private String motivoSustento;
    private String moneda;

    // Montos
    private double subtotal;
    private double igv;
    private double total;

    // Campos de control y SUNAT
    private String tipoNotaCredito; // "Anulación de la operación", etc.
    private String rutaXml;
    private String rutaPdf;
    private String estadoSunat; // ACEPTADO, RECHAZADO
    private String cdr; // Constancia de Recepción

    private List<ItemNotaCredito> items = new ArrayList<>();

    // Manual Getters and Setters to avoid Lombok issues
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

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
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

    public String getNumeroFacturaRef() {
        return numeroFacturaRef;
    }

    public void setNumeroFacturaRef(String numeroFacturaRef) {
        this.numeroFacturaRef = numeroFacturaRef;
    }

    public String getMotivoSustento() {
        return motivoSustento;
    }

    public void setMotivoSustento(String motivoSustento) {
        if (motivoSustento == null || motivoSustento.trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo de sustento es obligatorio");
        }
        this.motivoSustento = motivoSustento;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
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

    public String getTipoNotaCredito() {
        return tipoNotaCredito;
    }

    public void setTipoNotaCredito(String tipoNotaCredito) {
        this.tipoNotaCredito = tipoNotaCredito;
    }

    public String getRutaXml() {
        return rutaXml;
    }

    public void setRutaXml(String rutaXml) {
        this.rutaXml = rutaXml;
    }

    public String getRutaPdf() {
        return rutaPdf;
    }

    public void setRutaPdf(String rutaPdf) {
        this.rutaPdf = rutaPdf;
    }

    public String getEstadoSunat() {
        return estadoSunat;
    }

    public void setEstadoSunat(String estadoSunat) {
        this.estadoSunat = estadoSunat;
    }

    public String getCdr() {
        return cdr;
    }

    public void setCdr(String cdr) {
        this.cdr = cdr;
    }

    public List<ItemNotaCredito> getItems() {
        return items;
    }

    public void setItems(List<ItemNotaCredito> items) {
        this.items = items;
    }
}
