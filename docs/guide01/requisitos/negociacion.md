# Negociación y discusión de requisitos
Durante la negociación con la empresa InfleSusVentas SRL, se revisaron los requisitos identificados en la fase de levantamiento con el fin de validar su claridad, relevancia y factibilidad técnica. Este proceso permitió resolver posibles ambigüedades y priorizar aquellos que resultan críticos para el funcionamiento inicial del sistema.
Algunos requisitos iniciales presentaban cierto grado de ambigüedad:
- *“Necesito generar cotizaciones rápidas y numeradas correlativamente”*: se discutió qué significa “rápidas”. Se acordó que el sistema debe permitir emitir una cotización en menos de 1 minuto, incluyendo cálculo automático de IGV y fecha de vigencia.
- *“Registrar condiciones de pago”*: no se especificaban cuales y se precisaron modalidades de contado, crédito y adelanto 50%. El sistema debe registrar las tres modalidades definidas.
- *“Necesito que el sistema esté disponible durante los horarios laborales y se recupere rápido ante caídas”*: se aclaró que “rápido” equivale a un tiempo máximo de recuperación de 4 horas.
Gracias a esta discusión, se logró transformar en requisitos verificables lo que inicialmente eran solicitudes generales.

## Conflicto de intereses

- *Validación automática de errores en descripción/unidades de medida*: El cliente lo veía esencial, pero el equipo explicó la complejidad técnica y costo adicional. Se acordó trasladarlo a una segunda fase.
- *Generación de reportes mensuales de ventas y facturación*: El cliente aceptó que inicialmente se realicen manualmente y que los reportes automáticos se implementen como mejora.
- *Menú de productos comerciales*: Se priorizó como funcionalidad de segunda fase, ya que no afecta el cumplimiento normativo inmediato.
- *Promociones/descuentos y clientes frecuentes*: Se reconoció que no generan valor significativo al negocio y se descartaron del alcance inicial.

## Resultado de la negociación
- **Requisitos mandatorios**: Se implementarán en la primera versión (cotizaciones, emisión de comprobantes SUNAT, guías de remisión, PDF/XML, condiciones de pago, traslado).

- **Requisitos de mejora**: Se incluirán en una segunda fase (validación automática, reportes, menú de productos comerciales).

- **Requisitos sin valor**: Quedaron fuera del alcance inicial (promociones/descuentos, clientes frecuentes).
