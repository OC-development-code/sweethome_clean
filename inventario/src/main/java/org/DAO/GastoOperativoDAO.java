package org.DAO;

import org.modelo.GastoOperativo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GastoOperativoDAO {

    public boolean insertar(GastoOperativo g) {
        String sql = "INSERT INTO gastos_operativos (fecha, descripcion, monto, categoria, materia_id, cantidad_comprada, usuario_id) " +
                "VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, g.getDescripcion());
            ps.setDouble(2, g.getMonto());
            ps.setString(3, g.getCategoria());
            ps.setObject(4, g.getMateriaId());
            ps.setObject(5, g.getCantidadComprada());
            ps.setObject(6, g.getUsuarioId());
            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) g.setId(rs.getInt(1)); }
                return true;
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return false;
    }

    public List<GastoOperativo> obtenerTodos() {
        List<GastoOperativo> list = new ArrayList<>();
        try (Connection conn = ConexionDB.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM gastos_operativos ORDER BY fecha DESC")) {
            while (rs.next()) {
                GastoOperativo g = new GastoOperativo();
                g.setId(rs.getInt("id"));
                g.setFecha(rs.getString("fecha"));
                g.setDescripcion(rs.getString("descripcion"));
                g.setMonto(rs.getDouble("monto"));
                g.setCategoria(rs.getString("categoria"));
                try { g.setMateriaId(rs.getInt("materia_id")); } catch (SQLException ignored) {}
                try { g.setCantidadComprada(rs.getInt("cantidad_comprada")); } catch (SQLException ignored) {}
                try { g.setUsuarioId(rs.getInt("usuario_id")); } catch (SQLException ignored) {}
                list.add(g);
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return list;
    }

    public double calcularTotalPorCategoria(String categoria) {
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT SUM(monto) as total FROM gastos_operativos WHERE categoria = ?")) {
            ps.setString(1, categoria);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getDouble("total"); }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return 0;
    }
}