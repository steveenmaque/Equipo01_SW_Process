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

    // Componentes Paso 1: Configuración Inicial (Fijos)
    // cmbTipoRemitente y cmbMotivoTraslado eliminados, ahora son fijos.

    // Componentes Paso 2: Destinatario (Tipo Documento Fijo)
    private JTextField txtNumeroDocumento;
    private JTextField txtRazonSocial;

    // Componentes Paso 3: Bienes
    private JTable tablaBienes;
    private DefaultTableModel modeloTablaBienes;

    // Componentes Paso 4: Direcciones (Punto Partida Fijo)
    private JTextField txtPuntoLlegada;

    // Componentes Paso 5: Transporte (Tipo Transporte Fijo, sin Entidad)
    private JTextField txtPlaca;
    private JTextField txtNumeroLicencia;
    private JTextField txtNombreConductor; // Unificado para Nombre y Apellidos
    // private JTextField txtApellidosConductor; // ELIMINADO
    private JTextField txtDniConductor; // Mantenido
    private JSpinner spnFechaTraslado;

    // Valor fijo del Punto de Partida
    private static final String PUNTO_PARTIDA_FIJO = "Calle San Francisco Mz 1-4 Lt 13A CP. Zapallal Alto 1ra Etapa. Puente Piedra, Lima";


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

    // ========== SECCIÓN 1: Configuración Inicial (Valores Fijos) ==========
    private JPanel crearSeccionConfiguracion() {
        JPanel panelSeccion = crearPanelSeccion("1. Configuración Inicial");
        JPanel panelContenido = (JPanel) panelSeccion.getComponent(1);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Tipo de Remitente (FIJO)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblRemitente = new JLabel("Tipo de Remitente:");
        lblRemitente.setFont(new Font("Arial", Font.BOLD, 13));
        panelContenido.add(lblRemitente, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        JLabel lblRemitenteFijo = new JLabel("<html><b>REMITENTE</b></html>");
        lblRemitenteFijo.setForeground(COLOR_PRIMARIO);
        lblRemitenteFijo.setFont(new Font("Arial", Font.PLAIN, 13));
        panelContenido.add(lblRemitenteFijo, gbc);

        // Motivo de traslado (FIJO)
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblMotivo = new JLabel("Motivo de traslado:");
        lblMotivo.setFont(new Font("Arial", Font.BOLD, 13));
        panelContenido.add(lblMotivo, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        JLabel lblMotivoFijo = new JLabel("<html><b>VENTA</b></html>");
        lblMotivoFijo.setForeground(COLOR_PRIMARIO);
        lblMotivoFijo.setFont(new Font("Arial", Font.PLAIN, 13));
        panelContenido.add(lblMotivoFijo, gbc);

        return panelSeccion;
    }

    // ========== SECCIÓN 2: Destinatario (Tipo Documento Fijo) ==========
    private JPanel crearSeccionDestinatario() {
        JPanel panelSeccion = crearPanelSeccion("2. Datos del Destinatario");
        JPanel panelContenido = (JPanel) panelSeccion.getComponent(1);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Tipo de documento (FIJO: RUC)
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lblTipoDoc = new JLabel("Tipo de documento:");
        lblTipoDoc.setFont(new Font("Arial", Font.BOLD, 13));
        panelContenido.add(lblTipoDoc, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        JLabel lblTipoDocFijo = new JLabel("<html><b>RUC</b></html>");
        lblTipoDocFijo.setForeground(COLOR_PRIMARIO);
        lblTipoDocFijo.setFont(new Font("Arial", Font.PLAIN, 13));
        panelContenido.add(lblTipoDocFijo, gbc);

        row++;

        // Número de documento (VARIABLE)
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblNumDoc = new JLabel("Número de documento: *");
        lblNumDoc.setFont(new Font("Arial", Font.BOLD, 13));
        panelContenido.add(lblNumDoc, gbc);

        gbc.gridx = 1;
        txtNumeroDocumento = new JTextField(20);
        txtNumeroDocumento.setFont(new Font("Arial", Font.PLAIN, 13));
        panelContenido.add(txtNumeroDocumento, gbc);

        row++;

        // Razón Social (VARIABLE)
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblRazon = new JLabel("Razón Social / Nombre: *");
        lblRazon.setFont(new Font("Arial", Font.BOLD, 13));
        panelContenido.add(lblRazon, gbc);

        gbc.gridx = 1;
        txtRazonSocial = new JTextField(30);
        txtRazonSocial.setFont(new Font("Arial", Font.PLAIN, 13));
        panelContenido.add(txtRazonSocial, gbc);

        return panelSeccion;
    }

    // ========== SECCIÓN 3: Bienes a Trasladar ==========
    private JPanel crearSeccionBienes() {
        JPanel panel = crearPanelSeccion("3. Bienes a Trasladar");
        JPanel panelContenido = (JPanel) panel.getComponent(1);
        panelContenido.setLayout(new BorderLayout(10, 10));

        // Tabla
        String[] columnas = { "Código", "Descripción", "Cantidad", "Peso (KG)" };
        modeloTablaBienes = new DefaultTableModel(columnas, 0);
        tablaBienes = new JTable(modeloTablaBienes);
        tablaBienes.setRowHeight(28);
        tablaBienes.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaBienes.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tablaBienes.getTableHeader().setBackground(COLOR_PRIMARIO);
        tablaBienes.getTableHeader().setForeground(Color.BLACK); // Texto en NEGRO para contraste

        JScrollPane scroll = new JScrollPane(tablaBienes);
        scroll.setPreferredSize(new Dimension(0, 200));
        panelContenido.add(scroll, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotones.setBackground(Color.WHITE);

        JButton btnAgregar = new JButton("+ Agregar Bien");
        btnAgregar.setFont(new Font("Arial", Font.BOLD, 12));
        btnAgregar.setBackground(new Color(40, 167, 69));
        btnAgregar.setForeground(Color.BLACK);
        btnAgregar.setFocusPainted(false);
        btnAgregar.addActionListener(e -> agregarBien());

        JButton btnEliminar = new JButton("- Eliminar");
        btnEliminar.setFont(new Font("Arial", Font.BOLD, 12));
        btnEliminar.setBackground(new Color(220, 53, 69));
        btnEliminar.setForeground(Color.BLACK);
        btnEliminar.setFocusPainted(false);
        btnEliminar.addActionListener(e -> eliminarBien());

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);
        panelContenido.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    // ========== SECCIÓN 4: Puntos de Partida y Llegada (Partida Fijo) ==========
    private JPanel crearSeccionDirecciones() {
        JPanel panelSeccion = crearPanelSeccion("4. Puntos de Partida y Llegada");
        JPanel panelContenido = (JPanel) panelSeccion.getComponent(1);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Punto de partida (FIJO)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblPartida = new JLabel("Punto de Partida: *");
        lblPartida.setFont(new Font("Arial", Font.BOLD, 13));
        panelContenido.add(lblPartida, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        JLabel lblPartidaFija = new JLabel("<html><b>" + PUNTO_PARTIDA_FIJO + "</b></html>");
        lblPartidaFija.setFont(new Font("Arial", Font.PLAIN, 13));
        lblPartidaFija.setForeground(COLOR_PRIMARIO);
        panelContenido.add(lblPartidaFija, gbc);

        // Punto de llegada (VARIABLE)
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblLlegada = new JLabel("Punto de Llegada: *");
        lblLlegada.setFont(new Font("Arial", Font.BOLD, 13));
        panelContenido.add(lblLlegada, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        txtPuntoLlegada = new JTextField(40);
        txtPuntoLlegada.setFont(new Font("Arial", Font.PLAIN, 13));
        panelContenido.add(txtPuntoLlegada, gbc);

        return panelSeccion;
    }

    // ========== SECCIÓN 5: Datos de Transporte (Nombre/Apellido UNIFICADO, DNI separado) ==========
    private JPanel crearSeccionTransporte() {
        JPanel panelSeccion = crearPanelSeccion("5. Datos de Transporte");
        JPanel panelContenido = (JPanel) panelSeccion.getComponent(1);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Tipo de transporte (FIJO: PRIVADO)
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lblTipo = new JLabel("Tipo de transporte:");
        lblTipo.setFont(new Font("Arial", Font.BOLD, 13));
        panelContenido.add(lblTipo, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        JLabel lblTipoFijo = new JLabel("<html><b>TRANSPORTE PRIVADO</b></html>");
        lblTipoFijo.setForeground(COLOR_PRIMARIO);
        lblTipoFijo.setFont(new Font("Arial", Font.PLAIN, 13));
        panelContenido.add(lblTipoFijo, gbc);

        row++;

        // Placa
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblPlaca = new JLabel("Número de placa: *");
        lblPlaca.setFont(new Font("Arial", Font.BOLD, 13));
        panelContenido.add(lblPlaca, gbc);

        gbc.gridx = 1;
        txtPlaca = new JTextField(15);
        txtPlaca.setFont(new Font("Arial", Font.PLAIN, 13));
        panelContenido.add(txtPlaca, gbc);

        row++;

        // Número de licencia
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblLicencia = new JLabel("Número de licencia: *");
        lblLicencia.setFont(new Font("Arial", Font.BOLD, 13));
        panelContenido.add(lblLicencia, gbc);

        gbc.gridx = 1;
        txtNumeroLicencia = new JTextField(15);
        txtNumeroLicencia.setFont(new Font("Arial", Font.PLAIN, 13));
        panelContenido.add(txtNumeroLicencia, gbc);

        row++;

        // Nombre y Apellidos conductor (UNIFICADO)
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblNombre = new JLabel("Nombre y Apellidos del conductor: *");
        lblNombre.setFont(new Font("Arial", Font.BOLD, 13));
        panelContenido.add(lblNombre, gbc);

        gbc.gridx = 1;
        txtNombreConductor = new JTextField(40);
        txtNombreConductor.setFont(new Font("Arial", Font.PLAIN, 13));
        panelContenido.add(txtNombreConductor, gbc);

        row++;

        // DNI conductor (Mantenido)
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblDni = new JLabel("DNI del conductor: *");
        lblDni.setFont(new Font("Arial", Font.BOLD, 13));
        panelContenido.add(lblDni, gbc);

        gbc.gridx = 1;
        txtDniConductor = new JTextField(8);
        txtDniConductor.setFont(new Font("Arial", Font.PLAIN, 13));
        panelContenido.add(txtDniConductor, gbc);

        row++;

        // Fecha de inicio
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblFecha = new JLabel("Fecha de inicio de traslado:");
        lblFecha.setFont(new Font("Arial", Font.BOLD, 13));
        panelContenido.add(lblFecha, gbc);

        gbc.gridx = 1;
        spnFechaTraslado = new JSpinner(new SpinnerDateModel());
        spnFechaTraslado.setEditor(new JSpinner.DateEditor(spnFechaTraslado, "dd/MM/yyyy"));
        spnFechaTraslado.setValue(new Date());
        spnFechaTraslado.setFont(new Font("Arial", Font.PLAIN, 13));
        panelContenido.add(spnFechaTraslado, gbc);

        return panelSeccion;
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
        contenido.setLayout(new GridBagLayout());
        panel.add(contenido, BorderLayout.CENTER);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));

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

        // Sección 1 (Valores Fijos)
        guia.setTipoRemitente(TipoRemitente.REMITENTE);
        guia.setOperacionComercioExterior(false); // Eliminada la opción
        guia.setMotivoTraslado(MotivoTraslado.VENTA);

        // Sección 2 (Tipo Documento Fijo)
        guia.setTipoDocumentoDestinatario(TipoDocumento.RUC);
        guia.setNumeroDocumentoDestinatario(txtNumeroDocumento.getText().trim());
        guia.setRazonSocialDestinatario(txtRazonSocial.getText().trim());

        // Sección 4 (Punto de Partida Fijo)
        guia.setPuntoPartida(PUNTO_PARTIDA_FIJO);
        guia.setPuntoLlegada(txtPuntoLlegada.getText().trim());

        // Sección 5 (Nombre/Apellido unificado, DNI separado)
        DatosTransporte transporte = guia.getDatosTransporte();
        transporte.setTipoTransporte(TipoTransporte.TRANSPORTE_PRIVADO); // Valor Fijo
        transporte.setNumeroPlaca(txtPlaca.getText().trim());
        transporte.setEntidadAutorizacion(EntidadAutorizacion.MTC); // Se mantiene este valor en el modelo
        transporte.setNumeroLicencia(txtNumeroLicencia.getText().trim());

        // Nombre y Apellidos unificados
        transporte.setNombreConductor(txtNombreConductor.getText().trim());
        transporte.setApellidosConductor(""); // Se deja vacío
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
        // Punto de partida es fijo, solo validamos Llegada
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

        // Validar Nombre y Apellidos unificado
        if (txtNombreConductor.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese el nombre y apellidos del conductor",
                    "Campo Obligatorio",
                    JOptionPane.WARNING_MESSAGE);
            txtNombreConductor.requestFocus();
            return false;
        }

        // Validar DNI
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

            // Limpiar campos variables
            txtNumeroDocumento.setText("");
            txtRazonSocial.setText("");

            modeloTablaBienes.setRowCount(0);

            txtPuntoLlegada.setText("");

            txtPlaca.setText("");
            txtNumeroLicencia.setText("");

            // Campos de conductor
            txtNombreConductor.setText("");
            // txtApellidosConductor.setText(""); // ELIMINADO
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