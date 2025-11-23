package com.inflesusventas.view.notacredito;

import com.inflesusventas.controller.NotaCreditoController;
import com.inflesusventas.model.ItemNotaCredito;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class NotaCreditoPaso2View extends JPanel {
    // Vista para el paso 2 del wizard

    private NotaCreditoController controller;
    private JTable tablaItems;
    private DefaultTableModel modeloTabla;
    private JLabel lblResumen;

    public NotaCreditoPaso2View(NotaCreditoController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // Título
        JLabel lblTitulo = new JLabel("Paso 2: Revisión y Corrección de Ítems");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblTitulo, BorderLayout.NORTH);

        // Panel Central
        JPanel panelCentral = new JPanel(new BorderLayout());

        // Resumen
        lblResumen = new JLabel("Resumen de la operación...");
        lblResumen.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panelCentral.add(lblResumen, BorderLayout.NORTH);

        // Tabla
        String[] columnas = { "Cant", "Unidad", "Descripción Original", "Corrección (DICE / DEBE DECIR)", "V. Unit",
                "Subtotal" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Solo editable la columna de corrección
            }
        };
        tablaItems = new JTable(modeloTabla);
        tablaItems.setRowHeight(25);

        // Listener para guardar cambios en la descripción
        modeloTabla.addTableModelListener(e -> {
            if (e.getColumn() == 3) {
                int row = e.getFirstRow();
                String nuevaDesc = (String) modeloTabla.getValueAt(row, 3);
                controller.guardarEdicionItem(row, nuevaDesc);
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaItems);
        panelCentral.add(scrollTabla, BorderLayout.CENTER);

        // Instrucciones
        JLabel lblInstruccion = new JLabel(
                "<html><font color='blue'>Nota:</font> Edite la columna 'Corrección' solo si necesita modificar la descripción del ítem (ej: 'DICE: X / DEBE DECIR: Y').</html>");
        panelCentral.add(lblInstruccion, BorderLayout.SOUTH);

        add(panelCentral, BorderLayout.CENTER);
    }

    public void cargarItems(List<ItemNotaCredito> items) {
        modeloTabla.setRowCount(0);
        for (ItemNotaCredito item : items) {
            Object[] fila = {
                    item.getCantidad(),
                    item.getUnidadMedida(),
                    item.getDescripcionOriginal(),
                    item.getDescripcionCorregida() != null ? item.getDescripcionCorregida() : "",
                    String.format("%.2f", item.getValorUnitario()),
                    String.format("%.2f", item.getSubtotal())
            };
            modeloTabla.addRow(fila);
        }
    }

    public void mostrarResumen(String tipo, String factura, String motivo) {
        lblResumen.setText(String.format("<html><b>Tipo:</b> %s | <b>Factura:</b> %s<br><b>Motivo:</b> %s</html>",
                tipo, factura, motivo));
    }
}
