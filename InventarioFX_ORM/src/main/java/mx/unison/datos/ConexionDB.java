package mx.unison.datos;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;
import mx.unison.modelos.Almacen;
import mx.unison.modelos.Producto;
import mx.unison.modelos.Usuario;
import mx.unison.utileria.CryptoUtils;

import java.sql.SQLException;
import java.time.LocalDateTime;

/** Una conexión SQLite local; la demostración se habilita de forma explícita. */
public final class ConexionDB {
    private static JdbcConnectionSource connectionSource;
    private static Dao<Usuario, String> usuarioDao;
    private static Dao<Almacen, Integer> almacenDao;
    private static Dao<Producto, Integer> productoDao;

    private ConexionDB() {}

    public static void inicializar() throws SQLException {
        boolean demo = Boolean.getBoolean("inventario.demo");
        String archivo = System.getProperty("inventario.db", "").trim();
        if (archivo.isEmpty()) archivo = demo ? "inventario_demo.db" : "inventario_v2.db";
        inicializar("jdbc:sqlite:" + archivo, demo);
    }

    public static synchronized void inicializar(String url, boolean demo) throws SQLException {
        if (connectionSource != null) return;
        com.j256.ormlite.logger.Logger.setGlobalLogLevel(com.j256.ormlite.logger.Level.WARNING);
        JdbcConnectionSource source = new JdbcConnectionSource(url);
        try {
            DatabaseConnection connection = source.getReadWriteConnection(null);
            try {
                connection.executeStatement("PRAGMA foreign_keys = ON", DatabaseConnection.DEFAULT_RESULT_FLAGS);
            } finally {
                source.releaseConnection(connection);
            }
            TableUtils.createTableIfNotExists(source, Usuario.class);
            TableUtils.createTableIfNotExists(source, Almacen.class);
            TableUtils.createTableIfNotExists(source, Producto.class);
            usuarioDao = DaoManager.createDao(source, Usuario.class);
            almacenDao = DaoManager.createDao(source, Almacen.class);
            productoDao = DaoManager.createDao(source, Producto.class);
            connectionSource = source;
            if (demo && usuarioDao.countOf() == 0) crearDemostracion();
        } catch (SQLException | RuntimeException e) {
            connectionSource = null;
            usuarioDao = null;
            almacenDao = null;
            productoDao = null;
            try { source.close(); } catch (Exception closeError) { e.addSuppressed(closeError); }
            throw e;
        }
    }

    private static void crearDemostracion() throws SQLException {
        com.j256.ormlite.misc.TransactionManager.callInTransaction(connectionSource, () -> {
            usuarioDao.create(new Usuario("ADMIN", CryptoUtils.hashPassword("admin123"), "ADMIN"));
            usuarioDao.create(new Usuario("USER_PROD", CryptoUtils.hashPassword("prod123"), "PRODUCTO"));
            usuarioDao.create(new Usuario("USER_ALM", CryptoUtils.hashPassword("alm123"), "ALMACEN"));
            Almacen almacen = new Almacen();
            almacen.setNombre("Almacén central");
            almacen.setUbicacion("Edificio principal");
            almacen.setCreadoPor("ADMIN");
            almacen.setFechaHoraCreacion(LocalDateTime.now().toString());
            almacenDao.create(almacen);
            crearProductoDemo("Teclado mecánico", 850.0, 12, almacen);
            crearProductoDemo("Mouse inalámbrico", 320.0, 24, almacen);
            return null;
        });
    }

    private static void crearProductoDemo(String nombre, double precio, int cantidad, Almacen almacen)
            throws SQLException {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setCantidad(cantidad);
        producto.setAlmacen(almacen);
        producto.setUltimoUsuario("ADMIN");
        productoDao.create(producto);
    }

    public static Dao<Usuario, String> getUsuarioDao() { return usuarioDao; }
    public static Dao<Almacen, Integer> getAlmacenDao() { return almacenDao; }
    public static Dao<Producto, Integer> getProductoDao() { return productoDao; }

    public static synchronized void cerrar() throws Exception {
        try {
            if (connectionSource != null) connectionSource.close();
        } finally {
            connectionSource = null;
            usuarioDao = null;
            almacenDao = null;
            productoDao = null;
        }
    }
}
