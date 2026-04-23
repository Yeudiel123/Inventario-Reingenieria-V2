# Sistema de Inventario 2.0 (JavaFX + ORMLite)

## 📌 Descripción del Proyecto
Este proyecto es una reestructuración completa (v2.0) de un sistema de inventario legacy (Tarea 12). Se eliminó la dependencia de Swing y JDBC puro para adoptar tecnologías modernas, mejorando la seguridad, escalabilidad y la interfaz de usuario.

## 🏗️ Arquitectura
El proyecto sigue el patrón de diseño **MVC (Modelo-Vista-Controlador)** y el patrón **DAO (Data Access Object)**:
* **Modelos:** Entidades mapeadas con anotaciones de ORMLite (`@DatabaseTable`).
* **Vistas:** Interfaces gráficas desarrolladas en JavaFX (Archivos `.fxml`).
* **Controladores:** Clases Java encargadas de gestionar los eventos de la UI y conectar con la capa de datos.
* **Datos:** Gestión automatizada de SQLite mediante ORMLite.

## 🚀 Mejoras Principales respecto a la v1.0
1. **Interfaz (UI):** Transición de `javax.swing` a `JavaFX` para una experiencia moderna, responsiva y estilizada con CSS.
2. **Base de Datos:** Implementación de **ORMLite**, eliminando sentencias SQL hardcodeadas y automatizando la creación de tablas y relaciones de llaves foráneas.
3. **Seguridad:** Migración del algoritmo de encriptación obsoleto MD5 a **SHA-256 con codificación Base64**. Integración de Control de Acceso Basado en Roles (RBAC).
4. **Testing:** Integración de bases de datos SQLite "en memoria" (`jdbc:sqlite::memory:`) para pruebas unitarias limpias con JUnit 5.

## 📸 Vistas del Sistema
*(Arrastra y suelta aquí las capturas de pantalla de tu aplicación funcionando: Login, Productos y Almacenes)*
