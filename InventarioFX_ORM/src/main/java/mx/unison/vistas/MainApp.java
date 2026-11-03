package mx.unison.vistas;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import mx.unison.datos.ConexionDB;
import mx.unison.utileria.Sesion;

/** Punto de entrada de la aplicación de inventario de escritorio. */
public class MainApp extends Application {
    /** Constructor utilizado por el ciclo de vida de JavaFX. */
    public MainApp() {}

    @Override
    public void start(Stage stage) {
        try {
            ConexionDB.inicializar();
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/vistas/Login.fxml"));
            stage.setScene(new Scene(loader.load(), 480, 700));
            stage.setTitle("Inventario · Acceso");
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            System.getLogger(getClass().getName()).log(System.Logger.Level.ERROR, "No se pudo iniciar Inventario", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No se pudo iniciar Inventario");
            alert.setHeaderText("Revisa el acceso al archivo de datos");
            alert.setContentText("No fue posible preparar el sistema. Comprueba que la carpeta tenga permisos de escritura y vuelve a intentarlo.");
            alert.showAndWait();
            Platform.exit();
        }
    }

    @Override
    public void stop() throws Exception {
        Sesion.usuarioActual = null;
        ConexionDB.cerrar();
    }

    /**
     * Inicia JavaFX.
     * @param args argumentos de la aplicación
     */
    public static void main(String[] args) {
        launch(args);
    }
}
