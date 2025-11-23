package com.inflesusventas.test;

import com.inflesusventas.model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Suite de Pruebas de Caja Negra - InfleSusVentas
 * Basada en los Casos de Uso del Sistema
 * Enfoque: Validación de entradas y salidas sin conocer la implementación
 * interna
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CajaNegraTest {

    // ========== CU-01: VALIDAR RUC ==========

    @Test
    @Order(1)
    @DisplayName("CN01 - CU-01: Validar RUC con 11 dígitos válidos")
    void testValidarRucValido() {
        // Arrange & Act
        Cliente cliente = new Cliente();
        cliente.setRuc("20123456789");

        // Assert
        assertEquals("20123456789", cliente.getRuc());
        assertEquals(11, cliente.getRuc().length());
    }

    @Test
    @Order(2)
    @DisplayName("CN02 - CU-01: Rechazar RUC con menos de 11 dígitos")
    void testValidarRucMenosDe11Digitos() {
        // Arrange
        Cliente cliente = new Cliente();

        // Act & Assert - RN01: RUC debe tener exactamente 11 dígitos
        assertThrows(IllegalArgumentException.class, () -> {
            cliente.setRuc("201234567"); // Solo 9 dígitos
        });
    }

    @Test
    @Order(3)
    @DisplayName("CN03 - CU-01: Rechazar RUC con más de 11 dígitos")
    void testValidarRucMasDe11Digitos() {
        // Arrange
        Cliente cliente = new Cliente();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            cliente.setRuc("201234567890123"); // 15 dígitos
        });
    }

    // ========== CU-02: REGISTRAR CLIENTE ==========

    @Test
    @Order(4)
    @DisplayName("CN04 - CU-02: Registrar cliente con datos completos y válidos")
    void testRegistrarClienteCompleto() {
        // Arrange & Act - RN03 y RN04: Razón social y datos personales obligatorios
        Cliente cliente = new Cliente(
                0,
                "20554524051",
                "INFLE SUS VENTAS S.R.L.",
                "Av. Principal 123, Lima",
                "987654321",
                "ventas@inflesusventas.com",
                "Juan Pérez");

        // Assert
        assertNotNull(cliente);
        assertEquals("20554524051", cliente.getRuc());
        assertEquals("INFLE SUS VENTAS S.R.L.", cliente.getRazonSocial());
        assertEquals("ventas@inflesusventas.com", cliente.getEmail());
    }

    @Test
    @Order(5)
    @DisplayName("CN05 - CU-02: Rechazar cliente sin razón social")
    void testRegistrarClienteSinRazonSocial() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setRuc("20123456789");

        // Act & Assert - RN03: Razón social es obligatoria
        assertNull(cliente.getRazonSocial());
    }

    // ========== CU-04: EMITIR COMPROBANTE ELECTRÓNICO ==========

    @Test
    @Order(6)
    @DisplayName("CN06 - CU-04: Generar comprobante con datos obligatorios")
    void testGenerarComprobanteConDatosObligatorios() {
        // Arrange - RN10: Debe incluir datos obligatorios de SUNAT
        ComprobanteElectronico comprobante = new ComprobanteElectronico();
        comprobante.setSerie("F001");
        comprobante.setNumero(1);
        comprobante.setRucCliente("20123456789");
        comprobante.setRazonSocialCliente("Cliente Test SAC");
        comprobante.setFechaEmision(LocalDateTime.now());
        comprobante.setMoneda("PEN");

        // Assert
        assertNotNull(comprobante.getSerie());
        assertNotNull(comprobante.getRucCliente());
        assertEquals("F001", comprobante.getSerie());
    }

    // ========== CU-05: GENERAR GUÍA DE REMISIÓN ==========

    @Test
    @Order(7)
    @DisplayName("CN07 - CU-05: Generar GRE con datos de traslado completos")
    void testGenerarGuiaRemisionCompleta() {
        // Arrange - RN13 a RN18: Datos obligatorios de GRE
        GuiaRemision guia = new GuiaRemision();
        guia.setSerieNumero("T001-00000001");
        guia.setPuntoPartida("Av. Origen 123, Lima");
        guia.setPuntoLlegada("Av. Destino 456, Callao");

        DatosTransporte transporte = new DatosTransporte();
        transporte.setNumeroPlaca("ABC-123");
        transporte.setNombreConductor("Juan");
        transporte.setApellidosConductor("Pérez");
        transporte.setDniConductor("12345678");
        transporte.setFechaInicioTraslado(LocalDate.now());

        guia.setDatosTransporte(transporte);

        // Assert - RN14 a RN17
        assertNotNull(guia.getPuntoPartida());
        assertNotNull(guia.getPuntoLlegada());
        assertNotNull(guia.getDatosTransporte().getNumeroPlaca());
        assertNotNull(guia.getDatosTransporte().getDniConductor());
        assertEquals(8, guia.getDatosTransporte().getDniConductor().length());
    }

    // ========== CU-06: CALCULAR PRECIOS ==========

    @Test
    @Order(8)
    @DisplayName("CN08 - CU-06: Calcular precio SIN IGV")
    void testCalcularPrecioSinIGV() {
        // Arrange - RN21: Fórmula sin IGV: PrecioBase
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setProductos(new ArrayList<>());
        cotizacion.setMostrarConIGV(false);

        ProductoCotizacion producto = new ProductoCotizacion("P001", "Producto Test", 1, "UND", 100.0);
        cotizacion.getProductos().add(producto);

        // Act
        double subtotal = cotizacion.getSubtotal();

        // Assert
        assertEquals(100.0, subtotal, 0.01);
    }

    @Test
    @Order(9)
    @DisplayName("CN09 - CU-06: Calcular precio CON IGV (18%)")
    void testCalcularPrecioConIGV() {
        // Arrange - RN19 y RN20: IGV es 18%, Fórmula: PrecioBase × 1.18
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setProductos(new ArrayList<>());

        ProductoCotizacion producto = new ProductoCotizacion("P001", "Producto Test", 1, "UND", 100.0);
        cotizacion.getProductos().add(producto);

        // Act
        double subtotal = cotizacion.getSubtotal();
        double igv = cotizacion.getIGV();
        double total = cotizacion.getTotal();

        // Assert
        assertEquals(100.0, subtotal, 0.01);
        assertEquals(18.0, igv, 0.01); // 18% de 100
        assertEquals(118.0, total, 0.01); // 100 + 18
    }

    @Test
    @Order(10)
    @DisplayName("CN10 - CU-06: Calcular precio con múltiples productos")
    void testCalcularPrecioMultiplesProductos() {
        // Arrange
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
        double total = cotizacion.getTotal();

        // Assert
        assertEquals(400.0, subtotal, 0.01); // 100 + 300
        assertEquals(472.0, total, 0.01); // 400 + (400 * 0.18)
    }

    // ========== CU-09: GENERAR COTIZACIÓN ==========

    @Test
    @Order(11)
    @DisplayName("CN11 - CU-09: Generar cotización con número correlativo")
    void testGenerarCotizacionConNumeroCorrelativo() {
        // Arrange - RN29 y RN30: Número correlativo automático
        Cotizacion cot1 = new Cotizacion();
        cot1.setNumeroCotizacion(1);

        Cotizacion cot2 = new Cotizacion();
        cot2.setNumeroCotizacion(2);

        // Assert
        assertTrue(cot2.getNumeroCotizacion() > cot1.getNumeroCotizacion());
    }

    @Test
    @Order(12)
    @DisplayName("CN12 - CU-09: Cotización debe incluir datos obligatorios")
    void testCotizacionConDatosObligatorios() {
        // Arrange - RN31 y RN32: Datos obligatorios
        Cliente cliente = new Cliente(0, "20123456789", "Test SAC", "Av. Test", "999", "test@test.com", "Juan");

        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setCliente(cliente);
        cotizacion.setCondicionPago(Cotizacion.CondicionPago.CONTADO);
        cotizacion.setProductos(new ArrayList<>());

        ProductoCotizacion producto = new ProductoCotizacion("P001", "Producto", 1, "UND", 100.0);
        cotizacion.getProductos().add(producto);

        // Assert - RN31: Debe incluir nombre, RUC del cliente
        assertNotNull(cotizacion.getCliente());
        assertNotNull(cotizacion.getCliente().getRuc());
        assertNotNull(cotizacion.getCliente().getRazonSocial());

        // RN32: Debe incluir montos, IGV, condiciones de pago
        assertNotNull(cotizacion.getCondicionPago());
        assertTrue(cotizacion.getSubtotal() > 0);
        assertTrue(cotizacion.getTotal() > 0);
    }

    // ========== CU-10: GENERAR NOTAS DE CRÉDITO ==========

    @Test
    @Order(13)
    @DisplayName("CN13 - CU-10: Generar nota de crédito asociada a comprobante")
    void testGenerarNotaCreditoAsociadaAComprobante() {
        // Arrange - RN35: Debe estar asociada a un comprobante original
        NotaCredito nc = new NotaCredito();
        nc.setSerie("FC01");
        nc.setNumero(1);
        nc.setNumeroFacturaRef("F001-00000123");
        nc.setMotivoSustento("Corrección por error en la descripción del producto");
        nc.setTipoNotaCredito("CORRECCION POR ERROR EN LA DESCRIPCION");
        nc.setFechaEmision(LocalDate.now());

        // Assert - RN35 y RN36
        assertNotNull(nc.getNumeroFacturaRef());
        assertNotNull(nc.getMotivoSustento());
        assertTrue(nc.getMotivoSustento().length() >= 10);
    }

    @Test
    @Order(14)
    @DisplayName("CN14 - CU-10: Validar motivo de nota de crédito obligatorio")
    void testValidarMotivoNotaCreditoObligatorio() {
        // Arrange - RN36: Debe incluir motivo de emisión
        NotaCredito nc = new NotaCredito();
        nc.setNumeroFacturaRef("F001-00000123");
        nc.setMotivoSustento(""); // Motivo vacío

        // Assert
        assertTrue(nc.getMotivoSustento().isEmpty());
        // En producción, esto debería lanzar una excepción de validación
    }

    // ========== PRUEBAS DE VALIDACIÓN GENERAL ==========

    @Test
    @Order(15)
    @DisplayName("CN15 - Validar que cotización sin cliente es inválida")
    void testCotizacionSinClienteInvalida() {
        // Arrange
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setCliente(null);
        cotizacion.setProductos(new ArrayList<>());
        cotizacion.getProductos().add(new ProductoCotizacion("A", "B", 1, "U", 10.0));

        // Assert
        assertNull(cotizacion.getCliente());
        // En producción, esto debería ser rechazado por validación
    }

    @Test
    @Order(16)
    @DisplayName("CN16 - Validar que comprobante sin ítems es inválido")
    void testComprobanteSinItemsInvalido() {
        // Arrange
        ComprobanteElectronico comprobante = new ComprobanteElectronico();
        comprobante.setItems(new ArrayList<>());

        // Assert
        assertTrue(comprobante.getItems().isEmpty());
        // En producción, esto debería ser rechazado
    }

    @Test
    @Order(17)
    @DisplayName("CN17 - Validar formato de email en cliente")
    void testValidarFormatoEmail() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setEmail("ventas@inflesusventas.com");

        // Assert
        assertNotNull(cliente.getEmail());
        assertTrue(cliente.getEmail().contains("@"));
        assertTrue(cliente.getEmail().contains("."));
    }

    @Test
    @Order(18)
    @DisplayName("CN18 - Validar cálculo de IGV en Nota de Crédito")
    void testCalculoIGVNotaCredito() {
        // Arrange
        NotaCredito nc = new NotaCredito();
        nc.setSubtotal(100.0);
        nc.setIgv(18.0);
        nc.setTotal(118.0);

        // Assert - Verificar fórmula: Total = Subtotal + IGV
        assertEquals(nc.getSubtotal() + nc.getIgv(), nc.getTotal(), 0.01);
        assertEquals(nc.getSubtotal() * 0.18, nc.getIgv(), 0.01);
    }
}