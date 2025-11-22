package com.inflesusventas.controller;

import com.inflesusventas.model.GuiaRemision;
import com.inflesusventas.model.BienGuiaRemision;
import com.inflesusventas.service.GuiaRemisionService;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Controlador para gestión de Guías de Remisión Electrónica
 * Ruta: src/main/java/com/inflesusventas/controller/GuiaRemisionController.java
 */
@Controller
public class GuiaRemisionController {

    private final GuiaRemisionService guiaService;
    private GuiaRemision guiaActual;
    private int contadorGuias;

    private static final String ARCHIVO_CONTADOR = "documentos/contadores/contador_guias.txt";
    private static final String SERIE_GUIAS = "T001";

    public GuiaRemisionController(GuiaRemisionService guiaService) {
        this.guiaService = guiaService;
        cargarContador();
        iniciarNuevaGuia();
    }

    /**
     * Carga el contador de guías desde archivo
     */
    private void cargarContador() {
        try {
            if (Files.exists(Paths.get(ARCHIVO_CONTADOR))) {
                String contenido = Files.readString(Paths.get(ARCHIVO_CONTADOR));
                this.contadorGuias = Integer.parseInt(contenido.trim());
            } else {
                this.contadorGuias = 0;
            }
        } catch (Exception e) {
            System.err.println("⚠ Error al cargar contador de guías: " + e.getMessage());
            this.contadorGuias = 0;
        }
    }

    /**
     * Guarda el contador de guías en archivo
     */
    private void guardarContador() {
        try {
            Files.createDirectories(Paths.get(ARCHIVO_CONTADOR).getParent());
            Files.writeString(Paths.get(ARCHIVO_CONTADOR), String.valueOf(contadorGuias));
        } catch (IOException e) {
            System.err.println("⚠ Error al guardar contador de guías: " + e.getMessage());
        }
    }

    /**
     * Inicia una nueva guía de remisión
     */
    public void iniciarNuevaGuia() {
        this.guiaActual = new GuiaRemision();
        this.contadorGuias++;

        // Generar serie-número correlativo
        String numeroFormateado = String.format("%08d", contadorGuias);
        this.guiaActual.setSerieNumero(SERIE_GUIAS + "-" + numeroFormateado);

        System.out.println("✓ Nueva guía iniciada: " + guiaActual.getSerieNumero());
    }

    /**
     * Agrega un bien a la guía actual
     */
    public void agregarBien(String codigo, String descripcion, int cantidad, double peso) {
        BienGuiaRemision bien = new BienGuiaRemision(codigo, descripcion, cantidad, peso);
        this.guiaActual.getBienes().add(bien);
        System.out.println("✓ Bien agregado: " + descripcion);
    }

    /**
     * Elimina un bien de la guía actual
     */
    public void eliminarBien(int indice) {
        if (indice >= 0 && indice < guiaActual.getBienes().size()) {
            BienGuiaRemision eliminado = guiaActual.getBienes().remove(indice);
            System.out.println("✓ Bien eliminado: " + eliminado.getDescripcionDetallada());
        }
    }

    /**
     * Valida que la guía esté completa para generar documentos
     */
    private boolean validarGuia() {
        if (guiaActual == null) {
            System.err.println("✗ No hay guía activa");
            return false;
        }

        if (guiaActual.getNumeroDocumentoDestinatario() == null ||
                guiaActual.getNumeroDocumentoDestinatario().isEmpty()) {
            System.err.println("✗ Falta número de documento del destinatario");
            return false;
        }

        if (guiaActual.getBienes().isEmpty()) {
            System.err.println("✗ No hay bienes agregados");
            return false;
        }

        if (guiaActual.getPuntoPartida() == null || guiaActual.getPuntoPartida().isEmpty()) {
            System.err.println("✗ Falta punto de partida");
            return false;
        }

        if (guiaActual.getPuntoLlegada() == null || guiaActual.getPuntoLlegada().isEmpty()) {
            System.err.println("✗ Falta punto de llegada");
            return false;
        }

        if (guiaActual.getDatosTransporte().getNumeroPlaca() == null ||
                guiaActual.getDatosTransporte().getNumeroPlaca().isEmpty()) {
            System.err.println("✗ Falta número de placa");
            return false;
        }

        if (guiaActual.getDatosTransporte().getNumeroLicencia() == null ||
                guiaActual.getDatosTransporte().getNumeroLicencia().isEmpty()) {
            System.err.println("✗ Falta número de licencia del conductor");
            return false;
        }

        return true;
    }

    /**
     * Genera el XML de la guía de remisión
     */
    public String generarXML() {
        if (!validarGuia()) {
            return null;
        }

        try {
            System.out.println("🔄 Generando XML de guía " + guiaActual.getSerieNumero() + "...");
            String rutaXml = guiaService.generarXML(guiaActual);
            System.out.println("✓ XML generado: " + rutaXml);
            return rutaXml;
        } catch (Exception e) {
            System.err.println("✗ Error al generar XML: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Genera el PDF de la guía de remisión
     */
    public String generarPDF() {
        if (!validarGuia()) {
            return null;
        }

        try {
            System.out.println("🔄 Generando PDF de guía " + guiaActual.getSerieNumero() + "...");
            String rutaPdf = guiaService.generarPDF(guiaActual);
            System.out.println("✓ PDF generado: " + rutaPdf);

            // Guardar contador solo si se generó exitosamente
            guardarContador();

            return rutaPdf;
        } catch (Exception e) {
            System.err.println("✗ Error al generar PDF: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Genera XML y PDF de la guía
     */
    public boolean generarDocumentos() {
        String xml = generarXML();
        String pdf = generarPDF();

        return xml != null && pdf != null;
    }

    // Getters
    public GuiaRemision getGuiaActual() {
        return guiaActual;
    }

    public int getContadorGuias() {
        return contadorGuias;
    }

    public String getSiguienteNumero() {
        return SERIE_GUIAS + "-" + String.format("%08d", contadorGuias + 1);
    }
}