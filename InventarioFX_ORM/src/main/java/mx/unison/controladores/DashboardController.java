package mx.unison.controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import mx.unison.datos.ConexionDB;
import mx.unison.modelos.Usuario;
import mx.unison.utileria.Sesion;

import java.io.IOException;
import java.sql.SQLException;

public class DashboardController {
    @FXML private BorderPane mainPane;
    @FXML private Button btnProductos;
    @FXML private Button btnAlmacenes;
    @FXML private Button btnConfiguracion;
    @FXML private Label lblUsuario;
    @FXML private Label lblTotalProductos;
    @FXML private Label lblTotalAlmacenes;

    private Node vistaInicio;

    @FXML
    public void initialize() {
        Usuario usuario = Sesion.requerirRol("ADMIN", "PRODUCTO", "ALMACEN");
        vistaInicio = mainPane.getCenter();
        lblUsuario.setText(usuario.getNombre() + " · " + nombreRol(usuario.getRol()));
        mostrarBoton(btnProductos, Sesion.tieneRol("ADMIN", "PRODUCTO"));
        mostrarBoton(btnAlmacenes, Sesion.tieneRol("ADMIN", "ALMACEN"));
        mostrarBoton(btnConfiguracion, true);
        actualizarContadores();
    }

    private String nombreRol(String rol) {
        return switch (rol) {
            case "ADMIN" -> "Administración";
            case "PRODUCTO" -> "Productos";
            case "ALMACEN" -> "Almacenes";
            default -> rol;
        };
    }

    private void mostrarBoton(Button boton, boolean visible) {
        boton.setVisible(visible);
        boton.setManaged(visible);
    }

    @FXML
    protected void abrirInicio() {
        try {
            Sesion.requerirRol("ADMIN", "PRODUCTO", "ALMACEN");
            mainPane.setCenter(vistaInicio);
            actualizarContadores();
        } catch (SecurityException e) {
            mostrarError("Acceso restringido", e.getMessage());
        }
    }

    private void actualizarContadores() {
        try {
            lblTotalProductos.setText(String.valueOf(ConexionDB.getProductoDao().countOf()));
            lblTotalAlmacenes.setText(String.valueOf(ConexionDB.getAlmacenDao().countOf()));
        } catch (SQLException e) {
            lblTotalProductos.setText("—");
            lblTotalAlmacenes.setText("—");
            mostrarError("Resumen no disponible", "No se pudieron consultar los totales. Comprueba la base de datos y vuelve a Inicio para reintentar.");
        }
    }

    @FXML
    protected void abrirProductos() {
        abrirVista("/vistas/Productos.fxml", "ADMIN", "PRODUCTO");
    }

    @FXML
    protected void abrirAlmacenes() {
        abrirVista("/vistas/Almacenes.fxml", "ADMIN", "ALMACEN");
    }

    @FXML
    protected void abrirConfiguracion() {
        abrirVista("/vistas/Configuracion.fxml", "ADMIN", "PRODUCTO", "ALMACEN");
    }

    private void abrirVista(String recurso, String... roles) {
        try {
            Sesion.requerirRol(roles);
            mainPane.setCenter(FXMLLoader.load(getClass().getResource(recurso)));
        } catch (SecurityException e) {
            mostrarError("Acceso restringido", e.getMessage());
        } catch (IOException e) {
            mostrarError("No se pudo abrir la vista", "Vuelve a intentarlo. Si el problema continúa, reinicia la aplicación.");
        }
    }

    @FXML
    protected void cerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/Login.fxml"));
            Scene scene = new Scene(loader.load(), 480, 700);
            Stage stage = (Stage) mainPane.getScene().getWindow();
            Sesion.usuarioActual = null;
            stage.setMinWidth(480);
            stage.setMinHeight(700);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("Inventario · Iniciar sesión");
            stage.centerOnScreen();
        } catch (IOException e) {
            mostrarError("No se pudo cerrar sesión", "No se pudo abrir el inicio de sesión. Vuelve a intentarlo o cierra la aplicación.");
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
