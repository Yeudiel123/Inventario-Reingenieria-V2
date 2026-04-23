package mx.unison.modelos;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "usuarios")
public class Usuario {

    @DatabaseField(id = true, columnName = "nombre")
    private String nombre;

    @DatabaseField(canBeNull = false, columnName = "password_hash")
    private String passwordHash;

    @DatabaseField(canBeNull = false)
    private String rol;

    public Usuario() {}

    public Usuario(String nombre, String passwordHash, String rol) {
        this.nombre = nombre;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}