package mx.unison.controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mx.unison.modelos.Usuario;
import mx.unison.utileria.Autenticacion;
import mx.unison.utileria.Sesion;
import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmarPassword;
    @FXML private VBox panelConfirmacion;
    @FXML private Label lblTitulo;
    @FXML private Label lblSubtitulo;
    @FXML private Label lblMensaje;
    @FXML private Button btnAcceder;
    private boolean primeraCuenta;

    @FXML
    public void initialize() throws SQLException {
        primeraCuenta = Autenticacion.requiereConfiguracion();
        panelConfirmacion.setVisible(primeraCuenta);
        panelConfirmacion.setManaged(primeraCuenta);
        if (primeraCuenta) {
            lblTitulo.setText("Prepara tu inventario");
            lblSubtitulo.setText("Crea tu cuenta de administrador. Elige una contraseña de al menos 12 caracteres.");
            btnAcceder.setText("Crear cuenta y entrar");
        } else if (Boolean.getBoolean("inventario.demo")) {
            lblSubtitulo.setText("Modo demostración · Explora el sistema con datos de ejemplo.");
        }
    }

    @FXML
    protected void onLoginButtonClick() {
        String nombre = txtUsuario.getText().trim();
        String password = txtPassword.getText();
        if (nombre.isEmpty() || password.isEmpty()) {
            lblMensaje.setText("Escribe tu usuario y contraseña.");
            return;
        }
        if (primeraCuenta && !password.equals(txtConfirmarPassword.getText())) {
            lblMensaje.setText("Las contraseñas no coinciden.");
            return;
        }
        btnAcceder.setDisable(true);
        try {
            Usuario usuario = primeraCuenta ? Autenticacion.crearAdministrador(nombre, password)
                    : Autenticacion.autenticar(nombre, password);
            if (usuario == null) {
                lblMensaje.setText("Usuario o contraseña incorrectos.");
                txtPassword.clear();
                return;
            }
            primeraCuenta = false;
            panelConfirmacion.setVisible(false);
            panelConfirmacion.setManaged(false);
            btnAcceder.setText("Iniciar sesión");
            Sesion.usuarioActual = usuario;
            cargarDashboard();
        } catch (IllegalArgumentException | IllegalStateException e) {
            lblMensaje.setText(e.getMessage());
        } catch (SQLException e) {
            lblMensaje.setText("No se pudo acceder a los datos. Intenta de nuevo.");
            System.getLogger(getClass().getName()).log(System.Logger.Level.ERROR, "Error de acceso a datos", e);
        } catch (IOException e) {
            Sesion.usuarioActual = null;
            lblMensaje.setText("No se pudo abrir el panel. Intenta iniciar sesión de nuevo.");
            System.getLogger(getClass().getName()).log(System.Logger.Level.ERROR, "Error al abrir el panel", e);
        } finally {
            btnAcceder.setDisable(false);
        }
    }

    private void cargarDashboard() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/Dashboard.fxml"));
        Scene scene = new Scene(loader.load(), 1180, 760);
        Stage stage = (Stage) txtUsuario.getScene().getWindow();
        stage.setResizable(true);
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(680);
        stage.setTitle("Inventario · Panel de control");
        stage.centerOnScreen();
    }
}
