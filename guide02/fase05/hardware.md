## Hardware

En esta sección se detallan las características técnicas del equipo de cómputo utilizado durante todo el Ciclo de Vida de Desarrollo de Software (SDLC) del prototipo. La elección de este hardware sirvió como base para la codificación, pruebas y despliegue local de la solución.

### 1. Hardware Utilizado

El desarrollo se llevó a cabo en una estación de trabajo con las siguientes especificaciones técnicas:

| Componente | Especificación Técnica |
| :--- | :--- |
| **Procesador (CPU)** | Intel Core i3-10100F (10ª Generación, 4 núcleos, 8 hilos) |
| **Memoria RAM** | 16 GB |
| **Tarjeta Gráfica (GPU)** | NVIDIA GeForce GTX 1650 |
| **Almacenamiento** | Unidad de Estado Sólido (SSD) 120  GB |

### 2. Análisis de Consumo de Recursos

Durante las fases de construcción y ejecución del prototipo, se realizó un monitoreo del rendimiento del equipo. Se observó que el software desarrollado presenta una **baja demanda de recursos computacionales**. 

El uso del procesador y la memoria RAM se mantuvo muy por debajo de los límites operativos del hardware descrito (load average mínimo), lo que indica que:

1.  **Eficiencia del Código:** La arquitectura del software está optimizada y no genera procesos bloqueantes ni fugas de memoria significativas.
2.  **Viabilidad de Implementación:** Dado que el equipo de desarrollo (gama de entrada/media) no fue sometido a estrés de carga (stress test), se concluye que el prototipo es altamente viable para ser implementado en equipos con prestaciones similares o incluso inferiores sin comprometer la experiencia de usuario.
