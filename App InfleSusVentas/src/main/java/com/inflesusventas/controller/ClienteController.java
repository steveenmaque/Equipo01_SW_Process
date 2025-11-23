package com.inflesusventas.controller;

import com.inflesusventas.model.ComprobanteElectronico;
import com.inflesusventas.model.ProductoCotizacion;
import com.inflesusventas.service.ComprobanteService;
import com.inflesusventas.view.cliente.ClienteListView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class ClienteController {

    @Autowired
    private ComprobanteService comprobanteService;
    
    private ClienteListView view;
    private List<ComprobanteElectronico> listaActual; // Para saber qué fila es qué comprobante

    public JPanel getView() {
        view = new ClienteListView();
        cargarDatosUnificados();
        agregarLogicaBoton();
        return view;
    }

    private void cargarDatosUnificados() {
        view.modelo.setRowCount(0);
        
        // Obtenemos historial
        listaActual = comprobanteService.listarTodos();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (ComprobanteElectronico c : listaActual) {
            // Crear resumen de productos (ej: "Inflable x1, Motor x2...")
            StringBuilder resumen = new StringBuilder();
            if(c.getItems() != null) {
                for(ProductoCotizacion p : c.getItems()){
                    resumen.append(p.getDescripcion()).append(", ");
                }
            }
            String resumenStr = resumen.length() > 2 ? resumen.substring(0, resumen.length()-2) : "-";
            // Si es muy largo, cortarlo
            if(resumenStr.length() > 40) resumenStr = resumenStr.substring(0, 37) + "...";

            view.modelo.addRow(new Object[]{
                c.getFechaEmision() != null ? c.getFechaEmision().format(fmt) : "-",
                c.getRucCliente(),
                c.getRazonSocialCliente(),
                resumenStr,
                String.format("%.2f", c.getTotal()),
                "Ver Cotización" // Texto del botón (aunque el render lo sobreescribe)
            });
        }
    }

    private void agregarLogicaBoton() {
        // En Swing con botones en tabla, la forma más fácil de capturar el evento
        // externo sin crear clases complejas es detectar el click en la tabla.
        view.tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = view.tabla.getColumnModel().getColumnIndexAtX(e.getX());
                int row = e.getY() / view.tabla.getRowHeight();

                // Si clic en columna 5 (Botón) y la fila es válida
                if (row < view.tabla.getRowCount() && row >= 0 && row < listaActual.size() && column == 5) {
                    mostrarDetalleCotizacion(listaActual.get(row));
                }
            }
        });
    }

    private void mostrarDetalleCotizacion(ComprobanteElectronico comprobante) {
        // Construimos un mensaje bonito con el detalle
        StringBuilder detalle = new StringBuilder();
        detalle.append("COTIZACIÓN / COMPROBANTE: ").append(comprobante.getId()).append("\n\n");
        detalle.append("Cliente: ").append(comprobante.getRazonSocialCliente()).append("\n");
        detalle.append("RUC: ").append(comprobante.getRucCliente()).append("\n\n");
        detalle.append("--- PRODUCTOS ---\n");
        
        if (comprobante.getItems() != null) {
            for (ProductoCotizacion p : comprobante.getItems()) {
                detalle.append("• ").append(p.getDescripcion())
                       .append(" | Cant: ").append(p.getCantidad())
                       .append(" | Total: S/ ").append(p.getSubtotal()).append("\n");
            }
        }
        
        detalle.append("\nTOTAL PAGADO: S/ ").append(String.format("%.2f", comprobante.getTotal()));
        
        // Mostramos un Dialog con scroll por si es muy largo
        JTextArea textArea = new JTextArea(detalle.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setMargin(new Insets(10,10,10,10));
        
        JOptionPane.showMessageDialog(view, new JScrollPane(textArea), 
                "Detalle de Cotización", JOptionPane.INFORMATION_MESSAGE);
    }
}