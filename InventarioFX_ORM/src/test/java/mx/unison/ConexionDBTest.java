package mx.unison;

import mx.unison.datos.ConexionDB;
import mx.unison.utileria.Autenticacion;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

class ConexionDBTest {
    @TempDir Path carpeta;

    @AfterEach
    void cerrar() throws Exception {
        ConexionDB.cerrar();
    }

    @Test
    void demoPersistenteSinDuplicarNiReiniciarDatos() throws Exception {
        String url = "jdbc:sqlite:" + carpeta.resolve("demo.db");
        ConexionDB.inicializar(url, true);
        assertEquals(3, ConexionDB.getUsuarioDao().countOf());
        assertEquals(2, ConexionDB.getProductoDao().countOf());
        assertEquals(1, ConexionDB.getAlmacenDao().countOf());
        assertNotNull(Autenticacion.autenticar("USER_ALM", "alm123"));
        assertThrows(SQLException.class, () -> ConexionDB.getAlmacenDao().deleteById(
                ConexionDB.getAlmacenDao().queryForAll().getFirst().getId()));
        ConexionDB.getProductoDao().delete(ConexionDB.getProductoDao().queryForAll());
        ConexionDB.cerrar();
        ConexionDB.inicializar(url, true);
        assertEquals(3, ConexionDB.getUsuarioDao().countOf());
        assertEquals(0, ConexionDB.getProductoDao().countOf());
    }

    @Test
    void usaArchivosDistintosParaDatosNormalesYDemo() throws Exception {
        String normal = "jdbc:sqlite:" + carpeta.resolve("normal.db");
        ConexionDB.inicializar(normal, false);
        Autenticacion.crearAdministrador("PRIVADO", "mi contraseña privada");
        ConexionDB.cerrar();
        ConexionDB.inicializar("jdbc:sqlite:" + carpeta.resolve("demo.db"), true);
        assertNull(ConexionDB.getUsuarioDao().queryForId("PRIVADO"));
        ConexionDB.cerrar();
        ConexionDB.inicializar(normal, false);
        assertEquals(1, ConexionDB.getUsuarioDao().countOf());
        assertNotNull(Autenticacion.autenticar("PRIVADO", "mi contraseña privada"));
    }
}
