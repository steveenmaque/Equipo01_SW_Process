package com.inflesusventas.test;

import com.inflesusventas.model.Cliente;
import com.inflesusventas.model.Cotizacion;
import com.inflesusventas.model.ProductoCotizacion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

/**
 * Suite de Pruebas de Caja Blanca - InfleSusVentas
 * Cobertura de código: Métodos críticos
 * Tester: Clisman
 */
class CajaBlancaTest {

    @Test
    @DisplayName("CB01 - ProductoCotizacion.getSubtotal() con cantidad = 0")
    void testSubtotalCantidadCero() {
        ProductoCotizacion producto = new ProductoCotizacion(
                "TEST",
                "Test",
                0,
                "UND",
                100.0
        );

        double subtotal = producto.getSubtotal();
        assertEquals(0.0, subtotal, 0.01);
    }

    @Test
    @DisplayName("CB02 - ProductoCotizacion.getSubtotal() con valores normales")
    void testSubtotalNormal() {
        ProductoCotizacion producto = new ProductoCotizacion(
                "INF-001",
                "Inflable",
                5,
                "UND",
                200.0
        );

        double subtotal = producto.getSubtotal();
        assertEquals(1000.0, subtotal, 0.01); // 5 * 200 = 1000
    }

    @Test
    @DisplayName("CB03 - Cotizacion.getSubtotal() con lista vacía")
    void testCotizacionSubtotalListaVacia() {
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setProductos(new ArrayList<>());

        double subtotal = cotizacion.getSubtotal();
        assertEquals(0.0, subtotal, 0.01);
    }

    @Test
    @DisplayName("CB04 - Cotizacion.getSubtotal() con productos null")
    void testCotizacionSubtotalProductosNull() {
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setProductos(null);

        double subtotal = cotizacion.getSubtotal();
        assertEquals(0.0, subtotal, 0.01);
    }

    @Test
    @DisplayName("CB05 - Cotizacion.getIGV() cálculo correcto")
    void testCalculoIGV() {
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setProductos(new ArrayList<>());

        ProductoCotizacion producto = new ProductoCotizacion(
                "TEST",
                "Producto Test",
                1,
                "UND",
                100.0
        );
        cotizacion.getProductos().add(producto);

        double igv = cotizacion.getIGV();
        assertEquals(18.0, igv, 0.01); // 18% de 100
    }

    @Test
    @DisplayName("CB06 - Cotizacion.getTotal() suma correcta")
    void testCalculoTotal() {
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setProductos(new ArrayList<>());

        ProductoCotizacion producto = new ProductoCotizacion(
                "TEST",
                "Producto Test",
                1,
                "UND",
                100.0
        );
        cotizacion.getProductos().add(producto);

        double total = cotizacion.getTotal();
        assertEquals(118.0, total, 0.01); // 100 + 18 (IGV)
    }

    @Test
    @DisplayName("CB07 - Cliente constructor vacío inicialización")
    void testClienteConstructorVacio() {
        Cliente cliente = new Cliente();
        assertNotNull(cliente);
        assertNull(cliente.getRuc());
        assertNull(cliente.getRazonSocial());
    }

    @Test
    @DisplayName("CB08 - Cliente constructor completo")
    void testClienteConstructorCompleto() {
        Cliente cliente = new Cliente(
                0,
                "20123456789",
                "Test SAC",
                "Av. Test 123",
                "999888777",
                "test@test.com",
                "Juan Test"
        );

        assertNotNull(cliente);
        assertEquals("20123456789", cliente.getRuc());
        assertEquals("Test SAC", cliente.getRazonSocial());
        assertEquals("test@test.com", cliente.getEmail());
    }
}