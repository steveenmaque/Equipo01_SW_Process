package com.inflesusventas;

import com.inflesusventas.controller.CotizacionController;
import com.inflesusventas.view.MainWindow;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.swing.*;

/**
 * Clase principal de la aplicación InfleSusVentas - VERSIÓN DESKTOP
 * Sistema de gestión de cotizaciones y comprobantes electrónicos
 * 
 * Ruta: src/main/java/com/inflesusventas/InfleSusVentasApplication.java
 * 
 * @author InfleSusVentas Team
 * @version 1.0
 */
@SpringBootApplication
public class InfleSusVentasApplication {

    public static void main(String[] args) {
        System.out.println("  InfleSusVentas - Sistema Desktop");
        System.out.println("  Iniciando aplicación...");
        
        // Iniciar contexto de Spring Boot (sin servidor web)
        System.setProperty("java.awt.headless", "false");
        SpringApplication app = new SpringApplication(InfleSusVentasApplication.class);
        app.setHeadless(false); // Importante para aplicaciones Swing
        ApplicationContext context = app.run(args);
        
        // Iniciar interfaz gráfica en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            try {
                // Establecer Look and Feel del sistema operativo
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                // Obtener el controlador desde Spring
                CotizacionController controller = context.getBean(CotizacionController.class);
                
                // Crear y mostrar ventana principal
                MainWindow mainWindow = new MainWindow(controller);
                mainWindow.setVisible(true);
                
                System.out.println("✓ Interfaz gráfica iniciada correctamente");
                
            } catch (Exception e) {
                System.err.println("✗ Error al iniciar interfaz gráfica: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Error al iniciar la aplicación:\n" + e.getMessage(),
                    "Error de Inicio",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        System.out.println("===========================================");
        System.out.println("  Sistema iniciado correctamente");
        System.out.println("  Modo: DESKTOP (Sin servidor web)");
        System.out.println("  Documentos: Almacenamiento local");
        System.out.println("===========================================");
    }
}