## Seguimiento

A lo largo del desarrollo del sistema, siguiendo el modelo espiral, se presentaron diversos inconvenientes técnicos y de alcance que influyeron en las decisiones tomadas durante cada ciclo. A continuación, se describen los principales ajustes realizados y en qué ciclo ocurrieron.

### Ciclo 1 – Definición de objetivos y prototipo inicial

En el primer ciclo se identificaron los objetivos generales del sistema y se definieron los módulos principales: cotizaciones, ventas, comprobantes y guías de remisión, etc. También se levantaron los requisitos iniciales con el cliente y se evaluaron los posibles riesgos, como la integración con la API de SUNAT y la necesidad de generar comprobantes electrónicos. Para reducir la incertidumbre, se construyó un prototipo básico de la interfaz y del flujo de cotización.

### Ciclo 2 – Problemas con la API de SUNAT

Durante este ciclo se intentó implementar la API oficial de SUNAT para la validación de comprobantes electrónicos. Sin embargo, se presentaron varios inconvenientes, como la complejidad de los endpoints, restricciones en el acceso y fallas en las pruebas locales. Estos riesgos no pudieron ser reducidos de manera efectiva dentro del tiempo previsto.
Debido a ello, se decidió descartar la integración real con la API y optar por una simulación controlada de la validación, permitiendo continuar con el desarrollo sin comprometer la funcionalidad general del sistema.

### Ciclo 3 – Descarte del envío automático de comprobantes por correo

En este ciclo se evaluó la incorporación del envío automático de comprobantes electrónicos por correo electrónico. Aunque inicialmente se consideraba una característica importante, surgieron dificultades relacionadas con la configuración del servidor SMTP, limitaciones de seguridad y tiempos adicionales de configuración.
Tras analizar estos riesgos, se concluyó que la funcionalidad podía generar más retrasos que beneficios en esta etapa, por lo que se decidió postergar y finalmente descartar su implementación, dejando únicamente la generación local de los comprobantes.

### Ciclo 4 – Ajustes finales y estabilización del sistema

En este ciclo se revisaron las funciones implementadas y se enfocó en la estabilidad del sistema, priorizando la corrección de errores y la mejora de la interfaz. Las funcionalidades descartadas en ciclos anteriores fueron reemplazadas por procesos simulados o manuales, asegurando que el sistema se mantuviera operativo y coherente con los objetivos del proyecto.
