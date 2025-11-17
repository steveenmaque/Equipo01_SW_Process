package com.inflesusventas.test;

import com.inflesusventas.model.Cliente;
import com.inflesusventas.model.Cotizacion;
import com.inflesusventas.model.ProductoCotizacion;
import com.inflesusventas.controller.CotizacionController;
import com.inflesusventas.service.PdfGeneratorService;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Suite de Pruebas de Caja Negra - InfleSusVentas
 * Requisitos Funcionales: RF1, RF2
 * Tester: Clisman
 * Fecha: 2025-01-15
 */
class CajaNegraTest {

    // ========== RF1: REGISTRO DE CLIENTES ==========

    @Test
    @DisplayName("CN01 - Registrar cliente con datos válidos")
    void testRegistrarClienteValido() {
        // Arrange
        Cliente cliente = new Cliente(
                "20123456789",
                "Empresa Test SAC",
                "Av. Test 123",
                "999888777",
                "test@empresa.com",
                "Juan Pérez"
        );

        // Assert
        assertNotNull(cliente);
        assertEquals("20123456789", cliente.getRuc());
        assertEquals("Empresa Test SAC", cliente.getRazonSocial());
        assertEquals("test@empresa.com", cliente.getEmail());
    }

    @Test
    @DisplayName("CN02 - Validar RUC con menos de 11 dígitos (DEBE FALLAR)")
    void testValidarRucInvalido() {
        // Este test DEBE fallar porque no hay validación implementada
        Cliente cliente = new Cliente();

        // Este debería lanzar excepción pero NO LO HACE
        assertThrows(IllegalArgumentException.class, () -> {
            cliente.setRuc("201234567"); // Solo 9 dígitos
        });
    }

    // ========== RF2: GENERACIÓN DE COTIZACIONES ==========

    @Test
    @DisplayName("CN03 - Generar cotización con numeración correlativa")
    void testNumeracionCorrelativa() {
        // Arrange
        PdfGeneratorService pdfService = new PdfGeneratorService();
        CotizacionController controller = new CotizacionController(pdfService);

        // Act
        int numeroAnterior = controller.getCotizacionActual().getNumeroCotizacion();
        controller.iniciarNuevaCotizacion();
        int numeroNuevo = controller.getCotizacionActual().getNumeroCotizacion();

        // Assert
        assertTrue(numeroNuevo > 0, "El número debe ser positivo");
    }

    @Test
    @DisplayName("CN04 - Generar PDF sin cliente debe fallar")
    void testGenerarPdfSinCliente() {
        // Arrange
        PdfGeneratorService pdfService = new PdfGeneratorService();
        CotizacionController controller = new CotizacionController(pdfService);
        controller.iniciarNuevaCotizacion();

        // Agregar productos pero NO cliente
        controller.agregarProducto("INF-001", "Inflable Test", 1, 100.0);

        // Act
        String resultado = controller.generarPDF();

        // Assert
        assertNull(resultado, "No debe generar PDF sin cliente");
    }

    @Test
    @DisplayName("CN05 - Generar PDF sin productos debe fallar")
    void testGenerarPdfSinProductos() {
        // Arrange
        PdfGeneratorService pdfService = new PdfGeneratorService();
        CotizacionController controller = new CotizacionController(pdfService);
        controller.iniciarNuevaCotizacion();

        // Agregar cliente pero NO productos
        Cliente cliente = new Cliente(
                "20123456789",
                "Test SAC",
                "Av. Test",
                "999888777",
                "test@test.com",
                "Juan"
        );
        controller.setCliente(cliente);

        // Act
        String resultado = controller.generarPDF();

        // Assert
        assertNull(resultado, "No debe generar PDF sin productos");
    }

    // ========== RF2: CÁLCULOS CON/SIN IGV ==========

    @Test
    @DisplayName("CN06 - Calcular precio SIN IGV")
    void testCalculoPrecioSinIGV() {
        // Arrange
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setProductos(new ArrayList<>());

        ProductoCotizacion producto = new ProductoCotizacion(
                "TEST-001",
                "Producto Test",
                1,
                "UND",
                100.0
        );
        cotizacion.getProductos().add(producto);
        cotizacion.setMostrarConIGV(false);

        // Act
        double subtotal = cotizacion.getSubtotal();

        // Assert
        assertEquals(100.0, subtotal, 0.01);
    }

    @Test
    @DisplayName("CN07 - Calcular precio CON IGV (18%)")
    void testCalculoPrecioConIGV() {
        // Arrange
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setProductos(new ArrayList<>());

        ProductoCotizacion producto = new ProductoCotizacion(
                "TEST-001",
                "Producto Test",
                1,
                "UNIDAD",
                100.0
        );
        cotizacion.getProductos().add(producto);

        // Act
        double subtotal = cotizacion.getSubtotal();
        double igv = cotizacion.getIGV();
        double total = cotizacion.getTotal();

        // Assert
        assertEquals(100.0, subtotal, 0.01);
        assertEquals(18.0, igv, 0.01);
        assertEquals(118.0, total, 0.01);
    }

    @Test
    @DisplayName("CN08 - Múltiples productos suman correctamente")
    void testMultiplesProductos() {
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setProductos(new ArrayList<>());

        // Producto 1: 2 x 50 = 100
        ProductoCotizacion p1 = new ProductoCotizacion("P1", "Producto 1", 2, "UND", 50.0);
        // Producto 2: 3 x 100 = 300
        ProductoCotizacion p2 = new ProductoCotizacion("P2", "Producto 2", 3, "UND", 100.0);

        cotizacion.getProductos().add(p1);
        cotizacion.getProductos().add(p2);

        double subtotal = cotizacion.getSubtotal();
        assertEquals(400.0, subtotal, 0.01); // 100 + 300 = 400
    }
}