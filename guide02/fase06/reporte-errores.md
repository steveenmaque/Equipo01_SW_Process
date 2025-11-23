# Anexo 2: Informe de errores encontrados

Este documento detalla los defectos identificados durante la ejecución de las pruebas de caja negra, específicamente aquellos casos donde el comportamiento actual del sistema no coincide con las reglas de negocio esperadas para un entorno de producción.

##  Resumen de Defectos

| ID Defecto | Descripción | Prioridad | Estado |
|------------|-------------|-----------|--------|
| DEF-01 | El sistema permite crear Notas de Crédito con motivo de sustento vacío. | Alta | Abierto |
| DEF-02 | El sistema permite generar Cotizaciones sin un Cliente asignado. | Crítico | Abierto |
| DEF-03 | El sistema permite emitir Comprobantes Electrónicos sin ítems (lista de productos vacía). | Crítico | Abierto |

---

##  Detalle de Errores

###  DEF-01: Motivo de sustento vacío en Nota de Crédito

- **ID Defecto:** DEF-01
- **Descripción del defecto:** El sistema no valida que el campo "Motivo de Sustento" sea obligatorio al crear una Nota de Crédito. Actualmente acepta cadenas vacías sin lanzar error, violando la regla de negocio RN36.
- **Pasos para reproducir:**
  1. Instanciar un objeto `NotaCredito`.
  2. Asignar una factura de referencia válida.
  3. Asignar una cadena vacía `""` al campo `motivoSustento`.
  4. Ejecutar el método de creación/validación.
  5. **Resultado Actual:** El objeto se crea exitosamente con motivo vacío.
  6. **Resultado Esperado:** El sistema debería lanzar una `IllegalArgumentException` o error de validación indicando que el motivo es obligatorio.
- **Evidencia:**
  > *Ver captura: `evidencias/CN14_NC_Motivo_Obligatorio_FALLO.png`*
  > (El test `testValidarMotivoNotaCreditoObligatorio` confirma que `isEmpty()` es true en lugar de fallar).
- **Fecha del defecto:** 2025-11-23
- **Detectado por:** Junior Zelada Llaxa (Tester)
- **Estado del defecto:**  Abierto
- **Corregido por:** Junior Zelada Llaxa
- **Fecha de cierre:** 2025-11-23
- **Prioridad:**  Alta 

---

###  DEF-02: Generación de Cotización sin Cliente

- **ID Defecto:** DEF-02
- **Descripción del defecto:** Es posible instanciar y procesar una `Cotizacion` con el campo `cliente` en `null`. Esto viola la integridad de los datos y la regla RN06/RN31.
- **Pasos para reproducir:**
  1. Crear una nueva instancia de `Cotizacion`.
  2. Asignar `null` explícitamente al cliente (`setCliente(null)`).
  3. Agregar productos válidos.
  4. Intentar guardar o procesar la cotización.
  5. **Resultado Actual:** El sistema permite la operación o retorna null en el getter sin error previo.
  6. **Resultado Esperado:** El sistema debería impedir la creación de una cotización sin cliente asociado.
- **Evidencia:**
  > *Ver captura: `evidencias/CN15_Cotizacion_Sin_Cliente_FALLO.png`*
  > (El test `testCotizacionSinClienteInvalida` muestra que el cliente permanece nulo sin rechazo).
- **Fecha del defecto:** 2025-11-23
- **Detectado por:** Junior Zelada Llaxa (Tester)
- **Estado del defecto:**  Abierto
- **Corregido por:** Steeven Maque
- **Fecha de cierre:** 2025-11-23
- **Prioridad:**  Crítico (Impide flujo de venta posterior)

---

###  DEF-03: Emisión de Comprobante sin Ítems

- **ID Defecto:** DEF-03
- **Descripción del defecto:** El sistema permite crear un `ComprobanteElectronico` con una lista de ítems vacía. Un comprobante de pago debe tener al menos un ítem para ser válido ante SUNAT.
- **Pasos para reproducir:**
  1. Crear una instancia de `ComprobanteElectronico`.
  2. Inicializar la lista de ítems como una lista vacía (`new ArrayList<>()`).
  3. No agregar ningún `DetalleComprobante`.
  4. Verificar el estado del objeto.
  5. **Resultado Actual:** La lista de ítems está vacía y el objeto se considera válido por el sistema actual.
  6. **Resultado Esperado:** Debería existir una validación que exija `items.size() > 0`.
- **Evidencia:**
  > *Ver captura: `evidencias/CN16_Comprobante_Sin_Items_FALLO.png`*
  > (El test `testComprobanteSinItemsInvalido` confirma que la lista vacía es aceptada).
- **Fecha del defecto:** 2025-11-23
- **Detectado por:** Junior Zelada Llaxa (Tester)
- **Estado del defecto:**  Abierto
- **Corregido por:** Steveen Maque
- **Fecha de cierre:** 2025-11-23
- **Prioridad:**  Crítico
