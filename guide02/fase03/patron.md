<img width="1739" height="595" alt="Patrón" src="https://github.com/user-attachments/assets/25579049-0e1f-425c-be6b-328e5e37d8d3" />


## Patrón de software
El patrón seleccionado es el de Microservicios

### Componentes Principales del Sistema
**API Gateway:** Actúa como la puerta de entrada única. Gestiona la autenticación y distribuye las solicitudes a los servicios correspondientes.
**Microservicio de Clientes:** Administra el registro, validación y mantenimiento de los datos de empresas o personas, garantizando la unicidad y corrección del RUC.
**Microservicio de Cotizaciones:** Genera los presupuestos (cálculo de montos, condiciones, vigencia) y permite exportarlos a PDF.
**Microservicio de Comprobantes Electrónicos:** Emite facturas, boletas y notas de crédito/débito. Genera los XML/PDF y se conecta con la **SUNAT** para la validación y almacenamiento del CDR (Constancia de Recepción).
**Microservicio de Guías de Remisión:** Crea los documentos de traslado exigidos por la SUNAT, registrando datos de origen, destino, vehículo y conductor.
**Microservicio de Logística:** Gestiona la parte operativa del transporte, incluyendo vehículos, rutas, estados de envío y trazabilidad.
**Microservicio de Documentos y Notificaciones:** Almacena localmente todos los documentos (XML/PDF) y maneja el envío automático por correo electrónico a los clientes.
**Event Bus (Mensajería):** Es el sistema de comunicación asíncrona. Permite que los microservicios publiquen y se suscriban a eventos (ej. "cotización creada") sin depender directamente uno del otro.

El API Gateway centraliza el acceso, canalizando las solicitudes del usuario hacia los microservicios correspondientes. El servicio de Clientes gestiona los datos de los usuarios y valida el RUC, mientras que el de Cotizaciones genera presupuestos correlativos con cálculos automáticos del IGV. El microservicio de Comprobantes Electrónicos emite facturas y boletas, genera los archivos XML y PDF y se conecta con SUNAT para la validación y registro del CDR. El servicio de Guías de Remisión crea los documentos de traslado con los datos del vehículo, conductor y peso, en coordinación con el microservicio de Logística, que controla las rutas y estados de envío. Finalmente, Documentos y Notificaciones almacena los archivos y gestiona el envío automático por correo electrónico. Todos los servicios se comunican entre sí mediante una mensajería de eventos, lo que garantiza independencia, escalabilidad y resiliencia dentro del sistema.
