# Especificación de requisitos de software

## Requisitos funcionales
Los requisitos funcionales se expresan en lenguaje técnico a partir de los requisitos mandatorios identificados en la entrevista con el cliente.

### 1. Registro de clientes
- Registrar clientes con los siguientes campos obligatorios:
  - RUC
  - Razón social
  - Datos personales
- Validar que el RUC tenga 11 dígitos y sea único en la base de datos.
### 2. Generación de cotizaciones
- Generar cotizaciones numeradas de forma correlativa.
```
NroCotización = NroCotizaciónAnterior + 1
Fecha = FechaActual()
```
- Incluir en cada cotización: nombre del cliente, RUC, montos, IGV, condiciones de pago, vigencia y datos de contacto.
- Permitir mostrar precios con o sin IGV, según selección del usuario.
```
PrecioConIGV = PrecioBase * 1.18
PrecioSinIGV = PrecioBase
```
### 3. Emisión de comprobantes electrónicos
- Emitir facturas, boletas, notas de crédito y débito en formato electrónico (XML y PDF).
- Incluir el código QR, logo de la empresa y datos de SUNAT obligatorios.
- Conectarse a SUNAT para validar cada comprobante en línea y almacenar el CDR de aceptación o rechazo.
### 4. Generación de guías de remisión remitente (GRE)
- Permitir generar GRE asociadas al número de orden de compra (OC).
- Los campos obligatorios son los siguientes:
   - Origen y destino
   - Fecha de traslado
   - Vehículo y placa
   - Conductor (nombre y DNI)
   - Peso/volumen de la carga
### 5. Almacenamiento y envío de documentos electrónicos
- Almacenar todos los comprobantes y cotizaciones en formato PDF y XML.
- Permitir la descarga y el envío automático por correo electrónico al cliente.
### 6. Registro de datos de traslado
- Registrar información logística para los envíos como los siguientes datos:
   - Origen/destino
   - Vehículo y placa
   - Nombre y DNI del conductor
   - Peso y volumen de los inflables transportados

## Requisitos no funcionales
- **Portabilidad:** Instalarse y ejecutarse en computadoras de escritorio que utilicen sistemas operativos comunes, principalmente Windows.
- **Mantenibilidad:** Desarrollarse con herramientas y lenguajes ampliamente conocidos, además de contar con documentación técnica que facilite futuras actualizaciones o correcciones.
- **Usabilidad:** Proponer una interfaz sencilla e intuitiva, de manera que pueda ser utilizada sin dificultad por el personal administrativo de la empresa.
- **Rendimiento:** Generar cotizaciones y comprobantes en un tiempo no mayor a un minuto.
- **Disponibilidad:** Mantener la aplicación operativa durante la jornada laboral (8:00 a. m. – 6:00 p. m.) y, en caso de fallas, debe recuperarse en un plazo máximo de 4 horas.
- **Cumplimiento normativo:** Ajustarse a las disposiciones de la SUNAT para la emisión de comprobantes electrónicos (XML, CDR, PDF con QR) y mantenerse compatible con futuros proveedores autorizados (PSE/OSE).
