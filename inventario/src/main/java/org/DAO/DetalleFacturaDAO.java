package org.DAO;

import org.modelo.DetalleFactura;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleFacturaDAO {

    public boolean insertar(DetalleFactura d) {
        String sql = "INSERT INTO detalles_factura (factura_id, producto_id, cantidad, precio_unitario, subtotal) VALUES (?,?,?,?,?)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, d.getFacturaId());
            ps.setInt(2, d.getProductoId());
            ps.setInt(3, d.getCantidad());
            ps.setDouble(4, d.getPrecioUnitario());
            ps.setDouble(5, d.getSubtotal());
            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) d.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return false;
    }

    public List<DetalleFactura> obtenerPorFactura(int facturaId) {
        List<DetalleFactura> list = new ArrayList<>();
        String sql = "SELECT df.*, p.nombre as nombre_producto FROM detalles_factura df " +
                "JOIN productos p ON df.producto_id = p.id WHERE df.factura_id = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facturaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetalleFactura d = new DetalleFactura();
                    d.setId(rs.getInt("id"));
                    d.setFacturaId(rs.getInt("factura_id"));
                    d.setProductoId(rs.getInt("producto_id"));
                    d.setNombreProducto(rs.getString("nombre_producto"));
                    d.setCantidad(rs.getInt("cantidad"));
                    d.setPrecioUnitario(rs.getDouble("precio_unitario"));
                    d.setSubtotal(rs.getDouble("subtotal"));
                    list.add(d);
                }
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
        return list;
    }
}