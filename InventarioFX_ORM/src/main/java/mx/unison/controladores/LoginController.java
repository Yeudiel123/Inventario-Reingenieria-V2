package mx.unison.controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mx.unison.datos.ConexionDB;
import mx.unison.modelos.Usuario;
import mx.unison.utileria.CryptoUtils;

import java.io.IOException;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMensaje;

    @FXML
    protected void onLoginButtonClick() {
        String user = txtUsuario.getText().trim();
        String pass = txtPassword.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("Por favor, llena todos los campos.");
            return;
        }

        try {
            Usuario usuarioDb = ConexionDB.getUsuarioDao().queryForId(user);

            if (usuarioDb != null && usuarioDb.getPasswordHash().equals(CryptoUtils.hashPassword(pass))) {
                lblMensaje.setStyle("-fx-text-fill: green;");
                lblMensaje.setText("¡Acceso concedido! Cargando sistema...");

                // ¡AQUÍ ESTÁ LA MAGIA DE LA SESIÓN!
                mx.unison.utileria.Sesion.usuarioActual = usuarioDb;

                cargarDashboard();

            } else {
                lblMensaje.setStyle("-fx-text-fill: red;");
                lblMensaje.setText("Usuario o contraseña incorrectos.");
            }
        } catch (Exception e) {
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("Error al conectar con la base de datos.");
            e.printStackTrace();
        }
    }

    private void cargarDashboard() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/vistas/Dashboard.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("UNISON - Panel de Control");
            stage.centerOnScreen();
        } catch (IOException e) {
            System.err.println("Error al cargar el Dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
}