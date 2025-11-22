package com.inflesusventas.model;

import java.time.LocalDate;

/**
 * Modelo para datos de transporte en Guía de Remisión
 * Ruta: src/main/java/com/inflesusventas/model/DatosTransporte.java
 */
public class DatosTransporte {

    public enum TipoTransporte {
        TRANSPORTE_PUBLICO("01", "Transporte Público"),
        TRANSPORTE_PRIVADO("02", "Transporte Privado");

        private final String codigo;
        private final String descripcion;

        TipoTransporte(String codigo, String descripcion) {
            this.codigo = codigo;
            this.descripcion = descripcion;
        }

        public String getCodigo() { return codigo; }
        public String getDescripcion() { return descripcion; }
    }

    public enum EntidadAutorizacion {
        MTC("MTC", "Ministerio de Transportes y Comunicaciones"),
        OTRO("OTRO", "Otra entidad");

        private final String codigo;
        private final String descripcion;

        EntidadAutorizacion(String codigo, String descripcion) {
            this.codigo = codigo;
            this.descripcion = descripcion;
        }

        public String getCodigo() { return codigo; }
        public String getDescripcion() { return descripcion; }
    }

    // Datos generales
    private TipoTransporte tipoTransporte;

    // Datos del vehículo
    private String numeroPlaca;
    private EntidadAutorizacion entidadAutorizacion;

    // Datos del conductor
    private String numeroLicencia;
    private String nombreConductor;
    private String apellidosConductor;
    private String dniConductor;

    // Fecha de inicio de traslado
    private LocalDate fechaInicioTraslado;

    // Constructor
    public DatosTransporte() {
        this.tipoTransporte = TipoTransporte.TRANSPORTE_PRIVADO; // Por defecto
        this.entidadAutorizacion = EntidadAutorizacion.MTC; // Por defecto
        this.fechaInicioTraslado = LocalDate.now();
    }

    // Getters y Setters
    public TipoTransporte getTipoTransporte() {
        return tipoTransporte;
    }

    public void setTipoTransporte(TipoTransporte tipoTransporte) {
        this.tipoTransporte = tipoTransporte;
    }

    public String getNumeroPlaca() {
        return numeroPlaca;
    }

    public void setNumeroPlaca(String numeroPlaca) {
        this.numeroPlaca = numeroPlaca;
    }

    public EntidadAutorizacion getEntidadAutorizacion() {
        return entidadAutorizacion;
    }

    public void setEntidadAutorizacion(EntidadAutorizacion entidadAutorizacion) {
        this.entidadAutorizacion = entidadAutorizacion;
    }

    public String getNumeroLicencia() {
        return numeroLicencia;
    }

    public void setNumeroLicencia(String numeroLicencia) {
        this.numeroLicencia = numeroLicencia;
    }

    public String getNombreConductor() {
        return nombreConductor;
    }

    public void setNombreConductor(String nombreConductor) {
        this.nombreConductor = nombreConductor;
    }

    public String getApellidosConductor() {
        return apellidosConductor;
    }

    public void setApellidosConductor(String apellidosConductor) {
        this.apellidosConductor = apellidosConductor;
    }

    public String getDniConductor() {
        return dniConductor;
    }

    public void setDniConductor(String dniConductor) {
        this.dniConductor = dniConductor;
    }

    public LocalDate getFechaInicioTraslado() {
        return fechaInicioTraslado;
    }

    public void setFechaInicioTraslado(LocalDate fechaInicioTraslado) {
        this.fechaInicioTraslado = fechaInicioTraslado;
    }

    @Override
    public String toString() {
        return tipoTransporte.getDescripcion() + " - Placa: " + numeroPlaca;
    }
}