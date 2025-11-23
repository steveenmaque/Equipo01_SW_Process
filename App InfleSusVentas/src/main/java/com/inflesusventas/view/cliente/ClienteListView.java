package com.inflesusventas.view.cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ClienteListView extends JPanel {
    public JTable tabla;
    public DefaultTableModel modelo;
    public JButton btnNuevo; // Lo mantenemos por si quieres registrar manual

    public ClienteListView() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Título actualizado
        JLabel lblTitulo = new JLabel("Registro de Clientes y sus Compras");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // NUEVAS COLUMNAS SEGÚN TU PEDIDO
        modelo = new DefaultTableModel();
        modelo.addColumn("Fecha");
        modelo.addColumn("RUC / DNI");
        modelo.addColumn("Cliente (Razón Social)");
        modelo.addColumn("Descripción (Lo que llevó)"); // <--- IMPORTANTE
        modelo.addColumn("Monto Pagado");               // <--- IMPORTANTE

        tabla = new JTable(modelo);
        tabla.setRowHeight(30); // Un poco más alto para leer bien
        
        // Hacer que la columna de descripción sea más ancha
        tabla.getColumnModel().getColumn(2).setPreferredWidth(200); // Cliente
        tabla.getColumnModel().getColumn(3).setPreferredWidth(350); // Descripción productos
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        add(scrollPane, BorderLayout.CENTER);

        // Panel Inferior (Simplificado)
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnNuevo = new JButton("Refrescar Lista"); // Cambiamos la función del botón
        btnNuevo.setBackground(new Color(15,65,116));
        btnNuevo.setForeground(Color.WHITE);
        
        panelBotones.add(btnNuevo);
        add(panelBotones, BorderLayout.SOUTH);
    }
}