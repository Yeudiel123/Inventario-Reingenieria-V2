package mx.unison.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML private BorderPane mainPane;
    @FXML private javafx.scene.control.Button btnProductos;
    @FXML private javafx.scene.control.Button btnAlmacenes;
    @FXML private javafx.scene.control.Button btnConfiguracion;

    @FXML
    public void initialize() {
        // Obtenemos el rol del usuario que inició sesión
        String rol = mx.unison.utileria.Sesion.usuarioActual.getRol();

        // Ocultamos los botones según los permisos del usuario
        if (rol.equals("PRODUCTO")) {
            btnAlmacenes.setVisible(false);
            btnAlmacenes.setManaged(false); // Quita el espacio en blanco
            btnConfiguracion.setVisible(false);
            btnConfiguracion.setManaged(false);
        } else if (rol.equals("ALMACEN")) {
            btnProductos.setVisible(false);
            btnProductos.setManaged(false);
            btnConfiguracion.setVisible(false);
            btnConfiguracion.setManaged(false);
        }
        // Si es ADMIN, no entra en los if y ve todos los botones.
    }

    @FXML
    protected void abrirProductos() {
        try {
            Node vistaProductos = FXMLLoader.load(getClass().getResource("/vistas/Productos.fxml"));
            mainPane.setCenter(vistaProductos);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    protected void abrirAlmacenes() {
        try {
            Node vistaAlmacenes = FXMLLoader.load(getClass().getResource("/vistas/Almacenes.fxml"));
            mainPane.setCenter(vistaAlmacenes);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    protected void abrirConfiguracion() {
        try {
            Node vistaConfig = FXMLLoader.load(getClass().getResource("/vistas/Configuracion.fxml"));
            mainPane.setCenter(vistaConfig);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    protected void cerrarSesion(ActionEvent event) {
        try {
            mx.unison.utileria.Sesion.usuarioActual = null; // Limpiamos la sesión al salir

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/vistas/Login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 500);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) { e.printStackTrace(); }
    }
}