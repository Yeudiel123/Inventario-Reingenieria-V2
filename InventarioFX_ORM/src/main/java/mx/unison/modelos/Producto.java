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

    // ¡MAGIA ORM! En lugar de guardar un 'int almacenId', guardamos el objeto completo.
    // foreign = true hace la relación de llaves foráneas automáticamente en SQL.
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "almacen_id")
    private Almacen almacen;

    @DatabaseField(columnName = "ultimo_usuario")
    private String ultimoUsuario;

    public Producto() {}

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public Almacen getAlmacen() { return almacen; }
    public void setAlmacen(Almacen almacen) { this.almacen = almacen; }
    public String getUltimoUsuario() { return ultimoUsuario; }
    public void setUltimoUsuario(String ultimoUsuario) { this.ultimoUsuario = ultimoUsuario; }
}