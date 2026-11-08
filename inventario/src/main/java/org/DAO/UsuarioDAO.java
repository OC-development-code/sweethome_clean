package org.DAO;

import org.modelo.Usuario;
import java.sql.*;

public class UsuarioDAO {

    public Usuario validarCredenciales(String nomUsu, String pass) {
        String sql = "SELECT * FROM usuarios WHERE nom_usu = ? AND pass = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nomUsu);
            pstmt.setString(2, pass);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error validar credenciales: " + e.getMessage());
        }
        return null;
    }

    public Usuario obtenerPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error obtener usuario: " + e.getMessage());
        }
        return null;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNomUsu(rs.getString("nom_usu"));
        u.setPass(rs.getString("pass"));
        u.setRol(rs.getString("rol"));
        u.setFechaCreacion(rs.getString("fecha_creacion"));
        return u;
    }
}