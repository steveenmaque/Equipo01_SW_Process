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

    // Estado de la NC en proceso
    private NotaCredito notaActual;
    private ComprobanteElectronico facturaReferencia;

    private int contadorNC = 1; // En app real, cargar de BD o archivo

    public NotaCreditoController() {
        iniciarNuevaNota();
    }

    public void iniciarNuevaNota() {
        this.notaActual = new NotaCredito();
        this.notaActual.setSerie("FC01"); // Serie por defecto para NC
        this.notaActual.setNumero(contadorNC);
        this.notaActual.setFechaEmision(LocalDate.now());
        this.notaActual.setMoneda("PEN");
        this.facturaReferencia = null;
    }

    /**
     * Busca una factura y carga sus datos en la NC actual
     */
    public boolean cargarFacturaReferencia(String idFactura) {
        ComprobanteElectronico factura = comprobanteService.buscarPorId(idFactura);

        if (factura != null) {
            this.facturaReferencia = factura;

            // Copiar datos de cabecera
            this.notaActual.setRucCliente(factura.getRucCliente());
            this.notaActual.setRazonSocialCliente(factura.getRazonSocialCliente());
            this.notaActual.setNumeroFacturaRef(factura.getSerie() + "-" + String.format("%08d", factura.getNumero()));
            this.notaActual.setMoneda(factura.getMoneda());

            // Copiar ítems (adaptando de ProductoCotizacion a ItemNotaCredito)
            List<ItemNotaCredito> itemsNC = new ArrayList<>();
            if (factura.getItems() != null) {
                for (ProductoCotizacion prod : factura.getItems()) {
                    ItemNotaCredito item = new ItemNotaCredito(
                            "UND", // Asumido, ya que ProductoCotizacion no siempre tiene UM
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
        return false;
    }

    public void actualizarMotivo(String motivo) {
        this.notaActual.setMotivoSustento(motivo);
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

    public NotaCredito emitirNotaCredito() {
        try {
            String resultado = notaCreditoService.generarNotaCredito(this.notaActual);
            // Guardamos la referencia a la nota emitida antes de resetear
            NotaCredito notaEmitida = this.notaActual;

            this.contadorNC++;
            iniciarNuevaNota(); // Limpiar para la siguiente

            return notaEmitida;
        } catch (Exception e) {
            System.err.println("Error al emitir nota de crédito: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Getters para la vista
    public NotaCredito getNotaActual() {
        return notaActual;
    }

    public ComprobanteElectronico getFacturaReferencia() {
        return facturaReferencia;
    }

    public void guardarEdicionItem(int index, String nuevaDescripcion) {
        if (notaActual != null && notaActual.getItems() != null && index >= 0 && index < notaActual.getItems().size()) {
            notaActual.getItems().get(index).setDescripcionCorregida(nuevaDescripcion);
        }
    }
}
