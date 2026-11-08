package org.DAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class InicializadorBD {

    private static final String SCRIPT_CREACION = """
        CREATE TABLE IF NOT EXISTS usuarios (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nom_usu TEXT NOT NULL UNIQUE,
            pass TEXT NOT NULL,
            rol TEXT DEFAULT 'usuario',
            fecha_creacion TEXT DEFAULT CURRENT_TIMESTAMP
        );
        CREATE TABLE IF NOT EXISTS productos (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL, categoria TEXT, cantidad INTEGER DEFAULT 0,
            stock_minimo INTEGER DEFAULT 5, precio_venta REAL DEFAULT 0,
            fecha_actualizacion TEXT DEFAULT CURRENT_TIMESTAMP,
            usuario_creacion_id INTEGER,
            FOREIGN KEY (usuario_creacion_id) REFERENCES usuarios(id) ON DELETE SET NULL
        );
        CREATE TABLE IF NOT EXISTS materias_primas (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL, categoria TEXT, cantidad INTEGER DEFAULT 0,
            stock_minimo INTEGER DEFAULT 5, costo_unitario REAL DEFAULT 0,
            fecha_actualizacion TEXT DEFAULT CURRENT_TIMESTAMP,
            usuario_creacion_id INTEGER,
            FOREIGN KEY (usuario_creacion_id) REFERENCES usuarios(id) ON DELETE SET NULL
        );
        CREATE TABLE IF NOT EXISTS productos_materias (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            producto_id INTEGER NOT NULL, materia_id INTEGER NOT NULL,
            cantidad_necesaria REAL NOT NULL, unidad_medida TEXT DEFAULT 'unidades',
            FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE,
            FOREIGN KEY (materia_id) REFERENCES materias_primas(id) ON DELETE CASCADE,
            UNIQUE(producto_id, materia_id)
        );
        CREATE TABLE IF NOT EXISTS facturas (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            fecha_emision TEXT DEFAULT CURRENT_TIMESTAMP,
            nombre_cliente TEXT DEFAULT 'Cliente General',
            total REAL DEFAULT 0, usuario_id INTEGER,
            estado TEXT DEFAULT 'ACTIVA',
            FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
        );
        CREATE TABLE IF NOT EXISTS detalles_factura (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            factura_id INTEGER NOT NULL, producto_id INTEGER NOT NULL,
            cantidad INTEGER NOT NULL, precio_unitario REAL NOT NULL, subtotal REAL NOT NULL,
            FOREIGN KEY (factura_id) REFERENCES facturas(id) ON DELETE CASCADE,
            FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE
        );
        CREATE TABLE IF NOT EXISTS gastos_operativos (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            fecha TEXT DEFAULT CURRENT_TIMESTAMP, descripcion TEXT NOT NULL,
            monto REAL NOT NULL, categoria TEXT NOT NULL,
            materia_id INTEGER, cantidad_comprada INTEGER, usuario_id INTEGER,
            FOREIGN KEY (materia_id) REFERENCES materias_primas(id) ON DELETE SET NULL,
            FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
        );
        CREATE TABLE IF NOT EXISTS historial_productos (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            producto_id INTEGER NOT NULL, cantidad_anterior INTEGER NOT NULL,
            cantidad_nueva INTEGER NOT NULL,
            fecha_movimiento TEXT DEFAULT CURRENT_TIMESTAMP,
            justificacion TEXT NOT NULL, tipo_movimiento TEXT, usuario_id INTEGER,
            FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE,
            FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
        );
        CREATE TABLE IF NOT EXISTS historial_materias (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            materia_id INTEGER NOT NULL, cantidad_anterior INTEGER NOT NULL,
            cantidad_nueva INTEGER NOT NULL,
            fecha_movimiento TEXT DEFAULT CURRENT_TIMESTAMP,
            justificacion TEXT NOT NULL, tipo_movimiento TEXT, usuario_id INTEGER,
            FOREIGN KEY (materia_id) REFERENCES materias_primas(id) ON DELETE CASCADE,
            FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
        );
        CREATE TABLE IF NOT EXISTS reportes (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            categoria TEXT NOT NULL, contenido TEXT NOT NULL,
            fecha_generacion TEXT DEFAULT CURRENT_TIMESTAMP, usuario_id INTEGER,
            FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
        );
        CREATE INDEX IF NOT EXISTS idx_productos_nombre ON productos(nombre);
        CREATE INDEX IF NOT EXISTS idx_materias_nombre ON materias_primas(nombre);
        CREATE INDEX IF NOT EXISTS idx_facturas_fecha ON facturas(fecha_emision);
        CREATE INDEX IF NOT EXISTS idx_gastos_fecha ON gastos_operativos(fecha);
        CREATE INDEX IF NOT EXISTS idx_reportes_categoria ON reportes(categoria);
        INSERT OR IGNORE INTO usuarios (nom_usu, pass, rol) VALUES 
            ('admin', '1qaz2wsx3edc', 'admin'),
            ('sweethome', '*0sweethome0*', 'usuario');
        """;

    public static void inicializarBaseDatos() {
        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement()) {
            for (String sentencia : SCRIPT_CREACION.split(";")) {
                String sql = sentencia.trim();
                if (!sql.isEmpty()) {
                    try { stmt.execute(sql); }
                    catch (SQLException e) {
                        if (!e.getMessage().contains("already exists"))
                            System.err.println("⚠️ " + e.getMessage());
                    }
                }
            }
            System.out.println("✅ Base de datos inicializada correctamente");
        } catch (SQLException e) {
            System.err.println("❌ Error al inicializar BD: " + e.getMessage());
        }
    }

    public static void actualizarRelaciones() {
        String[] alters = {
                "ALTER TABLE facturas ADD COLUMN usuario_id INTEGER",
                "ALTER TABLE facturas ADD COLUMN estado TEXT DEFAULT 'ACTIVA'",
                "ALTER TABLE gastos_operativos ADD COLUMN usuario_id INTEGER",
                "ALTER TABLE historial_productos ADD COLUMN usuario_id INTEGER",
                "ALTER TABLE historial_materias ADD COLUMN usuario_id INTEGER",
                "ALTER TABLE productos ADD COLUMN usuario_creacion_id INTEGER",
                "ALTER TABLE materias_primas ADD COLUMN usuario_creacion_id INTEGER",
                "ALTER TABLE reportes ADD COLUMN usuario_id INTEGER"
        };
        try (Connection conn = ConexionDB.conectar();
             Statement stmt = conn.createStatement()) {
            for (String sql : alters) {
                try { stmt.execute(sql); }
                catch (SQLException e) {
                    if (!e.getMessage().contains("duplicate column name"))
                        System.err.println("⚠️ " + e.getMessage());
                }
            }
            String[] idx = {
                    "CREATE INDEX IF NOT EXISTS idx_facturas_usuario ON facturas(usuario_id)",
                    "CREATE INDEX IF NOT EXISTS idx_gastos_usuario ON gastos_operativos(usuario_id)",
                    "CREATE INDEX IF NOT EXISTS idx_reportes_usuario ON reportes(usuario_id)"
            };
            for (String i : idx) {
                try { stmt.execute(i); } catch (SQLException ignored) {}
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar: " + e.getMessage());
        }
    }
}