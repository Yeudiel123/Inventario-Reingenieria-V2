package mx.unison.controladores;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import mx.unison.datos.ConexionDB;
import mx.unison.modelos.Almacen;
import mx.unison.utileria.Sesion;

import java.sql.SQLException;

public class AlmacenesController {
    @FXML private TextField txtNombre;
    @FXML private TextField txtUbicacion;
    @FXML private TableView<Almacen> tablaAlmacenes;

    private Almacen almacenEnEdicion;

    @FXML
    public void initialize() {
        if (comprobarPermiso()) cargarDatos();
    }

    private void cargarDatos() {
        try {
            tablaAlmacenes.setItems(FXCollections.observableArrayList(ConexionDB.getAlmacenDao().queryForAll()));
        } catch (SQLException e) {
            mostrarAlerta("No se pudieron cargar los almacenes", "Comprueba el acceso a la base de datos e inténtalo de nuevo.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    protected void guardarAlmacen() {
        if (!comprobarPermiso()) return;
        try {
            Almacen original = almacenEnEdicion == null ? new Almacen() : almacenEnEdicion;
            Almacen almacen = original.conCambios(txtNombre.getText(), txtUbicacion.getText(), Sesion.usuarioActual.getNombre());
            if (almacenEnEdicion == null) {
                ConexionDB.getAlmacenDao().create(almacen);
            } else if (ConexionDB.getAlmacenDao().update(almacen) == 0) {
                mostrarAlerta("Almacén no disponible", "El almacén ya no existe. Actualiza tu selección.", Alert.AlertType.WARNING);
            }
            limpiarFormulario();
            cargarDatos();
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Revisa el formulario", e.getMessage(), Alert.AlertType.WARNING);
        } catch (SQLException e) {
            mostrarAlerta("No se pudo guardar", "Los cambios no se guardaron. Comprueba el acceso a la base de datos e inténtalo de nuevo.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    protected void cargarParaEditar() {
        if (!comprobarPermiso()) return;
        Almacen seleccionado = tablaAlmacenes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un almacén", "Selecciona una fila de la tabla para editarla.", Alert.AlertType.INFORMATION);
            return;
        }
        txtNombre.setText(seleccionado.getNombre());
        txtUbicacion.setText(seleccionado.getUbicacion());
        almacenEnEdicion = seleccionado;
    }

    @FXML
    protected void eliminarAlmacen() {
        if (!comprobarPermiso()) return;
        Almacen seleccionado = tablaAlmacenes.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un almacén", "Selecciona una fila de la tabla para eliminarla.", Alert.AlertType.INFORMATION);
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "Se eliminará el almacén «" + seleccionado.getNombre() + "». Esta acción no se puede deshacer.",
                ButtonType.CANCEL, ButtonType.OK);
        confirmacion.setTitle("Eliminar almacén");
        confirmacion.setHeaderText("Confirma la eliminación");
        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try {
            Sesion.requerirRol("ADMIN", "ALMACEN");
            if (ConexionDB.getProductoDao().queryBuilder().where().eq("almacen_id", seleccionado.getId()).countOf() > 0) {
                mostrarAlerta("Almacén con productos", "Reasigna o elimina sus productos antes de eliminar este almacén.", Alert.AlertType.WARNING);
                return;
            }
            ConexionDB.getAlmacenDao().delete(seleccionado);
            limpiarFormulario();
            cargarDatos();
        } catch (SecurityException e) {
            mostrarAlerta("Acceso restringido", e.getMessage(), Alert.AlertType.WARNING);
        } catch (SQLException e) {
            mostrarAlerta("No se pudo eliminar", "El almacén no se eliminó. Comprueba que no tenga productos y que la base de datos esté disponible.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    protected void limpiarFormulario() {
        txtNombre.clear();
        txtUbicacion.clear();
        tablaAlmacenes.getSelectionModel().clearSelection();
        almacenEnEdicion = null;
    }

    private boolean comprobarPermiso() {
        try {
            Sesion.requerirRol("ADMIN", "ALMACEN");
            return true;
        } catch (SecurityException e) {
            mostrarAlerta("Acceso restringido", e.getMessage(), Alert.AlertType.WARNING);
            return false;
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
