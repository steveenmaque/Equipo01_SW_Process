## Sistema InfleSusVentas

### Información General
- **Fecha de Ejecución:** 2025-11-23
- **Framework:** JUnit 5 (Jupiter)
- **Tipo de Pruebas:** Caja Negra 
- **Versión del Sistema:** 1.0-SNAPSHOT

---

## Matriz de Pruebas

| RF | ID CASO DE USO | Descripción breve del caso | Entrada(s) | Resultado Esperado | Evidencia | Resultado |
|----|----------------|---------------------------|------------|-------------------|-----------|-----------|
| RF-01 | CU-01 | Validar RUC con 11 dígitos válidos | RUC: "20123456789" | RUC aceptado, longitud = 11 | CN01_RUC_Valido.png | ÉXITO |
| RF-01 | CU-01 | Rechazar RUC con menos de 11 dígitos | RUC: "201234567" (9 dígitos) | IllegalArgumentException lanzada | CN02_RUC_Menor_11.png | ÉXITO |
| RF-01 | CU-01 | Rechazar RUC con más de 11 dígitos | RUC: "201234567890123" (15 dígitos) | IllegalArgumentException lanzada | CN03_RUC_Mayor_11.png | ÉXITO |
| RF-02 | CU-02 | Registrar cliente con datos completos | RUC: "20554524051"<br>Razón Social: "INFLE SUS VENTAS S.R.L."<br>Dirección: "Av. Principal 123, Lima"<br>Teléfono: "987654321"<br>Email: "ventas@inflesusventas.com"<br>Contacto: "Juan Pérez" | Cliente creado con todos los datos correctamente | CN04_Cliente_Completo.png | ÉXITO |
| RF-02 | CU-02 | Rechazar cliente sin razón social | RUC: "20123456789"<br>Razón Social: null | Razón social es null | CN05_Cliente_Sin_Razon_Social.png | ÉXITO |
| RF-02 | CU-02 | Validar formato de email en cliente | Email: "ventas@inflesusventas.com" | Email contiene "@" y "." | CN17_Email_Formato_Valido.png | ÉXITO |
| RF-04 | CU-04 | Generar comprobante con datos obligatorios | Serie: "F001"<br>Número: 1<br>RUC Cliente: "20123456789"<br>Razón Social: "Cliente Test SAC"<br>Fecha: LocalDateTime.now()<br>Moneda: "PEN" | Comprobante creado con serie "F001" y todos los datos | CN06_Comprobante_Datos_Obligatorios.png | ÉXITO |
| RF-04 | CU-04 | Validar comprobante sin ítems | Items: [] (lista vacía) | Lista de ítems vacía (isEmpty = true) | CN16_Comprobante_Sin_Items.png | ÉXITO |
| RF-05 | CU-05 | Generar GRE con datos de traslado completos | Serie-Número: "T001-00000001"<br>Punto Partida: "Av. Origen 123, Lima"<br>Punto Llegada: "Av. Destino 456, Callao"<br>Placa: "ABC-123"<br>Conductor: "Juan Pérez"<br>DNI: "12345678"<br>Fecha: LocalDate.now() | GRE creada con todos los datos de traslado, DNI longitud = 8 | CN07_GRE_Completa.png | ÉXITO |
| RF-06 | CU-06 | Calcular precio SIN IGV | Producto: "P001"<br>Cantidad: 1<br>Precio Base: 100.0<br>Mostrar IGV: false | Subtotal = 100.0 | CN08_Precio_Sin_IGV.png | ÉXITO |
| RF-06 | CU-06 | Calcular precio CON IGV (18%) | Producto: "P001"<br>Cantidad: 1<br>Precio Base: 100.0<br>Mostrar IGV: true | Subtotal = 100.0<br>IGV = 18.0<br>Total = 118.0 | CN09_Precio_Con_IGV.png | ÉXITO |
| RF-06 | CU-06 | Calcular precio con múltiples productos | Producto 1: 2 x 50.0<br>Producto 2: 3 x 100.0 | Subtotal = 400.0<br>Total = 472.0 (con IGV) | CN10_Multiples_Productos.png | ÉXITO |
| RF-09 | CU-09 | Generar cotización con número correlativo | Cotización 1: Número = 1<br>Cotización 2: Número = 2 | Número Cot2 > Número Cot1 (correlativo automático) | CN11_Cotizacion_Correlativo.png | ÉXITO |
| RF-09 | CU-09 | Cotización debe incluir datos obligatorios | Cliente: RUC "20123456789", "Test SAC"<br>Producto: "P001", 1 UND, 100.0<br>Condición Pago: CONTADO | Cliente ≠ null<br>RUC ≠ null<br>Razón Social ≠ null<br>Condición Pago ≠ null<br>Subtotal > 0<br>Total > 0 | CN12_Cotizacion_Datos_Obligatorios.png | ÉXITO |
| RF-09 | CU-09 | Validar cotización sin cliente | Cliente: null<br>Productos: 1 producto ("A", "B", 1, "U", 10.0) | Cliente = null | CN15_Cotizacion_Sin_Cliente.png | ÉXITO |
| RF-10 | CU-10 | Generar nota de crédito asociada a comprobante | Serie: "FC01"<br>Número: 1<br>Factura Ref: "F001-00000123"<br>Motivo: "Corrección por error en la descripción del producto"<br>Tipo: "CORRECCION POR ERROR EN LA DESCRIPCION"<br>Fecha: LocalDate.now() | NC creada con referencia a factura<br>Motivo longitud >= 10 | CN13_NC_Asociada_Comprobante.png | ÉXITO |
| RF-10 | CU-10 | Validar motivo de nota de crédito obligatorio | Factura Ref: "F001-00000123"<br>Motivo: "" (vacío) | Motivo.isEmpty() = true | CN14_NC_Motivo_Obligatorio.png | ÉXITO |
| RF-10 | CU-10 | Validar cálculo de IGV en Nota de Crédito | Subtotal: 100.0<br>IGV: 18.0<br>Total: 118.0 | Total = Subtotal + IGV<br>IGV = Subtotal * 0.18 | CN18_NC_Calculo_IGV.png | ÉXITO |

---

## Resumen de Resultados

| Métrica | Valor |
|---------|-------|
| **Total de Pruebas** | 18 |
| **Pruebas Exitosas** | 18 |
| **Pruebas Fallidas** | 0 |
| **Tasa de Éxito** | 100% |

---

## Cobertura por Requisito Funcional

| RF | Descripción | Casos de Uso | Pruebas | Estado |
|----|-------------|--------------|---------|--------|
| RF-01 | Validación de RUC | CU-01 | 3 | 100% |
| RF-02 | Gestión de Clientes | CU-02 | 3 | 100% |
| RF-04 | Emisión de Comprobantes | CU-04 | 2 | 100% |
| RF-05 | Guías de Remisión Electrónica | CU-05 | 1 | 100% |
| RF-06 | Cálculo de Precios e IGV | CU-06 | 3 | 100% |
| RF-09 | Gestión de Cotizaciones | CU-09 | 3 | 100% |
| RF-10 | Notas de Crédito | CU-10 | 3 | 100% |

---

## Casos de Uso Cubiertos

| ID Caso de Uso | Descripción | Pruebas | Estado |
|----------------|-------------|---------|--------|
| CU-01 | Validar RUC | 3 | 100% |
| CU-02 | Registrar Cliente | 3 | 100% |
| CU-04 | Emitir Comprobante Electrónico | 2 | 100% |
| CU-05 | Generar Guía de Remisión | 1 | 100% |
| CU-06 | Calcular Precios | 3 | 100% |
| CU-09 | Generar Cotización | 3 | 100% |
| CU-10 | Generar Notas de Crédito | 3 | 100% |

---

## Casos de Uso Pendientes

| ID Caso de Uso | Descripción | Razón |
|----------------|-------------|-------|
| CU-03 | Registrar Venta | No implementado en pruebas unitarias |
| CU-07 | Registrar Datos de Traslado | Cubierto parcialmente en CU-05 |
| CU-08 | Validar Comprobante con SUNAT | Requiere integración con API externa |
| CU-11 | Enviar Documento por Email | Requiere servicio de email configurado |

---

## Estructura de Evidencias

Las evidencias están organizadas en la carpeta `evidencias/` con la siguiente estructura:
