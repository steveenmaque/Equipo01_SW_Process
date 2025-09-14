# Especificación de requisitos de software

## Requisitos funcionales
Los requisitos funcionales se expresan en lenguaje técnico a partir de los requisitos mandatorios identificados en la entrevista con el cliente.

### 1. Registro de clientes
- El sistema debe permitir registrar clientes con los siguientes campos obligatorios:
  - RUC
  - Razón social
  - Datos personales
- El sistema debe validar que el RUC tenga 11 dígitos y sea único en la base de datos.
### 2. Generación de cotizaciones
- El sistema debe generar cotizaciones numeradas de forma correlativa.
```
NroCotización = NroCotizaciónAnterior + 1
Fecha = FechaActual()
```
- Cada cotización debe contener: nombre del cliente, RUC, montos, IGV, condiciones de pago, vigencia y datos de contacto.
- El sistema debe permitir mostrar precios con o sin IGV, según selección del usuario.
```
PrecioConIGV = PrecioBase * 1.18
PrecioSinIGV = PrecioBase
```
### 3. Emisión de comprobantes electrónicos
- El sistema debe emitir facturas, boletas, notas de crédito y débito en formato electrónico (XML y PDF).
- Cada comprobante debe incluir el código QR, logo de la empresa y datos de SUNAT obligatorios.
- El sistema debe conectarse a SUNAT para validar cada comprobante en línea y almacenar el CDR de aceptación o rechazo.
### 4. Generación de guías de remisión remitente (GRE)
- El sistema debe permitir generar GRE asociadas al número de orden de compra (OC).
- Los campos obligatorios son los siguientes:
   - Origen y destino
   - Fecha de traslado
   - Vehículo y placa
   - Conductor (nombre y DNI)
   - Peso/volumen de la carga
### 5. Almacenamiento y envío de documentos electrónicos
- El sistema debe almacenar todos los comprobantes y cotizaciones en formato PDF y XML.
- El sistema debe permitir la descarga y el envío automático por correo electrónico al cliente.
### 6. Registro de condiciones de pago
- El sistema debe permitir registrar las condiciones de pago siguientes:
   - Contado
   - Crédito
   - Adelanto (50%)
```
MontoAdelanto = MontoTotal * 0.5
MontoPendiente = MontoTotal - MontoAdelanto
```
### 7. Registro de datos de traslado
- El sistema debe registrar información logística para los envíos como los siguientes datos:
   - Origen/destino
   - Vehículo y placa
   - Nombre y DNI del conductor
   - Peso y volumen de los inflables transportados

## Requisitos no funcionales
- **Portabilidad:** El sistema debe poder instalarse y ejecutarse en computadoras de escritorio que utilicen sistemas operativos comunes, principalmente Windows.
- **Mantenibilidad:** La aplicación debe desarrollarse con herramientas y lenguajes ampliamente conocidos, además de contar con documentación técnica que facilite futuras actualizaciones o correcciones.
- **Usabilidad:** La interfaz debe ser sencilla e intuitiva, de manera que pueda ser utilizada sin dificultad por el personal administrativo de la empresa.
- **Rendimiento:** El sistema debe generar cotizaciones y comprobantes en un tiempo no mayor a un minuto.
- **Disponibilidad:** La aplicación debe encontrarse operativa durante la jornada laboral (8:00 a. m. – 6:00 p. m.) y, en caso de fallas, debe recuperarse en un plazo máximo de 4 horas.
- **Cumplimiento normativo:** El software debe ajustarse a las disposiciones de la SUNAT para la emisión de comprobantes electrónicos (XML, CDR, PDF con QR) y mantenerse compatible con futuros proveedores autorizados (PSE/OSE).
---------------------------------------------------------
Los requisitos funcionales se expresan en lenguaje técnico a partir de los requisitos funcionales mandatorios que se han identificado en la categorización de requisitos de usuario.
Ello implica el desarrollo de los siguientes puntos:
- Puede escribirse en pseudocódigo incluyendo anotaciones de fórmulas de cálculo matemático según sea el caso. Este escenario es factible cuando el requisito ha sido claramente definido y validado
  por el usuario/cliente.
- Formato de interfaz de usuario y GUI como parte de la capa de presentación (Front End) del aplicativo de software: Incluye la interfaz de usuario para el sistema como propuesta inicial a las necesidades
  del cliente. Deberá incluirlo en el Anexo “A”. Este escenario es factible cuando los requisitos no se encuentran definidos desde la perspectiva del usuario o existe dificultad para su obtención y entendimiento.
  Puede utilizar cualquier herramienta mockup libre para diseñar sus interfaces: https://careerfoundry.com/en/blog/ux-design/free-wireframing-tools/
------------------------------------------------------
- Portabilidad del software
- Facilidad de mantenimiento: Que implica el grado de conocimiento de la herramienta de desarrollo del software, así como de la disponibilidad de personal técnico apropiado entre otros.
- Usabilidad del software
- Velocidad de procesamiento de datos
- Restricciones técnicas del software: Por ejemplo, restricciones de diseño debido al sistema operativo utilizado, el entorno de la plataforma, problemas de compatibilidad con alguna aplicación interna o
  externa a la organización, estándar para alguna aplicación determinada, entre otros.


# Tips para mayor claridad
## Propósito
Definir los requisitos técnicos mínimos (RTM) del proyecto de software.

## Qué se espera
- Que sea el imput para el desglose de tareas de la planificación del proyecto, para su posterior diseño e implementación.
