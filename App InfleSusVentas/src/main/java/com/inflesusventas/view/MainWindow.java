package com.inflesusventas.view;

import com.inflesusventas.controller.CotizacionController;
import com.inflesusventas.controller.GuiaRemisionController;
import com.inflesusventas.service.ClienteService;
import com.inflesusventas.service.ComprobanteService;
import com.inflesusventas.view.cotizacion.CotizacionFormView;
import com.inflesusventas.view.comprobante.ComprobanteFormView;
import com.inflesusventas.view.comprobante.ComprobanteListView;
import com.inflesusventas.view.comprobante.ComprobanteListView;
import com.inflesusventas.view.guia.GuiaFormView;
import com.inflesusventas.view.notacredito.NotaCreditoWizardView;
import com.inflesusventas.view.notacredito.NotaCreditoListView;
import com.inflesusventas.controller.NotaCreditoController; // Importar Controller
import com.inflesusventas.service.NotaCreditoService;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal de la aplicación Desktop
 * Contiene el menú de navegación y el área de contenido
 *
 * Ruta: src/main/java/com/inflesusventas/view/MainWindow.java
 */
public class MainWindow extends JFrame {

    private static final Color COLOR_PRIMARIO = new Color(15, 65, 116);
    private static final Color COLOR_MENU = new Color(230, 130, 70);

    private CotizacionController cotizacionController;
    private ApplicationContext context; // AGREGADO: Contexto de Spring
    private JPanel panelContenido;

    // MODIFICADO: Constructor ahora recibe ApplicationContext
    public MainWindow(CotizacionController cotizacionController, ApplicationContext context) {
        this.cotizacionController = cotizacionController;
        this.context = context; // AGREGADO
        inicializarVentana();
    }

    /**
     * Inicializa la ventana principal
     */
    private void inicializarVentana() {
        setTitle("InfleSusVentas - Sistema de Gestión Desktop");
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Icono de la aplicación (si existe)
        try {
            Image icon = Toolkit.getDefaultToolkit().getImage(
                    getClass().getResource("/images/icon.png"));
            setIconImage(icon);
        } catch (Exception e) {
            // Icono no encontrado, continuar sin icono
        }

        // Layout principal
        setLayout(new BorderLayout());

        // Agregar componentes
        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearMenuLateral(), BorderLayout.WEST);
        add(crearPanelContenido(), BorderLayout.CENTER);
        add(crearBarraInferior(), BorderLayout.SOUTH);
    }

    /**
     * Crea la barra superior con logo y título
     */
    private JPanel crearBarraSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PRIMARIO);
        panel.setPreferredSize(new Dimension(0, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Logo y título
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelTitulo.setBackground(COLOR_PRIMARIO);

        JLabel lblLogo = new JLabel(""); // Falta colocar el logo de la empresa
        lblLogo.setFont(new Font("Arial", Font.PLAIN, 40));

        JLabel lblTitulo = new JLabel("InfleSusVentas SRL");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(COLOR_MENU);

        JLabel lblSubtitulo = new JLabel("Sistema de Gestión Desktop");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitulo.setForeground(new Color(220, 220, 220));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setBackground(COLOR_PRIMARIO);
        textos.add(lblTitulo);
        textos.add(lblSubtitulo);

        panelTitulo.add(lblLogo);
        panelTitulo.add(Box.createHorizontalStrut(15));
        panelTitulo.add(textos);

        // Información de usuario (opcional)
        JPanel panelUsuario = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelUsuario.setBackground(COLOR_PRIMARIO);

        JLabel lblUsuario = new JLabel("Usuario: Administrador");
        lblUsuario.setForeground(Color.BLACK);
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 12));

        panelUsuario.add(lblUsuario);

        panel.add(panelTitulo, BorderLayout.WEST);
        panel.add(panelUsuario, BorderLayout.EAST);

        return panel;
    }

    /**
     * Crea el menú lateral de navegación
     */
    private JPanel crearMenuLateral() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_MENU);
        panel.setPreferredSize(new Dimension(250, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Botones del menú
        panel.add(crearBotonMenu("Inicio", this::mostrarInicio));
        panel.add(Box.createVerticalStrut(5));
        panel.add(crearBotonMenu("Nueva Cotización", this::mostrarCotizacion));
        panel.add(Box.createVerticalStrut(5));
        panel.add(crearBotonMenu("Clientes", this::mostrarClientes));
        panel.add(Box.createVerticalStrut(5));
        panel.add(crearBotonMenu("Ventas", this::mostrarVentas));
        panel.add(Box.createVerticalStrut(5));
        panel.add(crearBotonMenu("Comprobantes", this::mostrarComprobantes));
        panel.add(Box.createVerticalStrut(5));
        panel.add(crearBotonMenu("Guías de Remisión", this::mostrarGuias));
        panel.add(Box.createVerticalStrut(5));
        panel.add(crearBotonMenu("Notas de Crédito", this::mostrarNotasCredito));
        panel.add(Box.createVerticalStrut(5));
        panel.add(crearBotonMenu("  → Historial NC", this::mostrarHistorialNC));
        panel.add(Box.createVerticalStrut(5));
        panel.add(crearBotonMenu("Reportes", this::mostrarReportes));
        panel.add(Box.createVerticalStrut(5));
        panel.add(crearBotonMenu("Configuración", this::mostrarConfiguracion));

        // Espaciador
        panel.add(Box.createVerticalGlue());

        // Botón de salir
        panel.add(crearBotonMenu("Salir", this::salir));

        return panel;
    }

    /**
     * Crea un botón del menú
     */
    private JButton crearBotonMenu(String texto, Runnable accion) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setForeground(Color.BLACK);
        btn.setBackground(COLOR_MENU);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_PRIMARIO);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_MENU);
            }
        });

        btn.addActionListener(e -> accion.run());

        return btn;
    }

    /**
     * Crea el panel de contenido principal
     */
    private JPanel crearPanelContenido() {
        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBackground(Color.WHITE);

        // Mostrar pantalla de inicio por defecto
        mostrarInicio();

        return panelContenido;
    }

    /**
     * Crea la barra inferior con información
     */
    private JPanel crearBarraInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 249, 250));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));

        JLabel lblEstado = new JLabel("✓ Sistema listo | Modo: Desktop | Almacenamiento: Local");
        lblEstado.setFont(new Font("Arial", Font.PLAIN, 11));

        JLabel lblVersion = new JLabel("v1.0.0");
        lblVersion.setFont(new Font("Arial", Font.PLAIN, 11));
        lblVersion.setForeground(Color.BLACK);

        panel.add(lblEstado, BorderLayout.WEST);
        panel.add(lblVersion, BorderLayout.EAST);

        return panel;
    }

    // ==================== MÉTODOS DE NAVEGACIÓN ====================

    private void mostrarInicio() {
        panelContenido.removeAll();

        JPanel panelInicio = new JPanel(new BorderLayout());
        panelInicio.setBackground(Color.WHITE);

        JLabel lblBienvenida = new JLabel(
                "<html><center>" +
                        "<h1 style='color: #667eea;'>Bienvenido a InfleSusVentas</h1>" +
                        "<p style='font-size: 14px;'>Sistema de Gestión de Cotizaciones y Comprobantes Electrónicos</p>"
                        +
                        "<br><br>" +
                        "<p>Seleccione una opción del menú lateral para comenzar</p>" +
                        "</center></html>");
        lblBienvenida.setHorizontalAlignment(SwingConstants.CENTER);

        panelInicio.add(lblBienvenida, BorderLayout.CENTER);

        panelContenido.add(panelInicio);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    private void mostrarCotizacion() {
        panelContenido.removeAll();
        panelContenido.add(new CotizacionFormView(cotizacionController));
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    private void mostrarClientes() {
        try {
            panelContenido.removeAll();

            // Obtenemos el controlador (Spring se encarga de inyectarle el
            // ComprobanteService)
            com.inflesusventas.controller.ClienteController clienteCtrl = context
                    .getBean(com.inflesusventas.controller.ClienteController.class);

            panelContenido.add(clienteCtrl.getView());

        } catch (Exception e) {
            e.printStackTrace();
        }
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    private void mostrarVentas() {
        mostrarEnConstruccion("Módulo de Ventas");
    }

    private void mostrarComprobantes() {
        panelContenido.removeAll();

        try {
            // 1. Obtener los servicios necesarios
            ClienteService clienteService = context.getBean(ClienteService.class);
            ComprobanteService compService = context.getBean(ComprobanteService.class);

            // 2. Crear la vista NUEVA (esto fuerza la recarga de datos)
            ComprobanteFormView vistaComprobantes = new ComprobanteFormView(
                    cotizacionController,
                    clienteService,
                    compService);

            // 3. IMPORTANTE: Forzar la lectura de datos explícitamente
            vistaComprobantes.cargarProductosDesdeCotizacion();

            panelContenido.add(vistaComprobantes);
            System.out.println("✓ Vista de comprobantes actualizada");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar comprobantes: " + e.getMessage());
        }

        panelContenido.revalidate();
        panelContenido.repaint();
    }

    // MODIFICADO: Método para mostrar Guías de Remisión
    private void mostrarGuias() {
        panelContenido.removeAll();

        try {
            // Obtener el controlador de guías desde Spring
            GuiaRemisionController guiaController = context.getBean(GuiaRemisionController.class);
            panelContenido.add(new GuiaFormView(guiaController));

            System.out.println("✓ Módulo de Guías de Remisión cargado correctamente");

        } catch (Exception e) {
            System.err.println("✗ Error al cargar módulo de guías: " + e.getMessage());
            e.printStackTrace();

            JOptionPane.showMessageDialog(this,
                    "Error al cargar el módulo de Guías de Remisión:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);

            // Mostrar pantalla de error
            mostrarEnConstruccion("Módulo de Guías de Remisión (Error al cargar)");
        }

        panelContenido.revalidate();
        panelContenido.repaint();
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    private void mostrarNotasCredito() {
        System.out.println("👉 Intentando abrir módulo Notas de Crédito...");
        panelContenido.removeAll();
        try {
            NotaCreditoController ncController = context.getBean(NotaCreditoController.class);
            if (ncController == null) {
                System.err.println("❌ Error: NotaCreditoController es NULL");
                JOptionPane.showMessageDialog(this, "Error interno: Controlador no encontrado.");
                return;
            }
            System.out.println("✓ Controlador obtenido: " + ncController);

            NotaCreditoWizardView view = new NotaCreditoWizardView(ncController);
            panelContenido.add(view, BorderLayout.CENTER); // Especificar CENTER explícitamente
            System.out.println("✓ Vista agregada al panel");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Excepción al abrir módulo NC: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error cargando módulo NC: " + e.getMessage());
        }
        panelContenido.revalidate();
        panelContenido.repaint();
        System.out.println("✓ Panel repintado");
    }

    private void mostrarHistorialComprobantes() {
        panelContenido.removeAll();
        ComprobanteService compService = context.getBean(ComprobanteService.class);
        panelContenido.add(new ComprobanteListView(compService));
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    private void mostrarHistorialNC() {
        panelContenido.removeAll();
        try {
            NotaCreditoService ncService = context.getBean(NotaCreditoService.class);
            panelContenido.add(new NotaCreditoListView(ncService));
            System.out.println("✓ Historial de Notas de Crédito cargado");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error cargando historial NC: " + e.getMessage());
        }
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    private void mostrarReportes() {
        mostrarEnConstruccion("Módulo de Reportes");
    }

    private void mostrarConfiguracion() {
        mostrarEnConstruccion("Módulo de Configuración");
    }

    private void mostrarEnConstruccion(String modulo) {
        panelContenido.removeAll();

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(
                "<html><center>" +
                        "<h2 style='color: #667eea;'>🚧 " + modulo + "</h2>" +
                        "<p>Este módulo está en construcción</p>" +
                        "</center></html>");
        lbl.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(lbl, BorderLayout.CENTER);

        panelContenido.add(panel);
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    private void salir() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea salir de la aplicación?",
                "Confirmar Salida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            System.out.println("===========================================");
            System.out.println("  InfleSusVentas - Sistema cerrado");
            System.out.println("===========================================");
            System.exit(0);
        }
    }
}