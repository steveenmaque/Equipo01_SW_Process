package com.inflesusventas.view.cotizacion;

import com.inflesusventas.controller.CotizacionController;
import com.inflesusventas.model.Cotizacion;
import com.inflesusventas.model.Cotizacion.CondicionPago;
import com.inflesusventas.model.Cliente;
import com.inflesusventas.model.ProductoCotizacion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Vista de formulario para crear cotizaciones - Aplicación Desktop
 * Interfaz gráfica con Java Swing - Patrón MVC
 * 
 * Ruta: src/main/java/com/inflesusventas/view/cotizacion/CotizacionFormView.java
 */
public class CotizacionFormView extends JPanel {
    
    // Colores corporativos
    private static final Color COLOR_PRIMARIO = new Color(102, 126, 234);
    private static final Color COLOR_SECUNDARIO = new Color(118, 75, 162);
    private static final Color COLOR_FONDO = new Color(248, 249, 250);
    
    // Controlador
    private CotizacionController controller;
    
    // Componentes de Cliente
    private JTextField txtRuc;
    private JTextField txtRazonSocial;
    private JTextField txtTelefono;
    private JTextField txtEmail;
    
    // Botones de Condición de Pago
    private ButtonGroup grupoPago;
    private JRadioButton btnContado;
    private JRadioButton btnCredito30;
    private JRadioButton btnAdelanto50;
    
    // Configuración
    private JSpinner spnDiasVigencia;
    private JCheckBox chkMostrarIGV;
    
    // Tabla de Productos
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    
    // Resumen
    private JLabel lblSubtotal;
    private JLabel lblIGV;
    private JLabel lblTotal;
    
    public CotizacionFormView(CotizacionController controller) {
        this.controller = controller;
        inicializarComponentes();
    }
    
    /**
     * Inicializa todos los componentes de la vista
     */
    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel principal con scroll
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBackground(Color.WHITE);
        
        // Agregar secciones
        panelPrincipal.add(crearEncabezado());
        panelPrincipal.add(Box.createVerticalStrut(20));
        panelPrincipal.add(crearSeccionCliente());
        panelPrincipal.add(Box.createVerticalStrut(15));
        panelPrincipal.add(crearSeccionCondicionPago());
        panelPrincipal.add(Box.createVerticalStrut(15));
        panelPrincipal.add(crearSeccionProductos());
        panelPrincipal.add(Box.createVerticalStrut(15));
        panelPrincipal.add(crearSeccionConfiguracion());
        panelPrincipal.add(Box.createVerticalStrut(15));
        panelPrincipal.add(crearSeccionResumen());
        panelPrincipal.add(Box.createVerticalStrut(20));
        panelPrincipal.add(crearBotonGenerar());
        
        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(panelPrincipal);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Crea el encabezado de la vista
     */
    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PRIMARIO);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblTitulo = new JLabel("Nueva Cotización");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel lblSubtitulo = new JLabel("InfleSusVentas SRL - Sistema de Gestión");
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
    
    /**
     * Crea sección de datos del cliente
     */
    private JPanel crearSeccionCliente() {
        JPanel panel = crearPanelSeccion("Datos del Cliente");
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // RUC
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        panel.add(new JLabel("RUC: *"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtRuc = new JTextField(20);
        txtRuc.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(txtRuc, gbc);
        
        // Razón Social
        gbc.gridx = 2; gbc.weightx = 0.3;
        panel.add(new JLabel("Razón Social: *"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.7;
        txtRazonSocial = new JTextField(30);
        txtRazonSocial.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(txtRazonSocial, gbc);
        
        // Teléfono
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        panel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtTelefono = new JTextField(15);
        txtTelefono.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(txtTelefono, gbc);
        
        // Email
        gbc.gridx = 2; gbc.weightx = 0.3;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.7;
        txtEmail = new JTextField(25);
        txtEmail.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(txtEmail, gbc);
        
        return panel;
    }
    
    /**
     * Crea sección de condición de pago con 3 botones
     */
    private JPanel crearSeccionCondicionPago() {
        JPanel panel = crearPanelSeccion("Condición de Pago");
        panel.setLayout(new GridLayout(1, 3, 15, 0));
        
        grupoPago = new ButtonGroup();
        
        // Botón 1: Contado
        btnContado = crearBotonPago("Pago al Contado", true);
        grupoPago.add(btnContado);
        panel.add(btnContado);
        
        // Botón 2: Crédito 30 días
        btnCredito30 = crearBotonPago("Crédito 30 días", false);
        grupoPago.add(btnCredito30);
        panel.add(btnCredito30);
        
        // Botón 3: 50% adelanto
        btnAdelanto50 = crearBotonPago("<html><center>50% adelanto<br>50% contra entrega</center></html>", false);
        grupoPago.add(btnAdelanto50);
        panel.add(btnAdelanto50);
        
        return panel;
    }
    
    private JRadioButton crearBotonPago(String texto, boolean seleccionado) {
        JRadioButton btn = new JRadioButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_PRIMARIO, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setSelected(seleccionado);

        if (seleccionado) {
            btn.setBackground(COLOR_PRIMARIO);
            btn.setForeground(Color.WHITE);
        } else{
            btn.setBackground(Color.WHITE);
            btn.setForeground(Color.BLACK);
        }

        // Efecto visual al seleccionar
        btn.addItemListener(e -> {
            if (btn.isSelected()) {
                btn.setBackground(COLOR_PRIMARIO);
                btn.setForeground(Color.WHITE);
            } else{
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.BLACK);
            }
        });

        return btn;
    }
    
    /**
     * Crea sección de productos
     */
    private JPanel crearSeccionProductos() {
        JPanel panel = crearPanelSeccion("Productos / Servicios");
        panel.setLayout(new BorderLayout(10, 10));
        
        // Tabla
        String[] columnas = {"Código", "Descripción", "Cantidad", "Unidad", "Precio Unit.", "Subtotal"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 5; // Subtotal no editable
            }
        };
        
        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaProductos.setRowHeight(30);
        tablaProductos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tablaProductos.getTableHeader().setBackground(COLOR_PRIMARIO);
        tablaProductos.getTableHeader().setForeground(Color.BLACK);
        
        // Agregar listener para calcular subtotales
        modeloTabla.addTableModelListener(e -> {
            int row = e.getFirstRow();
            if (row >= 0 && e.getColumn() >= 2 && e.getColumn() <= 4) {
                calcularSubtotalFila(row);
                calcularTotales();
            }
        });
        
        JScrollPane scrollTabla = new JScrollPane(tablaProductos);
        scrollTabla.setPreferredSize(new Dimension(0, 200));
        panel.add(scrollTabla, BorderLayout.CENTER);
        
        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotones.setBackground(Color.WHITE);
        
        JButton btnAgregar = new JButton("+ Agregar Producto");
        btnAgregar.setBackground(new Color(40, 167, 69));
        btnAgregar.setForeground(Color.BLACK);
        btnAgregar.setFont(new Font("Arial", Font.BOLD, 12));
        btnAgregar.setFocusPainted(false);
        btnAgregar.addActionListener(e -> agregarFilaProducto());
        
        JButton btnEliminar = new JButton("- Eliminar Seleccionado");
        btnEliminar.setBackground(new Color(220, 53, 69));
        btnEliminar.setForeground(Color.BLACK);
        btnEliminar.setFont(new Font("Arial", Font.BOLD, 12));
        btnEliminar.setFocusPainted(false);
        btnEliminar.addActionListener(e -> eliminarFilaProducto());
        
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        // Agregar primera fila por defecto
        agregarFilaProducto();
        
        return panel;
    }
    
    private void agregarFilaProducto() {
        modeloTabla.addRow(new Object[]{"", "", 1, "UND", 0.0, 0.0});
    }
    
    private void eliminarFilaProducto() {
        int selectedRow = tablaProductos.getSelectedRow();
        if (selectedRow != -1 && modeloTabla.getRowCount() > 1) {
            modeloTabla.removeRow(selectedRow);
            calcularTotales();
        } else if (modeloTabla.getRowCount() == 1) {
            JOptionPane.showMessageDialog(this, "Debe haber al menos un producto", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void calcularSubtotalFila(int row) {
        try {
            int cantidad = Integer.parseInt(modeloTabla.getValueAt(row, 2).toString());
            double precio = Double.parseDouble(modeloTabla.getValueAt(row, 4).toString());
            double subtotal = cantidad * precio;
            modeloTabla.setValueAt(subtotal, row, 5);
        } catch (Exception e) {
            modeloTabla.setValueAt(0.0, row, 5);
        }
    }
    
    /**
     * Crea sección de configuración
     */
    private JPanel crearSeccionConfiguracion() {
        JPanel panel = crearPanelSeccion("Configuración");
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        
        // Días de vigencia
        JLabel lblVigencia = new JLabel("Días de Vigencia:");
        lblVigencia.setFont(new Font("Arial", Font.BOLD, 13));
        spnDiasVigencia = new JSpinner(new SpinnerNumberModel(15, 1, 365, 1));
        spnDiasVigencia.setFont(new Font("Arial", Font.PLAIN, 14));
        ((JSpinner.DefaultEditor) spnDiasVigencia.getEditor()).getTextField().setColumns(5);
        
        // Checkbox IGV
        chkMostrarIGV = new JCheckBox("Mostrar precios con IGV", true);
        chkMostrarIGV.setFont(new Font("Arial", Font.BOLD, 13));
        chkMostrarIGV.setBackground(Color.WHITE);
        chkMostrarIGV.addActionListener(e -> calcularTotales());
        
        panel.add(lblVigencia);
        panel.add(spnDiasVigencia);
        panel.add(Box.createHorizontalStrut(30));
        panel.add(chkMostrarIGV);
        
        return panel;
    }
    
    /**
     * Crea sección de resumen
     */
    private JPanel crearSeccionResumen() {
        JPanel panel = crearPanelSeccion("Resumen");
        panel.setLayout(new GridLayout(3, 2, 10, 10));
        
        Font fontLabel = new Font("Arial", Font.BOLD, 14);
        Font fontValor = new Font("Arial", Font.PLAIN, 16);
        
        panel.add(crearLabelResumen("Subtotal:", fontLabel));
        lblSubtotal = crearLabelResumen("S/ 0.00", fontValor);
        panel.add(lblSubtotal);
        
        panel.add(crearLabelResumen("IGV (18%):", fontLabel));
        lblIGV = crearLabelResumen("S/ 0.00", fontValor);
        panel.add(lblIGV);
        
        JLabel lblTotalText = crearLabelResumen("TOTAL:", new Font("Arial", Font.BOLD, 16));
        lblTotalText.setForeground(COLOR_PRIMARIO);
        panel.add(lblTotalText);
        
        lblTotal = crearLabelResumen("S/ 0.00", new Font("Arial", Font.BOLD, 18));
        lblTotal.setForeground(COLOR_PRIMARIO);
        panel.add(lblTotal);
        
        return panel;
    }
    
    private JLabel crearLabelResumen(String texto, Font font) {
        JLabel label = new JLabel(texto);
        label.setFont(font);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }
    
    /**
     * Calcula totales
     */
    private void calcularTotales() {
        double subtotal = 0.0;
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            try {
                double subtotalFila = Double.parseDouble(modeloTabla.getValueAt(i, 5).toString());
                subtotal += subtotalFila;
            } catch (Exception e) {
                // Ignorar
            }
        }
        
        double igv = subtotal * 0.18;
        double total = chkMostrarIGV.isSelected() ? subtotal + igv : subtotal;
        
        lblSubtotal.setText(String.format("S/ %.2f", subtotal));
        lblIGV.setText(String.format("S/ %.2f", igv));
        lblTotal.setText(String.format("S/ %.2f", total));
    }
    
    /**
     * Crea botón de generar cotización
     */
    private JPanel crearBotonGenerar() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        
        JButton btnGenerar = new JButton("GENERAR COTIZACIÓN");
        btnGenerar.setFont(new Font("Arial", Font.BOLD, 18));
        btnGenerar.setPreferredSize(new Dimension(400, 50));
        btnGenerar.setBackground(COLOR_PRIMARIO);
        btnGenerar.setForeground(Color.BLACK);
        btnGenerar.setFocusPainted(false);
        btnGenerar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnGenerar.addActionListener(this::generarCotizacion);
        
        panel.add(btnGenerar);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        
        return panel;
    }
    
    /**
     * Genera la cotización
     */
    private void generarCotizacion(ActionEvent e) {
        try {
            // Validar campos
            if (!validarCampos()) {
                return;
            }
            
            // Crear cliente
            Cliente cliente = new Cliente(
                txtRuc.getText().trim(),
                txtRazonSocial.getText().trim(),
                null,
                txtTelefono.getText().trim(),
                txtEmail.getText().trim(),
                null
            );
            
            // Obtener condición de pago
            CondicionPago condicionPago = obtenerCondicionPagoSeleccionada();
            
            // Obtener productos
            List<ProductoCotizacion> productos = obtenerProductos();
            
            // Crear cotización
            Cotizacion cotizacion = new Cotizacion();
            cotizacion.setCliente(cliente);
            cotizacion.setProductos(productos);
            cotizacion.setCondicionPago(condicionPago);
            cotizacion.setDiasVigencia((Integer) spnDiasVigencia.getValue());
            cotizacion.setMostrarConIGV(chkMostrarIGV.isSelected());
            
            // Llamar al controlador
            String resultado = controller.generarCotizacion(cotizacion);
            
            JOptionPane.showMessageDialog(this,
                resultado,
                "Cotización Generada",
                JOptionPane.INFORMATION_MESSAGE);
            
            limpiarFormulario();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al generar cotización: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validarCampos() {
        if (txtRuc.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el RUC del cliente", 
                "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (txtRazonSocial.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la Razón Social", 
                "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Agregue al menos un producto", 
                "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private CondicionPago obtenerCondicionPagoSeleccionada() {
        if (btnContado.isSelected()) return CondicionPago.CONTADO;
        if (btnCredito30.isSelected()) return CondicionPago.CREDITO_30_DIAS;
        return CondicionPago.ADELANTO_50;
    }
    
    private List<ProductoCotizacion> obtenerProductos() {
        List<ProductoCotizacion> productos = new ArrayList<>();
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            ProductoCotizacion producto = new ProductoCotizacion(
                modeloTabla.getValueAt(i, 0).toString(),
                modeloTabla.getValueAt(i, 1).toString(),
                Integer.parseInt(modeloTabla.getValueAt(i, 2).toString()),
                modeloTabla.getValueAt(i, 3).toString(),
                Double.parseDouble(modeloTabla.getValueAt(i, 4).toString())
            );
            productos.add(producto);
        }
        return productos;
    }
    
    private void limpiarFormulario() {
        txtRuc.setText("");
        txtRazonSocial.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        btnContado.setSelected(true);
        spnDiasVigencia.setValue(15);
        chkMostrarIGV.setSelected(true);
        modeloTabla.setRowCount(0);
        agregarFilaProducto();
        calcularTotales();
    }
    
    private JPanel crearPanelSeccion(String titulo) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_FONDO, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(COLOR_PRIMARIO);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(lblTitulo, BorderLayout.NORTH);
        
        JPanel contenido = new JPanel();
        contenido.setBackground(Color.WHITE);
        panel.add(contenido, BorderLayout.CENTER);
        
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        
        return panel;
    }
}