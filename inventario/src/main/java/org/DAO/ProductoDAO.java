package org.DAO;

import org.modelo.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public boolean insertar(Producto p) {
        String sql = "INSERT INTO productos (nombre, categoria, cantidad, stock_minimo, precio_venta, fecha_actualizacion, usuario_creacion_id) " +
                "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getCategoria());
            ps.setInt(3, p.getCantidad());
            ps.setInt(4, p.getStockMinimo());
            ps.setDouble(5, p.getPrecioVenta());
            ps.setObject(6, p.getUsuarioCreacionId());
            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) p.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error insertar producto: " + e.getMessage());
        }
        return false;
    }

    public List<Producto> obtenerTodos() {
        List<Producto> list = new ArrayList<>();
        try (Connection conn = ConexionDB.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM productos ORDER BY nombre")) {
            while (rs.next()) list.add(mapear(rs));
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return list;
    }

    public Producto obtenerPorId(int id) {
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM productos WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return null;
    }

    public List<Producto> buscarPorNombre(String nombre) {
        List<Producto> list = new ArrayList<>();
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM productos WHERE nombre LIKE ? ORDER BY nombre")) {
            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapear(rs));
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return list;
    }

    public List<Producto> buscarPorCategoria(String cat) {
        List<Producto> list = new ArrayList<>();
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM productos WHERE categoria = ? ORDER BY nombre")) {
            ps.setString(1, cat);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapear(rs));
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return list;
    }

    public boolean actualizar(Producto p) {
        String sql = "UPDATE productos SET nombre=?, categoria=?, cantidad=?, stock_minimo=?, precio_venta=?, fecha_actualizacion=CURRENT_TIMESTAMP WHERE id=?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getCategoria());
            ps.setInt(3, p.getCantidad());
            ps.setInt(4, p.getStockMinimo());
            ps.setDouble(5, p.getPrecioVenta());
            ps.setInt(6, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }

    public boolean eliminar(int id) {
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM productos WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }

    public int obtenerCantidad(int id) {
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT cantidad FROM productos WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return 0;
    }

    public boolean ingresarProducto(int id, int cantidad, String just) {
        String sql = "UPDATE productos SET cantidad = cantidad + ?, fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }

    public boolean egresarProducto(int id, int cantidad, String just, String tipo) {
        String sql = "UPDATE productos SET cantidad = cantidad - ?, fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = ? AND cantidad >= ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, id);
            ps.setInt(3, cantidad);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }

    public List<Producto> obtenerProductosStockBajo() {
        List<Producto> list = new ArrayList<>();
        try (Connection conn = ConexionDB.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM productos WHERE cantidad <= stock_minimo ORDER BY nombre")) {
            while (rs.next()) list.add(mapear(rs));
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return list;
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setId(rs.getInt("id"));
        p.setNombre(rs.getString("nombre"));
        p.setCategoria(rs.getString("categoria"));
        p.setCantidad(rs.getInt("cantidad"));
        p.setStockMinimo(rs.getInt("stock_minimo"));
        p.setPrecioVenta(rs.getDouble("precio_venta"));
        p.setFechaActualizacion(rs.getString("fecha_actualizacion"));
        try { p.setUsuarioCreacionId(rs.getInt("usuario_creacion_id")); } catch (SQLException ignored) {}
        return p;
    }
}