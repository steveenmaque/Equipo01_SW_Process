## Diagrama de casos de uso

![Diagrama de Casos de Uso](../imagenes/DiagramaCasosdeUso.png)

## CU-01: Validar RUC

| Campo | Descripción |
|-------|-------------|
| **Título** | Caso de uso: Validar RUC |
| **ID** | CU_01 |
| **Descripción** | Verifica que el RUC del cliente tenga 11 dígitos y sea único en la base de datos. |
| **Actor** | Sistema (automático) |
| **Precondición** | Se ha ingresado un RUC para validar. |
| **Poscondición** | El RUC es validado como correcto o rechazado. |
| **Flujo Principal** | 1. Sistema recibe el RUC ingresado<br>2. Sistema verifica que tenga 11 dígitos<br>3. Sistema verifica que no exista en la base de datos<br>4. Sistema retorna resultado de validación |

Diagramas y documentación de cada uno de los casos de uso. Con respecto a la documentación de los casos de uso, incluirán lo siguiente:
- Además de los datos del encabezado del CU, incluirán flujo principal y reglas de negocio como mínimo.
- Usar la herramienta gráfica de su preferencia
- Mantener la notación UML de los casos de uso (relaciones include y exclude)
- Subir las imagenes en formato .png, .jpg.
- Resolución de 300 dpi para cada imagen.
