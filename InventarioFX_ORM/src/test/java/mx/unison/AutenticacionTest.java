package mx.unison;

import mx.unison.datos.ConexionDB;
import mx.unison.modelos.Usuario;
import mx.unison.utileria.Autenticacion;
import mx.unison.utileria.CryptoUtils;
import mx.unison.utileria.Sesion;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.*;

class AutenticacionTest {
    @TempDir Path carpeta;

    @BeforeEach
    void preparar() throws Exception {
        ConexionDB.inicializar("jdbc:sqlite:" + carpeta.resolve("cuentas.db"), false);
    }

    @AfterEach
    void cerrar() throws Exception {
        Sesion.usuarioActual = null;
        ConexionDB.cerrar();
    }

    @Test
    void creaSoloLaPrimeraCuentaYSinPasswordsPorDefecto() throws Exception {
        assertTrue(Autenticacion.requiereConfiguracion());
        assertEquals(0, ConexionDB.getUsuarioDao().countOf());
        assertThrows(IllegalArgumentException.class, () -> Autenticacion.crearAdministrador("ab", "una contraseña larga"));
        assertThrows(IllegalArgumentException.class, () -> Autenticacion.crearAdministrador("ADMIN", "corta"));
        Usuario admin = Autenticacion.crearAdministrador("Mi_Admin", "una contraseña larga");
        assertEquals("ADMIN", admin.getRol());
        assertFalse(Autenticacion.requiereConfiguracion());
        assertNotNull(Autenticacion.autenticar("Mi_Admin", "una contraseña larga"));
        assertNull(Autenticacion.autenticar("Mi_Admin", "incorrecta"));
        assertThrows(IllegalStateException.class, () -> Autenticacion.crearAdministrador("otro", "otra contraseña larga"));
    }

    @Test
    void migraLegacySoloTrasLoginCorrecto() throws Exception {
        String legacy = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256")
                .digest("admin123".getBytes(StandardCharsets.UTF_8)));
        ConexionDB.getUsuarioDao().create(new Usuario("ADMIN", legacy, "ADMIN"));
        assertNull(Autenticacion.autenticar("ADMIN", "incorrecta"));
        assertEquals(legacy, ConexionDB.getUsuarioDao().queryForId("ADMIN").getPasswordHash());
        assertNotNull(Autenticacion.autenticar("ADMIN", "admin123"));
        String nuevo = ConexionDB.getUsuarioDao().queryForId("ADMIN").getPasswordHash();
        assertNotEquals(legacy, nuevo);
        assertFalse(CryptoUtils.needsRehash(nuevo));
        assertTrue(CryptoUtils.verifyPassword("admin123", nuevo));
    }

    @Test
    void cambiaSoloLaPasswordDeLaCuentaConectada() throws Exception {
        Usuario admin = Autenticacion.crearAdministrador("ADMIN", "password admin larga");
        String hashAdmin = admin.getPasswordHash();
        ConexionDB.getUsuarioDao().create(new Usuario("PRODUCTOS", CryptoUtils.hashPassword("password anterior"), "PRODUCTO"));
        Sesion.usuarioActual = Autenticacion.autenticar("PRODUCTOS", "password anterior");
        assertThrows(IllegalArgumentException.class, () -> Autenticacion.cambiarPassword("incorrecta", "password nueva larga"));
        assertNotNull(Autenticacion.autenticar("PRODUCTOS", "password anterior"));
        Autenticacion.cambiarPassword("password anterior", "password nueva larga");
        assertNull(Autenticacion.autenticar("PRODUCTOS", "password anterior"));
        assertNotNull(Autenticacion.autenticar("PRODUCTOS", "password nueva larga"));
        assertEquals(hashAdmin, ConexionDB.getUsuarioDao().queryForId("ADMIN").getPasswordHash());
        Sesion.usuarioActual = null;
        assertThrows(SecurityException.class, () -> Autenticacion.cambiarPassword("password nueva larga", "otra contraseña larga"));
    }
}
