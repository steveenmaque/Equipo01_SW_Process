package com.inflesusventas.view.comprobante;

import com.inflesusventas.controller.CotizacionController;
import com.inflesusventas.model.Cotizacion;
import com.inflesusventas.model.ProductoCotizacion;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Vista para crear facturas a partir de la cotización actual en memoria.
 * Reescrita para seguir estilo de CotizacionFormView (panel principal con secciones apiladas).
 */
public class ComprobanteFormView extends JPanel {

    private static final Color COLOR_PRIMARIO = new Color(15,65,116);
    private static final Color COLOR_FONDO = new Color(248,249,250);
    private static final Color COLOR_NARANJA = new Color(230,130,70);

    private final CotizacionController cotizacionController;

    // Tabla de productos
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private double subtotalConIgvCotizacion = 0.0; // subtotal con IGV de todos los productos

    // componentes faltantes referenciados en la clase
    private JRadioButton rdoDetraccionSi, rdoDetraccionNo;
    private JTextField txtRucCliente;
    private JTextField txtRazonSocial;
    private JRadioButton rdoPagoAnticipadoSi, rdoPagoAnticipadoNo;
    private JRadioButton rdoEmisorItineranteSi, rdoEmisorItineranteNo;
    private JRadioButton rdoEstablecimientoSi, rdoEstablecimientoNo;
    private JRadioButton rdoConsigneDireccionSi, rdoConsigneDireccionNo;
    private JRadioButton rdoVentaCombustibleSi, rdoVentaCombustibleNo;
    private JComboBox<String> cmbMoneda;
    private JRadioButton rdoDescuentosSi, rdoDescuentosNo;
    private JRadioButton rdoIscSi, rdoIscNo;
    private JRadioButton rdoOperacionesGratuitasSi, rdoOperacionesGratuitasNo;
    private JRadioButton rdoCargosTributosSi, rdoCargosTributosNo;

    // Sección 2 - dinámicos / control de items
    private JSpinner spFechaEmision;
    private JPanel itemsContainer;
    private java.util.List<JPanel> listaItems = new java.util.ArrayList<>();
    private JScrollPane scrollTablaProductos;

    // referencias para mostrar valores / impuestos

    // Paneles / secciones
    private JPanel panelSeccion1;
    private JPanel panelSeccion2;
    // Selector de forma de pago en Sección 1
    private JComboBox<String> cmbFormaPagoSelector;
    // Sección 3 - Detracciones / Crédito
    private JPanel panelSeccion3;
    // Botón para generar comprobante (no hace nada por ahora)
    private JButton btnGenerarComprobante;

    // Campos Sección 3
    private JLabel lblObservaciones; // "SUJETO A DETRACCIÓN"
    private JLabel lblDetSubtitulo;
    private JLabel lblTipoOperacion;
    private JLabel lblCodigoBienServicio;
    private JTextField txtNroCuentaBN;
    private JLabel lblMedioPago;
    private JTextField txtPorcentajeDetraccion; // "12.00"
    private JTextField txtMontoDetraccion; // editable (redondeado entero por defecto)

    // Crédito
    private JLabel lblInfoCreditoSub;
    private JTextField txtMontoNetoPendiente; // editable
    private JSpinner spFechaVencimiento;
    private JTextField txtMontoCuota;
    // Labels para controlar visibilidad junto a los campos de crédito
    private JLabel lblVencLabel;
    private JLabel lblMontoCuotaLabel;

    public ComprobanteFormView(CotizacionController cotizacionController) {
        this.cotizacionController = cotizacionController;
        inicializarComponentes();
        cargarProductosDesdeCotizacion();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        // Panel principal con scroll y secciones verticales (estilo CotizacionFormView)
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBackground(Color.WHITE);

        panelPrincipal.add(crearEncabezado());
        panelPrincipal.add(Box.createVerticalStrut(12));
        panelPrincipal.add(crearSeccionTablaProductos());
        panelPrincipal.add(Box.createVerticalStrut(12));

        // secciones placeholders (se mostraran al seleccionar una fila)
        panelSeccion1 = crearSeccion1();
        panelSeccion2 = crearSeccion2(); // ya existente
        panelSeccion3 = crearSeccion3(); // nueva implementación
        panelPrincipal.add(panelSeccion1);
        panelPrincipal.add(Box.createVerticalStrut(12));
        panelPrincipal.add(panelSeccion2);
        panelPrincipal.add(Box.createVerticalStrut(12));
        panelPrincipal.add(panelSeccion3);
        // Botón central para generar comprobante electrónico (sin acción por ahora)
        JPanel botonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        botonPanel.setBackground(Color.WHITE);
        btnGenerarComprobante = new JButton("GENERAR COMPROBANTE ELECTRÓNICO");
        btnGenerarComprobante.setFont(new Font("Arial", Font.BOLD, 14));
        btnGenerarComprobante.setPreferredSize(new Dimension(340, 38));
        btnGenerarComprobante.addActionListener(e -> {
            // no-op por el momento
        });
        botonPanel.add(btnGenerarComprobante);
        panelPrincipal.add(Box.createVerticalStrut(12));
        panelPrincipal.add(botonPanel);
        panelPrincipal.add(Box.createVerticalStrut(20));

        JScrollPane scroll = new JScrollPane(panelPrincipal);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // Ocultar secciones hasta seleccionar
        mostrarSecciones(false);
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PRIMARIO);
        panel.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        JLabel lblTitulo = new JLabel("EMISIÓN DE COMPROBANTES");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblTitulo, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        return panel;
    }

    private JPanel crearSeccionTablaProductos() {
        JPanel panel = crearPanelSeccion("Productos de la Cotización - Selecciona para procesar");

        // columnas incluyendo RUC cliente
        String[] columnas = {"Código", "Descripción", "RUC Cliente", "Cantidad", "Unidad", "P. Unit. (S/)", "Subtotal (S/)", "Condición"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaProductos.setRowHeight(26);
        tablaProductos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        JScrollPane scrollTabla = new JScrollPane(tablaProductos);
        // Guardamos referencia y ajustamos alto según filas
        scrollTablaProductos = scrollTabla;
        ajustarAltoTabla();
        panel.add(scrollTablaProductos, BorderLayout.CENTER);

        // Listener selección - cuando seleccione cualquier fila, procesa TODA la cotización
        tablaProductos.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int fila = tablaProductos.getSelectedRow();
                    boolean seleccionado = fila != -1;
                    mostrarSecciones(seleccionado);
                    if (seleccionado) {
                        poblarSeccion1DesdeCotizacion();
                        poblarSeccion2SegunCondicion();
                        // actualizar sección 3 con los valores del subtotal total de la cotización
                        actualizarSeccion3();
                    }
                }
            }
        });

        return panel;
    }

    private JPanel crearSeccion1() {
        // Creamos panel sin título (el usuario pidió eliminar el título de la primera sección)
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_FONDO, 2),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 420));

        GridBagConstraints gbcMainHeader = new GridBagConstraints();
        gbcMainHeader.insets = new Insets(6, 6, 12, 6);
        gbcMainHeader.fill = GridBagConstraints.HORIZONTAL;
        gbcMainHeader.anchor = GridBagConstraints.WEST;
        // Selector de forma de pago (DECIDE SINGLE / DOUBLE)
        JLabel lblForma = new JLabel("Forma de pago:");
        lblForma.setFont(new Font("Arial", Font.BOLD, 14));
        gbcMainHeader.gridx = 0;
        gbcMainHeader.gridy = 0;
        gbcMainHeader.weightx = 0.25;
        panel.add(lblForma, gbcMainHeader);

        cmbFormaPagoSelector = new JComboBox<>(new String[] {"CONTADO", "CREDITO"});
        cmbFormaPagoSelector.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbFormaPagoSelector.setSelectedItem("CONTADO");
        gbcMainHeader.gridx = 1;
        gbcMainHeader.gridy = 0;
        gbcMainHeader.weightx = 0.75;
        gbcMainHeader.gridwidth = 3;
        panel.add(cmbFormaPagoSelector, gbcMainHeader);
        gbcMainHeader.gridwidth = 1;

        // Cuando cambie el selector, si hay fila seleccionada, actualizar secciones 2 y 3
        cmbFormaPagoSelector.addActionListener(e -> {
            if (tablaProductos != null && tablaProductos.getSelectedRow() != -1) {
                poblarSeccion2SegunCondicion();
            }
            // siempre actualizar sección 3 (muestra/oculta info de crédito)
            actualizarSeccion3();
        });

        // Ajustar grid start row para el resto de inputs (comenzar en row = 1)
        // reusamos la lógica siguiente para añadir el resto de campos (continuará abajo)
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        Font labelFont = new Font("Arial", Font.PLAIN, 14);
        int row = 1;

        // ahora añadimos el resto de campos exactamente como estaban (iniciando en row=1)

        // Detracción
        JLabel lblDet = new JLabel("Operaciones sujetas a detracción:");
        lblDet.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        panel.add(lblDet, gbc);

        rdoDetraccionSi = new JRadioButton("SI");
        rdoDetraccionNo = new JRadioButton("NO");
        rdoDetraccionSi.setFont(labelFont);
        rdoDetraccionNo.setFont(labelFont);
        ButtonGroup bgDet = new ButtonGroup();
        bgDet.add(rdoDetraccionSi);
        bgDet.add(rdoDetraccionNo);
        rdoDetraccionSi.setSelected(true);

        JPanel pnlDet = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlDet.setBackground(Color.WHITE);
        pnlDet.add(rdoDetraccionSi);
        pnlDet.add(rdoDetraccionNo);
        pnlDet.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.weightx = 0.65;
        panel.add(pnlDet, gbc);

        // RUC cliente
        JLabel lblRuc = new JLabel("RUC del Cliente:");
        lblRuc.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        panel.add(lblRuc, gbc);

        txtRucCliente = new JTextField();
        txtRucCliente.setFont(labelFont);
        txtRucCliente.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.weightx = 0.65;
        panel.add(txtRucCliente, gbc);

        // Razón social
        JLabel lblRazon = new JLabel("Razón social:");
        lblRazon.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        panel.add(lblRazon, gbc);

        txtRazonSocial = new JTextField();
        txtRazonSocial.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = row++;
        gbc.weightx = 0.65;
        panel.add(txtRazonSocial, gbc);

        // Pago anticipado
        JLabel lblPago = new JLabel("Pago Anticipado:");
        lblPago.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        panel.add(lblPago, gbc);

        rdoPagoAnticipadoSi = new JRadioButton("SI");
        rdoPagoAnticipadoNo = new JRadioButton("NO");
        rdoPagoAnticipadoSi.setFont(labelFont);
        rdoPagoAnticipadoNo.setFont(labelFont);
        ButtonGroup bgPagoAnt = new ButtonGroup();
        bgPagoAnt.add(rdoPagoAnticipadoSi);
        bgPagoAnt.add(rdoPagoAnticipadoNo);
        rdoPagoAnticipadoNo.setSelected(true);

        JPanel pnlPago = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlPago.setBackground(Color.WHITE);
        pnlPago.add(rdoPagoAnticipadoSi);
        pnlPago.add(rdoPagoAnticipadoNo);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(pnlPago, gbc);

        // Emisor itinerante
        JLabel lblIt = new JLabel("Emisor Itinerante:");
        lblIt.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(lblIt, gbc);

        rdoEmisorItineranteSi = new JRadioButton("SI");
        rdoEmisorItineranteNo = new JRadioButton("NO");
        rdoEmisorItineranteSi.setFont(labelFont);
        rdoEmisorItineranteNo.setFont(labelFont);
        ButtonGroup bgIt = new ButtonGroup();
        bgIt.add(rdoEmisorItineranteSi);
        bgIt.add(rdoEmisorItineranteNo);
        rdoEmisorItineranteNo.setSelected(true);

        JPanel pnlIt = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlIt.setBackground(Color.WHITE);
        pnlIt.add(rdoEmisorItineranteSi);
        pnlIt.add(rdoEmisorItineranteNo);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(pnlIt, gbc);

        // Establecimiento del emisor
        JLabel lblEst = new JLabel("Establecimiento del Emisor:");
        lblEst.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(lblEst, gbc);

        rdoEstablecimientoSi = new JRadioButton("SI");
        rdoEstablecimientoNo = new JRadioButton("NO");
        rdoEstablecimientoSi.setFont(labelFont);
        rdoEstablecimientoNo.setFont(labelFont);
        ButtonGroup bgEst = new ButtonGroup();
        bgEst.add(rdoEstablecimientoSi);
        bgEst.add(rdoEstablecimientoNo);
        rdoEstablecimientoNo.setSelected(true);

        JPanel pnlEst = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlEst.setBackground(Color.WHITE);
        pnlEst.add(rdoEstablecimientoSi);
        pnlEst.add(rdoEstablecimientoNo);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(pnlEst, gbc);

        // Consigne direccion
        JLabel lblCons = new JLabel("Consigne la dirección donde se brinde el servicio:");
        lblCons.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(lblCons, gbc);

        rdoConsigneDireccionSi = new JRadioButton("SI");
        rdoConsigneDireccionNo = new JRadioButton("NO");
        rdoConsigneDireccionSi.setFont(labelFont);
        rdoConsigneDireccionNo.setFont(labelFont);
        ButtonGroup bgCons = new ButtonGroup();
        bgCons.add(rdoConsigneDireccionSi);
        bgCons.add(rdoConsigneDireccionNo);
        rdoConsigneDireccionSi.setSelected(true);

        JPanel pnlCons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlCons.setBackground(Color.WHITE);
        pnlCons.add(rdoConsigneDireccionSi);
        pnlCons.add(rdoConsigneDireccionNo);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(pnlCons, gbc);

        // Venta combustible
        JLabel lblComb = new JLabel("Venta de combustible / mantenimiento vehicular:");
        lblComb.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(lblComb, gbc);

        rdoVentaCombustibleSi = new JRadioButton("SI");
        rdoVentaCombustibleNo = new JRadioButton("NO");
        rdoVentaCombustibleSi.setFont(labelFont);
        rdoVentaCombustibleNo.setFont(labelFont);
        ButtonGroup bgComb = new ButtonGroup();
        bgComb.add(rdoVentaCombustibleSi);
        bgComb.add(rdoVentaCombustibleNo);
        rdoVentaCombustibleNo.setSelected(true);

        JPanel pnlComb = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlComb.setBackground(Color.WHITE);
        pnlComb.add(rdoVentaCombustibleSi);
        pnlComb.add(rdoVentaCombustibleNo);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(pnlComb, gbc);

        // Moneda
        JLabel lblMon = new JLabel("Moneda de la factura:");
        lblMon.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(lblMon, gbc);

        cmbMoneda = new JComboBox<>(new String[] {"SOLES", "DÓLARES", "EUROS"});
        cmbMoneda.setSelectedItem("SOLES");
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(cmbMoneda, gbc);

        // Descuentos
        JLabel lblDesc = new JLabel("La factura tiene Descuentos / Deduce anticipos:");
        lblDesc.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(lblDesc, gbc);

        rdoDescuentosSi = new JRadioButton("SI");
        rdoDescuentosNo = new JRadioButton("NO");
        rdoDescuentosSi.setFont(labelFont);
        rdoDescuentosNo.setFont(labelFont);
        ButtonGroup bgDesc = new ButtonGroup();
        bgDesc.add(rdoDescuentosSi);
        bgDesc.add(rdoDescuentosNo);
        rdoDescuentosNo.setSelected(true);

        JPanel pnlDesc = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlDesc.setBackground(Color.WHITE);
        pnlDesc.add(rdoDescuentosSi);
        pnlDesc.add(rdoDescuentosNo);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(pnlDesc, gbc);

        // ISC
        JLabel lblIsc = new JLabel("La factura tiene ISC:");
        lblIsc.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(lblIsc, gbc);

        rdoIscSi = new JRadioButton("SI");
        rdoIscNo = new JRadioButton("NO");
        rdoIscSi.setFont(labelFont);
        rdoIscNo.setFont(labelFont);
        ButtonGroup bgIsc = new ButtonGroup();
        bgIsc.add(rdoIscSi);
        bgIsc.add(rdoIscNo);
        rdoIscNo.setSelected(true);

        JPanel pnlIsc = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlIsc.setBackground(Color.WHITE);
        pnlIsc.add(rdoIscSi);
        pnlIsc.add(rdoIscNo);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(pnlIsc, gbc);

        // Operaciones gratuitas
        JLabel lblGrat = new JLabel("Operaciones Gratuitas:");
        lblGrat.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(lblGrat, gbc);

        rdoOperacionesGratuitasSi = new JRadioButton("SI");
        rdoOperacionesGratuitasNo = new JRadioButton("NO");
        rdoOperacionesGratuitasSi.setFont(labelFont);
        rdoOperacionesGratuitasNo.setFont(labelFont);
        ButtonGroup bgGrat = new ButtonGroup();
        bgGrat.add(rdoOperacionesGratuitasSi);
        bgGrat.add(rdoOperacionesGratuitasNo);
        rdoOperacionesGratuitasNo.setSelected(true);

        JPanel pnlGrat = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlGrat.setBackground(Color.WHITE);
        pnlGrat.add(rdoOperacionesGratuitasSi);
        pnlGrat.add(rdoOperacionesGratuitasNo);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(pnlGrat, gbc);

        // Cargos/tributos fuera IGV
        JLabel lblCargos = new JLabel("Cargos/tributos fuera del IGV:");
        lblCargos.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(lblCargos, gbc);

        rdoCargosTributosSi = new JRadioButton("SI");
        rdoCargosTributosNo = new JRadioButton("NO");
        rdoCargosTributosSi.setFont(labelFont);
        rdoCargosTributosNo.setFont(labelFont);
        ButtonGroup bgCargos = new ButtonGroup();
        bgCargos.add(rdoCargosTributosSi);
        bgCargos.add(rdoCargosTributosNo);
        rdoCargosTributosNo.setSelected(true);

        JPanel pnlCargos = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlCargos.setBackground(Color.WHITE);
        pnlCargos.add(rdoCargosTributosSi);
        pnlCargos.add(rdoCargosTributosNo);
        gbc.gridx = 1;
        gbc.gridy = row++;
        panel.add(pnlCargos, gbc);

        return panel;
    }

    private JPanel crearPanelSeccion(String titulo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_FONDO, 2),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JLabel lblTitulo = new JLabel(titulo.toUpperCase());
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(COLOR_NARANJA);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        // aumentar espacio inferior para evitar superposición con contenido
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(lblTitulo, BorderLayout.NORTH);

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(Color.WHITE);
        // añadir padding en la parte superior del contenido para separar del título
        contenido.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        panel.add(contenido, BorderLayout.CENTER);

        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 360));
        return panel;
    }

    private void mostrarSecciones(boolean visible) {
        panelSeccion1.setVisible(visible);
        panelSeccion2.setVisible(visible);
        panelSeccion3.setVisible(visible);
        revalidate();
        repaint();
    }

    public void cargarProductosDesdeCotizacion() {
        modeloTabla.setRowCount(0);
        subtotalConIgvCotizacion = 0.0; // reinicializar
        Cotizacion cotizacion = cotizacionController.getCotizacionActual();
        if (cotizacion == null || cotizacion.getProductos() == null || cotizacion.getProductos().isEmpty()) {
            ajustarAltoTabla();
            return;
        }
        String condicionPago = (cotizacion.getCondicionPago() == null) ? "-" : cotizacion.getCondicionPago().toString();
        List<ProductoCotizacion> productos = cotizacion.getProductos();
        String ruc = (cotizacion.getCliente() != null && cotizacion.getCliente().getRuc() != null)
                ? cotizacion.getCliente().getRuc() : "-";
        
        // calcular subtotal con IGV
        for (ProductoCotizacion p : productos) {
            // Calcular subtotal con IGV (asumiendo 18% de IGV)
            double subtotalConIgv = p.getSubtotal() * 1.18;
            subtotalConIgvCotizacion += subtotalConIgv;
        }
        
        for (ProductoCotizacion p : productos) {
            Object[] fila = new Object[] {
                    safeToString(p.getCodigo()),
                    safeToString(p.getDescripcion()),
                    ruc,
                    p.getCantidad(),
                    safeToString(p.getUnidadMedida()),
                    String.format("%.2f", p.getPrecioBase()),
                    String.format("%.2f", p.getSubtotal()),
                    condicionPago
            };
            modeloTabla.addRow(fila);
        }
        // ajustar alto de la tabla según filas cargadas
        ajustarAltoTabla();
        // actualizar secciones dependientes si hay selección
        if (tablaProductos.getSelectedRow() != -1) {
            poblarSeccion2SegunCondicion();
            actualizarSeccion3();
        }
    }

    /**
     * Ajusta la altura del JScrollPane de la tabla según la cantidad de filas (sin ocupar espacio extra).
     */
    private void ajustarAltoTabla() {
        if (tablaProductos == null || scrollTablaProductos == null || modeloTabla == null) return;
        int rows = modeloTabla.getRowCount();
        int rowHeight = tablaProductos.getRowHeight();
        int header = tablaProductos.getTableHeader().getPreferredSize().height;
        // si no hay filas, mostrar una fila de altura para evitar colapso
        int visibleRows = Math.max(1, rows);
        int height = header + (visibleRows * rowHeight) + 8; // pequeño padding
        int max = 400;
        height = Math.min(height, max);
        scrollTablaProductos.setPreferredSize(new Dimension(0, height));
        scrollTablaProductos.revalidate();
    }

    private void poblarSeccion1DesdeCotizacion() {
        Cotizacion ctz = cotizacionController.getCotizacionActual();
        if (ctz == null) return;
        if (ctz.getCliente() != null && ctz.getCliente().getRuc() != null) {
            txtRucCliente.setText(ctz.getCliente().getRuc());
        } else {
            txtRucCliente.setText("");
        }
        // valores por defecto ya inicializados en los componentes
    }

    private String safeToString(Object o) { return (o == null) ? "-" : o.toString(); }

    public void refrescar() { cargarProductosDesdeCotizacion(); }

    // getters para valores de la sección 1 (usará el controlador/servicio después)
    public boolean isDetraccion() { return rdoDetraccionSi != null && rdoDetraccionSi.isSelected(); }
    public String getRucCliente() { return txtRucCliente != null ? txtRucCliente.getText().trim() : ""; }
    public boolean isPagoAnticipado() { return rdoPagoAnticipadoSi != null && rdoPagoAnticipadoSi.isSelected(); }
    public boolean isEmisorItinerante() { return rdoEmisorItineranteSi != null && rdoEmisorItineranteSi.isSelected(); }
    public boolean isEstablecimiento() { return rdoEstablecimientoSi != null && rdoEstablecimientoSi.isSelected(); }
    public boolean isConsigneDireccion() { return rdoConsigneDireccionSi != null && rdoConsigneDireccionSi.isSelected(); }
    public boolean isVentaCombustible() { return rdoVentaCombustibleSi != null && rdoVentaCombustibleSi.isSelected(); }
    public String getMoneda() { return cmbMoneda != null ? (String) cmbMoneda.getSelectedItem() : "SOLES"; }
    public boolean isDescuentos() { return rdoDescuentosSi != null && rdoDescuentosSi.isSelected(); }
    public boolean isIsc() { return rdoIscSi != null && rdoIscSi.isSelected(); }
    public boolean isOperacionesGratuitas() { return rdoOperacionesGratuitasSi != null && rdoOperacionesGratuitasSi.isSelected(); }
    public boolean isCargosTributos() { return rdoCargosTributosSi != null && rdoCargosTributosSi.isSelected(); }

    /**
     * Crea la Sección 2 (inputs dinámicos según condición de pago).
     * Incluye la fecha de emisión y panel(es) de item(s).
     */
    private JPanel crearSeccion2() {
        JPanel panel = crearPanelSeccion("Detalle del Item / Emisión");
        panel.setLayout(new BorderLayout(8,8));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 420));

        Font labelFont = new Font("Arial", Font.PLAIN, 14);

        // Fecha de emisión (por defecto hoy, editable)
        spFechaEmision = new JSpinner(new SpinnerDateModel());
        spFechaEmision.setEditor(new JSpinner.DateEditor(spFechaEmision, "dd/MM/yyyy"));
        spFechaEmision.setValue(new java.util.Date());
        spFechaEmision.setFont(labelFont);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(Color.WHITE);
        JLabel lblFecha = new JLabel("Fecha de emisión:");
        lblFecha.setFont(labelFont);
        top.add(lblFecha);
        top.add(spFechaEmision);

        panel.add(top, BorderLayout.NORTH);

        // contenedor donde se agregan los mini-items (1..2)
        itemsContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        itemsContainer.setBackground(Color.WHITE);
        itemsContainer.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        panel.add(itemsContainer, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crea un mini-panel de item usando la fuente provista.
     * El campo de descripción se inicializa con el texto pasado, editable.
     * El valor unitario se establece al subtotal del producto (sin IGV).
     */
    private JPanel crearMiniItemPanel(String tituloInicial, Font labelFont, boolean isLeft, double subtotalProducto) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;

        gbc.gridx = 0; gbc.gridy = r;
        JLabel lblT1 = new JLabel("Tipo:");
        lblT1.setFont(labelFont);
        p.add(lblT1, gbc);
        JComboBox<String> cmbTipo = new JComboBox<>(new String[] {"BIEN", "SERVICIO"});
        cmbTipo.setFont(labelFont);
        gbc.gridx = 1; gbc.gridy = r++;
        p.add(cmbTipo, gbc);

        // Cantidad (editable)
        gbc.gridx = 2; gbc.gridy = r-1;
        JLabel lblCant = new JLabel("Cantidad:");
        lblCant.setFont(labelFont);
        p.add(lblCant, gbc);
        JSpinner spnCant = new JSpinner(new SpinnerNumberModel(1, 1, 99999, 1));
        spnCant.setFont(labelFont);
        // spinner compacto (solo unos pocos dígitos)
        spnCant.setPreferredSize(new Dimension(60, spnCant.getPreferredSize().height));
        try {
            JComponent editor = spnCant.getEditor();
            if (editor instanceof JSpinner.DefaultEditor) {
                ((JSpinner.DefaultEditor) editor).getTextField().setColumns(3);
            }
        } catch (Exception ignored) { }
        gbc.gridx = 3; gbc.gridy = r-1;
        p.add(spnCant, gbc);

        gbc.gridx = 0; gbc.gridy = r;
        JLabel lblU = new JLabel("Unidad:");
        lblU.setFont(labelFont);
        p.add(lblU, gbc);
        JLabel lblUn = new JLabel("UNIDAD");
        lblUn.setFont(labelFont);
        gbc.gridx = 1; gbc.gridy = r++;
        p.add(lblUn, gbc);

        gbc.gridx = 0; gbc.gridy = r;
        JLabel lblCod = new JLabel("Código:");
        lblCod.setFont(labelFont);
        p.add(lblCod, gbc);
        JTextField txtCod = new JTextField();
        txtCod.setFont(labelFont);
        gbc.gridx = 1; gbc.gridy = r++;
        p.add(txtCod, gbc);

        gbc.gridx = 0; gbc.gridy = r;
        JLabel lblDesc = new JLabel("Descripción:");
        lblDesc.setFont(labelFont);
        p.add(lblDesc, gbc);
        JTextArea txtD = new JTextArea(3, 20);
        txtD.setLineWrap(true);
        txtD.setWrapStyleWord(true);
        txtD.setFont(labelFont);
        txtD.setText(tituloInicial); // inicializamos con el texto pedido
        gbc.gridx = 1; gbc.gridy = r++;
        p.add(new JScrollPane(txtD), gbc);

        gbc.gridx = 0; gbc.gridy = r;
        JLabel lblValUnit = new JLabel("Valor unitario (sin IGV):");
        lblValUnit.setFont(labelFont);
        p.add(lblValUnit, gbc);
        JLabel lblVal = new JLabel(String.format("S/ %.2f", subtotalProducto));
        lblVal.setFont(labelFont);
        gbc.gridx = 1; gbc.gridy = r++;
        p.add(lblVal, gbc);

        gbc.gridx = 0; gbc.gridy = r;
        JLabel lblIgvLocal = new JLabel("IGV:");
        lblIgvLocal.setFont(labelFont);
        p.add(lblIgvLocal, gbc);
        // IGV: porcentaje y tipo como campos automáticos (no editables)
        JTextField txtPct = new JTextField("18%");
        txtPct.setEditable(false);
        txtPct.setFont(labelFont);
        txtPct.setColumns(4);
        gbc.gridx = 1; gbc.gridy = r;
        p.add(txtPct, gbc);
        JTextField txtTipo = new JTextField("Gravado");
        txtTipo.setEditable(false);
        txtTipo.setFont(labelFont);
        gbc.gridx = 2; gbc.gridy = r++;
        p.add(txtTipo, gbc);

        // return panel
        return p;
    }

    /**
      * Rellena / adapta la Sección 2 según la condición de pago de la cotización.
      * Crea items automáticamente según la cantidad de productos diferentes.
      */
    private void poblarSeccion2SegunCondicion() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) return;

        // fecha por defecto
        spFechaEmision.setValue(new java.util.Date());

        // Limpiar items anteriores
        listaItems.clear();
        if (itemsContainer != null) {
            itemsContainer.removeAll();
        }

        // Crear un item por cada producto diferente
        Cotizacion cotizacion = cotizacionController.getCotizacionActual();
        if (cotizacion != null && cotizacion.getProductos() != null) {
            List<ProductoCotizacion> productos = cotizacion.getProductos();
            Font labelFont = new Font("Arial", Font.PLAIN, 14);
            
            for (int i = 0; i < productos.size(); i++) {
                ProductoCotizacion producto = productos.get(i);
                // Crear item con la descripción del producto
                JPanel mini = crearMiniItemPanel(producto.getDescripcion(), labelFont, i == 0, producto.getSubtotal());
                listaItems.add(mini);
                if (itemsContainer != null) {
                    itemsContainer.add(mini);
                }
            }
        }

        if (itemsContainer != null) {
            itemsContainer.revalidate();
            itemsContainer.repaint();
        }
    }

    /**
     * Crea la Sección 3 - Detracción / Información de crédito
     * Sin título (las secciones no deben mostrar título).
     */
    private JPanel crearSeccion3() {
        // panel sin título (consistente con otras secciones)
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_FONDO, 2),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 360));

        Font labelFont = new Font("Arial", Font.PLAIN, 14);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Observaciones: label + valor automático (alineado con el resto de inputs)
        JLabel lblObsLabel = new JLabel("Consigne las observaciones de la factura:");
        lblObsLabel.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(lblObsLabel, gbc);
        lblObservaciones = new JLabel("SUJETO A DETRACCION");
        lblObservaciones.setFont(labelFont);
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(lblObservaciones, gbc);
        gbc.gridwidth = 1;
        row++;

        // Subtítulo Información de Detracción (label normal)
        lblDetSubtitulo = new JLabel("Información de Detracción:");
        lblDetSubtitulo.setFont(labelFont.deriveFont(Font.BOLD));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        panel.add(lblDetSubtitulo, gbc);
        gbc.gridwidth = 1;
        row++;

        // Tipo de Operación (automático) - alineado como campo
        JLabel lblTipoOpLabel = new JLabel("Tipo de Operación:");
        lblTipoOpLabel.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(lblTipoOpLabel, gbc);
        lblTipoOperacion = new JLabel("<html>Operación sujeta al Sistema de Pago de Obligaciones Tributarias con el Gobierno Central</html>");
        lblTipoOperacion.setFont(labelFont);
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(lblTipoOperacion, gbc);
        gbc.gridwidth = 1;
        row++;

        // Código bien/servicio (automático) - alineado como campo
        JLabel lblCodLabel = new JLabel("Código de bien/servicio sujeto a detracción:");
        lblCodLabel.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(lblCodLabel, gbc);
        lblCodigoBienServicio = new JLabel("037 - Demás servicios gravados con el IGV");
        lblCodigoBienServicio.setFont(labelFont);
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(lblCodigoBienServicio, gbc);
        gbc.gridwidth = 1;
        row++;

        // Nro. Cuenta Banco de la Nación (editable)
        JLabel lblCuenta = new JLabel("Nro. Cta. Banco de la Nación:");
        lblCuenta.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.25;
        panel.add(lblCuenta, gbc);
        txtNroCuentaBN = new JTextField();
        txtNroCuentaBN.setFont(labelFont);
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 2; gbc.weightx = 0.75;
        panel.add(txtNroCuentaBN, gbc);
        gbc.gridwidth = 1;
        row++;

        // Medio de pago (automático)
        JLabel lblMedio = new JLabel("Medio de pago:");
        lblMedio.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(lblMedio, gbc);
        lblMedioPago = new JLabel("001-Depósito en cuenta");
        lblMedioPago.setFont(labelFont);
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(lblMedioPago, gbc);
        gbc.gridwidth = 1;
        row++;

        // Porcentaje detracción (automático)
        JLabel lblPct = new JLabel("Porcentaje de detracción:");
        lblPct.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(lblPct, gbc);
        txtPorcentajeDetraccion = new JTextField("12.00");
        txtPorcentajeDetraccion.setEditable(false);
        txtPorcentajeDetraccion.setFont(labelFont);
        gbc.gridx = 1; gbc.gridy = row;
        panel.add(txtPorcentajeDetraccion, gbc);
        row++;

        // Monto detracción (editable) - calculado por default: montoConIGV * 12% y redondeado entero
        JLabel lblMontoDet = new JLabel("Monto de detracción (S/):");
        lblMontoDet.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(lblMontoDet, gbc);
        txtMontoDetraccion = new JTextField();
        txtMontoDetraccion.setFont(labelFont);
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(txtMontoDetraccion, gbc);
        gbc.gridwidth = 1;
        row++;

        // Información del crédito (oculta por defecto, solo visible cuando selector = CREDITO)
        lblInfoCreditoSub = new JLabel("Información del crédito:");
        lblInfoCreditoSub.setFont(labelFont.deriveFont(Font.BOLD));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        panel.add(lblInfoCreditoSub, gbc);
        gbc.gridwidth = 1;
        row++;

        // Monto neto pendiente (editable)
        JLabel lblNeto = new JLabel("Monto neto pendiente de pago (S/):");
        lblNeto.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(lblNeto, gbc);
        txtMontoNetoPendiente = new JTextField();
        txtMontoNetoPendiente.setFont(labelFont);
        gbc.gridx = 1; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(txtMontoNetoPendiente, gbc);
        gbc.gridwidth = 1;
        row++;

        // Mini sección 1 cuota: Fecha vencimiento y Monto cuota
        lblVencLabel = new JLabel("Fecha de vencimiento:");
        lblVencLabel.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(lblVencLabel, gbc);
        spFechaVencimiento = new JSpinner(new SpinnerDateModel());
        spFechaVencimiento.setEditor(new JSpinner.DateEditor(spFechaVencimiento, "dd/MM/yyyy"));
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_MONTH, 30);
        spFechaVencimiento.setValue(cal.getTime());
        spFechaVencimiento.setFont(labelFont);
        gbc.gridx = 1; gbc.gridy = row;
        panel.add(spFechaVencimiento, gbc);

        lblMontoCuotaLabel = new JLabel("Monto de la cuota (S/):");
        lblMontoCuotaLabel.setFont(labelFont);
        gbc.gridx = 0; gbc.gridy = row + 1;
        panel.add(lblMontoCuotaLabel, gbc);
        txtMontoCuota = new JTextField();
        txtMontoCuota.setFont(labelFont);
        gbc.gridx = 1; gbc.gridy = row + 1; gbc.gridwidth = 2;
        panel.add(txtMontoCuota, gbc);
        gbc.gridwidth = 1;

        // listeners para recalcular cuando el usuario cambie valores
        txtMontoDetraccion.addActionListener(e -> actualizarSeccion3());
        txtMontoDetraccion.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent e) { actualizarSeccion3(); }
        });
        txtMontoNetoPendiente.addActionListener(e -> actualizarSeccion3());
        txtMontoNetoPendiente.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent e) { actualizarSeccion3(); }
        });

        // ocultar inicialmente la parte de crédito (se mostrará cuando selector == CREDITO)
        boolean esCreditoInit = (cmbFormaPagoSelector != null) && "CREDITO".equalsIgnoreCase((String)cmbFormaPagoSelector.getSelectedItem());
        if (lblInfoCreditoSub != null) lblInfoCreditoSub.setVisible(esCreditoInit);
        if (txtMontoNetoPendiente != null) txtMontoNetoPendiente.setVisible(esCreditoInit);
        if (spFechaVencimiento != null) spFechaVencimiento.setVisible(esCreditoInit);
        if (txtMontoCuota != null) txtMontoCuota.setVisible(esCreditoInit);
        if (lblVencLabel != null) lblVencLabel.setVisible(esCreditoInit);
        if (lblMontoCuotaLabel != null) lblMontoCuotaLabel.setVisible(esCreditoInit);

        return panel;
    }

    /**
     * Actualiza valores de la Sección 3 en base al SUBTOTAL CON IGV de la cotización completa y al selector de tipo de pago.
     * - Monto de detracción = Subtotal con IGV * 12% (redondeado a entero)
     * - Monto neto = Subtotal con IGV - Monto detracción
     * - Monto de la cuota = Subtotal con IGV - Monto detracción
     */
    private void actualizarSeccion3() {
        int fila = (tablaProductos != null) ? tablaProductos.getSelectedRow() : -1;
        if (fila == -1) {
            if (txtMontoDetraccion != null) txtMontoDetraccion.setText("");
            if (txtMontoNetoPendiente != null) txtMontoNetoPendiente.setText("");
            if (txtMontoCuota != null) txtMontoCuota.setText("");
            return;
        }

        // usar el SUBTOTAL CON IGV de todos los productos
        double subtotalConIgv = subtotalConIgvCotizacion;

        // Cálculo de detracción basado en SUBTOTAL CON IGV
        long detrPorDefecto = Math.round(subtotalConIgv * 0.12); // redondeado al entero

        // si el campo está vacío, rellenar con valor por defecto
        if (txtMontoDetraccion != null) {
            String t = txtMontoDetraccion.getText();
            if (t == null || t.trim().isEmpty()) {
                txtMontoDetraccion.setText(String.valueOf(detrPorDefecto));
            }
        }

        // parse monto detracción (editable)
        double montoDet = detrPorDefecto;
        try {
            String t = txtMontoDetraccion.getText().trim();
            if (!t.isEmpty()) montoDet = Double.parseDouble(t);
        } catch (Exception ignored) { montoDet = detrPorDefecto; }

        // monto neto pendiente = subtotal con IGV - montoDet
        double montoNeto = subtotalConIgv - montoDet;

        // si campo neto está vacío, rellenar calculado (editable por usuario)
        if (txtMontoNetoPendiente != null) {
            String t = txtMontoNetoPendiente.getText();
            if (t == null || t.trim().isEmpty()) {
                txtMontoNetoPendiente.setText(String.format("%.2f", montoNeto));
            }
        }

        // cuota por defecto = montoNeto (editable por usuario)
        if (txtMontoCuota != null) {
            String t = txtMontoCuota.getText();
            if (t == null || t.trim().isEmpty()) {
                txtMontoCuota.setText(String.format("%.2f", montoNeto));
            }
        }

        // Mostrar u ocultar la parte de crédito según selector (solo CREDITO muestra info crédito)
        String modo = (cmbFormaPagoSelector == null) ? "CONTADO" : ((String) cmbFormaPagoSelector.getSelectedItem());
        boolean esCredito = "CREDITO".equalsIgnoreCase(modo);

        if (lblInfoCreditoSub != null) lblInfoCreditoSub.setVisible(esCredito);
        if (spFechaVencimiento != null) spFechaVencimiento.setVisible(esCredito);
        if (lblVencLabel != null) lblVencLabel.setVisible(esCredito);
        if (lblMontoCuotaLabel != null) lblMontoCuotaLabel.setVisible(esCredito);
        if (esCredito) {
            if (txtMontoNetoPendiente != null) txtMontoNetoPendiente.setVisible(true);
            if (txtMontoCuota != null) txtMontoCuota.setVisible(true);
        } else {
            // ocultar y limpiar campos de crédito para evitar que aparezcan en CONTADO
            if (txtMontoNetoPendiente != null) {
                txtMontoNetoPendiente.setVisible(false);
                txtMontoNetoPendiente.setText("");
            }
            if (txtMontoCuota != null) {
                txtMontoCuota.setVisible(false);
                txtMontoCuota.setText("");
            }
            if (lblInfoCreditoSub != null) lblInfoCreditoSub.setVisible(false);
        }

         // forzar repaint para cambios de visibilidad/valores
        if (panelSeccion3 != null) {
            panelSeccion3.revalidate();
            panelSeccion3.repaint();
        }
    }

}
