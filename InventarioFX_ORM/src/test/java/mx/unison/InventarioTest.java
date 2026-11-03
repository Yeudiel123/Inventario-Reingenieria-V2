package mx.unison;

import mx.unison.modelos.Producto;
import mx.unison.modelos.Almacen;
import mx.unison.utileria.CryptoUtils;
import org.junit.jupiter.api.*;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InventarioTest {

    private JdbcConnectionSource conexionPrueba;
    private Dao<Producto, Integer> productoDao;
    private Almacen almacen;

    @BeforeAll
    void setup() throws Exception {
        // Usamos una base de datos EN MEMORIA solo para pruebas (no afecta tus datos reales)
        conexionPrueba = new JdbcConnectionSource("jdbc:sqlite::memory:");
        TableUtils.createTable(conexionPrueba, Almacen.class);
        TableUtils.createTable(conexionPrueba, Producto.class);
        almacen = new Almacen();
        almacen.setNombre("Almacén de prueba");
        almacen.setUbicacion("Campus");
        DaoManager.createDao(conexionPrueba, Almacen.class).create(almacen);
        productoDao = DaoManager.createDao(conexionPrueba, Producto.class);
    }

    @Test
    @DisplayName("Una contraseña se verifica contra su hash")
    void testSeguridadMejorada() {
        String hash = CryptoUtils.hashPassword("admin123");
        assertNotNull(hash);
        assertNotEquals("admin123", hash, "La contraseña no debe estar en texto plano");
        assertTrue(hash.length() > 20, "El hash en Base64 debe ser largo");
        assertTrue(CryptoUtils.verifyPassword("admin123", hash));
        assertFalse(CryptoUtils.verifyPassword("incorrecta", hash));
    }

    @Test
    @DisplayName("Prueba ORM: Insertar y Buscar Producto")
    void testORMProducto() throws Exception {
        // 1. Crear producto
        Producto p = new Producto();
        p.setNombre("Teclado Mecánico");
        p.setPrecio(1500.0);
        p.setCantidad(5);
        p.setAlmacen(almacen);

        // 2. Insertar (Si falla, el test truena)
        productoDao.create(p);
        assertTrue(p.getId() > 0, "ORMLite debe asignar un ID autoincremental");

        // 3. Buscar
        Producto guardado = productoDao.queryForId(p.getId());
        assertEquals("Teclado Mecánico", guardado.getNombre());
        assertEquals(1500.0, guardado.getPrecio());
        assertEquals(almacen.getId(), guardado.getAlmacen().getId());
        guardado.setCantidad(8);
        productoDao.update(guardado);
        assertEquals(8, productoDao.queryForId(p.getId()).getCantidad());
        productoDao.deleteById(p.getId());
        assertNull(productoDao.queryForId(p.getId()));
    }

    @AfterAll
    void tearDown() throws Exception {
        conexionPrueba.close();
    }
}
