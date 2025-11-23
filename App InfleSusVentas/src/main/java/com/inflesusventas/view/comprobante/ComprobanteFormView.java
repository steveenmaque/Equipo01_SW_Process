package com.inflesusventas.view.comprobante;

import com.inflesusventas.controller.CotizacionController;
import com.inflesusventas.model.Cliente;
import com.inflesusventas.model.ComprobanteElectronico;
import com.inflesusventas.model.Cotizacion;
import com.inflesusventas.model.ProductoCotizacion;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import com.inflesusventas.service.ClienteService;
import com.inflesusventas.service.ComprobanteService;
import com.inflesusventas.service.PdfGeneratorService;
import com.inflesusventas.service.PdfGeneratorService.DatosFacturaPDF;
import com.inflesusventas.service.XmlGeneratorService;
import com.inflesusventas.service.XmlGeneratorService.DatosFacturaElectronica;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Vista para crear facturas a partir de la cotización actual en memoria.
 * Reescrita para seguir estilo de CotizacionFormView (panel principal con secciones apiladas).
 */
public class ComprobanteFormView extends JPanel {
    
    private static final Color COLOR_PRIMARIO = new Color(15,65,116);
    private static final Color COLOR_FONDO = new Color(248,249,250);
    private static final Color COLOR_NARANJA = new Color(230,130,70);

    private final CotizacionController cotizacionController;
    
    // Lista para guardar TODAS las cotizaciones creadas
    private java.util.List<Cotizacion> listaCotizaciones = new java.util.ArrayList<>();

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

    private PdfGeneratorService pdfService;
    private XmlGeneratorService xmlService;

     private ClienteService clienteService;
    private ComprobanteService comprobanteService;

    // Conjunto para rastrear cotizaciones ya facturadas
    private java.util.Set<Integer> cotizacionesFacturadas = new java.util.HashSet<>();

    // Mapa para guardar los IDs de factura generados por cotización
    private java.util.Map<Integer, String> mapaIdFacturas = new java.util.HashMap<>();

    public ComprobanteFormView(CotizacionController cotizacionController, 
                               ClienteService clienteService, 
                               ComprobanteService comprobanteService) {
        this.cotizacionController = cotizacionController;
        this.clienteService = clienteService;         // <--- Guardamos referencia
        this.comprobanteService = comprobanteService; // <--- Guardamos referencia
        
        this.pdfService = new PdfGeneratorService();
        this.xmlService = new XmlGeneratorService();
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
        btnGenerarComprobante.addActionListener(e -> generarFacturaElectronica());
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

        // Nuevas columnas con ID de Factura
        String[] columnas = {"ID Factura", "RUC Cliente", "Razón Social", "Productos", "Subtotal (S/)", "Condición"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override 
            public boolean isCellEditable(int row, int column) { 
                return false; 
            }
        };

        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaProductos.setRowHeight(80); // Altura mayor para mostrar múltiples productos
        tablaProductos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        
        // Configurar renderizador para mostrar saltos de línea en la columna "Productos"
        tablaProductos.getColumnModel().getColumn(3).setCellRenderer(new MultiLineTableCellRenderer());
        
        // Ajustar anchos de columnas
        tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(150); // ID Factura
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(120); // RUC
        tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(200); // Razón Social
        tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(300); // Productos (más ancho)
        tablaProductos.getColumnModel().getColumn(4).setPreferredWidth(100); // Subtotal
        tablaProductos.getColumnModel().getColumn(5).setPreferredWidth(100); // Condición

        JScrollPane scrollTabla = new JScrollPane(tablaProductos);
        scrollTablaProductos = scrollTabla;
        ajustarAltoTabla();
        panel.add(scrollTablaProductos, BorderLayout.CENTER);

        // Listener selección
        tablaProductos.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override 
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int fila = tablaProductos.getSelectedRow();
                    
                    if (fila != -1 && fila < listaCotizaciones.size()) {
                        Cotizacion cotSelected = listaCotizaciones.get(fila);
                        
                        // VERIFICAR SI ESTÁ FACTURADA USANDO EL MODELO
                        if (cotSelected.isFacturada()) {
                            mostrarSecciones(false);
                            JOptionPane.showMessageDialog(ComprobanteFormView.this,
                                "Esta cotización ya fue procesada anteriormente.",
                                "Aviso", JOptionPane.INFORMATION_MESSAGE);
                            tablaProductos.clearSelection();
                        } else {
                            cargarDatosCotizacionSeleccionada(cotSelected);
                            mostrarSecciones(true);
                            poblarSeccion1DesdeCotizacion(cotSelected);
                            poblarSeccion2SegunCondicion(cotSelected);
                            actualizarSeccion3(cotSelected);
                        }
                    }
                }
            }
        });
        return panel;
    }

    /**
     * Carga los datos de la cotización seleccionada en las secciones
     */
    private void cargarDatosCotizacionSeleccionada(Cotizacion cotizacion) {
        // Calcular subtotal con IGV de ESTA cotización
        subtotalConIgvCotizacion = 0.0;
        for (ProductoCotizacion p : cotizacion.getProductos()) {
            subtotalConIgvCotizacion += p.getSubtotal();
        }
        subtotalConIgvCotizacion *= 1.18;
    }

    private JPanel crearSeccion1() {
        // Panel sin título
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_FONDO, 2),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280)); // Altura reducida

        GridBagConstraints gbcMainHeader = new GridBagConstraints();
        gbcMainHeader.insets = new Insets(6, 6, 12, 6);
        gbcMainHeader.fill = GridBagConstraints.HORIZONTAL;
        gbcMainHeader.anchor = GridBagConstraints.WEST;
        
        // Selector de forma de pago
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

        cmbFormaPagoSelector.addActionListener(e -> {
            if (tablaProductos != null && tablaProductos.getSelectedRow() != -1) {
                poblarSeccion2SegunCondicion();
            }
            actualizarSeccion3();
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        Font labelFont = new Font("Arial", Font.PLAIN, 14);
        int row = 1;

        // ========== CAMPOS VISIBLES ==========
        
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

        // ========== INICIALIZAR VARIABLES OCULTAS CON VALORES POR DEFECTO ==========
        // Estos componentes existen en memoria pero NO se muestran visualmente
        
        // Detracción - POR DEFECTO: SÍ
        rdoDetraccionSi = new JRadioButton("SI");
        rdoDetraccionNo = new JRadioButton("NO");
        ButtonGroup bgDet = new ButtonGroup();
        bgDet.add(rdoDetraccionSi);
        bgDet.add(rdoDetraccionNo);
        rdoDetraccionSi.setSelected(true); // Por defecto SÍ

        // Pago anticipado - POR DEFECTO: NO
        rdoPagoAnticipadoSi = new JRadioButton("SI");
        rdoPagoAnticipadoNo = new JRadioButton("NO");
        ButtonGroup bgPagoAnt = new ButtonGroup();
        bgPagoAnt.add(rdoPagoAnticipadoSi);
        bgPagoAnt.add(rdoPagoAnticipadoNo);
        rdoPagoAnticipadoNo.setSelected(true); // Por defecto NO

        // Emisor itinerante - POR DEFECTO: NO
        rdoEmisorItineranteSi = new JRadioButton("SI");
        rdoEmisorItineranteNo = new JRadioButton("NO");
        ButtonGroup bgIt = new ButtonGroup();
        bgIt.add(rdoEmisorItineranteSi);
        bgIt.add(rdoEmisorItineranteNo);
        rdoEmisorItineranteNo.setSelected(true); // Por defecto NO

        // Establecimiento del emisor - POR DEFECTO: NO
        rdoEstablecimientoSi = new JRadioButton("SI");
        rdoEstablecimientoNo = new JRadioButton("NO");
        ButtonGroup bgEst = new ButtonGroup();
        bgEst.add(rdoEstablecimientoSi);
        bgEst.add(rdoEstablecimientoNo);
        rdoEstablecimientoNo.setSelected(true); // Por defecto NO

        // Consigne dirección - POR DEFECTO: SÍ
        rdoConsigneDireccionSi = new JRadioButton("SI");
        rdoConsigneDireccionNo = new JRadioButton("NO");
        ButtonGroup bgCons = new ButtonGroup();
        bgCons.add(rdoConsigneDireccionSi);
        bgCons.add(rdoConsigneDireccionNo);
        rdoConsigneDireccionSi.setSelected(true); // Por defecto SÍ

        // Venta combustible - POR DEFECTO: NO
        rdoVentaCombustibleSi = new JRadioButton("SI");
        rdoVentaCombustibleNo = new JRadioButton("NO");
        ButtonGroup bgComb = new ButtonGroup();
        bgComb.add(rdoVentaCombustibleSi);
        bgComb.add(rdoVentaCombustibleNo);
        rdoVentaCombustibleNo.setSelected(true); // Por defecto NO

        // Descuentos - POR DEFECTO: NO
        rdoDescuentosSi = new JRadioButton("SI");
        rdoDescuentosNo = new JRadioButton("NO");
        ButtonGroup bgDesc = new ButtonGroup();
        bgDesc.add(rdoDescuentosSi);
        bgDesc.add(rdoDescuentosNo);
        rdoDescuentosNo.setSelected(true); // Por defecto NO

        // ISC - POR DEFECTO: NO
        rdoIscSi = new JRadioButton("SI");
        rdoIscNo = new JRadioButton("NO");
        ButtonGroup bgIsc = new ButtonGroup();
        bgIsc.add(rdoIscSi);
        bgIsc.add(rdoIscNo);
        rdoIscNo.setSelected(true); // Por defecto NO

        // Operaciones gratuitas - POR DEFECTO: NO
        rdoOperacionesGratuitasSi = new JRadioButton("SI");
        rdoOperacionesGratuitasNo = new JRadioButton("NO");
        ButtonGroup bgGrat = new ButtonGroup();
        bgGrat.add(rdoOperacionesGratuitasSi);
        bgGrat.add(rdoOperacionesGratuitasNo);
        rdoOperacionesGratuitasNo.setSelected(true); // Por defecto NO

        // Cargos/tributos - POR DEFECTO: NO
        rdoCargosTributosSi = new JRadioButton("SI");
        rdoCargosTributosNo = new JRadioButton("NO");
        ButtonGroup bgCargos = new ButtonGroup();
        bgCargos.add(rdoCargosTributosSi);
        bgCargos.add(rdoCargosTributosNo);
        rdoCargosTributosNo.setSelected(true); // Por defecto NO

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
        // 1. Obtener la lista COMPLETA del controlador (que viene del JSON)
        List<Cotizacion> todas = cotizacionController.getTodasLasCotizaciones();
        
        if (todas == null) {
            todas = new java.util.ArrayList<>();
        }

        // 2. Actualizar la lista local de la vista
        this.listaCotizaciones = todas;
        System.out.println("Vista Comprobantes: cargando " + listaCotizaciones.size() + " cotizaciones.");

        // 3. Refrescar la tabla visual
        refrescarTablaCompleta();
    }

    /**
     * Recarga la tabla mostrando TODAS las cotizaciones almacenadas
     */
    private void refrescarTablaCompleta() {
        modeloTabla.setRowCount(0); // Limpiar tabla
        subtotalConIgvCotizacion = 0.0;
        
        // Agregar cada cotización a la tabla
        for (int i = 0; i < listaCotizaciones.size(); i++) {
            Cotizacion cotizacion = listaCotizaciones.get(i);
            agregarCotizacionATabla(cotizacion, i);
        }
        
        ajustarAltoTabla();
    }

    /**
     * Agrega una cotización individual a la tabla
     * @param cotizacion La cotización a agregar
     * @param indice Posición en la lista (usado como ID interno)
     */
        private void agregarCotizacionATabla(Cotizacion cotizacion, int indice) {
        List<ProductoCotizacion> productos = cotizacion.getProductos();
        double subtotalSinIgv = 0.0;
        for (ProductoCotizacion p : productos) {
            subtotalSinIgv += p.getSubtotal();
        }
        
        String ruc = (cotizacion.getCliente() != null) ? cotizacion.getCliente().getRuc() : "-";
        String razonSocial = (cotizacion.getCliente() != null) ? cotizacion.getCliente().getRazonSocial() : "-";
        String condicionPago = (cotizacion.getCondicionPago() == null) ? "-" : cotizacion.getCondicionPago().toString();
        
        String estadoFactura;
        
        // USAMOS EL CAMPO PERSISTENTE DEL MODELO
        if (cotizacion.isFacturada()) {
            estadoFactura = "✓ FACTURADA"; // O puedes poner el ID si lo guardaras en la cotización
        } else {
            estadoFactura = "PENDIENTE";
        }
        
        // Construir HTML lista productos (sin cambios)
        StringBuilder listaProductos = new StringBuilder("<html>");
        for (int i = 0; i < productos.size(); i++) {
            ProductoCotizacion p = productos.get(i);
            listaProductos.append("• ").append(p.getDescripcion());
            if (i < productos.size() - 1) listaProductos.append("<br>");
        }
        listaProductos.append("</html>");
        
        Object[] fila = new Object[] {
            estadoFactura, // Columna 0: Estado
            ruc,
            razonSocial,
            listaProductos.toString(),
            String.format("%.2f", subtotalSinIgv),
            condicionPago
        };
        modeloTabla.addRow(fila);
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

    private void poblarSeccion1DesdeCotizacion(Cotizacion cotizacion) {
        if (cotizacion == null) return;
        
        if (cotizacion.getCliente() != null) {
            if (cotizacion.getCliente().getRuc() != null) {
                txtRucCliente.setText(cotizacion.getCliente().getRuc());
            } else {
                txtRucCliente.setText("");
            }
            if (cotizacion.getCliente().getRazonSocial() != null) {
                txtRazonSocial.setText(cotizacion.getCliente().getRazonSocial());
            } else {
                txtRazonSocial.setText("");
            }
        }
    }

    private void poblarSeccion1DesdeCotizacion() {
        Cotizacion ctz = cotizacionController.getCotizacionActual();
        poblarSeccion1DesdeCotizacion(ctz);
    }

    private String safeToString(Object o) { return (o == null) ? "-" : o.toString(); }

    public void refrescar() { 
        cargarProductosDesdeCotizacion(); 
    }

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
     * Genera el ID de factura en formato: RUC-SERIE-NUMERO
     * Ejemplo: 20456789012-F001-00000001
     */
    private String generarIdFactura(String rucCliente) {
        String serie = "F001";
        int numero = obtenerSiguienteNumero();
        
        return String.format("%s-%s-%08d", 
            rucCliente, 
            serie, 
            numero);
    }

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
    private void poblarSeccion2SegunCondicion(Cotizacion cotizacion) {
        if (cotizacion == null) return;
        
        spFechaEmision.setValue(new java.util.Date());
        
        listaItems.clear();
        if (itemsContainer != null) {
            itemsContainer.removeAll();
        }
        
        List<ProductoCotizacion> productos = cotizacion.getProductos();
        Font labelFont = new Font("Arial", Font.PLAIN, 14);
        
        for (int i = 0; i < productos.size(); i++) {
            ProductoCotizacion producto = productos.get(i);
            JPanel mini = crearMiniItemPanel(producto.getDescripcion(), labelFont, i == 0, producto.getSubtotal());
            listaItems.add(mini);
            if (itemsContainer != null) {
                itemsContainer.add(mini);
            }
        }
        
        if (itemsContainer != null) {
            itemsContainer.revalidate();
            itemsContainer.repaint();
        }
    }

    // MANTENER el método sin parámetros para compatibilidad
    private void poblarSeccion2SegunCondicion() {
        int fila = tablaProductos.getSelectedRow();
        if (fila != -1 && fila < listaCotizaciones.size()) {
            poblarSeccion2SegunCondicion(listaCotizaciones.get(fila));
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
    private void actualizarSeccion3(Cotizacion cotizacion) {
        if (cotizacion == null) {
            if (txtMontoDetraccion != null) txtMontoDetraccion.setText("");
            if (txtMontoNetoPendiente != null) txtMontoNetoPendiente.setText("");
            if (txtMontoCuota != null) txtMontoCuota.setText("");
            return;
        }
        
        // Calcular usando los datos de ESTA cotización
        double subtotalConIgv = 0.0;
        for (ProductoCotizacion p : cotizacion.getProductos()) {
            subtotalConIgv += p.getSubtotal();
        }
        subtotalConIgv *= 1.18;
        
        long detrPorDefecto = Math.round(subtotalConIgv * 0.12);
        
        if (txtMontoDetraccion != null) {
            String t = txtMontoDetraccion.getText();
            if (t == null || t.trim().isEmpty()) {
                txtMontoDetraccion.setText(String.valueOf(detrPorDefecto));
            }
        }
        
        double montoDet = detrPorDefecto;
        try {
            String t = txtMontoDetraccion.getText().trim();
            if (!t.isEmpty()) montoDet = Double.parseDouble(t);
        } catch (Exception ignored) { 
            montoDet = detrPorDefecto; 
        }
        
        double montoNeto = subtotalConIgv - montoDet;
        
        if (txtMontoNetoPendiente != null) {
            String t = txtMontoNetoPendiente.getText();
            if (t == null || t.trim().isEmpty()) {
                txtMontoNetoPendiente.setText(String.format("%.2f", montoNeto));
            }
        }
        
        if (txtMontoCuota != null) {
            String t = txtMontoCuota.getText();
            if (t == null || t.trim().isEmpty()) {
                txtMontoCuota.setText(String.format("%.2f", montoNeto));
            }
        }
        
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
        
        if (panelSeccion3 != null) {
            panelSeccion3.revalidate();
            panelSeccion3.repaint();
        }
    }

    private void actualizarSeccion3() {
        int fila = tablaProductos.getSelectedRow();
        if (fila != -1 && fila < listaCotizaciones.size()) {
            actualizarSeccion3(listaCotizaciones.get(fila));
        }
    }

    /**
     * Genera XML y PDF de la factura electrónica
     */
    private void generarFacturaElectronica() {
        try {
            int filaSeleccionada = tablaProductos.getSelectedRow();
            
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this,
                    "Seleccione una cotización de la tabla",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Obtener la cotización seleccionada de la lista (NO del controlador)
            if (filaSeleccionada >= listaCotizaciones.size()) {
                JOptionPane.showMessageDialog(this,
                    "Error: Cotización no encontrada",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Cotizacion cotizacion = listaCotizaciones.get(filaSeleccionada);
            
            if (cotizacion.getProductos() == null || cotizacion.getProductos().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No hay productos para facturar",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validar datos del cliente
            String rucCliente = txtRucCliente.getText().trim();
            String razonCliente = txtRazonSocial.getText().trim();
            
            if (rucCliente.isEmpty() || razonCliente.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Complete RUC y Razón Social del cliente",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Construir datos
            DatosFacturaElectronica datosXML = new DatosFacturaElectronica();
            configurarDatosComunes(datosXML, rucCliente, razonCliente);
            
            DatosFacturaPDF datosPDF = new DatosFacturaPDF();
            configurarDatosComunes(datosPDF, rucCliente, razonCliente);
            
            // Mostrar progreso
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            btnGenerarComprobante.setEnabled(false);
            btnGenerarComprobante.setText("GENERANDO XML Y PDF...");
            
            final int indiceCotizacion = filaSeleccionada; // Guardar el índice
            
            // Generar en hilo separado
            new Thread(() -> {
                String rutaXML = null;
                String rutaPDF = null;
                
                try {
                    rutaXML = xmlService.generarXMLFactura(cotizacion, datosXML);
                    rutaPDF = pdfService.generarPdfFactura(cotizacion, datosPDF);
                    
                    // REGISTRAR como facturada usando ÍNDICE
                    String idFacturaGenerado = String.format("%s-%s-%08d",
                        datosXML.rucCliente,
                        datosXML.serie,
                        datosXML.numero);
                    
                    Cliente clienteFactura = new Cliente();
                    clienteFactura.setId(0); // 0 para que el servicio decida si crea o actualiza (según tu lógica de servicio)
                    clienteFactura.setRuc(datosXML.rucCliente);
                    clienteFactura.setRazonSocial(datosXML.razonSocialCliente);
                    // Datos que quizás no tenemos en el form de factura, ponemos vacíos para evitar nulls
                    clienteFactura.setDireccion(""); 
                    clienteFactura.setTelefono("");
                    clienteFactura.setEmail("");
                    clienteFactura.setNombreContacto("");
                    
                    cotizacion.setFacturada(true);
                    cotizacionController.actualizarCotizacion(cotizacion);
                    if (clienteService != null) {
                    // Nota: Tu servicio debe ser capaz de buscar por RUC para no duplicar
                    // Si tu servicio guarda ciegamente, podrías tener duplicados.
                    // Lo ideal es: Cliente existente = service.buscarPorRuc(...);
                    clienteService.guardarCliente(clienteFactura);
                    System.out.println("Cliente guardado/actualizado: " + datosXML.razonSocialCliente);
                    }

                    ComprobanteElectronico ce = new ComprobanteElectronico();
                    ce.setId(idFacturaGenerado);
                    ce.setFechaEmision(LocalDateTime.ofInstant(datosXML.fechaEmision.toInstant(), ZoneId.systemDefault()));
                    ce.setRucCliente(datosXML.rucCliente);
                    ce.setRazonSocialCliente(datosXML.razonSocialCliente);
                    ce.setTotal(cotizacion.getTotal());
                    ce.setItems(cotizacion.getProductos()); // Aquí guardamos la lista de lo que llevaron
                    ce.setTipoComprobante("Factura");
                    ce.setCondicionPago(datosXML.esCredito ? "CREDITO" : "CONTADO");
                    
                    if (comprobanteService != null) {
                        comprobanteService.registrarComprobante(ce);
                        System.out.println("Comprobante registrado en historial: " + idFacturaGenerado);
                    }

                    cotizacionesFacturadas.add(indiceCotizacion); // Usar índice
                    mapaIdFacturas.put(indiceCotizacion, idFacturaGenerado); // Usar índice
                    
                    final String xmlFinal = rutaXML;
                    final String pdfFinal = rutaPDF;
                    
                    SwingUtilities.invokeLater(() -> {
                        restaurarBoton();
                        refrescarTablaCompleta(); // Ahora se verá como "FACTURADA"
                        mostrarSecciones(false);
                        tablaProductos.clearSelection();
                        mostrarDialogoExito(xmlFinal, pdfFinal);
                    });
                                        
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        restaurarBoton();
                        JOptionPane.showMessageDialog(this,
                            "Error al generar factura:\n" + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    });
                }
            }).start();
            
        } catch (Exception ex) {
            restaurarBoton();
            JOptionPane.showMessageDialog(this,
                "Error inesperado: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Configura los datos comunes para XML (sobrecarga para DatosFacturaElectronica)
     */
    private void configurarDatosComunes(DatosFacturaElectronica datos, String rucCliente, String razonCliente) {
        // Serie y número
        datos.serie = "F001";
        datos.numero = obtenerSiguienteNumero();
        datos.fechaEmision = (Date) spFechaEmision.getValue();
        
        // Moneda
        String monedaStr = (String) cmbMoneda.getSelectedItem();
        datos.moneda = convertirMoneda(monedaStr);
        
        // Emisor (reemplaza con tus datos reales)
        datos.rucEmisor = "20123456789";
        datos.razonSocialEmisor = "InfleSusVentas SRL";
        
        // Cliente
        datos.rucCliente = rucCliente;
        datos.razonSocialCliente = razonCliente;
        
        // Detracción
        if (isDetraccion()) {
            try {
                String txtDet = txtMontoDetraccion.getText().trim();
                datos.montoDetraccion = txtDet.isEmpty() ? 0 : Double.parseDouble(txtDet);
                datos.porcentajeDetraccion = 12.0;
                datos.cuentaBancoNacion = txtNroCuentaBN.getText().trim();
            } catch (NumberFormatException ex) {
                datos.montoDetraccion = 0;
            }
        }
        
        // Crédito
        String formaPago = (String) cmbFormaPagoSelector.getSelectedItem();
        datos.esCredito = "CREDITO".equalsIgnoreCase(formaPago);
        
        if (datos.esCredito) {
            try {
                String txtNeto = txtMontoNetoPendiente.getText().trim();
                datos.montoNetoPendiente = txtNeto.isEmpty() ? 0 : Double.parseDouble(txtNeto);
                
                String txtCuota = txtMontoCuota.getText().trim();
                datos.montoCuota = txtCuota.isEmpty() ? 0 : Double.parseDouble(txtCuota);
                
                datos.fechaVencimiento = (Date) spFechaVencimiento.getValue();
            } catch (NumberFormatException ex) {
                // valores por defecto
            }
        }
    }

    /**
     * Configura los datos comunes para PDF (sobrecarga para DatosFacturaPDF)
     */
    private void configurarDatosComunes(DatosFacturaPDF datos, String rucCliente, String razonCliente) {
        // Serie y número
        datos.serie = "F001";
        datos.numero = obtenerSiguienteNumero();
        datos.fechaEmision = (Date) spFechaEmision.getValue();
        
        // Moneda
        String monedaStr = (String) cmbMoneda.getSelectedItem();
        datos.moneda = convertirMoneda(monedaStr);
        
        // Emisor
        datos.rucEmisor = "20123456789";
        datos.razonSocialEmisor = "InfleSusVentas SRL";
        
        // Cliente
        datos.rucCliente = rucCliente;
        datos.razonSocialCliente = razonCliente;
        
        // Detracción
        if (isDetraccion()) {
            try {
                String txtDet = txtMontoDetraccion.getText().trim();
                datos.montoDetraccion = txtDet.isEmpty() ? 0 : Double.parseDouble(txtDet);
                datos.porcentajeDetraccion = 12.0;
                datos.cuentaBancoNacion = txtNroCuentaBN.getText().trim();
            } catch (NumberFormatException ex) {
                datos.montoDetraccion = 0;
            }
        }
        
        // Crédito
        String formaPago = (String) cmbFormaPagoSelector.getSelectedItem();
        datos.esCredito = "CREDITO".equalsIgnoreCase(formaPago);
        
        if (datos.esCredito) {
            try {
                String txtNeto = txtMontoNetoPendiente.getText().trim();
                datos.montoNetoPendiente = txtNeto.isEmpty() ? 0 : Double.parseDouble(txtNeto);
                
                String txtCuota = txtMontoCuota.getText().trim();
                datos.montoCuota = txtCuota.isEmpty() ? 0 : Double.parseDouble(txtCuota);
                
                datos.fechaVencimiento = (Date) spFechaVencimiento.getValue();
            } catch (NumberFormatException ex) {
                // valores por defecto
            }
        }
    }

    /**
     * Convierte nombre de moneda a código ISO
     */
    private String convertirMoneda(String moneda) {
        if (moneda == null) return "PEN";
        switch (moneda.toUpperCase()) {
            case "SOLES": return "PEN";
            case "DÓLARES": case "DOLARES": return "USD";
            case "EUROS": return "EUR";
            default: return "PEN";
        }
    }

    /**
     * Obtiene el siguiente número de factura
     */
    private int obtenerSiguienteNumero() {
        // TODO: Implementar lógica con tu base de datos
        // Por ahora retorna número basado en timestamp
        return (int) (System.currentTimeMillis() % 100000);
    }

    /**
     * Restaura el estado del botón
     */
    private void restaurarBoton() {
        setCursor(Cursor.getDefaultCursor());
        btnGenerarComprobante.setEnabled(true);
        btnGenerarComprobante.setText("GENERAR COMPROBANTE ELECTRÓNICO");
    }

    /**
     * Muestra diálogo de éxito con opciones para abrir archivos
     */
    private void mostrarDialogoExito(String rutaXML, String rutaPDF) {
        Object[] opciones = {"Abrir PDF", "Abrir XML", "Ver Carpeta", "Cerrar"};
        
        int resultado = JOptionPane.showOptionDialog(
            this,
            "✅ Factura electrónica generada exitosamente\n\n" +
            "XML: " + new File(rutaXML).getName() + "\n" +
            "PDF: " + new File(rutaPDF).getName() + "\n\n" +
            "¿Qué desea hacer?",
            "Factura Generada",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            opciones,
            opciones[0]
        );
        
        switch (resultado) {
            case 0: // Abrir PDF
                abrirArchivo(rutaPDF);
                break;
            case 1: // Abrir XML
                abrirArchivo(rutaXML);
                break;
            case 2: // Ver carpeta
                abrirCarpeta(rutaPDF);
                break;
        }
    }

    /**
     * Abre un archivo con la aplicación predeterminada
     */
    private void abrirArchivo(String ruta) {
        try {
            File archivo = new File(ruta);
            if (archivo.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivo);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "No se pudo abrir el archivo.\nUbicación: " + ruta,
                "Aviso",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Abre la carpeta que contiene el archivo
     */
    private void abrirCarpeta(String rutaArchivo) {
        try {
            File archivo = new File(rutaArchivo);
            File carpeta = archivo.getParentFile();
            
            if (carpeta.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(carpeta);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "No se pudo abrir la carpeta",
                "Aviso",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Renderizador personalizado para mostrar HTML en celdas (saltos de línea)
     */
    private static class MultiLineTableCellRenderer extends JTextArea implements javax.swing.table.TableCellRenderer {
        
        public MultiLineTableCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
        }
        
        @Override
        public java.awt.Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            
            if (value instanceof String) {
                String texto = (String) value;
                // Convertir HTML a texto plano con saltos de línea
                texto = texto.replace("<html>", "").replace("</html>", "");
                texto = texto.replace("<br>", "\n");
                setText(texto);
            } else {
                setText(value != null ? value.toString() : "");
            }
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }
            
            setFont(table.getFont());
            
            return this;
        }
    }

}
