package com.inflesusventas.controller;

import com.inflesusventas.model.Cliente;
import com.inflesusventas.model.Cotizacion;
import com.inflesusventas.service.JsonPersistenceService;
import com.inflesusventas.service.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CotizacionController {

    @Autowired
    private JsonPersistenceService storageService;
    
    // Si usas PdfGeneratorService en este controlador, inyéctalo también
    @Autowired 
    private PdfGeneratorService pdfService;

    private List<Cotizacion> cotizaciones = null;
    private Cotizacion cotizacionActual;

    public CotizacionController() {
        // Constructor vacío para Spring
    }
    
    // ESTE ES EL MÉTODO QUE FALTA: Carga los datos al iniciar
    @PostConstruct
    public void init() {
        try {
            // 1. Intentar cargar del archivo
            List<Cotizacion> cargadas = storageService.cargarCotizaciones();
            
            if (cargadas != null && !cargadas.isEmpty()) {
                this.cotizaciones = cargadas;
                System.out.println("✅ Cotizaciones recuperadas del historial: " + cotizaciones.size());
            } else {
                this.cotizaciones = new ArrayList<>();
                System.out.println("ℹ️ No se encontraron cotizaciones previas.");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar cotizaciones: " + e.getMessage());
            this.cotizaciones = new ArrayList<>();
        }
    }

    // Cuando generas una nueva, guardamos en disco
    public String generarCotizacion(Cotizacion nuevaCotizacion) {
        // Asegurar que tenemos la lista cargada antes de agregar
        getTodasLasCotizaciones(); 
        
        int nuevoNumero = cotizaciones.size() + 1;
        nuevaCotizacion.setNumeroCotizacion(nuevoNumero);
        cotizaciones.add(nuevaCotizacion);
        
        storageService.guardarCotizaciones(cotizaciones);
        return "Cotización Nº " + nuevoNumero + " generada correctamente.";
    }
    
    // Método para actualizar (ej: marcar como facturada)
    public void actualizarCotizacion(Cotizacion cotizacionModificada) {
        for (int i = 0; i < cotizaciones.size(); i++) {
            if (cotizaciones.get(i).getNumeroCotizacion() == cotizacionModificada.getNumeroCotizacion()) {
                cotizaciones.set(i, cotizacionModificada);
                break;
            }
        }
        // Guardar cambios (estado facturado) en disco
        storageService.guardarCotizaciones(cotizaciones);
    }

     public List<Cotizacion> getTodasLasCotizaciones() {
        // Si la lista es nula (primera vez que se llama), intentamos cargarla
        if (this.cotizaciones == null) {
            System.out.println(" La lista en memoria estaba vacía. Intentando cargar del disco...");
            this.cotizaciones = storageService.cargarCotizaciones();
            
            if (this.cotizaciones == null) {
                this.cotizaciones = new ArrayList<>();
            }
        }
        return this.cotizaciones;
    }

    public Cotizacion getCotizacionActual() {
        // Si es null, retorna una nueva para evitar errores
        if (cotizacionActual == null) cotizacionActual = new Cotizacion();
        return cotizacionActual;
    }
    
    public void iniciarNuevaCotizacion() {
        this.cotizacionActual = new Cotizacion();
    }
}