package com.inflesusventas.view.notacredito;

import com.inflesusventas.util.ErrorHandler;
import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class NotaCreditoPaso1View extends JPanel {

    private JComboBox<String> cmbTipoNota;
    private JTextField txtFacturaRef;
    private JTextArea txtMotivo;
    private JSpinner spinnerFecha;

    public NotaCreditoPaso1View() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Título del paso
        JLabel lblTitulo = new JLabel("Paso 1: Datos Generales");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(lblTitulo, gbc);

        // Tipo de Nota de Crédito
        gbc.gridy++;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Tipo de Nota de Crédito:"), gbc);

        String[] tipos = {
                "Anulación de la Operación",
                "Anulación por Error en el RUC",
                "Corrección por error en la descripción",
                "Descuento Global",
                "Descuento por ítem",
                "Devolución Total",
                "Devolución por ítem",
                "Otros Conceptos",
                "Ajustes - montos y/o fechas de pago"
        };
        cmbTipoNota = new JComboBox<>(tipos);
        gbc.gridx = 1;
        formPanel.add(cmbTipoNota, gbc);

        // Factura de Referencia
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Factura de Referencia (Serie-Número):"), gbc);
        txtFacturaRef = new JTextField(20);
        // Valor por defecto para pruebas
        txtFacturaRef.setText("E001-00000045");
        gbc.gridx = 1;
        formPanel.add(txtFacturaRef, gbc);

        // Fecha de Emisión
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Fecha de Emisión:"), gbc);
        spinnerFecha = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy");
        spinnerFecha.setEditor(dateEditor);
        spinnerFecha.setValue(new Date());
        gbc.gridx = 1;
        formPanel.add(spinnerFecha, gbc);

        // Motivo / Sustento
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Motivo o Sustento:"), gbc);
        txtMotivo = new JTextArea(4, 30);
        txtMotivo.setLineWrap(true);
        txtMotivo.setWrapStyleWord(true);
        JScrollPane scrollMotivo = new JScrollPane(txtMotivo);
        gbc.gridx = 1;
        formPanel.add(scrollMotivo, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Panel informativo
        JTextArea info = new JTextArea(
                "Nota: Asegúrese de ingresar correctamente el número de la factura electrónica que desea modificar.\n" +
                        "El sistema validará que la factura exista en el historial local.");
        info.setEditable(false);
        info.setBackground(new Color(255, 255, 240));
        info.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        info.setMargin(new Insets(10, 10, 10, 10));
        add(info, BorderLayout.SOUTH);
    }

    public boolean validarCampos() {
        if (txtFacturaRef.getText().trim().isEmpty()) {
            ErrorHandler.mostrarAdvertencia(this, "Debe ingresar el número de factura de referencia.");
            return false;
        }
        if (txtMotivo.getText().trim().isEmpty()) {
            ErrorHandler.mostrarAdvertencia(this, "Debe ingresar el motivo o sustento.");
            return false;
        }
        return true;
    }

    public void limpiarFormulario() {
        txtFacturaRef.setText("");
        txtMotivo.setText("");
        cmbTipoNota.setSelectedIndex(0);
        spinnerFecha.setValue(new Date());
    }

    // Getters
    public String getTipoNotaCredito() {
        return (String) cmbTipoNota.getSelectedItem();
    }

    public String getNumeroFacturaRef() {
        return txtFacturaRef.getText().trim();
    }

    public Date getFechaEmision() {
        return (Date) spinnerFecha.getValue();
    }

    public String getMotivoSustento() {
        return txtMotivo.getText().trim();
    }
}
