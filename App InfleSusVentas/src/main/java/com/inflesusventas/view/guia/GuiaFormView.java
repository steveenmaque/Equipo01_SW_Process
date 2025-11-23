package com.inflesusventas.view.guia;

import com.inflesusventas.controller.GuiaRemisionController;
import com.inflesusventas.model.GuiaRemision;
import com.inflesusventas.model.GuiaRemision.*;
import com.inflesusventas.model.BienGuiaRemision;
import com.inflesusventas.model.DatosTransporte;
import com.inflesusventas.model.DatosTransporte.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Vista de formulario para Guías de Remisión - Todo en una sola vista
 * Ruta: src/main/java/com/inflesusventas/view/guia/GuiaFormView.java
 */
public class GuiaFormView extends JPanel {

    private static final Color COLOR_PRIMARIO = new Color(15, 65, 116);
    private static final Color COLOR_NARANJA = new Color(230, 130, 70);
    private static final Color COLOR_FONDO = new Color(248, 249, 250);

    private GuiaRemisionController controller;

    // Componentes Paso 1: Configuración Inicial
    private JComboBox<String> cmbTipoRemitente;
    private JRadioButton rdoComercioExteriorSi, rdoComercioExteriorNo;
    private JComboBox<MotivoTraslado> cmbMotivoTraslado;

    // Componentes Paso 2: Destinatario
    private JComboBox<TipoDocumento> cmbTipoDocumento;
    private JTextField txtNumeroDocumento;
    private JTextField txtRazonSocial;

    // Componentes Paso 5: Bienes
    private JTable tablaBienes;
    private DefaultTableModel modeloTablaBienes;

    // Componentes Paso 6: Direcciones
    private JTextField txtPuntoPartida;
    private JTextField txtPuntoLlegada;

    // Componentes Paso 7: Transporte
    private JComboBox<TipoTransporte> cmbTipoTransporte;
    private JTextField txtPlaca;
    private JComboBox<EntidadAutorizacion> cmbEntidadAutorizacion;
    private JTextField txtNumeroLicencia;
    private JTextField txtNombreConductor;
    private JTextField txtApellidosConductor;
    private JTextField txtDniConductor;
    private JSpinner spnFechaTraslado;

    public GuiaFormView(GuiaRemisionController controller) {
        this.controller = controller;
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel principal con todas las secciones
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBackground(Color.WHITE);

        // Agregar todas las secciones
        panelPrincipal.add(crearEncabezado());
        panelPrincipal.add(Box.createVerticalStrut(20));
        panelPrincipal.add(crearSeccionConfiguracion());
        panelPrincipal.add(Box.createVerticalStrut(15));
        panelPrincipal.add(crearSeccionDestinatario());
        panelPrincipal.add(Box.createVerticalStrut(15));
        panelPrincipal.add(crearSeccionBienes());
        panelPrincipal.add(Box.createVerticalStrut(15));
        panelPrincipal.add(crearSeccionDirecciones());
        panelPrincipal.add(Box.createVerticalStrut(15));
        panelPrincipal.add(crearSeccionTransporte());
        panelPrincipal.add(Box.createVerticalStrut(20));
        panelPrincipal.add(crearBotonGenerar());
        panelPrincipal.add(Box.createVerticalStrut(20));

        // Scroll pane para toda la vista
        JScrollPane scrollPane = new JScrollPane(panelPrincipal);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PRIMARIO);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitulo = new JLabel("GUÍA DE REMISIÓN ELECTRÓNICA");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblSubtitulo = new JLabel("Serie: " + controller.getSiguienteNumero());
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitulo.setForeground(Color.WHITE);
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel textos = new JPanel(new GridLayout(2, 1));
        textos.setBackground(COLOR_PRIMARIO);
        textos.add(lblTitulo);
        textos.add(lblSubtitulo);

        panel.add(textos, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        return panel;
    }

    // ========== SECCIÓN 1: Configuración Inicial ==========
    private JPanel crearSeccionConfiguracion() {
        JPanel panel = crearPanelSeccion("1. Configuración Inicial");
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Tipo de Remitente
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel lblRemitente = new JLabel("Seleccionar:");
        lblRemitente.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblRemitente, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        cmbTipoRemitente = new JComboBox<>(new String[] { "REMITENTE", "DESTINATARIO" });
        cmbTipoRemitente.setSelectedIndex(0);
        cmbTipoRemitente.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(cmbTipoRemitente, gbc);

        row++;

        // Operación de comercio exterior
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblComercio = new JLabel("Operación de comercio exterior:");
        lblComercio.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblComercio, gbc);

        gbc.gridx = 1;
        JPanel panelComercio = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelComercio.setBackground(Color.WHITE);
        rdoComercioExteriorSi = new JRadioButton("SI");
        rdoComercioExteriorNo = new JRadioButton("NO");
        rdoComercioExteriorNo.setSelected(true);
        ButtonGroup bgComercio = new ButtonGroup();
        bgComercio.add(rdoComercioExteriorSi);
        bgComercio.add(rdoComercioExteriorNo);
        panelComercio.add(rdoComercioExteriorSi);
        panelComercio.add(rdoComercioExteriorNo);
        panel.add(panelComercio, gbc);

        row++;

        // Motivo de traslado
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblMotivo = new JLabel("Motivo de traslado:");
        lblMotivo.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblMotivo, gbc);

        gbc.gridx = 1;
        cmbMotivoTraslado = new JComboBox<>(MotivoTraslado.values());
        cmbMotivoTraslado.setSelectedItem(MotivoTraslado.VENTA);
        cmbMotivoTraslado.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(cmbMotivoTraslado, gbc);

        return panel;
    }

    // ========== SECCIÓN 2: Destinatario ==========
    private JPanel crearSeccionDestinatario() {
        JPanel panel = crearPanelSeccion("2. Datos del Destinatario");
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Tipo de documento
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel lblTipoDoc = new JLabel("Tipo de documento:");
        lblTipoDoc.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblTipoDoc, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        cmbTipoDocumento = new JComboBox<>(TipoDocumento.values());
        cmbTipoDocumento.setSelectedItem(TipoDocumento.RUC);
        cmbTipoDocumento.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(cmbTipoDocumento, gbc);

        row++;

        // Número de documento
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblNumDoc = new JLabel("Número de documento: *");
        lblNumDoc.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblNumDoc, gbc);

        gbc.gridx = 1;
        txtNumeroDocumento = new JTextField(20);
        txtNumeroDocumento.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(txtNumeroDocumento, gbc);

        row++;

        // Razón Social
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblRazon = new JLabel("Razón Social / Nombre: *");
        lblRazon.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblRazon, gbc);

        gbc.gridx = 1;
        txtRazonSocial = new JTextField(30);
        txtRazonSocial.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(txtRazonSocial, gbc);

        return panel;
    }

    // ========== SECCIÓN 3: Bienes a Trasladar ==========
    private JPanel crearSeccionBienes() {
        JPanel panel = crearPanelSeccion("3. Bienes a Trasladar");
        panel.setLayout(new BorderLayout(10, 10));

        // Tabla
        String[] columnas = { "Código", "Descripción", "Cantidad", "Peso (KG)" };
        modeloTablaBienes = new DefaultTableModel(columnas, 0);
        tablaBienes = new JTable(modeloTablaBienes);
        tablaBienes.setRowHeight(28);
        tablaBienes.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaBienes.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tablaBienes.getTableHeader().setBackground(COLOR_PRIMARIO);
        tablaBienes.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(tablaBienes);
        scroll.setPreferredSize(new Dimension(0, 200));
        panel.add(scroll, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotones.setBackground(Color.WHITE);

        JButton btnAgregar = new JButton("+ Agregar Bien");
        btnAgregar.setFont(new Font("Arial", Font.BOLD, 12));
        btnAgregar.setBackground(new Color(40, 167, 69));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFocusPainted(false);
        btnAgregar.addActionListener(e -> agregarBien());

        JButton btnEliminar = new JButton("- Eliminar");
        btnEliminar.setFont(new Font("Arial", Font.BOLD, 12));
        btnEliminar.setBackground(new Color(220, 53, 69));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.addActionListener(e -> eliminarBien());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);
        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    // ========== SECCIÓN 4: Direcciones ==========
    private JPanel crearSeccionDirecciones() {
        JPanel panel = crearPanelSeccion("4. Puntos de Partida y Llegada");
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Punto de partida
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel lblPartida = new JLabel("Punto de Partida: *");
        lblPartida.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblPartida, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtPuntoPartida = new JTextField(40);
        txtPuntoPartida.setFont(new Font("Arial", Font.PLAIN, 13));
        txtPuntoPartida.setText("Av. Ejemplo 123, Lima, Perú"); // Valor por defecto
        panel.add(txtPuntoPartida, gbc);

        // Punto de llegada
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblLlegada = new JLabel("Punto de Llegada: *");
        lblLlegada.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblLlegada, gbc);

        gbc.gridx = 1;
        txtPuntoLlegada = new JTextField(40);
        txtPuntoLlegada.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(txtPuntoLlegada, gbc);

        return panel;
    }

    // ========== SECCIÓN 5: Transporte ==========
    private JPanel crearSeccionTransporte() {
        JPanel panel = crearPanelSeccion("5. Datos de Transporte");
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Tipo de transporte
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel lblTipo = new JLabel("Tipo de transporte:");
        lblTipo.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblTipo, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        cmbTipoTransporte = new JComboBox<>(TipoTransporte.values());
        cmbTipoTransporte.setSelectedItem(TipoTransporte.TRANSPORTE_PRIVADO);
        cmbTipoTransporte.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(cmbTipoTransporte, gbc);

        row++;

        // Placa
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblPlaca = new JLabel("Número de placa: *");
        lblPlaca.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblPlaca, gbc);

        gbc.gridx = 1;
        txtPlaca = new JTextField(15);
        txtPlaca.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(txtPlaca, gbc);

        row++;

        // Entidad autorización
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblEntidad = new JLabel("Entidad emisora:");
        lblEntidad.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblEntidad, gbc);

        gbc.gridx = 1;
        cmbEntidadAutorizacion = new JComboBox<>(EntidadAutorizacion.values());
        cmbEntidadAutorizacion.setSelectedItem(EntidadAutorizacion.MTC);
        cmbEntidadAutorizacion.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(cmbEntidadAutorizacion, gbc);

        row++;

        // Número de licencia
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblLicencia = new JLabel("Número de licencia: *");
        lblLicencia.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblLicencia, gbc);

        gbc.gridx = 1;
        txtNumeroLicencia = new JTextField(15);
        txtNumeroLicencia.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(txtNumeroLicencia, gbc);

        row++;

        // Nombre conductor
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblNombre = new JLabel("Nombre del conductor: *");
        lblNombre.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblNombre, gbc);

        gbc.gridx = 1;
        txtNombreConductor = new JTextField(20);
        txtNombreConductor.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(txtNombreConductor, gbc);

        row++;

        // Apellidos conductor
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblApellidos = new JLabel("Apellidos del conductor: *");
        lblApellidos.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblApellidos, gbc);

        gbc.gridx = 1;
        txtApellidosConductor = new JTextField(20);
        txtApellidosConductor.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(txtApellidosConductor, gbc);

        row++;

        // DNI conductor
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblDni = new JLabel("DNI del conductor: *");
        lblDni.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblDni, gbc);

        gbc.gridx = 1;
        txtDniConductor = new JTextField(8);
        txtDniConductor.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(txtDniConductor, gbc);

        row++;

        // Fecha de inicio
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lblFecha = new JLabel("Fecha de inicio de traslado:");
        lblFecha.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(lblFecha, gbc);

        gbc.gridx = 1;
        spnFechaTraslado = new JSpinner(new SpinnerDateModel());
        spnFechaTraslado.setEditor(new JSpinner.DateEditor(spnFechaTraslado, "dd/MM/yyyy"));
        spnFechaTraslado.setValue(new Date());
        spnFechaTraslado.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(spnFechaTraslado, gbc);

        return panel;
    }

    // ========== Botón de Generar ==========
    private JPanel crearBotonGenerar() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);

        JButton btnGenerar = new JButton("GENERAR GUÍA DE REMISIÓN");
        btnGenerar.setFont(new Font("Arial", Font.BOLD, 18));
        btnGenerar.setPreferredSize(new Dimension(400, 50));
        btnGenerar.setBackground(COLOR_NARANJA);
        btnGenerar.setForeground(Color.BLACK);
        btnGenerar.setFocusPainted(false);
        btnGenerar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGenerar.addActionListener(e -> generarGuia());

        panel.add(btnGenerar);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        return panel;
    }

    // ========== Métodos Auxiliares ==========

    private JPanel crearPanelSeccion(String titulo) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_FONDO, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(COLOR_PRIMARIO);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(lblTitulo, BorderLayout.NORTH);

        JPanel contenido = new JPanel();
        contenido.setBackground(Color.WHITE);
        panel.add(contenido, BorderLayout.CENTER);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        return panel;
    }

    private void agregarBien() {
        // Diálogo para agregar bien
        JPanel dialogPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField txtCodigo = new JTextField();
        JTextField txtDescripcion = new JTextField();
        JSpinner spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        JTextField txtPeso = new JTextField();

        dialogPanel.add(new JLabel("Código del bien:"));
        dialogPanel.add(txtCodigo);
        dialogPanel.add(new JLabel("Descripción:"));
        dialogPanel.add(txtDescripcion);
        dialogPanel.add(new JLabel("Cantidad:"));
        dialogPanel.add(spnCantidad);
        dialogPanel.add(new JLabel("Peso bruto (KG):"));
        dialogPanel.add(txtPeso);

        int result = JOptionPane.showConfirmDialog(this, dialogPanel,
                "Agregar Bien", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String codigo = txtCodigo.getText().trim();
                String descripcion = txtDescripcion.getText().trim();
                int cantidad = (Integer) spnCantidad.getValue();
                double peso = Double.parseDouble(txtPeso.getText().trim());

                if (codigo.isEmpty() || descripcion.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Código y descripción son obligatorios",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                controller.agregarBien(codigo, descripcion, cantidad, peso);
                modeloTablaBienes.addRow(new Object[] { codigo, descripcion, cantidad, peso });

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Peso debe ser un número válido",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarBien() {
        int fila = tablaBienes.getSelectedRow();
        if (fila != -1) {
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar el bien seleccionado?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                controller.eliminarBien(fila);
                modeloTablaBienes.removeRow(fila);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un bien de la tabla",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void generarGuia() {
        // Validar campos obligatorios
        if (!validarCampos()) {
            return;
        }

        // Guardar todos los datos en el modelo
        GuiaRemision guia = controller.getGuiaActual();

        // Sección 1
        guia.setTipoRemitente(
                cmbTipoRemitente.getSelectedItem().equals("REMITENTE")
                        ? TipoRemitente.REMITENTE
                        : TipoRemitente.DESTINATARIO);
        guia.setOperacionComercioExterior(rdoComercioExteriorSi.isSelected());
        guia.setMotivoTraslado((MotivoTraslado) cmbMotivoTraslado.getSelectedItem());

        // Sección 2
        guia.setTipoDocumentoDestinatario((TipoDocumento) cmbTipoDocumento.getSelectedItem());
        guia.setNumeroDocumentoDestinatario(txtNumeroDocumento.getText().trim());
        guia.setRazonSocialDestinatario(txtRazonSocial.getText().trim());

        // Sección 4
        guia.setPuntoPartida(txtPuntoPartida.getText().trim());
        guia.setPuntoLlegada(txtPuntoLlegada.getText().trim());

        // Sección 5
        DatosTransporte transporte = guia.getDatosTransporte();
        transporte.setTipoTransporte((TipoTransporte) cmbTipoTransporte.getSelectedItem());
        transporte.setNumeroPlaca(txtPlaca.getText().trim());
        transporte.setEntidadAutorizacion((EntidadAutorizacion) cmbEntidadAutorizacion.getSelectedItem());
        transporte.setNumeroLicencia(txtNumeroLicencia.getText().trim());
        transporte.setNombreConductor(txtNombreConductor.getText().trim());
        transporte.setApellidosConductor(txtApellidosConductor.getText().trim());
        transporte.setDniConductor(txtDniConductor.getText().trim());

        Date fecha = (Date) spnFechaTraslado.getValue();
        LocalDate localDate = fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        transporte.setFechaInicioTraslado(localDate);

        // Datos de la empresa (remitente)
        guia.setRucRemitente("20123456789"); // TODO: Cargar desde configuración
        guia.setRazonSocialRemitente("InfleSusVentas SRL");

        // Generar documentos
        boolean exito = controller.generarDocumentos();

        if (exito) {
            JOptionPane.showMessageDialog(this,
                    "¡Guía de Remisión generada exitosamente!\n\n" +
                            "Serie-Número: " + guia.getSerieNumero() + "\n" +
                            "Destinatario: " + guia.getRazonSocialDestinatario() + "\n" +
                            "Total de bienes: " + guia.getCantidadTotalBienes() + "\n" +
                            "Peso total: " + String.format("%.2f", guia.getPesoTotalCarga()) + " KG",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error al generar la guía de remisión.\n" +
                            "Verifique que todos los campos obligatorios estén completos.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCampos() {
        // Validar destinatario
        if (txtNumeroDocumento.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese el número de documento del destinatario",
                    "Campo Obligatorio",
                    JOptionPane.WARNING_MESSAGE);
            txtNumeroDocumento.requestFocus();
            return false;
        }

        if (txtRazonSocial.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese la razón social del destinatario",
                    "Campo Obligatorio",
                    JOptionPane.WARNING_MESSAGE);
            txtRazonSocial.requestFocus();
            return false;
        }

        // Validar bienes
        if (modeloTablaBienes.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Debe agregar al menos un bien a trasladar",
                    "Campo Obligatorio",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validar direcciones
        if (txtPuntoPartida.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese el punto de partida",
                    "Campo Obligatorio",
                    JOptionPane.WARNING_MESSAGE);
            txtPuntoPartida.requestFocus();
            return false;
        }

        if (txtPuntoLlegada.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese el punto de llegada",
                    "Campo Obligatorio",
                    JOptionPane.WARNING_MESSAGE);
            txtPuntoLlegada.requestFocus();
            return false;
        }

        // Validar transporte
        if (txtPlaca.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese el número de placa del vehículo",
                    "Campo Obligatorio",
                    JOptionPane.WARNING_MESSAGE);
            txtPlaca.requestFocus();
            return false;
        }

        if (txtNumeroLicencia.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese el número de licencia del conductor",
                    "Campo Obligatorio",
                    JOptionPane.WARNING_MESSAGE);
            txtNumeroLicencia.requestFocus();
            return false;
        }

        if (txtNombreConductor.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese el nombre del conductor",
                    "Campo Obligatorio",
                    JOptionPane.WARNING_MESSAGE);
            txtNombreConductor.requestFocus();
            return false;
        }

        if (txtApellidosConductor.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese los apellidos del conductor",
                    "Campo Obligatorio",
                    JOptionPane.WARNING_MESSAGE);
            txtApellidosConductor.requestFocus();
            return false;
        }

        if (txtDniConductor.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese el DNI del conductor",
                    "Campo Obligatorio",
                    JOptionPane.WARNING_MESSAGE);
            txtDniConductor.requestFocus();
            return false;
        }

        // Validar formato de DNI (8 dígitos)
        if (!txtDniConductor.getText().trim().matches("\\d{8}")) {
            JOptionPane.showMessageDialog(this,
                    "El DNI debe tener 8 dígitos",
                    "Formato Incorrecto",
                    JOptionPane.WARNING_MESSAGE);
            txtDniConductor.requestFocus();
            return false;
        }

        return true;
    }

    private void limpiarFormulario() {
        // Confirmar si desea crear una nueva guía
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Desea crear una nueva guía de remisión?",
                "Nueva Guía",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            // Reiniciar controlador
            controller.iniciarNuevaGuia();

            // Limpiar campos
            cmbTipoRemitente.setSelectedIndex(0);
            rdoComercioExteriorNo.setSelected(true);
            cmbMotivoTraslado.setSelectedItem(MotivoTraslado.VENTA);

            cmbTipoDocumento.setSelectedItem(TipoDocumento.RUC);
            txtNumeroDocumento.setText("");
            txtRazonSocial.setText("");

            modeloTablaBienes.setRowCount(0);

            txtPuntoPartida.setText("Av. Ejemplo 123, Lima, Perú");
            txtPuntoLlegada.setText("");

            cmbTipoTransporte.setSelectedItem(TipoTransporte.TRANSPORTE_PRIVADO);
            txtPlaca.setText("");
            cmbEntidadAutorizacion.setSelectedItem(EntidadAutorizacion.MTC);
            txtNumeroLicencia.setText("");
            txtNombreConductor.setText("");
            txtApellidosConductor.setText("");
            txtDniConductor.setText("");
            spnFechaTraslado.setValue(new Date());

            // Actualizar número de serie en el encabezado
            Component[] components = getComponents();
            if (components.length > 0 && components[0] instanceof JScrollPane) {
                JScrollPane scroll = (JScrollPane) components[0];
                JViewport viewport = scroll.getViewport();
                if (viewport.getView() instanceof JPanel) {
                    JPanel panelPrincipal = (JPanel) viewport.getView();
                    Component[] children = panelPrincipal.getComponents();
                    if (children.length > 0 && children[0] instanceof JPanel) {
                        JPanel encabezado = (JPanel) children[0];
                        Component centerComp = ((BorderLayout) encabezado.getLayout())
                                .getLayoutComponent(BorderLayout.CENTER);
                        if (centerComp instanceof JPanel) {
                            JPanel textos = (JPanel) centerComp;
                            Component[] labels = textos.getComponents();
                            if (labels.length > 1 && labels[1] instanceof JLabel) {
                                JLabel lblSubtitulo = (JLabel) labels[1];
                                lblSubtitulo.setText("Serie: " + controller.getSiguienteNumero());
                            }
                        }
                    }
                }
            }

            System.out.println("✓ Formulario limpiado - Nueva guía: " + controller.getSiguienteNumero());
        }
    }
}