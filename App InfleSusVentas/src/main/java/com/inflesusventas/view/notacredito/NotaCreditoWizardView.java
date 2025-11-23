package com.inflesusventas.view.notacredito;

import com.inflesusventas.controller.NotaCreditoController;
import com.inflesusventas.model.NotaCredito;
import com.inflesusventas.view.notacredito.NotaCreditoPaso2View;
import com.inflesusventas.util.ErrorHandler;
import javax.swing.*;
import java.awt.*;

/**
 * Vista principal del Wizard de Nota de Crédito (3 pasos)
 */
public class NotaCreditoWizardView extends JPanel {

    private static final Color COLOR_PRIMARIO = new Color(15, 65, 116);

    private NotaCreditoController controller;
    private CardLayout cardLayout;
    private JPanel panelPasos;

    private NotaCreditoPaso1View paso1;
    private NotaCreditoPaso2View paso2;
    private NotaCreditoPaso3View paso3;

    private int pasoActual = 1;

    // Labels para el indicador de pasos
    private JLabel lblPaso1, lblPaso2, lblPaso3;
    private JLabel lblFlecha1, lblFlecha2;

    public NotaCreditoWizardView(NotaCreditoController controller) {
        this.controller = controller;
        controller.iniciarNuevaNota();
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Encabezado
        JPanel panelEncabezado = new JPanel();
        panelEncabezado.setBackground(COLOR_PRIMARIO);
        panelEncabezado.setPreferredSize(new Dimension(0, 80));

        JLabel lblTitulo = new JLabel("EMISIÓN DE NOTAS DE CRÉDITO ELECTRÓNICAS");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        panelEncabezado.add(lblTitulo);

        add(panelEncabezado, BorderLayout.NORTH);

        // Indicador de progreso de pasos
        JPanel panelIndicadorPasos = crearIndicadorPasos();

        // Panel contenedor para encabezado + indicador
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelEncabezado, BorderLayout.NORTH);
        panelSuperior.add(panelIndicadorPasos, BorderLayout.CENTER);

        add(panelSuperior, BorderLayout.NORTH);

        // Panel de pasos con CardLayout
        cardLayout = new CardLayout();
        panelPasos = new JPanel(cardLayout);

        paso1 = new NotaCreditoPaso1View();
        paso2 = new NotaCreditoPaso2View(controller);
        paso3 = new NotaCreditoPaso3View();

        panelPasos.add(crearPanelPaso1(), "PASO1");
        panelPasos.add(crearPanelPaso2(), "PASO2");
        panelPasos.add(crearPanelPaso3(), "PASO3");

        // Mostrar explícitamente el primer paso
        cardLayout.show(panelPasos, "PASO1");

        add(panelPasos, BorderLayout.CENTER);
    }

    /**
     * Crea el indicador visual de pasos (1 → 2 → 3)
     */
    private JPanel crearIndicadorPasos() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBackground(new Color(240, 240, 240));

        // Paso 1
        lblPaso1 = crearLabelPaso("1. Datos Generales", true);
        panel.add(lblPaso1);

        // Flecha 1
        lblFlecha1 = new JLabel("→");
        lblFlecha1.setFont(new Font("Arial", Font.BOLD, 20));
        lblFlecha1.setForeground(Color.GRAY);
        panel.add(lblFlecha1);

        // Paso 2
        lblPaso2 = crearLabelPaso("2. Revisión de Ítems", false);
        panel.add(lblPaso2);

        // Flecha 2
        lblFlecha2 = new JLabel("→");
        lblFlecha2.setFont(new Font("Arial", Font.BOLD, 20));
        lblFlecha2.setForeground(Color.GRAY);
        panel.add(lblFlecha2);

        // Paso 3
        lblPaso3 = crearLabelPaso("3. Vista Previa", false);
        panel.add(lblPaso3);

        return panel;
    }

    /**
     * Crea un label para un paso del wizard
     */
    private JLabel crearLabelPaso(String texto, boolean activo) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        lbl.setOpaque(true);
        lbl.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        if (activo) {
            lbl.setBackground(COLOR_PRIMARIO);
            lbl.setForeground(Color.BLACK);
        } else {
            lbl.setBackground(Color.WHITE);
            lbl.setForeground(Color.GRAY);
        }

        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(activo ? COLOR_PRIMARIO : Color.LIGHT_GRAY, 2),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));

        return lbl;
    }

    /**
     * Actualiza el indicador visual de pasos
     */
    private void actualizarIndicadorPasos() {
        // Resetear todos los pasos
        actualizarEstiloPaso(lblPaso1, pasoActual == 1);
        actualizarEstiloPaso(lblPaso2, pasoActual == 2);
        actualizarEstiloPaso(lblPaso3, pasoActual == 3);
    }

    /**
     * Actualiza el estilo de un label de paso
     */
    private void actualizarEstiloPaso(JLabel lbl, boolean activo) {
        if (activo) {
            lbl.setBackground(COLOR_PRIMARIO);
            lbl.setForeground(Color.BLACK);
            lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_PRIMARIO, 2),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        } else {
            lbl.setBackground(Color.WHITE);
            lbl.setForeground(Color.GRAY);
            lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        }
    }

    private JPanel crearPanelPaso1() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(paso1, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton btnContinuar = new JButton("Continuar →");
        btnContinuar.setPreferredSize(new Dimension(150, 35));
        btnContinuar.setBackground(COLOR_PRIMARIO);
        btnContinuar.setForeground(Color.BLACK);
        btnContinuar.addActionListener(e -> irAPaso2());

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(150, 35));
        btnCancelar.addActionListener(e -> cancelar());

        panelBotones.add(btnContinuar);
        panelBotones.add(btnCancelar);

        panel.add(panelBotones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelPaso2() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(paso2, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton btnRetroceder = new JButton("← Retroceder");
        btnRetroceder.setPreferredSize(new Dimension(150, 35));
        btnRetroceder.addActionListener(e -> retrocederAPaso1());

        JButton btnContinuar = new JButton("Continuar →");
        btnContinuar.setPreferredSize(new Dimension(150, 35));
        btnContinuar.setBackground(COLOR_PRIMARIO);
        btnContinuar.setForeground(Color.BLACK);
        btnContinuar.addActionListener(e -> irAPaso3());

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(150, 35));
        btnCancelar.addActionListener(e -> cancelar());

        panelBotones.add(btnRetroceder);
        panelBotones.add(btnContinuar);
        panelBotones.add(btnCancelar);

        panel.add(panelBotones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelPaso3() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(paso3, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton btnRetroceder = new JButton("← Retroceder");
        btnRetroceder.setPreferredSize(new Dimension(150, 35));
        btnRetroceder.addActionListener(e -> retrocederAPaso2());

        JButton btnEmitir = new JButton("EMITIR NC");
        btnEmitir.setPreferredSize(new Dimension(180, 40));
        btnEmitir.setBackground(new Color(40, 167, 69));
        btnEmitir.setForeground(Color.BLACK);
        btnEmitir.setFont(new Font("Arial", Font.BOLD, 14));
        btnEmitir.addActionListener(e -> emitirNC());

        JButton btnCancelar = new JButton("Cerrar");
        btnCancelar.setPreferredSize(new Dimension(150, 35));
        btnCancelar.addActionListener(e -> cancelar());

        panelBotones.add(btnRetroceder);
        panelBotones.add(btnEmitir);
        panelBotones.add(btnCancelar);

        panel.add(panelBotones, BorderLayout.SOUTH);
        return panel;
    }

    private void irAPaso2() {
        if (!paso1.validarCampos())
            return;

        try {
            // Guardar datos del paso 1
            // Guardar datos del paso 1
            java.util.Date fechaUtil = paso1.getFechaEmision();
            java.time.LocalDate fechaLocal = fechaUtil.toInstant().atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();

            controller.getNotaActual().setFechaEmision(fechaLocal);
            controller.getNotaActual().setTipoNotaCredito(paso1.getTipoNotaCredito());
            controller.getNotaActual().setNumeroFacturaRef(paso1.getNumeroFacturaRef());
            controller.getNotaActual().setMotivoSustento(paso1.getMotivoSustento());

            // Cargar ítems de la factura (si cambió la referencia)
            boolean cargada = controller.cargarFacturaReferencia(paso1.getNumeroFacturaRef());

            if (cargada) {
                paso2.cargarItems(controller.getNotaActual().getItems());
            }

            paso2.mostrarResumen(
                    paso1.getTipoNotaCredito(),
                    paso1.getNumeroFacturaRef(),
                    paso1.getMotivoSustento());

            cardLayout.show(panelPasos, "PASO2");
            pasoActual = 2;
            actualizarIndicadorPasos();

        } catch (Exception ex) {
            ErrorHandler.mostrarError(this, "Error al cargar factura", ex);
        }
    }

    private void irAPaso3() {
        paso3.cargarVistaPreviaNc(controller.getNotaActual());
        cardLayout.show(panelPasos, "PASO3");
        pasoActual = 3;
        actualizarIndicadorPasos();
    }

    private void retrocederAPaso1() {
        cardLayout.show(panelPasos, "PASO1");
        pasoActual = 1;
        actualizarIndicadorPasos();
    }

    private void retrocederAPaso2() {
        cardLayout.show(panelPasos, "PASO2");
        pasoActual = 2;
        actualizarIndicadorPasos();
    }

    private void emitirNC() {
        if (!ErrorHandler.confirmar(this, "¿Está seguro de emitir esta Nota de Crédito?")) {
            return;
        }

        try {
            NotaCredito notaEmitida = controller.emitirNotaCredito();

            if (notaEmitida != null) {
                String mensaje = "Nota de Crédito emitida correctamente.\n" +
                        "Serie: " + notaEmitida.getSerie() + "-" + notaEmitida.getNumero() + "\n\n" +
                        "Archivos generados:\n" +
                        "PDF: " + notaEmitida.getRutaPdf() + "\n" +
                        "XML: " + notaEmitida.getRutaXml();

                String[] opciones = { "Ver PDF", "Cerrar" };
                int opcion = ErrorHandler.mostrarOpciones(this, mensaje, opciones);

                if (opcion == 0) {
                    // Ver PDF
                    if (notaEmitida.getRutaPdf() != null) {
                        abrirPDF(notaEmitida.getRutaPdf());
                    } else {
                        ErrorHandler.mostrarAdvertencia(this, "No se encontró la ruta del PDF.");
                    }
                }
            } else {
                throw new Exception("Error al emitir la nota (el controlador devolvió null).");
            }

            // Reiniciar wizard (ya lo hace el controller, pero limpiamos vista)
            paso1.limpiarFormulario();
            cardLayout.show(panelPasos, "PASO1");
            pasoActual = 1;
            actualizarIndicadorPasos();

        } catch (Exception ex) {
            ErrorHandler.mostrarError(this, "Error al generar NC", ex);
        }
    }

    private void cancelar() {
        if (ErrorHandler.confirmar(this, "¿Cancelar la emisión de la Nota de Crédito?")) {
            controller.iniciarNuevaNota();
            paso1.limpiarFormulario();
            cardLayout.show(panelPasos, "PASO1");
            pasoActual = 1;
            actualizarIndicadorPasos();
        }
    }

    private void abrirPDF(String rutaPdf) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new java.io.File(rutaPdf));
            }
        } catch (Exception e) {
            ErrorHandler.mostrarError(this, "No se pudo abrir el PDF", e);
        }
    }
}
