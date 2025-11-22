package com.inflesusventas.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo principal para Guía de Remisión Electrónica
 * Ruta: src/main/java/com/inflesusventas/model/GuiaRemision.java
 */
public class GuiaRemision {

    // Enumeraciones
    public enum TipoRemitente {
        REMITENTE("Remitente"),
        DESTINATARIO("Destinatario");

        private final String descripcion;
        TipoRemitente(String descripcion) { this.descripcion = descripcion; }
        public String getDescripcion() { return descripcion; }
    }

    public enum MotivoTraslado {
        VENTA("01", "Venta"),
        COMPRA("02", "Compra"),
        TRASLADO_ENTRE_ESTABLECIMIENTOS("04", "Traslado entre establecimientos"),
        CONSIGNACION("08", "Consignación"),
        DEVOLUCION("09", "Devolución"),
        OTROS("13", "Otros");

        private final String codigo;
        private final String descripcion;

        MotivoTraslado(String codigo, String descripcion) {
            this.codigo = codigo;
            this.descripcion = descripcion;
        }

        public String getCodigo() { return codigo; }
        public String getDescripcion() { return descripcion; }
    }

    public enum TipoDocumento {
        RUC("6", "RUC"),
        DNI("1", "DNI"),
        CARNET_EXTRANJERIA("4", "Carnet de Extranjería");

        private final String codigo;
        private final String descripcion;

        TipoDocumento(String codigo, String descripcion) {
            this.codigo = codigo;
            this.descripcion = descripcion;
        }

        public String getCodigo() { return codigo; }
        public String getDescripcion() { return descripcion; }
    }

    // Datos básicos
    private String serieNumero; // T001-00000001
    private LocalDate fechaEmision;

    // Paso 1: Configuración inicial
    private TipoRemitente tipoRemitente;
    private boolean operacionComercioExterior;
    private MotivoTraslado motivoTraslado;

    // Paso 2: Destinatario
    private TipoDocumento tipoDocumentoDestinatario;
    private String numeroDocumentoDestinatario;
    private String razonSocialDestinatario;

    // Paso 5: Bienes a trasladar
    private List<BienGuiaRemision> bienes;

    // Paso 6: Puntos de partida y llegada
    private String puntoPartida; // Dirección completa
    private String ubigeoPartida; // Código UBIGEO
    private String puntoLlegada;
    private String ubigeoLlegada;

    // Paso 7: Datos de transporte
    private DatosTransporte datosTransporte;

    // Datos del remitente (empresa)
    private String rucRemitente;
    private String razonSocialRemitente;

    // Constructor
    public GuiaRemision() {
        this.fechaEmision = LocalDate.now();
        this.tipoRemitente = TipoRemitente.REMITENTE;
        this.operacionComercioExterior = false;
        this.motivoTraslado = MotivoTraslado.VENTA;
        this.tipoDocumentoDestinatario = TipoDocumento.RUC;
        this.bienes = new ArrayList<>();
        this.datosTransporte = new DatosTransporte();
    }

    // Getters y Setters
    public String getSerieNumero() {
        return serieNumero;
    }

    public void setSerieNumero(String serieNumero) {
        this.serieNumero = serieNumero;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public TipoRemitente getTipoRemitente() {
        return tipoRemitente;
    }

    public void setTipoRemitente(TipoRemitente tipoRemitente) {
        this.tipoRemitente = tipoRemitente;
    }

    public boolean isOperacionComercioExterior() {
        return operacionComercioExterior;
    }

    public void setOperacionComercioExterior(boolean operacionComercioExterior) {
        this.operacionComercioExterior = operacionComercioExterior;
    }

    public MotivoTraslado getMotivoTraslado() {
        return motivoTraslado;
    }

    public void setMotivoTraslado(MotivoTraslado motivoTraslado) {
        this.motivoTraslado = motivoTraslado;
    }

    public TipoDocumento getTipoDocumentoDestinatario() {
        return tipoDocumentoDestinatario;
    }

    public void setTipoDocumentoDestinatario(TipoDocumento tipoDocumentoDestinatario) {
        this.tipoDocumentoDestinatario = tipoDocumentoDestinatario;
    }

    public String getNumeroDocumentoDestinatario() {
        return numeroDocumentoDestinatario;
    }

    public void setNumeroDocumentoDestinatario(String numeroDocumentoDestinatario) {
        this.numeroDocumentoDestinatario = numeroDocumentoDestinatario;
    }

    public String getRazonSocialDestinatario() {
        return razonSocialDestinatario;
    }

    public void setRazonSocialDestinatario(String razonSocialDestinatario) {
        this.razonSocialDestinatario = razonSocialDestinatario;
    }

    public List<BienGuiaRemision> getBienes() {
        return bienes;
    }

    public void setBienes(List<BienGuiaRemision> bienes) {
        this.bienes = bienes;
    }

    public String getPuntoPartida() {
        return puntoPartida;
    }

    public void setPuntoPartida(String puntoPartida) {
        this.puntoPartida = puntoPartida;
    }

    public String getUbigeoPartida() {
        return ubigeoPartida;
    }

    public void setUbigeoPartida(String ubigeoPartida) {
        this.ubigeoPartida = ubigeoPartida;
    }

    public String getPuntoLlegada() {
        return puntoLlegada;
    }

    public void setPuntoLlegada(String puntoLlegada) {
        this.puntoLlegada = puntoLlegada;
    }

    public String getUbigeoLlegada() {
        return ubigeoLlegada;
    }

    public void setUbigeoLlegada(String ubigeoLlegada) {
        this.ubigeoLlegada = ubigeoLlegada;
    }

    public DatosTransporte getDatosTransporte() {
        return datosTransporte;
    }

    public void setDatosTransporte(DatosTransporte datosTransporte) {
        this.datosTransporte = datosTransporte;
    }

    public String getRucRemitente() {
        return rucRemitente;
    }

    public void setRucRemitente(String rucRemitente) {
        this.rucRemitente = rucRemitente;
    }

    public String getRazonSocialRemitente() {
        return razonSocialRemitente;
    }

    public void setRazonSocialRemitente(String razonSocialRemitente) {
        this.razonSocialRemitente = razonSocialRemitente;
    }

    // Métodos auxiliares
    public double getPesoTotalCarga() {
        return bienes.stream()
                .mapToDouble(BienGuiaRemision::getPesoBrutoTotal)
                .sum();
    }

    public int getCantidadTotalBienes() {
        return bienes.stream()
                .mapToInt(BienGuiaRemision::getCantidad)
                .sum();
    }

    @Override
    public String toString() {
        return "Guía " + serieNumero + " - " + razonSocialDestinatario;
    }
}