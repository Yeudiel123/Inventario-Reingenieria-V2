package mx.unison.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import mx.unison.utileria.Autenticacion;
import mx.unison.utileria.Sesion;
import java.sql.SQLException;

public class ConfiguracionController {
    @FXML private PasswordField txtPasswordActual;
    @FXML private PasswordField txtNuevaPass;
    @FXML private PasswordField txtConfirmarPass;

    @FXML
    public void initialize() {
        Sesion.requerirRol("ADMIN", "PRODUCTO", "ALMACEN");
    }

    @FXML
    protected void cambiarPassword() {
        if (!txtNuevaPass.getText().equals(txtConfirmarPass.getText())) {
            mostrarAlerta("Revisa tu contraseña", "Las contraseñas nuevas no coinciden.", Alert.AlertType.WARNING);
            return;
        }
        try {
            Autenticacion.cambiarPassword(txtPasswordActual.getText(), txtNuevaPass.getText());
            txtPasswordActual.clear();
            txtNuevaPass.clear();
            txtConfirmarPass.clear();
            mostrarAlerta("Contraseña actualizada", "Usa tu nueva contraseña la próxima vez que inicies sesión.", Alert.AlertType.INFORMATION);
        } catch (IllegalArgumentException | SecurityException e) {
            mostrarAlerta("Revisa tus datos", e.getMessage(), Alert.AlertType.WARNING);
        } catch (SQLException e) {
            System.getLogger(getClass().getName()).log(System.Logger.Level.ERROR, "Error al actualizar contraseña", e);
            mostrarAlerta("No se guardó el cambio", "No se pudo actualizar la contraseña. Intenta de nuevo.", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
