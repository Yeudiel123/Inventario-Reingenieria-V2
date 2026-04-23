module InventarioFX_ORM {
    // Librerías que necesitamos
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires ormlite.jdbc;

    // Damos permisos a JavaFX para que lea nuestros diseños y controladores
    opens mx.unison.controladores to javafx.fxml;

    // Damos permiso a ORMLite para que convierta nuestros modelos en base de datos
    opens mx.unison.modelos to ormlite.jdbc, javafx.base;

    // Exportamos la vista principal para que pueda arrancar
    exports mx.unison.vistas;
}