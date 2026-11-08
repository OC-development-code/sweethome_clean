package org.DAO;

import org.modelo.MateriaPrima;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MateriaPrimaDAO {

    public boolean insertar(MateriaPrima m) {
        String sql = "INSERT INTO materias_primas (nombre, categoria, cantidad, stock_minimo, costo_unitario, fecha_actualizacion, usuario_creacion_id) " +
                "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getNombre());
            ps.setString(2, m.getCategoria());
            ps.setInt(3, m.getCantidad());
            ps.setInt(4, m.getStockMinimo());
            ps.setDouble(5, m.getCostoUnitario());
            ps.setObject(6, m.getUsuarioCreacionId());
            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) m.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return false;
    }

    public List<MateriaPrima> obtenerTodos() {
        List<MateriaPrima> list = new ArrayList<>();
        try (Connection conn = ConexionDB.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM materias_primas ORDER BY nombre")) {
            while (rs.next()) list.add(mapear(rs));
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return list;
    }

    public MateriaPrima obtenerPorId(int id) {
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM materias_primas WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return null;
    }

    public List<MateriaPrima> buscarPorNombre(String nombre) {
        List<MateriaPrima> list = new ArrayList<>();
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM materias_primas WHERE nombre LIKE ? ORDER BY nombre")) {
            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapear(rs));
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return list;
    }

    public List<MateriaPrima> buscarPorCategoria(String cat) {
        List<MateriaPrima> list = new ArrayList<>();
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM materias_primas WHERE categoria = ? ORDER BY nombre")) {
            ps.setString(1, cat);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapear(rs));
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return list;
    }

    public boolean actualizar(MateriaPrima m) {
        String sql = "UPDATE materias_primas SET nombre=?, categoria=?, cantidad=?, stock_minimo=?, costo_unitario=?, fecha_actualizacion=CURRENT_TIMESTAMP WHERE id=?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getNombre());
            ps.setString(2, m.getCategoria());
            ps.setInt(3, m.getCantidad());
            ps.setInt(4, m.getStockMinimo());
            ps.setDouble(5, m.getCostoUnitario());
            ps.setInt(6, m.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }

    public boolean eliminar(int id) {
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM materias_primas WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }

    public int obtenerCantidad(int id) {
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT cantidad FROM materias_primas WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return 0;
    }

    public double obtenerCostoUnitario(int id) {
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT costo_unitario FROM materias_primas WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getDouble(1); }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return 0;
    }

    public String obtenerNombre(int id) {
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT nombre FROM materias_primas WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getString(1); }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return "Materia #" + id;
    }

    public boolean ingresarMateriaPrima(int id, int cantidad, String just) {
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE materias_primas SET cantidad = cantidad + ?, fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = ?")) {
            ps.setInt(1, cantidad);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }

    public boolean egresarMateriaPrima(int id, int cantidad, String just, String tipo) {
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE materias_primas SET cantidad = cantidad - ?, fecha_actualizacion = CURRENT_TIMESTAMP WHERE id = ? AND cantidad >= ?")) {
            ps.setInt(1, cantidad);
            ps.setInt(2, id);
            ps.setInt(3, cantidad);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }

    public List<MateriaPrima> obtenerMateriasStockBajo() {
        List<MateriaPrima> list = new ArrayList<>();
        try (Connection conn = ConexionDB.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM materias_primas WHERE cantidad <= stock_minimo ORDER BY nombre")) {
            while (rs.next()) list.add(mapear(rs));
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return list;
    }

    private MateriaPrima mapear(ResultSet rs) throws SQLException {
        MateriaPrima m = new MateriaPrima();
        m.setId(rs.getInt("id"));
        m.setNombre(rs.getString("nombre"));
        m.setCategoria(rs.getString("categoria"));
        m.setCantidad(rs.getInt("cantidad"));
        m.setStockMinimo(rs.getInt("stock_minimo"));
        m.setCostoUnitario(rs.getDouble("costo_unitario"));
        m.setFechaActualizacion(rs.getString("fecha_actualizacion"));
        try { m.setUsuarioCreacionId(rs.getInt("usuario_creacion_id")); } catch (SQLException ignored) {}
        return m;
    }
}