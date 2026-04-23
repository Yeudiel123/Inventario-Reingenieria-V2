package mx.unison.controladores;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import mx.unison.datos.ConexionDB;
import mx.unison.modelos.Almacen;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class AlmacenesController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtUbicacion;
    @FXML private TableView<Almacen> tablaAlmacenes;

    private Almacen almacenEnEdicion = null;

    @FXML
    public void initialize() {
        cargarDatos();
    }

    private void cargarDatos() {
        try {
            var lista = ConexionDB.getAlmacenDao().queryForAll();
            tablaAlmacenes.setItems(FXCollections.observableArrayList(lista));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void guardarAlmacen() {
        try {
            if (txtNombre.getText().trim().isEmpty() || txtUbicacion.getText().trim().isEmpty()) {
                mostrarAlerta("Error", "Todos los campos son obligatorios.", Alert.AlertType.WARNING);
                return;
            }

            Almacen a = (almacenEnEdicion == null) ? new Almacen() : almacenEnEdicion;

            a.setNombre(txtNombre.getText().trim());
            a.setUbicacion(txtUbicacion.getText().trim());
            a.setCreadoPor("ADMIN");

            if (almacenEnEdicion == null) {
                a.setFechaHoraCreacion(LocalDateTime.now().toString()); // Simulamos fecha automática
                ConexionDB.getAlmacenDao().create(a);
            } else {
                ConexionDB.getAlmacenDao().update(a);
            }

            limpiarFormulario();
            cargarDatos();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void cargarParaEditar() {
        Almacen seleccionado = tablaAlmacenes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            txtNombre.setText(seleccionado.getNombre());
            txtUbicacion.setText(seleccionado.getUbicacion());
            almacenEnEdicion = seleccionado;
        } else {
            mostrarAlerta("Aviso", "Seleccione un almacén de la tabla para modificar.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    protected void eliminarAlmacen() {
        Almacen seleccionado = tablaAlmacenes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            try {
                ConexionDB.getAlmacenDao().delete(seleccionado);
                cargarDatos();
                limpiarFormulario();
            } catch (SQLException e) {
                mostrarAlerta("Error", "No se puede eliminar. Verifique que no haya productos asociados.", Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Aviso", "Seleccione un almacén de la tabla para eliminar.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    protected void limpiarFormulario() {
        txtNombre.clear();
        txtUbicacion.clear();
        almacenEnEdicion = null;
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}