## Software

Para garantizar un flujo de trabajo eficiente, estandarizado y colaborativo, el equipo de desarrollo seleccionó un stack tecnológico moderno. A continuación, se detallan las herramientas de software empleadas en cada fase del ciclo de vida del proyecto, desde la planificación hasta el despliegue del prototipo.

### 1. Entorno de Desarrollo y Construcción (Backend)

La base lógica del prototipo se construyó sobre tecnologías de largo soporte para asegurar la estabilidad y mantenibilidad del código.

* **Lenguaje de Programación: Java SE 21 (LTS)**
    Se utilizó la versión 21 (Long-Term Support) de Java Standard Edition. Esta elección permitió aprovechar las últimas características del lenguaje, como la gestión optimizada de memoria y la sintaxis moderna, garantizando un rendimiento superior en la ejecución de la lógica de negocio y una arquitectura robusta orientada a objetos.

* **Gestión de Proyectos y Construcción: Apache Maven**
    Para la automatización del ciclo de vida de construcción, se implementó **Maven**. Esta herramienta fue fundamental para:
    * La gestión centralizada de dependencias a través del archivo `pom.xml`.
    * La estandarización de la estructura de directorios del proyecto.
    * La compilación, empaquetado y ejecución de pruebas del software de manera automatizada.

### 2. Control de Versiones y Trabajo Colaborativo

Para asegurar la integridad del código fuente y facilitar el trabajo en equipo, se implementó un sistema de control de versiones distribuido.

* **Git:** Utilizado como herramienta de control de versiones local, permitiendo el seguimiento detallado de cambios, creación de ramas (branches) y gestión del historial de modificaciones en el código fuente.
* **GitHub:** Plataforma de alojamiento remoto utilizada para centralizar el repositorio del proyecto. Facilitó la integración del trabajo del equipo y sirvió como respaldo de seguridad (backup) de todo el ciclo de desarrollo.
* **VS Code** Utilizado para el trabajo colaborativo con su extension a cuentas de github y modificando cada archivo de manera sencilla.

### 3. Herramientas de Análisis y Diseño (CASE)

Antes de la codificación, se utilizaron herramientas de modelado visual para definir la arquitectura y la experiencia de usuario.

* **Diseño de Arquitectura: Draw.io**
    Empleada durante las fases de análisis para la elaboración de diagramas técnicos estandarizados bajo la notación UML (Diagramas de Clases, Secuencia y Casos de Uso), permitiendo visualizar la interacción entre los objetos del sistema y la lógica de flujo de datos.

* **Diseño de Interfaces (UI/UX): Figma**
    Utilizada para el prototipado de alta fidelidad en la fase de diseño. Figma permitió definir la experiencia visual del usuario y la navegación antes de programar las vistas, asegurando que la interfaz gráfica (GUI) final implementada en Java fuera coherente con los requerimientos funcionales.
