package org.DAO;

import org.modelo.Reporte;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {

    public boolean insertar(Reporte r) {
        String sql = "INSERT INTO reportes (categoria, contenido, fecha_generacion, usuario_id) VALUES (?, ?, CURRENT_TIMESTAMP, ?)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getCategoria());
            ps.setString(2, r.getContenido());
            ps.setObject(3, r.getUsuarioId());
            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) r.setId(rs.getInt(1)); }
                return true;
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return false;
    }

    public List<Reporte> obtenerPorCategoria(String categoria) {
        List<Reporte> list = new ArrayList<>();
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM reportes WHERE categoria = ? ORDER BY fecha_generacion DESC")) {
            ps.setString(1, categoria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapear(rs));
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return list;
    }

    public Reporte obtenerPorId(int id) {
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM reportes WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapear(rs); }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return null;
    }

    public boolean eliminar(int id) {
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM reportes WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }

    private Reporte mapear(ResultSet rs) throws SQLException {
        Reporte r = new Reporte();
        r.setId(rs.getInt("id"));
        r.setCategoria(rs.getString("categoria"));
        r.setContenido(rs.getString("contenido"));
        r.setFechaGeneracion(rs.getString("fecha_generacion"));
        try { r.setUsuarioId(rs.getInt("usuario_id")); } catch (SQLException ignored) {}
        return r;
    }
}