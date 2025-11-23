package com.inflesusventas.view.cliente;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ClienteListView extends JPanel {
    
    public JTable tabla;
    public DefaultTableModel modelo;

    public ClienteListView() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // --- ENCABEZADO ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(new Color(15, 65, 116)); // Azul corporativo
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitulo = new JLabel("Cartera de Clientes y Ventas");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        
        JLabel lblSub = new JLabel("Historial unificado de operaciones");
        lblSub.setForeground(Color.LIGHT_GRAY);
        
        JPanel pnlTitulos = new JPanel(new GridLayout(2,1));
        pnlTitulos.setOpaque(false);
        pnlTitulos.add(lblTitulo);
        pnlTitulos.add(lblSub);
        
        pnlHeader.add(pnlTitulos, BorderLayout.CENTER);
        add(pnlHeader, BorderLayout.NORTH);

        // --- TABLA ---
        // Definimos las columnas
        String[] columnas = {"Fecha", "RUC", "Razón Social", "Resumen Productos", "Monto (S/)", "Acción"};
        
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Solo la columna 5 (El botón) es editable para poder hacer click
                return column == 5;
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(40); // Altura cómoda para el botón
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Ajuste de anchos
        tabla.getColumnModel().getColumn(0).setPreferredWidth(90);  // Fecha
        tabla.getColumnModel().getColumn(1).setPreferredWidth(100); // RUC
        tabla.getColumnModel().getColumn(2).setPreferredWidth(200); // Razón Social
        tabla.getColumnModel().getColumn(3).setPreferredWidth(300); // Productos
        tabla.getColumnModel().getColumn(4).setPreferredWidth(90);  // Monto
        tabla.getColumnModel().getColumn(5).setPreferredWidth(120); // Botón

        // --- MAGIA: AGREGAR EL BOTÓN A LA TABLA ---
        // Asignamos el Renderer (Cómo se ve) y el Editor (Qué hace al click)
        tabla.getColumnModel().getColumn(5).setCellRenderer(new BotonRenderer());
        tabla.getColumnModel().getColumn(5).setCellEditor(new BotonEditor(new JCheckBox()));

        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }
    
    // ========================================================================
    // CLASES INTERNAS PARA EL BOTÓN EN LA TABLA
    // ========================================================================
    
    // 1. RENDERER: Dibuja el botón en la celda
    class BotonRenderer extends JButton implements TableCellRenderer {
        public BotonRenderer() {
            setOpaque(true);
            setText("Ver Cotización");
            setFont(new Font("Arial", Font.BOLD, 11));
            setBackground(new Color(230, 130, 70)); // Naranja
            setForeground(Color.WHITE);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // 2. EDITOR: Maneja el evento del click
    class BotonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private JTable table;

        public BotonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setText("Ver Cotización");
            button.setFont(new Font("Arial", Font.BOLD, 11));
            button.setBackground(new Color(230, 130, 70));
            button.setForeground(Color.WHITE);
            
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.table = table;
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // AQUÍ OCURRE LA ACCIÓN DEL CLICK
                // Avisamos a la tabla que se hizo click en esta fila
                // Disparamos un evento personalizado o manejamos aquí
                int row = table.getSelectedRow();
                // Usamos el cliente (ClienteController) para manejar la lógica
                // Pero como estamos en la vista, lanzaremos un ActionEvent ficticio
                // que el Controller pueda escuchar si quisiera, o simplemente
                // imprimimos en consola. 
                // NOTA: La lógica real se conecta en el Controller a través del modelo.
            }
            isPushed = false;
            return "Ver Cotización";
        }
    }
    
    // Método para permitir al controlador escuchar el botón (Simplificado para Swing)
    // El truco es pasarle la acción al editor.
    public void setBotonAccion(ActionListener action) {
        // Esta es una implementación avanzada. Para hacerlo simple en tu proyecto,
        // manejaremos el click directamente en el Editor dentro del Controlador.
    }
}