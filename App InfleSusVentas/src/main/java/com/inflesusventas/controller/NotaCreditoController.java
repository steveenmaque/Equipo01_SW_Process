package com.inflesusventas.controller;

import com.inflesusventas.model.ComprobanteElectronico;
import com.inflesusventas.model.ItemNotaCredito;
import com.inflesusventas.model.NotaCredito;
import com.inflesusventas.model.ProductoCotizacion;
import com.inflesusventas.service.ComprobanteService;
import com.inflesusventas.service.NotaCreditoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class NotaCreditoController {

    @Autowired
    private NotaCreditoService notaCreditoService;

    @Autowired
    private ComprobanteService comprobanteService;

    private NotaCredito notaActual;
    private ComprobanteElectronico facturaReferencia;
    private int contadorNC = 1; 

    public NotaCreditoController() {
        iniciarNuevaNota();
    }

    public void recalcularTotales() {
    double subtotal = 0;
    for (ItemNotaCredito item : notaActual.getItems()) {
        subtotal += item.getSubtotal();
    }
    this.notaActual.setSubtotal(subtotal);
    this.notaActual.setIgv(subtotal * 0.18);
    this.notaActual.setTotal(subtotal * 1.18);
    }
    public void iniciarNuevaNota() {
        this.notaActual = new NotaCredito();
        this.notaActual.setSerie("FC01");
        this.notaActual.setNumero(contadorNC);
        this.notaActual.setFechaEmision(LocalDate.now());
        this.notaActual.setMoneda("PEN");
        this.facturaReferencia = null;
    }

    /**
     * CORREGIDO: Asegura que la referencia tenga el formato completo ID
     */
    public boolean cargarFacturaReferencia(String idFactura) {
        System.out.println("🔍 Buscando factura para referencia: " + idFactura);
        ComprobanteElectronico factura = comprobanteService.buscarPorId(idFactura);

        if (factura != null) {
            this.facturaReferencia = factura;

            this.notaActual.setRucCliente(factura.getRucCliente());
            this.notaActual.setRazonSocialCliente(factura.getRazonSocialCliente());
            
            // --- CORRECCIÓN CLAVE ---
            // Usamos el ID completo de la factura (que incluye RUC) para que coincida con la cotización
            // Si factura.getId() es nulo, intentamos reconstruirlo, pero preferimos el ID.
            String refCompleta = (factura.getId() != null) ? factura.getId() : idFactura;
            
            this.notaActual.setNumeroFacturaRef(refCompleta);
            // ------------------------

            this.notaActual.setMoneda(factura.getMoneda());

            List<ItemNotaCredito> itemsNC = new ArrayList<>();
            if (factura.getItems() != null) {
                for (ProductoCotizacion prod : factura.getItems()) {
                    ItemNotaCredito item = new ItemNotaCredito(
                            "UND", 
                            prod.getCantidad(),
                            prod.getDescripcion(),
                            prod.getPrecioBase());
                    itemsNC.add(item);
                }
            }
            this.notaActual.setItems(itemsNC);
            recalcularTotales();

            return true;
        }
        System.err.println("❌ No se encontró la factura con ID: " + idFactura);
        return false;
    }

    // ... resto de métodos (actualizarMotivo, recalcularTotales, etc.) igual ...

    public NotaCredito emitirNotaCredito() {
        try {
            // Validar que la referencia no sea nula antes de enviar
            if (notaActual.getNumeroFacturaRef() == null || notaActual.getNumeroFacturaRef().startsWith("null")) {
                System.err.println("⚠️ ADVERTENCIA: La referencia parece incorrecta: " + notaActual.getNumeroFacturaRef());
                // Podrías lanzar error o corregirlo aquí si tienes el ID a mano
            }

            String resultado = notaCreditoService.generarNotaCredito(this.notaActual);
            NotaCredito notaEmitida = this.notaActual;

            this.contadorNC++;
            iniciarNuevaNota(); 

            return notaEmitida;
        } catch (Exception e) {
            System.err.println("Error al emitir nota de crédito: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    // ... Getters y setters ...
    public NotaCredito getNotaActual() { return notaActual; }
    public ComprobanteElectronico getFacturaReferencia() { return facturaReferencia; }
    public void guardarEdicionItem(int index, String nuevaDescripcion) {
        if (notaActual != null && notaActual.getItems() != null && index >= 0 && index < notaActual.getItems().size()) {
            notaActual.getItems().get(index).setDescripcionCorregida(nuevaDescripcion);
        }
    }
}