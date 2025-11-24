## Descripción detallada de requisitos funcionales mandatorios del software

## Requisitos funcionales

Los requisitos funcionales se expresan en lenguaje técnico a partir de los requisitos mandatorios identificados en la entrevista con el cliente.

### 1. Registro de clientes

- Registrar clientes con los siguientes campos obligatorios:
    - RUC
    - Razón social
    - Datos personales
- Validar que el RUC tenga 11 dígitos y sea único.

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

- Emitir solamente facturas y notas de crédito en formato electrónico (XML y PDF).

### 4. Generación de guías de remisión (GRE)

- Permitir generar GRE asociadas al número de orden de compra (OC).
- Los campos obligatorios son los siguientes:
    - Origen y destino
    - Fecha de traslado
    - Vehículo y placa
    - Conductor
    - Peso/volumen de la carga

### 5. Almacenamiento y envío de documentos electrónicos

- Almacenar todos los comprobantes y cotizaciones en formato PDF y XML.

### 6. Registro de datos de traslado

- Registrar información logística para los envíos como los siguientes datos:
    - Origen/destino
    - Vehículo y placa
    - Nombre del conductor
    - Licencia del conductor
    - Peso y volumen de los inflables transportados

