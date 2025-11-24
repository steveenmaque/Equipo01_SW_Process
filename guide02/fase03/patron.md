<img width="1600" height="751" alt="Patrón" src="https://github.com/user-attachments/assets/4d9718de-6ebc-454d-aca0-754569ac8026" />


## Patrón de software
El patrón seleccionado es el **MVC**

El sistema sigue el patrón MVC (Modelo-Vista-Controlador), donde Java Swing se utiliza para la Vista, proporcionando la interfaz de usuario para que el cliente pueda interactuar con el sistema, como generar cotizaciones, realizar compras o emitir comprobantes. La Vista captura las interacciones del usuario y las envía al Controlador. El Controlador en Spring Boot gestiona la lógica de las solicitudes, orquestando las operaciones del modelo, como la gestión de cotizaciones, órdenes de compra, emisión de comprobantes y la generación de notas de crédito. El Modelo está compuesto por los servicios que implementan la lógica de negocio, sin necesidad de persistencia en base de datos, ya que toda la gestión se realiza mediante la validación de documentos y generación de archivos locales (como XML y PDF). Esta estructura asegura una clara separación de responsabilidades, donde Spring Boot maneja el backend, mientras que Java Swing se encarga del frontend y de la interacción directa con el usuario.

**Gestión de Cotizaciones :** Este módulo se encarga de crear y gestionar las cotizaciones, calculando los precios con o sin IGV y generando el documento correspondiente en formato PDF.

**Gestión de Órdenes de Compra :** Una vez que el cliente acepta la cotización, este módulo registra la orden de compra, vinculándola a la cotización y gestionando los datos relevantes para la transacción.

**Emisión de Comprobantes (Factura) :** Se encarga de generar los comprobantes electrónicos (facturas o boletas) en formato XML. También genera los documentos en formato PDF para ser entregados al cliente.

**Generación de Notas de Crédito :** Este módulo permite la emisión de notas de crédito en caso de que un comprobante sea rechazado o necesite ser corregido por algún error en la transacción. También se genera el PDF correspondiente.
