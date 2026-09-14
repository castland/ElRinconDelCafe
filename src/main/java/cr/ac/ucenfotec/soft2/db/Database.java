package cr.ac.ucenfotec.soft2.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Punto central de acceso a la base de datos SQLite.
 *
 * La base de datos es un único archivo local (`data/elrincondelcafe.db`) dentro
 * del proyecto. Se crea automáticamente en el primer arranque junto con las
 * tablas y los datos iniciales (semilla). El archivo está excluido de git.
 *
 * SQLite es una base de datos embebida: no requiere servidor ni instalación.
 */
public final class Database {

    /** Carpeta y archivo de la base de datos (relativos al directorio de trabajo). */
    private static final String DB_DIR = "data";
    private static final String DB_FILE = DB_DIR + File.separator + "elrincondelcafe.db";
    private static final String URL = "jdbc:sqlite:" + DB_FILE;

    private static boolean inicializada = false;

    private Database() {
    }

    /** Abre una nueva conexión a la base de datos. */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * Inicializa la base de datos: crea la carpeta, las tablas y los datos
     * de semilla si aún no existen. Es seguro llamarla varias veces.
     */
    public static synchronized void init() {
        if (inicializada) {
            return;
        }
        File dir = new File(DB_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try (Connection con = getConnection(); Statement st = con.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
            crearTablas(st);
            sembrarSiVacio(con);
            inicializada = true;
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo inicializar la base de datos: " + e.getMessage(), e);
        }
    }

    private static void crearTablas(Statement st) throws SQLException {
        st.execute("""
            CREATE TABLE IF NOT EXISTS usuarios (
                cedula   TEXT PRIMARY KEY,
                nombre   TEXT NOT NULL,
                apellido TEXT NOT NULL,
                rol      TEXT NOT NULL
            )""");

        st.execute("""
            CREATE TABLE IF NOT EXISTS clientes (
                cedula   TEXT PRIMARY KEY,
                nombre   TEXT NOT NULL,
                apellido TEXT NOT NULL,
                correo   TEXT NOT NULL
            )""");

        st.execute("""
            CREATE TABLE IF NOT EXISTS productos (
                codigo    TEXT PRIMARY KEY,
                nombre    TEXT NOT NULL,
                categoria TEXT NOT NULL,
                precio    REAL NOT NULL,
                stock     INTEGER NOT NULL
            )""");

        // Cabecera de factura + líneas de detalle (para persistir las ventas)
        st.execute("""
            CREATE TABLE IF NOT EXISTS facturas (
                numero_factura TEXT PRIMARY KEY,
                numero_venta   TEXT,
                fecha_emision  TEXT,
                cliente_cedula TEXT,
                cliente_nombre TEXT,
                vendedor       TEXT,
                subtotal       REAL,
                iva            REAL,
                total          REAL,
                estado         TEXT
            )""");

        st.execute("""
            CREATE TABLE IF NOT EXISTS factura_detalle (
                id              INTEGER PRIMARY KEY AUTOINCREMENT,
                numero_factura  TEXT NOT NULL,
                codigo_producto TEXT,
                nombre_producto TEXT,
                cantidad        INTEGER,
                precio_unitario REAL,
                subtotal        REAL,
                FOREIGN KEY (numero_factura) REFERENCES facturas(numero_factura)
            )""");
    }

    /** Inserta los datos iniciales sólo si las tablas están vacías. */
    private static void sembrarSiVacio(Connection con) throws SQLException {
        if (contar(con, "usuarios") == 0) {
            sembrarUsuarios(con);
        }
        if (contar(con, "clientes") == 0) {
            sembrarClientes(con);
        }
        if (contar(con, "productos") == 0) {
            sembrarProductos(con);
        }
    }

    private static int contar(Connection con, String tabla) throws SQLException {
        try (Statement st = con.createStatement();
             var rs = st.executeQuery("SELECT COUNT(*) FROM " + tabla)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private static void sembrarUsuarios(Connection con) throws SQLException {
        String sql = "INSERT INTO usuarios (cedula, nombre, apellido, rol) VALUES (?,?,?,?)";
        try (var ps = con.prepareStatement(sql)) {
            insertar(ps, "111111111", "Carlos", "Villalobos", "Gerente");
            insertar(ps, "222222222", "Mauricio", "Zamora", "Gerente");
            insertar(ps, "333333333", "Kenner", "Gamboa", "Barista");
            insertar(ps, "444444444", "John", "Doe", "Barista");
        }
    }

    private static void sembrarClientes(Connection con) throws SQLException {
        String sql = "INSERT INTO clientes (cedula, nombre, apellido, correo) VALUES (?,?,?,?)";
        try (var ps = con.prepareStatement(sql)) {
            insertar(ps, "301450789", "Ana", "García", "ana.garcia@email.com");
            insertar(ps, "205670123", "Luis", "Rodríguez", "luis.rodriguez@email.com");
            insertar(ps, "108920456", "María", "Fernández", "maria.fernandez@email.com");
        }
    }

    private static void insertar(java.sql.PreparedStatement ps, String a, String b, String c, String d)
            throws SQLException {
        ps.setString(1, a);
        ps.setString(2, b);
        ps.setString(3, c);
        ps.setString(4, d);
        ps.executeUpdate();
    }

    private static void sembrarProductos(Connection con) throws SQLException {
        String sql = "INSERT INTO productos (codigo, nombre, categoria, precio, stock) VALUES (?,?,?,?,?)";
        try (var ps = con.prepareStatement(sql)) {
            producto(ps, "CAF-001", "Café Negro", "Café", 1200, 50);
            producto(ps, "CAF-002", "Capuchino", "Café", 1500, 45);
            producto(ps, "CAF-003", "Latte", "Café", 1600, 40);
            producto(ps, "CAF-004", "Espresso", "Café", 1000, 60);
            producto(ps, "CAF-005", "Americano", "Café", 1300, 55);
            producto(ps, "BEB-001", "Té Frío", "Bebida Fría", 1000, 30);
            producto(ps, "BEB-002", "Frappé de Chocolate", "Bebida Fría", 1800, 25);
            producto(ps, "BEB-003", "Smoothie de Fresa", "Bebida Fría", 2000, 20);
            producto(ps, "BEB-004", "Limonada Natural", "Bebida Fría", 1200, 35);
            producto(ps, "BEB-005", "Café Frío", "Bebida Fría", 1500, 28);
            producto(ps, "PAS-001", "Croissant", "Pastelería", 1300, 40);
            producto(ps, "PAS-002", "Brownie", "Pastelería", 1500, 35);
            producto(ps, "PAS-003", "Cheesecake", "Pastelería", 2200, 20);
            producto(ps, "PAS-004", "Muffin de Arándanos", "Pastelería", 1400, 30);
            producto(ps, "PAS-005", "Galleta de Chocolate", "Pastelería", 800, 50);
        }
    }

    private static void producto(java.sql.PreparedStatement ps, String codigo, String nombre,
                                 String categoria, double precio, int stock) throws SQLException {
        ps.setString(1, codigo);
        ps.setString(2, nombre);
        ps.setString(3, categoria);
        ps.setDouble(4, precio);
        ps.setInt(5, stock);
        ps.executeUpdate();
    }
}
