package com.inflesusventas.controller;

import com.inflesusventas.model.Cotizacion;
import com.inflesusventas.service.JsonPersistenceService;
import com.inflesusventas.service.PdfGeneratorService; // Importar el servicio PDF
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CotizacionController {

    @Autowired
    private JsonPersistenceService storageService;

    @Autowired
    private PdfGeneratorService pdfService; // Inyectar el servicio PDF
    
    private List<Cotizacion> cotizaciones = new ArrayList<>();

    @PostConstruct
    public void init() {
        System.out.println("INICIANDO CONTROLADOR DE COTIZACIONES...");
        recargarDatos();
    }

    public void recargarDatos() {
        this.cotizaciones = storageService.cargarCotizaciones();
        if (this.cotizaciones == null) {
            this.cotizaciones = new ArrayList<>();
        }
        System.out.println("Datos recargados desde disco. Total: " + cotizaciones.size());
    }

    public List<Cotizacion> getTodasLasCotizaciones() {
        if (this.cotizaciones.isEmpty()) recargarDatos();
        return this.cotizaciones;
    }

    public String generarCotizacion(Cotizacion nuevaCotizacion) {
        if (cotizaciones.isEmpty()) recargarDatos();
        
        int nuevoNumero = cotizaciones.size() + 1;
        nuevaCotizacion.setNumeroCotizacion(nuevoNumero);
        
        // 1. Guardar en memoria y JSON
        cotizaciones.add(nuevaCotizacion);
        storageService.guardarCotizaciones(cotizaciones);

        // 2. GENERAR PDF (Esto era lo que faltaba)
        try {
            if (pdfService != null) {
                String rutaPdf = pdfService.generarPdfCotizacion(nuevaCotizacion);
                System.out.println("PDF Generado correctamente en: " + rutaPdf);
            } else {
                System.err.println("Error: El servicio de PDF no esta disponible (es null).");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al crear el archivo PDF: " + e.getMessage());
        }

        return "Cotizacion N " + nuevoNumero + " generada.";
    }

    public void actualizarCotizacion(Cotizacion mod) {
        boolean encontrado = false;
        for (int i = 0; i < cotizaciones.size(); i++) {
            if (cotizaciones.get(i).getNumeroCotizacion() == mod.getNumeroCotizacion()) {
                cotizaciones.set(i, mod);
                encontrado = true;
                break;
            }
        }
        if (encontrado) {
            storageService.guardarCotizaciones(cotizaciones);
        }
    }

    /**
     * Busca la cotización que originó la factura y le quita el estado "FACTURADA".
     */
    public void anularCotizacionPorFactura(String numeroFacturaRef) {
        System.out.println("[CotizacionController] Intentando liberar cotizacion. Ref: " + numeroFacturaRef);
        
        recargarDatos(); 

        if (numeroFacturaRef == null || numeroFacturaRef.trim().isEmpty()) {
            System.err.println("Referencia vacia, no se puede anular.");
            return;
        }

        boolean cambioRealizado = false;
        String refBuscada = numeroFacturaRef.trim();

        for (Cotizacion c : cotizaciones) {
            if (c.isFacturada() && c.getIdFacturaGenerada() != null) {
                String idGuardado = c.getIdFacturaGenerada().trim();

                boolean coincide = false;
                if (idGuardado.equalsIgnoreCase(refBuscada)) coincide = true;
                else if (idGuardado.contains(refBuscada)) coincide = true;
                else if (refBuscada.contains(idGuardado)) coincide = true;

                if (coincide) {
                    System.out.println("ENCONTRADO! Cotizacion N " + c.getNumeroCotizacion() + " liberada.");
                    c.setFacturada(false);
                    c.setAnulada(true);
                    cambioRealizado = true;
                }
            }
        }

        if (cambioRealizado) {
            storageService.guardarCotizaciones(cotizaciones);
            System.out.println("Cambios guardados en data_cotizaciones.json");
        } else {
            System.err.println("No se encontro ninguna cotizacion vinculada a: " + refBuscada);
        }
    }
}