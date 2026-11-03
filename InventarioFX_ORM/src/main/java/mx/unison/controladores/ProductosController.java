package mx.unison.controladores;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import mx.unison.datos.ConexionDB;
import mx.unison.modelos.Almacen;
import mx.unison.modelos.Producto;
import mx.unison.utileria.Sesion;

import java.sql.SQLException;
import java.util.Locale;

public class ProductosController {
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtCantidad;
    @FXML private ComboBox<Almacen> cmbAlmacen;
    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, Double> colPrecio;

    private Producto productoEnEdicion;

    @FXML
    public void initialize() {
        if (comprobarPermiso()) {
            colPrecio.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(Double precio, boolean vacia) {
                    super.updateItem(precio, vacia);
                    setText(vacia || precio == null ? null : String.format(Locale.ROOT, "%.2f", precio));
                }
            });
            cargarDatos();
            cargarAlmacenes();
        }
    }

    private void cargarDatos() {
        try {
            tablaProductos.setItems(FXCollections.observableArrayList(ConexionDB.getProductoDao().queryForAll()));
        } catch (SQLException e) {
            mostrarAlerta("No se pudieron cargar los productos", "Comprueba el acceso a la base de datos e inténtalo de nuevo.", Alert.AlertType.ERROR);
        }
    }

    private void cargarAlmacenes() {
        try {
            cmbAlmacen.setItems(FXCollections.observableArrayList(ConexionDB.getAlmacenDao().queryForAll()));
        } catch (SQLException e) {
            mostrarAlerta("No se pudieron cargar los almacenes", "Comprueba el acceso a la base de datos e inténtalo de nuevo.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    protected void guardarProducto() {
        if (!comprobarPermiso()) return;
        try {
            Producto original = productoEnEdicion == null ? new Producto() : productoEnEdicion;
            Producto producto = original.conCambios(
                    txtNombre.getText(), Double.parseDouble(txtPrecio.getText().trim()),
                    Integer.parseInt(txtCantidad.getText().trim()), cmbAlmacen.getValue(),
                    Sesion.usuarioActual.getNombre());

            if (!ConexionDB.getAlmacenDao().idExists(producto.getAlmacen().getId())) {
                mostrarAlerta("Almacén no disponible", "El almacén seleccionado ya no existe. Selecciona otro.", Alert.AlertType.WARNING);
                cargarAlmacenes();
                return;
            }
            if (productoEnEdicion == null) {
                ConexionDB.getProductoDao().create(producto);
            } else if (ConexionDB.getProductoDao().update(producto) == 0) {
                mostrarAlerta("Producto no disponible", "El producto ya no existe. Actualiza tu selección.", Alert.AlertType.WARNING);
            }
            limpiarFormulario();
            cargarDatos();
        } catch (NumberFormatException e) {
            mostrarAlerta("Revisa los números", "Escribe un precio válido con punto decimal y una cantidad entera dentro del rango permitido.", Alert.AlertType.WARNING);
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Revisa el formulario", e.getMessage(), Alert.AlertType.WARNING);
        } catch (SQLException e) {
            mostrarAlerta("No se pudo guardar", "Los cambios no se guardaron. Comprueba el acceso a la base de datos e inténtalo de nuevo.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    protected void cargarParaEditar() {
        if (!comprobarPermiso()) return;
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un producto", "Selecciona una fila de la tabla para editarla.", Alert.AlertType.INFORMATION);
            return;
        }
        txtNombre.setText(seleccionado.getNombre());
        txtPrecio.setText(String.valueOf(seleccionado.getPrecio()));
        txtCantidad.setText(String.valueOf(seleccionado.getCantidad()));
        cmbAlmacen.setValue(cmbAlmacen.getItems().stream()
                .filter(a -> seleccionado.getAlmacen() != null && a.getId() == seleccionado.getAlmacen().getId())
                .findFirst().orElse(null));
        productoEnEdicion = seleccionado;
    }

    @FXML
    protected void eliminarProducto() {
        if (!comprobarPermiso()) return;
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un producto", "Selecciona una fila de la tabla para eliminarla.", Alert.AlertType.INFORMATION);
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "Se eliminará el producto «" + seleccionado.getNombre() + "». Esta acción no se puede deshacer.",
                ButtonType.CANCEL, ButtonType.OK);
        confirmacion.setTitle("Eliminar producto");
        confirmacion.setHeaderText("Confirma la eliminación");
        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;
        try {
            Sesion.requerirRol("ADMIN", "PRODUCTO");
            ConexionDB.getProductoDao().delete(seleccionado);
            limpiarFormulario();
            cargarDatos();
        } catch (SecurityException e) {
            mostrarAlerta("Acceso restringido", e.getMessage(), Alert.AlertType.WARNING);
        } catch (SQLException e) {
            mostrarAlerta("No se pudo eliminar", "El producto no se eliminó. Comprueba el acceso a la base de datos e inténtalo de nuevo.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    protected void limpiarFormulario() {
        txtNombre.clear();
        txtPrecio.clear();
        txtCantidad.clear();
        cmbAlmacen.setValue(null);
        tablaProductos.getSelectionModel().clearSelection();
        productoEnEdicion = null;
    }

    private boolean comprobarPermiso() {
        try {
            Sesion.requerirRol("ADMIN", "PRODUCTO");
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
