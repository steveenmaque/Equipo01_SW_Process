<img width="1379" height="831" alt="Patrón" src="https://github.com/user-attachments/assets/4b2b7eb5-9021-41d6-bae4-728e3ef81b14" />


## Patrón de software
El patrón seleccionado es el de **Monolito basado en capas**

Se eligió el patrón de arquitectura monolítica porque el sistema se ejecutará localmente en una sola aplicación de escritorio, lo que simplifica su implementación, mantenimiento y control, al concentrar toda la lógica de negocio en un único proceso sin depender de servicios externos, salvo la conexión con la API de SUNAT. La estructura se compone de una capa de presentación y una capa de negocio: la primera representa la interfaz del usuario, desde donde se registran clientes, generan cotizaciones, confirman órdenes de compra y emiten documentos; mientras que la capa de negocio agrupa los módulos que ejecutan las reglas y flujos del proceso, incluyendo el registro y verificación de clientes (RUC), la gestión de cotizaciones, órdenes de compra, registro de ventas, emisión de comprobantes electrónicos, notas de crédito o débito, generación de guías de remisión y almacenamiento de archivos PDF y XML locales. El sistema genera los comprobantes en formato XML conforme al estándar UBL, los firma digitalmente y los envía a la SUNAT, que devuelve un XML de respuesta (CDR) con el resultado de la validación; si el comprobante es aceptado, se genera su representación en PDF, y si es rechazado, se emite una nota de crédito o débito referenciada al documento original. Finalmente, los módulos de negocio se comunican internamente de manera directa dentro del mismo ejecutable, garantizando coherencia transaccional, rapidez de respuesta y simplicidad operativa, mientras la interacción con la SUNAT se limita al intercambio de XML y CDR como validación oficial de los documentos emitidos.

**Registro y verificación de clientes (RUC):** permite ingresar los datos del cliente y validar que el RUC tenga 11 dígitos y no esté duplicado.

**Gestión de cotizaciones:** genera cotizaciones numeradas de forma correlativa, calcula precios con o sin IGV y prepara el documento para ser enviado al cliente.

**Gestión de órdenes de compra:** se activa cuando el cliente acepta la cotización, registrando la orden de compra con su número y condiciones de pago.

**Registro de ventas y credenciales:** registra la venta confirmada y administra las credenciales (RUC, usuario SOL, clave SOL y certificado digital) necesarias para conectarse con la SUNAT.

**Emisión de comprobantes (XML/PDF):** genera el comprobante electrónico (factura o boleta), lo firma digitalmente, lo envía a SUNAT y procesa el XML de respuesta (CDR) para confirmar su validez.

**Notas de crédito/débito:** emite documentos que corrigen o anulan comprobantes anteriores, siguiendo el mismo proceso de validación ante SUNAT.

**Generación de guías de remisión:** crea las guías de traslado de productos asociadas a una venta, con los datos de origen, destino, vehículo y conductor.

**Gestión de archivos (PDF/XML locales):** almacena de forma ordenada los comprobantes, notas, guías y los CDR de SUNAT, junto con sus versiones en PDF para consulta o envío al cliente.
