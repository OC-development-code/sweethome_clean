package org.DAO;

import org.modelo.HistorialMateria;
import java.sql.*;

public class HistorialMateriaDAO {
    public boolean insertar(HistorialMateria h) {
        String sql = "INSERT INTO historial_materias (materia_id, cantidad_anterior, cantidad_nueva, justificacion, tipo_movimiento, usuario_id) VALUES (?,?,?,?,?,?)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, h.getMateriaId());
            ps.setInt(2, h.getCantidadAnterior());
            ps.setInt(3, h.getCantidadNueva());
            ps.setString(4, h.getJustificacion());
            ps.setString(5, h.getTipoMovimiento());
            ps.setObject(6, h.getUsuarioId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println(e.getMessage()); return false; }
    }
}