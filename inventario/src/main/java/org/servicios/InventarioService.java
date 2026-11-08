package org.servicios;

import org.DAO.*;
import org.modelo.*;

import java.util.List;

public class InventarioService {
    private final ProductoDAO productoDAO;
    private final MateriaPrimaDAO materiaDAO;
    private final HistorialProductoDAO historialProductoDAO;
    private final HistorialMateriaDAO historialMateriaDAO;
    private final GastoOperativoDAO gastoDAO;

    public InventarioService() {
        this.productoDAO = new ProductoDAO();
        this.materiaDAO = new MateriaPrimaDAO();
        this.historialProductoDAO = new HistorialProductoDAO();
        this.historialMateriaDAO = new HistorialMateriaDAO();
        this.gastoDAO = new GastoOperativoDAO();
    }

    // ============ PRODUCTOS ============
    public boolean crearProducto(Producto producto, Integer usuarioId) {
        producto.setUsuarioCreacionId(usuarioId);
        return productoDAO.insertar(producto);
    }

    public List<Producto> listarProductos() { return productoDAO.obtenerTodos(); }
    public Producto obtenerProducto(int id) { return productoDAO.obtenerPorId(id); }
    public List<Producto> buscarProductos(String nombre) { return productoDAO.buscarPorNombre(nombre); }
    public List<Producto> buscarProductosPorCategoria(String c) { return productoDAO.buscarPorCategoria(c); }
    public boolean actualizarProducto(Producto p) { return productoDAO.actualizar(p); }
    public boolean eliminarProducto(int id) { return productoDAO.eliminar(id); }

    public boolean ingresarProducto(int productoId, int cantidad, String just, Integer usuarioId) {
        int actual = productoDAO.obtenerCantidad(productoId);
        if (productoDAO.ingresarProducto(productoId, cantidad, just)) {
            HistorialProducto h = new HistorialProducto(productoId, actual, actual + cantidad, just, "REPOSICION");
            h.setUsuarioId(usuarioId);
            return historialProductoDAO.insertar(h);
        }
        return false;
    }

    public boolean venderProducto(int productoId, int cantidad, String just, Integer usuarioId) {
        int actual = productoDAO.obtenerCantidad(productoId);
        if (productoDAO.egresarProducto(productoId, cantidad, just, "VENTA")) {
            HistorialProducto h = new HistorialProducto(productoId, actual, actual - cantidad, just, "VENTA");
            h.setUsuarioId(usuarioId);
            return historialProductoDAO.insertar(h);
        }
        return false;
    }

    public boolean mermaProducto(int productoId, int cantidad, String just, Integer usuarioId) {
        int actual = productoDAO.obtenerCantidad(productoId);
        if (productoDAO.egresarProducto(productoId, cantidad, just, "MERMA")) {
            HistorialProducto h = new HistorialProducto(productoId, actual, actual - cantidad, just, "MERMA");
            h.setUsuarioId(usuarioId);
            historialProductoDAO.insertar(h);

            Producto p = productoDAO.obtenerPorId(productoId);
            if (p != null) {
                GastoOperativo g = new GastoOperativo(
                        "Merma producto: " + p.getNombre() + " x" + cantidad + " - " + just,
                        cantidad * p.getPrecioVenta(), "Merma");
                g.setUsuarioId(usuarioId);
                gastoDAO.insertar(g);
            }
            return true;
        }
        return false;
    }

    public List<Producto> obtenerProductosStockBajo() { return productoDAO.obtenerProductosStockBajo(); }

    // ============ MATERIAS PRIMAS ============
    public boolean crearMateriaPrima(MateriaPrima materia, Integer usuarioId) {
        materia.setUsuarioCreacionId(usuarioId);
        return materiaDAO.insertar(materia);
    }

    public List<MateriaPrima> listarMateriasPrimas() { return materiaDAO.obtenerTodos(); }
    public MateriaPrima obtenerMateriaPrima(int id) { return materiaDAO.obtenerPorId(id); }
    public List<MateriaPrima> buscarMateriasPrimas(String n) { return materiaDAO.buscarPorNombre(n); }
    public List<MateriaPrima> buscarMateriasPorCategoria(String c) { return materiaDAO.buscarPorCategoria(c); }
    public boolean actualizarMateriaPrima(MateriaPrima m) { return materiaDAO.actualizar(m); }
    public boolean eliminarMateriaPrima(int id) { return materiaDAO.eliminar(id); }

    public boolean comprarMateriaPrima(int materiaId, int cantidad, String just, Integer usuarioId) {
        int actual = materiaDAO.obtenerCantidad(materiaId);
        double costo = materiaDAO.obtenerCostoUnitario(materiaId);
        if (materiaDAO.ingresarMateriaPrima(materiaId, cantidad, just)) {
            HistorialMateria h = new HistorialMateria(materiaId, actual, actual + cantidad, just, "COMPRA");
            h.setUsuarioId(usuarioId);
            historialMateriaDAO.insertar(h);

            GastoOperativo g = new GastoOperativo(
                    "Compra de " + cantidad + " uds. de " + materiaDAO.obtenerNombre(materiaId),
                    cantidad * costo, "Materia Prima");
            g.setMateriaId(materiaId);
            g.setCantidadComprada(cantidad);
            g.setUsuarioId(usuarioId);
            gastoDAO.insertar(g);
            return true;
        }
        return false;
    }

    public boolean usarMateriaPrima(int materiaId, int cantidad, String just, Integer usuarioId) {
        int actual = materiaDAO.obtenerCantidad(materiaId);
        if (materiaDAO.egresarMateriaPrima(materiaId, cantidad, just, "USO_EN_FABRICACION")) {
            HistorialMateria h = new HistorialMateria(materiaId, actual, actual - cantidad, just, "USO_EN_FABRICACION");
            h.setUsuarioId(usuarioId);
            return historialMateriaDAO.insertar(h);
        }
        return false;
    }

    public boolean mermaMateriaPrima(int materiaId, int cantidad, String just, Integer usuarioId) {
        int actual = materiaDAO.obtenerCantidad(materiaId);
        double costo = materiaDAO.obtenerCostoUnitario(materiaId);
        if (materiaDAO.egresarMateriaPrima(materiaId, cantidad, just, "MERMA")) {
            HistorialMateria h = new HistorialMateria(materiaId, actual, actual - cantidad, just, "MERMA");
            h.setUsuarioId(usuarioId);
            historialMateriaDAO.insertar(h);

            GastoOperativo g = new GastoOperativo(
                    "Merma de " + cantidad + " uds. de " + materiaDAO.obtenerNombre(materiaId) + " - " + just,
                    cantidad * costo, "Merma");
            g.setMateriaId(materiaId);
            g.setUsuarioId(usuarioId);
            gastoDAO.insertar(g);
            return true;
        }
        return false;
    }

    public List<MateriaPrima> obtenerMateriasStockBajo() { return materiaDAO.obtenerMateriasStockBajo(); }
}