# Pruebas de caja negra

## Matriz de trazabilidad de las pruebas de caja negra

| RF | ID CASO | CU Relacionado | Descripción breve del caso | Entrada (s) | Resultado Esperado | Evidencia | Resultado |
|----|---------|---------------|---------------------------|-------------|-------------------|-----------|-----------|
| RF1 | CN01 | CU-01, CU-02 | Registrar cliente con datos válidos | RUC: "20123456789", Razón Social: "Empresa Test SAC", Email: "test@empresa.com", Teléfono: "999888777" | Cliente registrado exitosamente con todos los datos correctos | Screenshot_CN01.png | EXITO |
| RF1 | CN02 | CU-01 | Validar RUC con menos de 11 dígitos | RUC: "201234567" (9 dígitos) | Error: "RUC debe tener 11 dígitos" - Lanzar IllegalArgumentException | Screenshot_CN02.png | FALLO |
| RF2 | CN03 | CU-09 | Generar cotización con numeración correlativa | Número cotización generado aleatoriamente | Cotización con número > 0 asignado correctamente | Screenshot_CN03.png | EXITO |
| RF2 | CN04 | CU-09 | Generar PDF sin cliente debe fallar | Cliente: null, productos: 1 | No genera PDF, retorna null | Screenshot_CN04.png | EXITO |
| RF2 | CN05 | CU-09 | Generar PDF sin productos debe fallar | Cliente válido, productos: [] | No genera PDF, retorna null | Screenshot_CN05.png | EXITO |
| RF2 | CN06 | CU-06, CU-09 | Calcular precio SIN IGV | Producto: cantidad=1, precio=100, mostrarIGV=false | Subtotal = 100.00 (sin IGV) | Screenshot_CN06.png | EXITO |
| RF2 | CN07 | CU-06, CU-09 | Calcular precio CON IGV (18%) | Producto: cantidad=1, precio=100 | Subtotal=100.00, IGV=18.00, Total=118.00 | Screenshot_CN07.png | EXITO |
| RF2 | CN08 | CU-06, CU-09 | Múltiples productos suman correctamente | Producto1: 2x50=100, Producto2: 3x100=300 | Subtotal = 400.00 | Screenshot_CN08.png | EXITO |

## Trazabilidad con Casos de Uso

| Caso de Uso | Descripción | Pruebas relacionadas |
|-------------|-------------|---------------------|
| **CU-01:** Validar RUC | Verifica que el RUC del cliente tenga 11 dígitos y sea único | CN01, CN02 |
| **CU-02:** Registrar cliente | Registra un nuevo cliente en el sistema con sus datos obligatorios | CN01 |
| **CU-06:** Calcular precios | Calcula el precio con o sin IGV según selección del usuario | CN06, CN07 |
| **CU-09:** Generar cotización | Genera una cotización numerada con los datos del cliente y productos | CN03, CN04, CN05, CN06, CN07 |

## Resumen de resultados

- **Total de casos de prueba:** 7
- **Casos exitosos (PASS):** 6 (85.7%)
- **Casos fallidos (FAIL):** 1 (14.3%)
- **Defectos encontrados:** 2 (DEF-001, DEF-002)
- **Casos de uso cubiertos:** 4 de 11 (CU-01, CU-02, CU-06, CU-09)

## Cobertura de Requisitos Funcionales

| Requisito | Descripción | Estado de implementación | Casos de prueba |
|-----------|-------------|------------------------|-----------------|
| **RF1** | Registro de clientes |  Parcialmente implementado | CN01, CN02 |
| **RF2** | Generación de cotizaciones |  Parcialmente implementado | CN03, CN04, CN05, CN06, CN07, CN08 |
| **RF3** | Emisión de comprobantes electrónicos | ⏳ Pendiente | - |
| **RF4** | Generación de guías de remisión | ⏳ Pendiente | - |
| **RF5** | Almacenamiento y envío de documentos | ⏳ Pendiente | - |
| **RF6** | Registro de datos de traslado | ⏳ Pendiente | - |

## Observaciones

### CN02 - Validar RUC con menos de 11 dígitos (FALLO ESPERADO)

Este caso de prueba está diseñado para **FALLAR** y detectar el defecto **DEF-001**.

**Análisis:**
- La clase `Cliente` actualmente **NO implementa** la validación del **CU-01: Validar RUC**
- Según la **regla de negocio RN01**: "El RUC debe tener exactamente 11 dígitos"
- La prueba CN02 verifica esta regla y detecta que **NO está implementada**
- Este es un defecto de **prioridad ALTA** que debe ser corregido por el equipo de desarrollo

### CN03 - Numeración correlativa

**Nota técnica:** La prueba CN03 verifica que el número de cotización sea mayor a 0, ya que actualmente el sistema genera números aleatorios. En producción, debería implementarse la fórmula:
```
NroCotización = NroCotizaciónAnterior + 1
```
según lo especificado en la regla de negocio RN30 del CU-09.

## Evidencias

Todas las capturas de pantalla se encuentran en la carpeta `imagenes/evidencias-pruebas/`:
![CN01](imagenes/evidencias-pruebas/Screenshot_CN01.png)

- `Screenshot_CN01.png` - Cliente registrado con datos válidos 
- `Screenshot_CN02.png` - Error de validación de RUC (defecto DEF-001) 
- `Screenshot_CN03.png` - Cotización con número asignado 
- `Screenshot_CN04.png` - PDF sin cliente falla correctamente 
- `Screenshot_CN05.png` - PDF sin productos falla correctamente 
- `Screenshot_CN06.png` - Cálculo de precio SIN IGV 
- `Screenshot_CN07.png` - Cálculo de precio CON IGV (18%)
- `Screenshot_CN08.png` - Múltiples productos sumando correctamente
- `Screenshot_CajaBlanca_Resultados.png` - Resumen de pruebas de caja blanca
- `Screenshot_CajaNegra_Resultados.png` - Resumen general de pruebas
