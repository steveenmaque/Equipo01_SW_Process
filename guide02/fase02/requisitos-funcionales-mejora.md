# Descripción detallada de requisitos funcionales de mejora del software

Los siguientes requisitos de mejora fueron identificados en la fase de análisis y negociación con el cliente.  
Aunque no son esenciales para el funcionamiento inicial del sistema, **aportan valor añadido** al optimizar los procesos administrativos, mejorar la precisión de los datos y facilitar la toma de decisiones.

---

## Validar automáticamente errores en descripción/unidades de medida

**Descripción:**  
El sistema deberá implementar un mecanismo de validación automática que detecte inconsistencias en la descripción de los productos o en las unidades de medida ingresadas durante la creación de cotizaciones o facturas.  

**Detalles técnicos:**  
- Verificará que las unidades de medida correspondan a los valores definidos en el catálogo interno del sistema.  
- Validará que la descripción del producto no contenga campos vacíos o caracteres inválidos.  
- Mostrará mensajes de advertencia cuando se detecten errores antes de guardar la transacción.  


---

## Generar reportes mensuales de ventas y facturación

**Descripción:**  
El sistema permitirá generar reportes automáticos que consoliden las ventas y facturaciones mensuales, mostrando información clave de clientes, montos facturados, IGV total, productos más vendidos y medios de pago utilizados.  

**Detalles técnicos:**  
- Los reportes podrán generarse en formato **PDF y/o Excel (.xlsx)**.  
- Se almacenarán en una carpeta del sistema con nombre identificador (por ejemplo, `Reportes_Mensuales/2025_10.pdf`).  
- El usuario podrá seleccionar el **mes y año** a consultar desde el panel administrativo.  
- Los reportes mostrarán gráficos simples (barras o pastel) con el resumen de ventas.  

---

## Ofrecer un menú de opciones para productos más comerciales

**Descripción:**  
El sistema incluirá un módulo adicional con un menú rápido de acceso a los **productos más vendidos o más cotizados**, facilitando su selección durante el registro de cotizaciones o ventas.  

**Detalles técnicos:**  
- El menú se actualizará automáticamente según las estadísticas de ventas almacenadas.  
- Mostrará los productos con mayor frecuencia de uso (por cantidad o monto).  
- Permitirá agregar un producto al carrito con un solo clic.  
- Opción de búsqueda rápida dentro del menú (por nombre o categoría).  


---
