package mx.unison.controladores;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import mx.unison.datos.ConexionDB;
import mx.unison.modelos.Almacen;
import mx.unison.modelos.Producto;

import java.sql.SQLException;

public class ProductosController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtCantidad;
    @FXML private ComboBox<Almacen> cmbAlmacen; // ¡NUEVO!
    @FXML private TableView<Producto> tablaProductos;

    private Producto productoEnEdicion = null;

    @FXML
    public void initialize() {
        cargarDatos();
        cargarAlmacenes(); // Llenamos el ComboBox al abrir la pantalla
    }

    private void cargarDatos() {
        try {
            var lista = ConexionDB.getProductoDao().queryForAll();
            tablaProductos.setItems(FXCollections.observableArrayList(lista));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void cargarAlmacenes() {
        try {
            var almacenes = ConexionDB.getAlmacenDao().queryForAll();
            cmbAlmacen.setItems(FXCollections.observableArrayList(almacenes));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    protected void guardarProducto() {
        try {
            Almacen almacenSeleccionado = cmbAlmacen.getValue();
            if (txtNombre.getText().trim().isEmpty() || almacenSeleccionado == null) {
                mostrarAlerta("Error", "El nombre y el almacén son obligatorios.", Alert.AlertType.WARNING);
                return;
            }

            Producto p = (productoEnEdicion == null) ? new Producto() : productoEnEdicion;

            p.setNombre(txtNombre.getText().trim());
            p.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
            p.setCantidad(Integer.parseInt(txtCantidad.getText().trim()));
            p.setAlmacen(almacenSeleccionado); // ¡Guardamos la relación!
            p.setUltimoUsuario("ADMIN");

            if (productoEnEdicion == null) {
                ConexionDB.getProductoDao().create(p);
            } else {
                ConexionDB.getProductoDao().update(p);
            }

            limpiarFormulario();
            cargarDatos();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Precio y Cantidad deben ser números.", Alert.AlertType.ERROR);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    protected void cargarParaEditar() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            txtNombre.setText(seleccionado.getNombre());
            txtPrecio.setText(String.valueOf(seleccionado.getPrecio()));
            txtCantidad.setText(String.valueOf(seleccionado.getCantidad()));
            cmbAlmacen.setValue(seleccionado.getAlmacen()); // Seleccionamos su almacén en el menú
            productoEnEdicion = seleccionado;
        } else {
            mostrarAlerta("Aviso", "Seleccione un producto.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    protected void eliminarProducto() {
        Producto seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            try {
                ConexionDB.getProductoDao().delete(seleccionado);
                cargarDatos();
                limpiarFormulario();
            } catch (SQLException e) { e.printStackTrace(); }
        } else {
            mostrarAlerta("Aviso", "Seleccione un producto para eliminar.", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    protected void limpiarFormulario() {
        txtNombre.clear(); txtPrecio.clear(); txtCantidad.clear();
        cmbAlmacen.setValue(null); // Limpiamos el menú
        productoEnEdicion = null;
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo); alert.setTitle(titulo); alert.setHeaderText(null);
        alert.setContentText(mensaje); alert.showAndWait();
    }
}