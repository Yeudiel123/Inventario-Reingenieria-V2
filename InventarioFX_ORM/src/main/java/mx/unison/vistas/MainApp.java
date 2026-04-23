package mx.unison.vistas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mx.unison.datos.ConexionDB;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Inicializamos la base de datos antes de mostrar la pantalla
        ConexionDB.inicializar();

        // Cargamos el diseño que hicimos en la carpeta resources
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/vistas/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 500);

        stage.setTitle("Sistema de Inventario Unison - v2.0");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}