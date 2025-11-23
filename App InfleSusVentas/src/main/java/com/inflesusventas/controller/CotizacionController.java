package com.inflesusventas.controller;

import com.inflesusventas.model.Cotizacion;
import com.inflesusventas.service.JsonPersistenceService;
import com.inflesusventas.service.PdfGeneratorService; // Si lo usas
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CotizacionController {

    @Autowired
    private JsonPersistenceService storageService;
    
    // Lista en memoria
    private List<Cotizacion> cotizaciones = new ArrayList<>();
    private Cotizacion cotizacionActual;

    // Cargar AUTOMÁTICAMENTE al iniciar la app
    @PostConstruct
    public void init() {
        System.out.println("🔄 INICIANDO CONTROLADOR DE COTIZACIONES...");
        recargarDatos();
    }

    // Método público para forzar recarga
    public void recargarDatos() {
        List<Cotizacion> cargadas = storageService.cargarCotizaciones();
        if (cargadas != null) {
            this.cotizaciones = cargadas;
        } else {
            this.cotizaciones = new ArrayList<>();
        }
        System.out.println("📊 Controlador tiene en memoria: " + this.cotizaciones.size() + " cotizaciones.");
    }

    public List<Cotizacion> getTodasLasCotizaciones() {
        // Si por alguna razón está vacía, intentamos cargar de nuevo
        if (this.cotizaciones.isEmpty()) {
            recargarDatos();
        }
        return this.cotizaciones;
    }

    public String generarCotizacion(Cotizacion nuevaCotizacion) {
        // Asegurarnos de tener la lista actualizada antes de agregar
        if (cotizaciones.isEmpty()) recargarDatos();
        
        int nuevoNumero = cotizaciones.size() + 1;
        nuevaCotizacion.setNumeroCotizacion(nuevoNumero);
        
        cotizaciones.add(nuevaCotizacion);
        storageService.guardarCotizaciones(cotizaciones);
        
        return "Cotización Nº " + nuevoNumero + " generada.";
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
     * Libera una cotización anulada para permitir facturarla de nuevo.
     */
    public void anularCotizacionPorFactura(String numeroFactura) {
        if (cotizaciones == null || cotizaciones.isEmpty()) {
            recargarDatos();
        }

        boolean encontrado = false;
        for (Cotizacion c : cotizaciones) {
            // Buscamos la cotización que tenía esa factura vinculada
            if (c.getIdFacturaGenerada() != null && c.getIdFacturaGenerada().equals(numeroFactura)) {
                
                // 1. Marcamos que fue anulada (para historial visual si quieres)
                c.setAnulada(true); 
                
                // 2. IMPORTANTE: La liberamos para poder facturar de nuevo
                c.setFacturada(false); 
                
                // 3. Opcional: Borramos el vínculo con la factura vieja para que no confunda
                // O puedes guardarlo en un campo "historialFacturas" si quisieras auditoría
                c.setIdFacturaGenerada(null); 
                
                encontrado = true;
                System.out.println("🔄 Cotización COT-" + c.getNumeroCotizacion() + " liberada (Estado: PENDIENTE).");
                break;
            }
        }

        if (encontrado) {
            storageService.guardarCotizaciones(cotizaciones);
        }
    }
    
    // Getters y Setters básicos
    public Cotizacion getCotizacionActual() {
        if (cotizacionActual == null) cotizacionActual = new Cotizacion();
        return cotizacionActual;
    }
    public void iniciarNuevaCotizacion() {
        this.cotizacionActual = new Cotizacion();
    }
}