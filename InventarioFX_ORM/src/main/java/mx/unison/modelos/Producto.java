package mx.unison.modelos;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "productos")
public class Producto {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String nombre;

    @DatabaseField
    private double precio;

    @DatabaseField
    private int cantidad;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "almacen_id",
            columnDefinition = "INTEGER NOT NULL REFERENCES almacenes(id) ON DELETE RESTRICT")
    private Almacen almacen;

    @DatabaseField(columnName = "ultimo_usuario")
    private String ultimoUsuario;

    public Producto() {}

    /** Devuelve una edición validada sin modificar la fila que muestra la tabla. */
    public Producto conCambios(String nombre, double precio, int cantidad, Almacen almacen, String usuario) {
        if (usuario == null || usuario.isBlank()) {
            throw new IllegalArgumentException("Se necesita un usuario para registrar el cambio.");
        }
        Producto copia = new Producto();
        copia.id = id;
        copia.setNombre(nombre);
        copia.setPrecio(precio);
        copia.setCantidad(cantidad);
        copia.setAlmacen(almacen);
        copia.ultimoUsuario = usuario;
        return copia;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        this.nombre = nombre.trim();
    }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) {
        if (!Double.isFinite(precio) || precio < 0) {
            throw new IllegalArgumentException("El precio debe ser un número finito mayor o igual a cero.");
        }
        this.precio = precio;
    }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad debe ser un entero mayor o igual a cero.");
        }
        this.cantidad = cantidad;
    }
    public Almacen getAlmacen() { return almacen; }
    public void setAlmacen(Almacen almacen) {
        if (almacen == null) {
            throw new IllegalArgumentException("Selecciona un almacén para el producto.");
        }
        this.almacen = almacen;
    }
    public String getUltimoUsuario() { return ultimoUsuario; }
    public void setUltimoUsuario(String ultimoUsuario) { this.ultimoUsuario = ultimoUsuario; }
}
