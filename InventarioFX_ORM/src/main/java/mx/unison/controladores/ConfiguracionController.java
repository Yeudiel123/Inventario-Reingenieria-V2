package mx.unison.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import mx.unison.datos.ConexionDB;
import mx.unison.modelos.Usuario;
import mx.unison.utileria.CryptoUtils;

import java.sql.SQLException;

public class ConfiguracionController {

    @FXML private PasswordField txtNuevaPass;
    @FXML private PasswordField txtConfirmarPass;

    @FXML
    protected void cambiarPassword() {
        String nueva = txtNuevaPass.getText();
        String confirma = txtConfirmarPass.getText();

        if (nueva.isEmpty() || confirma.isEmpty()) {
            mostrarAlerta("Error", "Los campos no pueden estar vacíos.", Alert.AlertType.WARNING);
            return;
        }

        if (!nueva.equals(confirma)) {
            mostrarAlerta("Error", "Las contraseñas no coinciden.", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Buscamos al usuario ADMIN, le actualizamos el Hash Seguro y lo guardamos
            Usuario admin = ConexionDB.getUsuarioDao().queryForId("ADMIN");
            admin.setPasswordHash(CryptoUtils.hashPassword(nueva));
            ConexionDB.getUsuarioDao().update(admin);

            txtNuevaPass.clear(); txtConfirmarPass.clear();
            mostrarAlerta("Éxito", "¡Contraseña actualizada correctamente! Usa tu nueva contraseña la próxima vez que inicies sesión.", Alert.AlertType.INFORMATION);

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Hubo un problema al conectar con la BD.", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo); alert.setTitle(titulo); alert.setHeaderText(null);
        alert.setContentText(mensaje); alert.showAndWait();
    }
}