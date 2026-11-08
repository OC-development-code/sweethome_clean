package org.servicios;

import org.DAO.*;
import org.modelo.*;

import java.util.List;

public class FacturaService {
    private final FacturaDAO facturaDAO;
    private final DetalleFacturaDAO detalleDAO;
    private final ProductoDAO productoDAO;
    private final HistorialProductoDAO historialProductoDAO;
    private final GastoOperativoDAO gastoDAO;

    public FacturaService() {
        this.facturaDAO = new FacturaDAO();
        this.detalleDAO = new DetalleFacturaDAO();
        this.productoDAO = new ProductoDAO();
        this.historialProductoDAO = new HistorialProductoDAO();
        this.gastoDAO = new GastoOperativoDAO();
    }

    /**
     * Crear factura con detalles + descontar stock + registrar historial
     */
    public Factura crearFactura(Factura factura, List<DetalleFactura> detalles, Integer usuarioId) {
        factura.setUsuarioId(usuarioId);
        int id = facturaDAO.insertar(factura);
        if (id == -1) return null;
        factura.setId(id);

        double total = 0;
        for (DetalleFactura d : detalles) {
            d.setFacturaId(id);
            if (!detalleDAO.insertar(d)) {
                return null;
            }
            total += d.getSubtotal();

            // Descontar stock + registrar historial
            int stockActual = productoDAO.obtenerCantidad(d.getProductoId());
            if (productoDAO.egresarProducto(d.getProductoId(), d.getCantidad(),
                    "Venta factura #" + id, "VENTA")) {
                HistorialProducto h = new HistorialProducto(
                        d.getProductoId(), stockActual, stockActual - d.getCantidad(),
                        "Venta factura #" + id, "VENTA");
                h.setUsuarioId(usuarioId);
                historialProductoDAO.insertar(h);
            }
        }

        facturaDAO.actualizarTotal(id, total);
        factura.setTotal(total);
        return factura;
    }

    public List<Factura> obtenerTodas() {
        return facturaDAO.obtenerTodas();
    }

    public List<Factura> buscarPorCliente(String cliente) {
        return facturaDAO.buscarPorCliente(cliente);
    }

    public Factura obtenerPorId(int id) {
        return facturaDAO.obtenerPorId(id);
    }

    public List<DetalleFactura> obtenerDetalles(int facturaId) {
        return detalleDAO.obtenerPorFactura(facturaId);
    }

    /**
     * Anular factura: devuelve stock, registra gasto por la pérdida
     */
    public boolean anularFactura(int facturaId, String justificacion, Integer usuarioId) {
        Factura factura = facturaDAO.obtenerPorId(facturaId);
        if (factura == null || "ANULADA".equals(factura.getEstado())) {
            return false;
        }

        List<DetalleFactura> detalles = detalleDAO.obtenerPorFactura(facturaId);

        // Devolver stock
        for (DetalleFactura d : detalles) {
            int stockActual = productoDAO.obtenerCantidad(d.getProductoId());
            productoDAO.ingresarProducto(d.getProductoId(), d.getCantidad(),
                    "Anulación factura #" + facturaId);
            HistorialProducto h = new HistorialProducto(
                    d.getProductoId(), stockActual, stockActual + d.getCantidad(),
                    "Anulación factura #" + facturaId + " - " + justificacion,
                    "REPOSICION");
            h.setUsuarioId(usuarioId);
            historialProductoDAO.insertar(h);
        }

        // Registrar gasto por pérdida
        GastoOperativo gasto = new GastoOperativo(
                "Anulación factura #" + facturaId + " - " + justificacion,
                factura.getTotal(), "ANULACION");
        gasto.setUsuarioId(usuarioId);
        gastoDAO.insertar(gasto);

        return facturaDAO.anular(facturaId);
    }
}