package com.inflesusventas.service;

import org.springframework.stereotype.Service;
import java.util.UUID;

/**
 * Servicio de API SUNAT SIMULADA
 * NO hace conexiones reales, solo simula respuestas
 */
@Service
public class SunatApiService {

    /**
     * Envía una factura a SUNAT (SIMULADO)
     */
    public RespuestaSUNAT enviarFactura(String rutaXml) {
        System.out.println("📤 [SIMULADO] Enviando factura a SUNAT...");

        // Simular delay de red
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simular respuesta exitosa
        RespuestaSUNAT respuesta = new RespuestaSUNAT();
        respuesta.setAceptado(true);
        respuesta.setCodigoRespuesta("0");
        respuesta.setMensajeRespuesta("La Factura ha sido aceptada");
        respuesta.setCdr("CDR-FACTURA-" + UUID.randomUUID().toString());

        System.out.println("✓ [SIMULADO] Respuesta SUNAT: ACEPTADO");

        return respuesta;
    }

    /**
     * Envía una nota de crédito a SUNAT (SIMULADO)
     */
    public RespuestaSUNAT enviarNotaCredito(String rutaXml) {
        System.out.println("📤 [SIMULADO] Enviando NC a SUNAT...");

        // Simular delay de red
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simular respuesta exitosa (95% de aceptación)
        boolean aceptado = Math.random() < 0.95;

        RespuestaSUNAT respuesta = new RespuestaSUNAT();
        respuesta.setAceptado(aceptado);

        if (aceptado) {
            respuesta.setCodigoRespuesta("0");
            respuesta.setMensajeRespuesta("La Nota de Crédito ha sido aceptada");
            respuesta.setCdr("CDR-NC-" + UUID.randomUUID().toString());
            System.out.println("✓ [SIMULADO] Respuesta SUNAT: ACEPTADO");
        } else {
            respuesta.setCodigoRespuesta("2324");
            respuesta.setMensajeRespuesta("Error en validación de datos");
            respuesta.setCdr(null);
            System.out.println("✗ [SIMULADO] Respuesta SUNAT: RECHAZADO");
        }

        return respuesta;
    }

    /**
     * Envía una guía de remisión a SUNAT (SIMULADO)
     */
    public RespuestaSUNAT enviarGuiaRemision(String rutaXml) {
        System.out.println("📤 [SIMULADO] Enviando GRE a SUNAT...");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        RespuestaSUNAT respuesta = new RespuestaSUNAT();
        respuesta.setAceptado(true);
        respuesta.setCodigoRespuesta("0");
        respuesta.setMensajeRespuesta("La Guía de Remisión Electrónica ha sido aceptada");
        respuesta.setCdr("CDR-GRE-" + UUID.randomUUID().toString());

        System.out.println("✓ [SIMULADO] Respuesta SUNAT: ACEPTADO");

        return respuesta;
    }

    /**
     * Clase interna para la respuesta de SUNAT
     */
    public static class RespuestaSUNAT {
        private boolean aceptado;
        private String codigoRespuesta;
        private String mensajeRespuesta;
        private String cdr; // Constancia De Recepción

        public boolean isAceptado() {
            return aceptado;
        }

        public void setAceptado(boolean aceptado) {
            this.aceptado = aceptado;
        }

        public String getCodigoRespuesta() {
            return codigoRespuesta;
        }

        public void setCodigoRespuesta(String codigoRespuesta) {
            this.codigoRespuesta = codigoRespuesta;
        }

        public String getMensajeRespuesta() {
            return mensajeRespuesta;
        }

        public void setMensajeRespuesta(String mensajeRespuesta) {
            this.mensajeRespuesta = mensajeRespuesta;
        }

        public String getCdr() {
            return cdr;
        }

        public void setCdr(String cdr) {
            this.cdr = cdr;
        }
    }
}
