<img width="1869" height="566" alt="Patrón" src="https://github.com/user-attachments/assets/9d78c79b-8a3c-4532-ae81-3da2cb515ab5" />


## Patrón de software
El patrón seleccionado es el de **Microservicios.**

### Componentes Principales del Sistema
**API Gateway:** Actúa como la puerta de entrada única. Gestiona la autenticación y distribuye las solicitudes a los servicios correspondientes.

**Microservicio de Clientes:** Administra el registro, validación y mantenimiento de los datos de empresas o personas, garantizando la unicidad y corrección del RUC.

**Microservicio de Cotizaciones:** Genera los presupuestos (cálculo de montos, condiciones, vigencia) y permite exportarlos a PDF.

**Microservicio de Comprobantes Electrónicos:** Emite facturas, boletas y notas de crédito/débito. Genera los XML/PDF y se conecta con la **SUNAT** para la validación y almacenamiento del CDR (Constancia de Recepción).

**Microservicio de Guías de Remisión:** Crea los documentos de traslado exigidos por la SUNAT, registrando datos de origen, destino, vehículo y conductor.

**Pedidos (Orden de Compra):** Registra y gestiona las órdenes de compra (OC), sirviendo de enlace entre cotizaciones y comprobantes.

**Microservicio de Logística:** Gestiona la parte operativa del transporte, incluyendo vehículos, rutas, estados de envío y trazabilidad.

**Microservicio de Documentos y Notificaciones:** Almacena localmente todos los documentos (XML/PDF) y maneja el envío automático por correo electrónico a los clientes.

**Event Bus (Mensajería):** Es el sistema de comunicación asíncrona. Permite que los microservicios publiquen y se suscriban a eventos (ej. "cotización creada") sin depender directamente uno del otro.

El API Gateway direcciona las solicitudes del usuario hacia los microservicios correspondientes, garantizando un acceso seguro y controlado. El servicio de Clientes gestiona la información de las empresas y valida el RUC, mientras que Cotizaciones genera propuestas comerciales con cálculos de IGV y vigencia. Cuando el cliente confirma una compra, el Microservicio de Pedidos registra la Orden de Compra (OC), vinculándola con la cotización y sirviendo de puente hacia la emisión del comprobante electrónico y la guía de remisión. El Microservicio de Comprobantes Electrónicos se encarga de generar los archivos XML y PDF, conectarse con la SUNAT para su validación y almacenar el CDR correspondiente. El servicio de Guías de Remisión documenta los traslados de mercancía y se coordina con Logística, que controla los vehículos, conductores y rutas. Finalmente, Documentos y Notificaciones gestiona el almacenamiento local y el envío de los documentos por correo electrónico. Todos los servicios interactúan mediante un Event Bus, garantizando independencia, resiliencia y escalabilidad en toda la solución.
