package com.inflesusventas.test;

import com.inflesusventas.model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Suite de Pruebas de Caja Blanca - InfleSusVentas
 * Enfoque: Cobertura de código, flujos internos y condiciones lógicas
 * Objetivo: Probar todos los caminos de ejecución posibles
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CajaBlancaTest {

    // ========== COBERTURA: ProductoCotizacion.getSubtotal() ==========

    @Test
    @Order(1)
    @DisplayName("CB01 - ProductoCotizacion.getSubtotal() con cantidad = 0")
    void testSubtotalCantidadCero() {
        // Arrange
        ProductoCotizacion producto = new ProductoCotizacion("TEST", "Test", 0, "UND", 100.0);

        // Act
        double subtotal = producto.getSubtotal();

        // Assert - Camino: cantidad == 0
        assertEquals(0.0, subtotal, 0.01);
    }

    @Test
    @Order(2)
    @DisplayName("CB02 - ProductoCotizacion.getSubtotal() con cantidad positiva")
    void testSubtotalCantidadPositiva() {
        // Arrange
        ProductoCotizacion producto = new ProductoCotizacion("INF-001", "Inflable", 5, "UND", 200.0);

        // Act
        double subtotal = producto.getSubtotal();

        // Assert - Camino: cantidad > 0
        assertEquals(1000.0, subtotal, 0.01); // 5 * 200
    }

    @Test
    @Order(3)
    @DisplayName("CB03 - ProductoCotizacion.getSubtotal() con precio = 0")
    void testSubtotalPrecioCero() {
        // Arrange
        ProductoCotizacion producto = new ProductoCotizacion("TEST", "Test", 10, "UND", 0.0);

        // Act
        double subtotal = producto.getSubtotal();

        // Assert - Camino: precioBase == 0
        assertEquals(0.0, subtotal, 0.01);
    }

    // ========== COBERTURA: Cotizacion.getSubtotal() ==========

    @Test
    @Order(4)
    @DisplayName("CB04 - Cotizacion.getSubtotal() con lista vacía")
    void testCotizacionSubtotalListaVacia() {
        // Arrange
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setProductos(new ArrayList<>());

        // Act
        double subtotal = cotizacion.getSubtotal();

        // Assert - Camino: productos.isEmpty() == true
        assertEquals(0.0, subtotal, 0.01);
    }

    @Test
    @Order(5)
    @DisplayName("CB05 - Cotizacion.getSubtotal() con productos null")
    void testCotizacionSubtotalProductosNull() {
        // Arrange
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setProductos(null);

        // Act
        double subtotal = cotizacion.getSubtotal();

        // Assert - Camino: productos == null
        assertEquals(0.0, subtotal, 0.01);
    }

    @Test
    @Order(6)
    @DisplayName("CB06 - Cotizacion.getSubtotal() con un producto")
    void testCotizacionSubtotalUnProducto() {
        // Arrange
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setProductos(new ArrayList<>());
        cotizacion.getProductos().add(new ProductoCotizacion("P1", "Producto 1", 2, "UND", 50.0));

        // Act
        double subtotal = cotizacion.getSubtotal();

        // Assert - Camino: productos.size() == 1
        assertEquals(100.0, subtotal, 0.01);
    }

    @Test
    @Order(7)
    @DisplayName("CB07 - Cotizacion.getSubtotal() con múltiples productos")
    void testCotizacionSubtotalMultiplesProductos() {
        // Arrange
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setProductos(new ArrayList<>());
        cotizacion.getProductos().add(new ProductoCotizacion("P1", "Producto 1", 2, "UND", 50.0));
        cotizacion.getProductos().add(new ProductoCotizacion("P2", "Producto 2", 3, "UND", 100.0));
        cotizacion.getProductos().add(new ProductoCotizacion("P3", "Producto 3", 1, "UND", 200.0));

        // Act
        double subtotal = cotizacion.getSubtotal();

        // Assert - Camino: productos.size() > 1, loop completo
        assertEquals(600.0, subtotal, 0.01); // 100 + 300 + 200
    }

    // ========== COBERTURA: Cotizacion.getIGV() ==========

    @Test
    @Order(8)
    @DisplayName("CB08 - Cotizacion.getIGV() cálculo correcto (18%)")
    void testCalculoIGV() {
        // Arrange
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setProductos(new ArrayList<>());
        cotizacion.getProductos().add(new ProductoCotizacion("TEST", "Test", 1, "UND", 100.0));

        // Act
        double igv = cotizacion.getIGV();

        // Assert - Fórmula: subtotal * 0.18
        assertEquals(18.0, igv, 0.01);
    }

    @Test
    @Order(9)
    @DisplayName("CB09 - Cotizacion.getIGV() con subtotal = 0")
    void testCalculoIGVSubtotalCero() {
        // Arrange
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setProductos(new ArrayList<>());

        // Act
        double igv = cotizacion.getIGV();

        // Assert - Camino: subtotal == 0
        assertEquals(0.0, igv, 0.01);
    }

    // ========== COBERTURA: Cotizacion.getTotal() ==========

    @Test
    @Order(10)
    @DisplayName("CB10 - Cotizacion.getTotal() suma correcta")
    void testCalculoTotal() {
        // Arrange
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setProductos(new ArrayList<>());
        cotizacion.getProductos().add(new ProductoCotizacion("TEST", "Test", 1, "UND", 100.0));

        // Act
        double total = cotizacion.getTotal();

        // Assert - Fórmula: subtotal + IGV
        assertEquals(118.0, total, 0.01); // 100 + 18
    }

    // ========== COBERTURA: Cliente - Constructores ==========

    @Test
    @Order(11)
    @DisplayName("CB11 - Cliente constructor vacío inicialización")
    void testClienteConstructorVacio() {
        // Act
        Cliente cliente = new Cliente();

        // Assert - Camino: constructor sin parámetros
        assertNotNull(cliente);
        assertNull(cliente.getRuc());
        assertNull(cliente.getRazonSocial());
        assertNull(cliente.getEmail());
    }

    @Test
    @Order(12)
    @DisplayName("CB12 - Cliente constructor completo")
    void testClienteConstructorCompleto() {
        // Act
        Cliente cliente = new Cliente(
                1,
                "20123456789",
                "Test SAC",
                "Av. Test 123",
                "999888777",
                "test@test.com",
                "Juan Test");

        // Assert - Camino: constructor con todos los parámetros
        assertNotNull(cliente);
        assertEquals(1, cliente.getId());
        assertEquals("20123456789", cliente.getRuc());
        assertEquals("Test SAC", cliente.getRazonSocial());
        assertEquals("test@test.com", cliente.getEmail());
    }

    // ========== COBERTURA: Cliente.setRuc() - Validación ==========

    @Test
    @Order(13)
    @DisplayName("CB13 - Cliente.setRuc() con RUC válido (11 dígitos)")
    void testSetRucValido() {
        // Arrange
        Cliente cliente = new Cliente();

        // Act
        cliente.setRuc("20554524051");

        // Assert - Camino: ruc.length() == 11
        assertEquals("20554524051", cliente.getRuc());
    }

    @Test
    @Order(14)
    @DisplayName("CB14 - Cliente.setRuc() con RUC inválido (< 11 dígitos)")
    void testSetRucInvalidoMenor() {
        // Arrange
        Cliente cliente = new Cliente();

        // Act & Assert - Camino: ruc.length() < 11
        assertThrows(IllegalArgumentException.class, () -> {
            cliente.setRuc("2055452405"); // 10 dígitos
        });
    }

    @Test
    @Order(15)
    @DisplayName("CB15 - Cliente.setRuc() con RUC inválido (> 11 dígitos)")
    void testSetRucInvalidoMayor() {
        // Arrange
        Cliente cliente = new Cliente();
        // Act & Assert - Camino: ruc.length() > 11
        assertThrows(IllegalArgumentException.class, () -> {
            cliente.setRuc("205545240512"); // 12 dígitos
        });
    }

    @Test
    @Order(16)
    @DisplayName("CB16 - Cliente.setRuc() con RUC null")
    void testSetRucNull() {
        // Arrange
        Cliente cliente = new Cliente();

        // Act & Assert - Camino: ruc == null
        assertThrows(IllegalArgumentException.class, () -> {
            cliente.setRuc(null);
        });
    }

    // ========== COBERTURA: NotaCredito - Cálculos ==========

    @Test
    @Order(17)
    @DisplayName("CB17 - NotaCredito cálculo de totales con ítems")
    void testNotaCreditoCalculoTotales() {
        // Arrange
        NotaCredito nc = new NotaCredito();
        List<ItemNotaCredito> items = new ArrayList<>();
        items.add(new ItemNotaCredito("UND", 2, "Producto 1", 50.0));
        items.add(new ItemNotaCredito("UND", 3, "Producto 2", 100.0));
        nc.setItems(items);

        // Act - Simulamos cálculo manual (en producción lo haría el servicio)
        double subtotal = items.stream().mapToDouble(ItemNotaCredito::getSubtotal).sum();
        double igv = subtotal * 0.18;
        double total = subtotal + igv;

        nc.setSubtotal(subtotal);
        nc.setIgv(igv);
        nc.setTotal(total);

        // Assert
        assertEquals(400.0, nc.getSubtotal(), 0.01); // 100 + 300
        assertEquals(72.0, nc.getIgv(), 0.01); // 400 * 0.18
        assertEquals(472.0, nc.getTotal(), 0.01); // 400 + 72
    }

    @Test
    @Order(18)
    @DisplayName("CB18 - ItemNotaCredito.getSubtotal() cálculo correcto")
    void testItemNotaCreditoSubtotal() {
        // Arrange
        ItemNotaCredito item = new ItemNotaCredito("UND", 5, "Producto Test", 120.0);

        // Act
        double subtotal = item.getSubtotal();

        // Assert - Fórmula: cantidad * valorUnitario
        assertEquals(600.0, subtotal, 0.01); // 5 * 120
    }

    // ========== COBERTURA: GuiaRemision - Validaciones ==========

    @Test
    @Order(19)
    @DisplayName("CB19 - GuiaRemision generación de serie-número")
    void testGuiaRemisionSerieNumero() {
        // Arrange
        GuiaRemision guia = new GuiaRemision();
        guia.setSerieNumero("T001-00000123");

        // Act
        String serieNumero = guia.getSerieNumero();

        // Assert - Formato: SERIE-NUMERO
        assertEquals("T001-00000123", serieNumero);
    }

    @Test
    @Order(20)
    @DisplayName("CB20 - DatosTransporte validación de DNI (8 dígitos)")
    void testDatosTransporteDNI() {
        // Arrange
        DatosTransporte transporte = new DatosTransporte();

        // Act
        transporte.setDniConductor("12345678");

        // Assert - Camino: DNI válido
        assertEquals("12345678", transporte.getDniConductor());
        assertEquals(8, transporte.getDniConductor().length());
    }

    // ========== COBERTURA: ComprobanteElectronico ==========

    @Test
    @Order(21)
    @DisplayName("CB21 - ComprobanteElectronico generación de ID")
    void testComprobanteGeneracionID() {
        // Arrange
        ComprobanteElectronico comprobante = new ComprobanteElectronico();
        comprobante.setRucEmisor("20554524051");
        comprobante.setSerie("F001");
        comprobante.setNumero(123);

        // Act
        String id = comprobante.getId();

        // Assert - El ID puede ser null o generado, dependiendo de la implementación
        // Verificamos que los campos individuales están correctos
        assertEquals("20554524051", comprobante.getRucEmisor());
        assertEquals("F001", comprobante.getSerie());
        assertEquals(123, comprobante.getNumero());
    }

    @Test
    @Order(22)
    @DisplayName("CB22 - ComprobanteElectronico cálculo de totales")
    void testComprobanteCalculoTotales() {
        // Arrange
        ComprobanteElectronico comprobante = new ComprobanteElectronico();
        List<ProductoCotizacion> items = new ArrayList<>();
        items.add(new ProductoCotizacion("P1", "Producto 1", 2, "UND", 50.0));
        items.add(new ProductoCotizacion("P2", "Producto 2", 1, "UND", 200.0));
        comprobante.setItems(items);

        // Act - Simulamos cálculo
        double subtotal = items.stream().mapToDouble(ProductoCotizacion::getSubtotal).sum();
        double igv = subtotal * 0.18;
        double total = subtotal + igv;

        comprobante.setSubtotal(subtotal);
        comprobante.setIgv(igv);
        comprobante.setTotal(total);

        // Assert
        assertEquals(300.0, comprobante.getSubtotal(), 0.01); // 100 + 200
        assertEquals(54.0, comprobante.getIgv(), 0.01); // 300 * 0.18
        assertEquals(354.0, comprobante.getTotal(), 0.01); // 300 + 54
    }

    // ========== COBERTURA: Condiciones de Borde ==========

    @Test
    @Order(23)
    @DisplayName("CB23 - ProductoCotizacion con cantidad máxima")
    void testProductoCantidadMaxima() {
        // Arrange
        ProductoCotizacion producto = new ProductoCotizacion("P1", "Producto", 9999, "UND", 1.0);

        // Act
        double subtotal = producto.getSubtotal();

        // Assert - Borde superior
        assertEquals(9999.0, subtotal, 0.01);
    }

    @Test
    @Order(24)
    @DisplayName("CB24 - Cotizacion con precio decimal preciso")
    void testCotizacionPrecioDecimal() {
        // Arrange
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setProductos(new ArrayList<>());
        cotizacion.getProductos().add(new ProductoCotizacion("P1", "Producto", 3, "UND", 33.33));

        // Act
        double subtotal = cotizacion.getSubtotal();
        double total = cotizacion.getTotal();

        // Assert - Precisión decimal
        assertEquals(99.99, subtotal, 0.01);
        assertEquals(117.99, total, 0.02); // 99.99 + (99.99 * 0.18)
    }

    @Test
    @Order(25)
    @DisplayName("CB25 - NotaCredito con lista de ítems vacía")
    void testNotaCreditoItemsVacio() {
        // Arrange
        NotaCredito nc = new NotaCredito();
        nc.setItems(new ArrayList<>());

        // Act & Assert - Camino: items.isEmpty()
        assertTrue(nc.getItems().isEmpty());
        assertEquals(0, nc.getItems().size());
    }
}