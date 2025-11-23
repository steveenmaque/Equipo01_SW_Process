package com.inflesusventas.view.notacredito;

import com.inflesusventas.model.NotaCredito;
import com.inflesusventas.model.ItemNotaCredito;
import com.inflesusventas.service.NotaCreditoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Vista de historial de Notas de Crédito emitidas
 */
public class NotaCreditoListView extends JPanel {

    private static final Color COLOR_PRIMARIO = new Color(15, 65, 116);
    private static final Color COLOR_EXITO = new Color(40, 167, 69);
    private static final Color COLOR_ERROR = new Color(220, 53, 69);

    private JTable table;
    private DefaultTableModel model;
    private NotaCreditoService notaCreditoService;

    public NotaCreditoListView(NotaCreditoService notaCreditoService) {
        this.notaCreditoService = notaCreditoService;
        inicializarComponentes();
        cargarDatos();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Panel de encabezado
        JPanel panelEncabezado = new JPanel(new BorderLayout());
        panelEncabezado.setBackground(COLOR_PRIMARIO);
        panelEncabezado.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("📋 Historial de Notas de Crédito Emitidas");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        panelEncabezado.add(lblTitulo, BorderLayout.WEST);

        add(panelEncabezado, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {
                "Serie-Número",
                "Fecha Emisión",
                "Factura Ref.",
                "RUC Cliente",
                "Razón Social",
                "Motivo",
                "Total",
                "Estado SUNAT"
        };

        model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(230, 230, 230));

        // Ajustar anchos de columnas
        table.getColumnModel().getColumn(0).setPreferredWidth(120); // Serie-Número
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // Fecha
        table.getColumnModel().getColumn(2).setPreferredWidth(120); // Factura Ref
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // RUC
        table.getColumnModel().getColumn(4).setPreferredWidth(200); // Razón Social
        table.getColumnModel().getColumn(5).setPreferredWidth(250); // Motivo
        table.getColumnModel().getColumn(6).setPreferredWidth(100); // Total
        table.getColumnModel().getColumn(7).setPreferredWidth(120); // Estado

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        panelBotones.setBackground(Color.WHITE);

        JButton btnRefrescar = new JButton("🔄 Actualizar Lista");
        btnRefrescar.setFont(new Font("Arial", Font.PLAIN, 14));
        btnRefrescar.setPreferredSize(new Dimension(180, 35));
        btnRefrescar.setBackground(COLOR_PRIMARIO);
        btnRefrescar.setForeground(Color.WHITE);
        btnRefrescar.setFocusPainted(false);
        btnRefrescar.addActionListener(e -> cargarDatos());

        JButton btnVerPDF = new JButton("📄 Ver PDF");
        btnVerPDF.setFont(new Font("Arial", Font.PLAIN, 14));
        btnVerPDF.setPreferredSize(new Dimension(150, 35));
        btnVerPDF.setBackground(COLOR_EXITO);
        btnVerPDF.setForeground(Color.WHITE);
        btnVerPDF.setFocusPainted(false);
        btnVerPDF.addActionListener(e -> verPDFSeleccionado());

        JButton btnVerXML = new JButton("📝 Ver XML");
        btnVerXML.setFont(new Font("Arial", Font.PLAIN, 14));
        btnVerXML.setPreferredSize(new Dimension(150, 35));
        btnVerXML.setBackground(new Color(255, 193, 7));
        btnVerXML.setForeground(Color.BLACK);
        btnVerXML.setFocusPainted(false);
        btnVerXML.addActionListener(e -> verXMLSeleccionado());

        panelBotones.add(btnRefrescar);
        panelBotones.add(btnVerPDF);
        panelBotones.add(btnVerXML);

        add(panelBotones, BorderLayout.SOUTH);
    }

    public void cargarDatos() {
        model.setRowCount(0);
        List<NotaCredito> lista = notaCreditoService.obtenerTodasLasNC();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (NotaCredito nc : lista) {
            // Truncar motivo si es muy largo
            String motivoCorto = nc.getMotivoSustento();
            if (motivoCorto != null && motivoCorto.length() > 50) {
                motivoCorto = motivoCorto.substring(0, 47) + "...";
            }

            model.addRow(new Object[] {
                    nc.getSerie() + "-" + String.format("%08d", nc.getNumero()),
                    nc.getFechaEmision() != null ? nc.getFechaEmision().format(formatter) : "-",
                    nc.getNumeroFacturaRef() != null ? nc.getNumeroFacturaRef() : "-",
                    nc.getRucCliente() != null ? nc.getRucCliente() : "-",
                    nc.getRazonSocialCliente() != null ? nc.getRazonSocialCliente() : "-",
                    motivoCorto != null ? motivoCorto : "-",
                    String.format("S/ %.2f", nc.getTotal()),
                    nc.getEstadoSunat() != null ? nc.getEstadoSunat() : "PENDIENTE"
            });
        }

        // Mensaje si no hay datos
        if (lista.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay notas de crédito emitidas aún.",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void verPDFSeleccionado() {
        int filaSeleccionada = table.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione una nota de crédito de la lista.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<NotaCredito> lista = notaCreditoService.obtenerTodasLasNC();
        if (filaSeleccionada < lista.size()) {
            NotaCredito nc = lista.get(filaSeleccionada);
            if (nc.getRutaPdf() != null && !nc.getRutaPdf().isEmpty()) {
                try {
                    Desktop.getDesktop().open(new java.io.File(nc.getRutaPdf()));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error al abrir el PDF: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se encontró la ruta del PDF para esta nota de crédito.",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void verXMLSeleccionado() {
        int filaSeleccionada = table.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione una nota de crédito de la lista.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<NotaCredito> lista = notaCreditoService.obtenerTodasLasNC();
        if (filaSeleccionada < lista.size()) {
            NotaCredito nc = lista.get(filaSeleccionada);
            if (nc.getRutaXml() != null && !nc.getRutaXml().isEmpty()) {
                try {
                    Desktop.getDesktop().open(new java.io.File(nc.getRutaXml()));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error al abrir el XML: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se encontró la ruta del XML para esta nota de crédito.",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
