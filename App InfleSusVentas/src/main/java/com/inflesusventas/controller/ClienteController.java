package com.inflesusventas.controller;

import com.inflesusventas.model.ComprobanteElectronico;
import com.inflesusventas.model.ProductoCotizacion;
import com.inflesusventas.service.ComprobanteService;
import com.inflesusventas.view.cliente.ClienteListView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class ClienteController {

    // Inyectamos el servicio de COMPROBANTES, no el de clientes
    @Autowired
    private ComprobanteService comprobanteService;
    
    private ClienteListView view;

    public JPanel getView() {
        if (view == null) {
            view = new ClienteListView();
            iniciarControlador();
        }
        cargarDatosDeCompras();
        return view;
    }

    private void iniciarControlador() {
        // El botón ahora sirve para refrescar la data
        view.btnNuevo.addActionListener(e -> cargarDatosDeCompras());
    }

    private void cargarDatosDeCompras() {
        view.modelo.setRowCount(0); // Limpiar tabla
        
        // Obtenemos TODAS las facturas emitidas
        List<ComprobanteElectronico> ventas = comprobanteService.listarTodos();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (ComprobanteElectronico venta : ventas) {
            
            // 1. Construir la DESCRIPCIÓN de lo que llevó
            StringBuilder descripcionProductos = new StringBuilder();
            if (venta.getItems() != null) {
                for (ProductoCotizacion prod : venta.getItems()) {
                    descripcionProductos.append(prod.getDescripcion())
                                        .append(" (x").append(prod.getCantidad()).append("), ");
                }
            }
            // Borrar la última coma
            String descFinal = descripcionProductos.length() > 2 
                    ? descripcionProductos.substring(0, descripcionProductos.length() - 2) 
                    : "Sin detalles";

            // 2. Agregar la fila con lo que pediste: Producto y Monto
            view.modelo.addRow(new Object[]{
                venta.getFechaEmision() != null ? venta.getFechaEmision().format(formatter) : "-",
                venta.getRucCliente(),
                venta.getRazonSocialCliente(),
                descFinal, // <--- Aquí va lo que llevó
                String.format("S/ %.2f", venta.getTotal()) // <--- Aquí va el monto
            });
        }
    }
}