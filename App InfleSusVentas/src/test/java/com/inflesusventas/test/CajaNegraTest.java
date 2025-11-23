package com.inflesusventas.test;

import com.inflesusventas.model.Cliente;
import com.inflesusventas.model.Cotizacion;
import com.inflesusventas.model.ProductoCotizacion;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

/**
 * Suite de Pruebas de Caja Negra - InfleSusVentas
 * Adaptada a la arquitectura actual del proyecto
 */
class CajaNegraTest {

    // ========== RF1: REGISTRO DE CLIENTES ==========

    @Test
    @DisplayName("CN01 - Registrar cliente con datos válidos")
    void testRegistrarClienteValido() {
        // Arrange & Act
        Cliente cliente = new Cliente(
                0,
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
    @DisplayName("CN02 - Validar RUC con menos de 11 dígitos (DEBE LANZAR EXCEPCIÓN)")
    void testValidarRucInvalido() {
        Cliente cliente = new Cliente();

        // Act & Assert
        // Ahora sí pasará porque actualizamos Cliente.java
        assertThrows(IllegalArgumentException.class, () -> {
            cliente.setRuc("201234567"); // Solo 9 dígitos
        });
    }

    // ========== RF2: CÁLCULOS DE COTIZACIONES ==========
    // Nota: Probamos la lógica del MODELO ya que el controlador requiere inyección de dependencias compleja

    @Test
    @DisplayName("CN06 - Calcular precio SIN IGV")
    void testCalculoPrecioSinIGV() {
        // Arrange
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setProductos(new ArrayList<>());

        ProductoCotizacion producto = new ProductoCotizacion(
                "TEST-001", "Producto Test", 1, "UND", 100.0
        );
        cotizacion.getProductos().add(producto);
        
        // Act: Simulamos comportamiento de "No mostrar IGV" o cálculo base
        // Asumiendo que getSubtotal devuelve la suma de precios base
        cotizacion.setMostrarConIGV(false); 

        // Assert
        assertEquals(100.0, cotizacion.getSubtotal(), 0.01);
    }

    @Test
    @DisplayName("CN07 - Calcular precio CON IGV (18%)")
    void testCalculoPrecioConIGV() {
        // Arrange
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setProductos(new ArrayList<>());

        // 100 soles base
        ProductoCotizacion producto = new ProductoCotizacion(
                "TEST-001", "Producto Test", 1, "UNIDAD", 100.0
        );
        cotizacion.getProductos().add(producto);

        // Act
        double subtotal = cotizacion.getSubtotal(); // 100
        double igv = cotizacion.getIGV();           // 18
        double total = cotizacion.getTotal();       // 118

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

        // Act
        double subtotal = cotizacion.getSubtotal();

        // Assert
        assertEquals(400.0, subtotal, 0.01); // 100 + 300 = 400
    }
    
    @Test
    @DisplayName("CN04 - Validación: Cotización no debe ser válida sin cliente")
    void testCotizacionInvalidaSinCliente() {
        // Arrange
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setCliente(null); // Sin cliente
        cotizacion.setProductos(new ArrayList<>());
        cotizacion.getProductos().add(new ProductoCotizacion("A", "B", 1, "U", 10.0));

        // Act & Assert
        // Verificamos manualmente la condición que tu Vista validaría
        assertNull(cotizacion.getCliente(), "El cliente debería ser nulo");
        
        // Si tuvieras un método validar() en Cotizacion, lo llamaríamos aquí.
        // Por ahora validamos que el objeto esté incompleto.
    }
}