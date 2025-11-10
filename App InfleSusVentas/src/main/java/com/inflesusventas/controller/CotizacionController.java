package com.inflesusventas.controller;

import com.inflesusventas.model.Cliente;
import com.inflesusventas.model.Cotizacion;
import com.inflesusventas.model.ProductoCotizacion;
import com.inflesusventas.service.PdfGeneratorService;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CotizacionController {

    private final PdfGeneratorService pdfService;
    private Cotizacion cotizacionActual;

    // Spring inyecta automáticamente el servicio aquí
    public CotizacionController(PdfGeneratorService pdfService) {
        this.pdfService = pdfService;
        iniciarNuevaCotizacion();
        
        // ¡TRUCO! Descomenta esta línea si quieres probar generar un PDF 
        // apenas arranque la aplicación para ver si funciona:
        // probarGeneracionInmediata();
    }

    // --- MÉTODOS DE GESTIÓN (Lógica de negocio) ---

    public void iniciarNuevaCotizacion() {
        this.cotizacionActual = new Cotizacion();
        // Generamos un número aleatorio para probar (en real sería correlativo de BD)
        this.cotizacionActual.setNumeroCotizacion((int) (Math.random() * 10000));
        this.cotizacionActual.setProductos(new ArrayList<>());
        // Valores por defecto
        this.cotizacionActual.setCondicionPago(Cotizacion.CondicionPago.CONTADO);
        this.cotizacionActual.setDiasVigencia(15);
    }

    public void setCliente(Cliente cliente) {
        this.cotizacionActual.setCliente(cliente);
    }

    public void agregarProducto(String codigo, String descripcion, int cantidad, double precioUnitario) {
        ProductoCotizacion producto = new ProductoCotizacion(codigo, descripcion, cantidad, codigo, precioUnitario);
        this.cotizacionActual.getProductos().add(producto);
    }

    public String generarPDF() {
        if (!esCotizacionValida()) {
            System.err.println("⚠ No se puede generar PDF: Faltan datos del cliente o productos.");
            return null;
        }

        try {
            System.out.println("🔄 Generando PDF para cotización N° " + cotizacionActual.getNumeroCotizacion() + "...");
            String rutaPdf = pdfService.generarPdfCotizacion(cotizacionActual);
            System.out.println("✅ ¡ÉXITO! PDF guardado en: " + rutaPdf);
            return rutaPdf;
        } catch (Exception e) {
            System.err.println("❌ ERROR GRAVE generando PDF: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // --- MÉTODOS AUXILIARES ---

    private boolean esCotizacionValida() {
        return cotizacionActual != null &&
               cotizacionActual.getCliente() != null &&
               cotizacionActual.getProductos() != null &&
               !cotizacionActual.getProductos().isEmpty();
    }

    public Cotizacion getCotizacionActual() {
        return cotizacionActual;
    }

    // --- MÉTODO DE PRUEBA RÁPIDA ---
    // Llámalo desde el constructor o tu Main para probar todo el sistema de una
    public void probarGeneracionInmediata() {
        System.out.println("--- INICIANDO PRUEBA AUTOMÁTICA DE PDF ---");
        iniciarNuevaCotizacion();
        
        // 1. Creamos un cliente ficticio
        Cliente clienteTest = new Cliente(
            "20555555551", 
            "EMPRESA DE PRUEBA S.A.C.", 
            "Av. Javier Prado Este 1234, Lima", 
            "999-888-777", 
            "contacto@empresaprueba.com", 
            "Juan Pérez (Logística)"
        );
        setCliente(clienteTest);

        // 2. Agregamos productos ficticios
        agregarProducto("INF-001", "Inflable Publicitario Tipo Arco 5x3m", 1, 1500.00);
        agregarProducto("MOT-HP1", "Motor soplador 1HP Importado", 2, 450.50);
        agregarProducto("INST-LIMA", "Servicio de Instalación en Lima Metropolitana", 1, 200.00);

        // 3. Configuraciones extra
        cotizacionActual.setCondicionPago(Cotizacion.CondicionPago.ADELANTO_50);
        cotizacionActual.setDiasVigencia(10);

        // 4. ¡Generar!
        generarPDF();
        System.out.println("--- FIN DE PRUEBA AUTOMÁTICA ---");
    }
    public String generarCotizacion(Cotizacion cotizacionDesdeVista) {
        this.cotizacionActual = cotizacionDesdeVista; // Recibimos los datos del formulario
        return generarPDF(); // Reutilizamos la lógica que ya teníamos
    }
}