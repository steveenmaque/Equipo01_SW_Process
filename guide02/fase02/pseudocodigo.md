# Pseudocódigo de los Requisitos de Sistema más Importantes

A continuación, se presentan los pseudocódigos correspondientes a los **requisitos funcionales más importantes** del sistema **InfleSusVentas SRL**, relacionados con los procesos de cotización, emisión de comprobantes electrónicos y generación de guías de remisión.

---

## 1.Generar Cotización

```plaintext
PROCESO GenerarCotizacion
    ENTRADA: DatosCliente, ListaProductos, CondicionPago, Vigencia
    SALIDA: CotizacionPDF, CotizacionXML

    LEER NroCotizacionAnterior
    NroCotizacion ← NroCotizacionAnterior + 1
    FechaActual ← OBTENER_FECHA_ACTUAL()

    SUBTOTAL ← 0
    PARA CADA producto EN ListaProductos HACER
        SUBTOTAL ← SUBTOTAL + (producto.precio * producto.cantidad)
    FIN PARA

    IGV ← SUBTOTAL * 0.18
    TOTAL ← SUBTOTAL + IGV

    CREAR RegistroCotizacion CON:
        Numero = NroCotizacion
        Fecha = FechaActual
        Cliente = DatosCliente
        Subtotal = SUBTOTAL
        IGV = IGV
        Total = TOTAL
        CondicionPago = CondicionPago
        Vigencia = Vigencia

    GUARDAR RegistroCotizacion EN BaseDeDatos
    GENERAR CotizacionPDF Y CotizacionXML
    MOSTRAR MENSAJE "Cotización generada correctamente"

FIN PROCESO
```
---

## 2.Emitir Comprobante Electrónico
```plaintext
PROCESO EmitirComprobanteElectronico
    ENTRADA: DatosVenta, TipoComprobante (Factura o Boleta)
    SALIDA: ComprobantePDF, ComprobanteXML, CDR_SUNAT

    VALIDAR DatosVenta (cliente, montos, IGV, productos)
    SI VALIDACION_ES_CORRECTA ENTONCES
        GENERAR ComprobanteXML CON datos de la venta
        ENVIAR ComprobanteXML A SERVICIO_SUNAT
        ESPERAR RESPUESTA

        SI RESPUESTA = "ACEPTADO" ENTONCES
            GENERAR ComprobantePDF CON código QR y logo empresa
            ALMACENAR ComprobanteXML, PDF y CDR_SUNAT EN BaseDeDatos
            ENVIAR PDF Y XML AL CLIENTE POR CORREO
            MOSTRAR "Comprobante aceptado por SUNAT"
        SINO
            MOSTRAR "Error: Comprobante rechazado por SUNAT"
        FIN SI
    SINO
        MOSTRAR "Error: Datos de venta inválidos"
    FIN SI

FIN PROCESO
```
---

## 3.Generar Guía de Remisión
```plaintext
PROCESO GenerarGuiaRemision
    ENTRADA: NroOrdenCompra, DatosTraslado (origen, destino, vehículo, conductor)
    SALIDA: GuiaPDF, GuiaXML

    VALIDAR existencia de la OrdenCompra(NroOrdenCompra)
    SI EXISTE ENTONCES
        LEER DatosVenta ASOCIADOS
        CREAR GuiaRemision CON:
            NumeroGuia ← GENERAR_NUMERO_CORRELATIVO()
            FechaTraslado ← OBTENER_FECHA_ACTUAL()
            Origen ← DatosTraslado.origen
            Destino ← DatosTraslado.destino
            Vehiculo ← DatosTraslado.vehiculo
            Conductor ← DatosTraslado.conductor
            Productos ← DatosVenta.Productos
        FIN CREAR

        GENERAR GuiaPDF Y GuiaXML
        GUARDAR EN BaseDeDatos
        MOSTRAR "Guía de remisión generada correctamente"
    SINO
        MOSTRAR "Error: Orden de compra no encontrada"
    FIN SI
FIN PROCESO
```






