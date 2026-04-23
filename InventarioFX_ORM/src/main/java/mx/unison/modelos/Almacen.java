package mx.unison.modelos;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "almacenes")
public class Almacen {

    // generatedId = true hace el AUTOINCREMENT automáticamente
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String nombre;

    @DatabaseField
    private String ubicacion;

    @DatabaseField(columnName = "creado_por")
    private String creadoPor;

    @DatabaseField(columnName = "fecha_hora_creacion")
    private String fechaHoraCreacion;

    public Almacen() {}

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public String getCreadoPor() { return creadoPor; }
    public void setCreadoPor(String creadoPor) { this.creadoPor = creadoPor; }
    public String getFechaHoraCreacion() { return fechaHoraCreacion; }
    public void setFechaHoraCreacion(String fechaHoraCreacion) { this.fechaHoraCreacion = fechaHoraCreacion; }

    // Este método nos servirá para que el ComboBox en JavaFX muestre el nombre bonito
    @Override
    public String toString() {
        return nombre;
    }
}