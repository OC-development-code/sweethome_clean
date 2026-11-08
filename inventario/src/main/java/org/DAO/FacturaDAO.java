package org.DAO;

import org.modelo.Factura;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacturaDAO {

    public int insertar(Factura f) {
        String sql = "INSERT INTO facturas (fecha_emision, nombre_cliente, total, usuario_id, estado) " +
                "VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, f.getNombreCliente());
            ps.setDouble(2, f.getTotal());
            ps.setObject(3, f.getUsuarioId());
            ps.setString(4, f.getEstado() != null ? f.getEstado() : "ACTIVA");
            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) { f.setId(rs.getInt(1)); return f.getId(); }
                }
            }
        } catch (SQLException e) { System.err.println("Error insertar factura: " + e.getMessage()); }
        return -1;
    }

    public List<Factura> obtenerTodas() {
        List<Factura> list = new ArrayList<>();
        try (Connection conn = ConexionDB.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM facturas ORDER BY fecha_emision DESC")) {
            while (rs.next()) list.add(mapear(rs));
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return list;
    }

    public Factura obtenerPorId(int id) {
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM facturas WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapear(rs); }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return null;
    }

    public List<Factura> buscarPorCliente(String cliente) {
        List<Factura> list = new ArrayList<>();
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM facturas WHERE nombre_cliente LIKE ? ORDER BY fecha_emision DESC")) {
            ps.setString(1, "%" + cliente + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapear(rs));
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return list;
    }

    public boolean actualizarTotal(int id, double total) {
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement("UPDATE facturas SET total = ? WHERE id = ?")) {
            ps.setDouble(1, total);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }

    public boolean anular(int id) {
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement("UPDATE facturas SET estado = 'ANULADA' WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }

    private Factura mapear(ResultSet rs) throws SQLException {
        Factura f = new Factura();
        f.setId(rs.getInt("id"));
        f.setFechaEmision(rs.getString("fecha_emision"));
        f.setNombreCliente(rs.getString("nombre_cliente"));
        f.setTotal(rs.getDouble("total"));
        try { f.setUsuarioId(rs.getInt("usuario_id")); } catch (SQLException ignored) {}
        try { f.setEstado(rs.getString("estado")); } catch (SQLException ignored) { f.setEstado("ACTIVA"); }
        return f;
    }
}