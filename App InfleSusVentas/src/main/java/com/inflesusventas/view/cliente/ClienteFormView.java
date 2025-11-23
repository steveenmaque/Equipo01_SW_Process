package com.inflesusventas.view.cliente;

import javax.swing.*;
import java.awt.*;

public class ClienteFormView extends JDialog {
    public JTextField txtId, txtRuc, txtRazonSocial, txtDireccion, txtTelefono, txtEmail, txtContacto;
    public JButton btnGuardar, btnCancelar;

    public ClienteFormView(Window owner) {
        super(owner, "Datos del Cliente", ModalityType.APPLICATION_MODAL);
        setSize(450, 500);
        setLayout(new GridBagLayout());
        setLocationRelativeTo(owner);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // ID
        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("ID:"), gbc);
        txtId = new JTextField(); txtId.setEditable(false);
        gbc.gridx = 1; add(txtId, gbc);

        // RUC
        gbc.gridx = 0; gbc.gridy++; add(new JLabel("RUC / Documento:"), gbc);
        txtRuc = new JTextField(15);
        gbc.gridx = 1; add(txtRuc, gbc);

        // Razón Social
        gbc.gridx = 0; gbc.gridy++; add(new JLabel("Razón Social / Nombre:"), gbc);
        txtRazonSocial = new JTextField(15);
        gbc.gridx = 1; add(txtRazonSocial, gbc);

        // Dirección
        gbc.gridx = 0; gbc.gridy++; add(new JLabel("Dirección Fiscal:"), gbc);
        txtDireccion = new JTextField(15);
        gbc.gridx = 1; add(txtDireccion, gbc);

        // Contacto
        gbc.gridx = 0; gbc.gridy++; add(new JLabel("Persona de Contacto:"), gbc);
        txtContacto = new JTextField(15);
        gbc.gridx = 1; add(txtContacto, gbc);

        // Teléfono
        gbc.gridx = 0; gbc.gridy++; add(new JLabel("Teléfono:"), gbc);
        txtTelefono = new JTextField(15);
        gbc.gridx = 1; add(txtTelefono, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy++; add(new JLabel("Email Facturación:"), gbc);
        txtEmail = new JTextField(15);
        gbc.gridx = 1; add(txtEmail, gbc);

        // Botones
        JPanel panelBotones = new JPanel();
        btnGuardar = new JButton("Guardar Datos");
        btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        add(panelBotones, gbc);
    }
}