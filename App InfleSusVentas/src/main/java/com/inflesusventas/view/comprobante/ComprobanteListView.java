package com.inflesusventas.view.comprobante;

import com.inflesusventas.controller.ComprobanteController; // Opcional si usas controlador
import com.inflesusventas.model.ComprobanteElectronico;
import com.inflesusventas.model.ProductoCotizacion;
import com.inflesusventas.service.ComprobanteService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ComprobanteListView extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private ComprobanteService comprobanteService;

    public ComprobanteListView(ComprobanteService comprobanteService) {
        this.comprobanteService = comprobanteService;
        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("Historial de Comprobantes Emitidos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(lblTitulo, BorderLayout.NORTH);

        // Columnas que pediste
        String[] columnas = {"ID", "Fecha", "RUC", "Razón Social", "Descripción (Items)", "Total"};
        
        model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(40); // Altura para que se vea bien
        
        // Renderizador para ver la lista de productos como tooltip si es muy larga
        table.getColumnModel().getColumn(4).setPreferredWidth(300);

        add(new JScrollPane(table), BorderLayout.CENTER);
        
        JButton btnRefrescar = new JButton("Actualizar Lista");
        btnRefrescar.addActionListener(e -> cargarDatos());
        JPanel pnlBtn = new JPanel();
        pnlBtn.add(btnRefrescar);
        add(pnlBtn, BorderLayout.SOUTH);
    }

    public void cargarDatos() {
        model.setRowCount(0);
        List<ComprobanteElectronico> lista = comprobanteService.listarTodos();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (ComprobanteElectronico c : lista) {
            // Generar descripción resumen
            StringBuilder descripcion = new StringBuilder();
            if (c.getItems() != null) {
                for (ProductoCotizacion p : c.getItems()) {
                    descripcion.append(p.getDescripcion()).append(" (x").append(p.getCantidad()).append("), ");
                }
            }
            // Quitar la última coma
            String descFinal = descripcion.length() > 2 ? descripcion.substring(0, descripcion.length() - 2) : "";

            model.addRow(new Object[]{
                c.getId(),
                c.getFechaEmision() != null ? c.getFechaEmision().format(formatter) : "-",
                c.getRucCliente(),
                c.getRazonSocialCliente(),
                descFinal,
                String.format("S/ %.2f", c.getTotal())
            });
        }
    }
}