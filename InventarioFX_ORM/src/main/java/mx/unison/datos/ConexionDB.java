package mx.unison.datos;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import mx.unison.modelos.Almacen;
import mx.unison.modelos.Producto;
import mx.unison.modelos.Usuario;
import mx.unison.utileria.CryptoUtils;

import java.sql.SQLException;

/**
 * Clase principal para la gestión de la base de datos utilizando el framework ORMLite.
 * Implementa el patrón DAO (Data Access Object) para abstraer las consultas SQL.
 * @author Yeudiel
 * @version 2.0
 */
public class ConexionDB {
    // Archivo de base de datos nuevo para la versión 2.0
    private static final String URL = "jdbc:sqlite:inventario_v2.db";
    private static ConnectionSource connectionSource;

    // Implementación del Patrón DAO solicitado por el profesor
    private static Dao<Usuario, String> usuarioDao;
    private static Dao<Almacen, Integer> almacenDao;
    private static Dao<Producto, Integer> productoDao;

    public static void inicializar() {
        try {
            connectionSource = new JdbcConnectionSource(URL);

            // 1. Magia ORM: Crea las tablas automáticamente leyendo las clases
            TableUtils.createTableIfNotExists(connectionSource, Usuario.class);
            TableUtils.createTableIfNotExists(connectionSource, Almacen.class);
            TableUtils.createTableIfNotExists(connectionSource, Producto.class);

            // 2. Instanciar los DAOs
            usuarioDao = DaoManager.createDao(connectionSource, Usuario.class);
            almacenDao = DaoManager.createDao(connectionSource, Almacen.class);
            productoDao = DaoManager.createDao(connectionSource, Producto.class);

            // 3. Crear los 3 usuarios por defecto si no existen (¡Con Roles!)
            if (!usuarioDao.idExists("ADMIN")) {
                usuarioDao.create(new Usuario("ADMIN", CryptoUtils.hashPassword("admin123"), "ADMIN"));
                usuarioDao.create(new Usuario("USER_PROD", CryptoUtils.hashPassword("prod123"), "PRODUCTO"));
                usuarioDao.create(new Usuario("USER_ALM", CryptoUtils.hashPassword("alm123"), "ALMACEN"));
            }

        } catch (SQLException e) {
            System.err.println("Error crítico al conectar con SQLite: " + e.getMessage());
        }
    }

    // Getters públicos para usar los DAOs desde cualquier controlador
    public static Dao<Usuario, String> getUsuarioDao() { return usuarioDao; }
    public static Dao<Almacen, Integer> getAlmacenDao() { return almacenDao; }
    public static Dao<Producto, Integer> getProductoDao() { return productoDao; }
}