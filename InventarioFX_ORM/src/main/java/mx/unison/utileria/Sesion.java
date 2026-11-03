package mx.unison.utileria;

import mx.unison.modelos.Usuario;

public final class Sesion {
    public static Usuario usuarioActual;

    private Sesion() {}

    public static boolean tieneRol(String... roles) {
        if (usuarioActual == null || usuarioActual.getRol() == null || roles == null) {
            return false;
        }
        for (String rol : roles) {
            if (usuarioActual.getRol().equals(rol)) {
                return true;
            }
        }
        return false;
    }

    public static Usuario requerirRol(String... roles) {
        if (!tieneRol(roles)) {
            throw new SecurityException("Tu sesión no tiene permiso para realizar esta acción. Inicia sesión con una cuenta autorizada.");
        }
        return usuarioActual;
    }
}
