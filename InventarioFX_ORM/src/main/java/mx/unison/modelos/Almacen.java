package mx.unison.modelos;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.time.LocalDateTime;

@DatabaseTable(tableName = "almacenes")
public class Almacen {

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

    /** Conserva la autoría original al editar y evita cambios parciales en la tabla. */
    public Almacen conCambios(String nombre, String ubicacion, String usuario) {
        if (usuario == null || usuario.isBlank()) {
            throw new IllegalArgumentException("Se necesita un usuario para registrar el cambio.");
        }
        Almacen copia = new Almacen();
        copia.id = id;
        copia.setNombre(nombre);
        copia.setUbicacion(ubicacion);
        copia.creadoPor = id == 0 ? usuario : creadoPor;
        copia.fechaHoraCreacion = id == 0 ? LocalDateTime.now().toString() : fechaHoraCreacion;
        return copia;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del almacén es obligatorio.");
        }
        this.nombre = nombre.trim();
    }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) {
        if (ubicacion == null || ubicacion.isBlank()) {
            throw new IllegalArgumentException("La ubicación del almacén es obligatoria.");
        }
        this.ubicacion = ubicacion.trim();
    }
    public String getCreadoPor() { return creadoPor; }
    public void setCreadoPor(String creadoPor) { this.creadoPor = creadoPor; }
    public String getFechaHoraCreacion() { return fechaHoraCreacion; }
    public void setFechaHoraCreacion(String fechaHoraCreacion) { this.fechaHoraCreacion = fechaHoraCreacion; }

    @Override
    public String toString() {
        return nombre;
    }
}
