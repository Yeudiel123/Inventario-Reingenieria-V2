package mx.unison.utileria;

import mx.unison.datos.ConexionDB;
import mx.unison.modelos.Usuario;
import java.sql.SQLException;
import java.util.Set;

/** Operaciones de cuenta compartidas por las pantallas y las pruebas. */
public final class Autenticacion {
    private Autenticacion() {}

    public static boolean requiereConfiguracion() throws SQLException {
        return ConexionDB.getUsuarioDao().countOf() == 0;
    }

    public static synchronized Usuario crearAdministrador(String nombre, String password) throws SQLException {
        if (!requiereConfiguracion()) {
            throw new IllegalStateException("La cuenta inicial ya fue creada. Inicia sesión para continuar.");
        }
        nombre = nombre == null ? "" : nombre.trim();
        if (!nombre.matches("[A-Za-z0-9._-]{3,40}")) {
            throw new IllegalArgumentException("El usuario debe tener de 3 a 40 letras, números, puntos, guiones o guiones bajos.");
        }
        validarNuevaPassword(password);
        Usuario usuario = new Usuario(nombre, CryptoUtils.hashPassword(password), "ADMIN");
        ConexionDB.getUsuarioDao().create(usuario);
        return usuario;
    }

    public static Usuario autenticar(String nombre, String password) throws SQLException {
        if (nombre == null || nombre.isBlank()) return null;
        Usuario usuario = ConexionDB.getUsuarioDao().queryForId(nombre.trim());
        if (usuario == null || !Set.of("ADMIN", "PRODUCTO", "ALMACEN").contains(usuario.getRol())
                || !CryptoUtils.verifyPassword(password, usuario.getPasswordHash())) return null;
        if (CryptoUtils.needsRehash(usuario.getPasswordHash())) {
            usuario.setPasswordHash(CryptoUtils.hashPassword(password));
            ConexionDB.getUsuarioDao().update(usuario);
        }
        return usuario;
    }

    public static void cambiarPassword(String actual, String nueva) throws SQLException {
        Usuario sesion = Sesion.requerirRol("ADMIN", "PRODUCTO", "ALMACEN");
        Usuario usuario = ConexionDB.getUsuarioDao().queryForId(sesion.getNombre());
        if (usuario == null || !CryptoUtils.verifyPassword(actual, usuario.getPasswordHash())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta.");
        }
        validarNuevaPassword(nueva);
        String hash = CryptoUtils.hashPassword(nueva);
        usuario.setPasswordHash(hash);
        ConexionDB.getUsuarioDao().update(usuario);
        sesion.setPasswordHash(hash);
    }

    private static void validarNuevaPassword(String password) {
        if (password == null || password.isBlank() || password.length() < 12 || password.length() > 128) {
            throw new IllegalArgumentException("La nueva contraseña debe tener entre 12 y 128 caracteres.");
        }
    }
}
