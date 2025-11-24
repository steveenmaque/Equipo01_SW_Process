package com.inflesusventas.controller;

import com.inflesusventas.model.Cotizacion;
import com.inflesusventas.service.JsonPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CotizacionController {

    @Autowired
    private JsonPersistenceService storageService;
    
    private List<Cotizacion> cotizaciones = new ArrayList<>();

    @PostConstruct
    public void init() {
        recargarDatos();
    }

    public void recargarDatos() {
        this.cotizaciones = storageService.cargarCotizaciones();
        if (this.cotizaciones == null) {
            this.cotizaciones = new ArrayList<>();
        }
    }

    public List<Cotizacion> getTodasLasCotizaciones() {
        if (this.cotizaciones.isEmpty()) recargarDatos();
        return this.cotizaciones;
    }

    public String generarCotizacion(Cotizacion nuevaCotizacion) {
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

    // --- AQUÍ ESTÁ LA SOLUCIÓN DEL PROBLEMA ---
    
    /**
     * Busca la cotización que originó la factura y le quita el estado "FACTURADA".
     */
    public void anularCotizacionPorFactura(String numeroFacturaRef) {
        System.out.println("🔓 [CotizacionController] Intentando liberar cotización. Ref: " + numeroFacturaRef);
        
        recargarDatos(); // 1. Aseguramos tener los datos más frescos

        if (numeroFacturaRef == null || numeroFacturaRef.trim().isEmpty()) {
            System.err.println("⚠️ Referencia vacía, no se puede anular.");
            return;
        }

        boolean cambioRealizado = false;
        String refBuscada = numeroFacturaRef.trim();

        // Limpieza de emergencia: Si viene como "null-00000000" (el error de tu imagen), 
        // no va a encontrar nada. Pero si en el futuro viene bien, esto funcionará.
        
        for (Cotizacion c : cotizaciones) {
            // Solo miramos las que están facturadas
            if (c.isFacturada() && c.getIdFacturaGenerada() != null) {
                String idGuardado = c.getIdFacturaGenerada().trim(); // Ej: 20554524051-F001-000066087

                // Lógica de coincidencia "inteligente"
                boolean coincide = false;

                // 1. Coincidencia Exacta
                if (idGuardado.equalsIgnoreCase(refBuscada)) coincide = true;
                
                // 2. Coincidencia Parcial (Si la nota trae solo F001-...)
                else if (idGuardado.contains(refBuscada)) coincide = true;
                
                // 3. Coincidencia Inversa (Si la nota trae todo el RUC y guardamos corto)
                else if (refBuscada.contains(idGuardado)) coincide = true;

                if (coincide) {
                    System.out.println("✅ ENCONTRADO! Cotización Nº " + c.getNumeroCotizacion() + " liberada.");
                    c.setFacturada(false);  // <--- ESTO ES LO QUE NECESITAS
                    c.setAnulada(true);     // Marca visual para saber que hubo un cambio
                    cambioRealizado = true;
                    // No hacemos 'break' por si hubiera duplicados por error
                }
            }
        }

        if (cambioRealizado) {
            storageService.guardarCotizaciones(cotizaciones); // <--- IMPORTANTE: GUARDA EN DISCO
            System.out.println("💾 Cambios guardados en data_cotizaciones.json");
        } else {
            System.err.println("❌ No se encontró ninguna cotización vinculada a: " + refBuscada);
        }
    }
}