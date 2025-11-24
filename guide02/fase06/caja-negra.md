## Sistema InfleSusVentas

### Información General
- **Fecha de Ejecución:** 2025-11-23
- **Framework:** JUnit 5 
- **Tipo de Pruebas:** Caja Negra 

---

## Matriz de Pruebas

| RF | ID CASO DE USO | Descripción breve del caso | Entrada(s) | Resultado Esperado | Evidencia | Resultado |
|----|----------------|---------------------------|------------|-------------------|-----------|-----------|
| RF-01 | CU-01 | Validar RUC con 11 dígitos válidos | RUC: "20123456789" | RUC aceptado, longitud = 11 | <img width="1469" height="1123" alt="CN01_RUC_Valido" src="https://github.com/user-attachments/assets/eae95377-2c49-4769-a3b1-4032b8188200" /> | ÉXITO |
| RF-01 | CU-01 | Rechazar RUC con menos de 11 dígitos | RUC: "201234567" (9 dígitos) | IllegalArgumentException lanzada | <img width="1469" height="1123" alt="CN02_RUC_Menor_11" src="https://github.com/user-attachments/assets/bd143ff6-35b1-4bc8-b931-cb53d59ee97b" /> | ÉXITO |
| RF-01 | CU-01 | Rechazar RUC con más de 11 dígitos | RUC: "201234567890123" (15 dígitos) | IllegalArgumentException lanzada | <img width="1469" height="1123" alt="CN03_RUC_Mayor_11" src="https://github.com/user-attachments/assets/53fc2380-aa16-4e6b-8cd4-d1b23500e9b3" /> | ÉXITO |
| RF-02 | CU-02 | Registrar cliente con datos completos | RUC: "20554524051"<br>Razón Social: "INFLE SUS VENTAS S.R.L."<br>Dirección: "Av. Principal 123, Lima"<br>Teléfono: "987654321"<br>Email: "ventas@inflesusventas.com"<br>Contacto: "Juan Pérez" | Cliente creado con todos los datos correctamente | <img width="1469" height="1123" alt="CN04_Cliente_Completo" src="https://github.com/user-attachments/assets/1bdf533b-4482-444a-a776-9fff0bab933d" /> | ÉXITO |
| RF-02 | CU-02 | Rechazar cliente sin razón social | RUC: "20123456789"<br>Razón Social: null | Razón social es null | <img width="1469" height="1123" alt="CN05_Cliente_Sin_Razon_Social" src="https://github.com/user-attachments/assets/907d7083-3ce1-4558-bc70-4ca6b47df410" /> | ÉXITO |
| RF-02 | CU-02 | Validar formato de email en cliente | Email: "ventas@inflesusventas.com" | Email contiene "@" y "." | <img width="1469" height="1123" alt="CN17_Email_Formato_Valido" src="https://github.com/user-attachments/assets/cbe315cd-40c6-4a31-91e2-8576920bbc5e" /> | ÉXITO |
| RF-04 | CU-04 | Generar comprobante con datos obligatorios | Serie: "F001"<br>Número: 1<br>RUC Cliente: "20123456789"<br>Razón Social: "Cliente Test SAC"<br>Fecha: LocalDateTime.now()<br>Moneda: "PEN" | Comprobante creado con serie "F001" y todos los datos | <img width="1469" height="1123" alt="CN06_Comprobante_Datos_Obligatorios" src="https://github.com/user-attachments/assets/1ff45a3d-e9c8-4335-a659-0f500150e900" />  | ÉXITO |
| RF-04 | CU-04 | Validar comprobante sin ítems | Items: [] (lista vacía) | Lista de ítems vacía (isEmpty = true) | <img width="1469" height="1123" alt="CN16_Comprobante_Sin_Items" src="https://github.com/user-attachments/assets/68a78498-bd2f-4192-96ec-fb93208a7a0b" /> | ÉXITO |
| RF-05 | CU-05 | Generar GRE con datos de traslado completos | Serie-Número: "T001-00000001"<br>Punto Partida: "Av. Origen 123, Lima"<br>Punto Llegada: "Av. Destino 456, Callao"<br>Placa: "ABC-123"<br>Conductor: "Juan Pérez"<br>DNI: "12345678"<br>Fecha: LocalDate.now() | GRE creada con todos los datos de traslado, DNI longitud = 8 | <img width="1469" height="1123" alt="CN07_GRE_Completa" src="https://github.com/user-attachments/assets/7aa07711-cc3e-4b40-8798-0ec2f60a54b0" /> | ÉXITO |
| RF-06 | CU-06 | Calcular precio SIN IGV | Producto: "P001"<br>Cantidad: 1<br>Precio Base: 100.0<br>Mostrar IGV: false | Subtotal = 100.0 | <img width="1469" height="1123" alt="CN08_Precio_Sin_IGV" src="https://github.com/user-attachments/assets/e1b24563-40f6-404f-93c5-1d2d4245ec9c" /> | ÉXITO |
| RF-06 | CU-06 | Calcular precio CON IGV (18%) | Producto: "P001"<br>Cantidad: 1<br>Precio Base: 100.0<br>Mostrar IGV: true | Subtotal = 100.0<br>IGV = 18.0<br>Total = 118.0 | <img width="1469" height="1123" alt="CN09_Precio_Con_IGV" src="https://github.com/user-attachments/assets/0aca0ceb-d7b2-4a10-98fb-385da48bb435" /> | ÉXITO |
| RF-06 | CU-06 | Calcular precio con múltiples productos | Producto 1: 2 x 50.0<br>Producto 2: 3 x 100.0 | Subtotal = 400.0<br>Total = 472.0 (con IGV) | <img width="1469" height="1123" alt="CN10_Multiples_Productos" src="https://github.com/user-attachments/assets/aad8cdc3-9d5e-496b-a9fe-02221bd1b8d0" /> | ÉXITO |
| RF-09 | CU-09 | Generar cotización con número correlativo | Cotización 1: Número = 1<br>Cotización 2: Número = 2 | Número Cot2 > Número Cot1 (correlativo automático) | <img width="1469" height="1123" alt="CN11_Cotizacion_Correlativo" src="https://github.com/user-attachments/assets/7814b863-cc1d-4f56-bb8f-3d4a1bea86d7" /> | ÉXITO |
| RF-09 | CU-09 | Cotización debe incluir datos obligatorios | Cliente: RUC "20123456789", "Test SAC"<br>Producto: "P001", 1 UND, 100.0<br>Condición Pago: CONTADO | Cliente ≠ null<br>RUC ≠ null<br>Razón Social ≠ null<br>Condición Pago ≠ null<br>Subtotal > 0<br>Total > 0 |<img width="1469" height="1123" alt="CN12_Cotizacion_Datos_Obligatorios" src="https://github.com/user-attachments/assets/b1ce09c1-108b-44c3-9505-feb29a2d31b2" /> | ÉXITO |
| RF-09 | CU-09 | Validar cotización sin cliente | Cliente: null<br>Productos: 1 producto ("A", "B", 1, "U", 10.0) | Cliente = null | <img width="1469" height="1123" alt="CN15_Cotizacion_Sin_Cliente" src="https://github.com/user-attachments/assets/ebde82f2-b7d9-4d74-97c9-30851cfd1467" /> | ÉXITO |
| RF-10 | CU-10 | Generar nota de crédito asociada a comprobante | Serie: "FC01"<br>Número: 1<br>Factura Ref: "F001-00000123"<br>Motivo: "Corrección por error en la descripción del producto"<br>Tipo: "CORRECCION POR ERROR EN LA DESCRIPCION"<br>Fecha: LocalDate.now() | NC creada con referencia a factura<br>Motivo longitud >= 10 | <img width="1469" height="1123" alt="CN13_NC_Asociada_Comprobante" src="https://github.com/user-attachments/assets/ed515d05-3277-4d64-8c0d-7acc0803a1ff" /> | ÉXITO |
| RF-10 | CU-10 | Validar motivo de nota de crédito obligatorio | Factura Ref: "F001-00000123"<br>Motivo: "" (vacío) | Motivo.isEmpty() = true | <img width="1469" height="1123" alt="CN14_NC_Motivo_Obligatorio" src="https://github.com/user-attachments/assets/fd92b9d5-2824-499c-baff-323458ffb8b0" /> | ÉXITO |
| RF-10 | CU-10 | Validar cálculo de IGV en Nota de Crédito | Subtotal: 100.0<br>IGV: 18.0<br>Total: 118.0 | Total = Subtotal + IGV<br>IGV = Subtotal * 0.18 | <img width="1469" height="1123" alt="CN18_NC_Calculo_IGV" src="https://github.com/user-attachments/assets/10243e4d-e837-4ab0-a7ce-382ce6af21a3" /> | ÉXITO |

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
| RF-04 | Emisión de Comprobantes | CU-03 | 2 | 100% |
| RF-05 | Guías de Remisión Electrónica | CU-04 | 1 | 100% |
| RF-06 | Cálculo de Precios e IGV | CU-05 | 3 | 100% |
| RF-09 | Gestión de Cotizaciones | CU-07 | 3 | 100% |
| RF-10 | Notas de Crédito | CU-08 | 3 | 100% |

---

## Casos de Uso Cubiertos

| ID Caso de Uso | Descripción | Pruebas | Estado |
|----------------|-------------|---------|--------|
| CU-01 | Validar RUC | 3 | 100% |
| CU-02 | Registrar Cliente | 3 | 100% |
| CU-03 | Emitir Comprobante Electrónico | 2 | 100% |
| CU-04 | Generar Guía de Remisión | 1 | 100% |
| CU-05 | Calcular Precios | 3 | 100% |
| CU-07 | Generar Cotización | 3 | 100% |
| CU-08 | Generar Notas de Crédito | 3 | 100% |

---

## Estructura de Evidencias

Las evidencias están organizadas en la carpeta `guide02/imagenes/evidencias-pruebas` con la siguiente estructura:
