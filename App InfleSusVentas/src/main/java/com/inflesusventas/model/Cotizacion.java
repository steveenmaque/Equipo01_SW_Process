package com.inflesusventas.model;

import java.time.LocalDate;
import java.util.List;

public class Cotizacion {
    private int numeroCotizacion;
    private LocalDate fecha;
    private Cliente cliente;
    private List<ProductoCotizacion> productos;
    private CondicionPago condicionPago;
    private int diasVigencia = 15; // Valor por defecto razonable
    private boolean mostrarConIGV;
    private String moneda = "PEN";
    private double tipoCambio = 3.85;
    private boolean facturada = false; 
    
    // --- ENUM CORREGIDO (Con descripciones reales) ---
    public enum CondicionPago {
        CONTADO("Contado contra entrega"),
        CREDITO_15_DIAS("Crédito a 15 días"),
        CREDITO_30_DIAS("Crédito a 30 días"),
        ADELANTO_50("50% Adelanto, 50% Contra entrega");

        private final String descripcion;

        CondicionPago(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    public Cotizacion() {
        this.fecha = LocalDate.now();
    }

    // --- GETTERS Y SETTERS ---
    public int getNumeroCotizacion() { return numeroCotizacion; }
    public void setNumeroCotizacion(int numeroCotizacion) { this.numeroCotizacion = numeroCotizacion; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public List<ProductoCotizacion> getProductos() { return productos; }
    public void setProductos(List<ProductoCotizacion> productos) { this.productos = productos; }
    public CondicionPago getCondicionPago() { return condicionPago; }
    public void setCondicionPago(CondicionPago condicionPago) { this.condicionPago = condicionPago; }
    public int getDiasVigencia() { return diasVigencia; }
    public void setDiasVigencia(int diasVigencia) { this.diasVigencia = diasVigencia; }
    public boolean isMostrarConIGV() { return mostrarConIGV; }
    public void setMostrarConIGV(boolean mostrarConIGV) { this.mostrarConIGV = mostrarConIGV; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public double getTipoCambio() { return tipoCambio; }
    public void setTipoCambio(double tipoCambio) { this.tipoCambio = tipoCambio; }
    public boolean isFacturada() { return facturada; }
    public void setFacturada(boolean facturada) { this.facturada = facturada; }
    // --- MÉTODOS CALCULADOS ---
    public double getSubtotal() {
        if (productos == null) return 0.0;
        return productos.stream().mapToDouble(ProductoCotizacion::getSubtotal).sum();
    }

    // Este es el que te daba problemas por mayúsculas/minúsculas. Usamos getIGV() (estándar Java)
    public double getIGV() {
        return getSubtotal() * 0.18;
    }

    public double getTotal() {
        return getSubtotal() + getIGV();
    }

    public LocalDate getFechaVigencia() {
        return (fecha != null) ? fecha.plusDays(diasVigencia) : LocalDate.now().plusDays(diasVigencia);
    }
}