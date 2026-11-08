package org.servicios;

import org.DAO.MateriaPrimaDAO;
import org.DAO.ProductoDAO;
import org.modelo.MateriaPrima;
import org.modelo.Producto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio de validación de stock para productos y materias primas
 */
public class ValidacionStockService {
    private ProductoDAO productoDAO;
    private MateriaPrimaDAO materiaDAO;

    public ValidacionStockService() {
        this.productoDAO = new ProductoDAO();
        this.materiaDAO = new MateriaPrimaDAO();
    }

    /**
     * Verifica si hay suficiente stock de todas las materias primas necesarias
     * para fabricar una cantidad específica de un producto
     * @param productoId ID del producto
     * @param cantidad Cantidad de productos a fabricar
     * @return Mapa con el estado de cada materia prima
     */
    public Map<MateriaPrima, Integer> verificarStockMateriasPrimas(int productoId, int cantidad) {
        Map<MateriaPrima, Integer> resultado = new HashMap<>();

        // Obtener las materias primas necesarias para el producto
        // NOTA: Esto asume que tienes una tabla productos_materias
        // Por ahora, devolvemos un mapa vacío (se implementará cuando se complete)

        // Ejemplo de cómo se implementaría:
        // List<MateriaNecesaria> materiasNecesarias = obtenerMateriasNecesarias(productoId);
        // for (MateriaNecesaria mn : materiasNecesarias) {
        //     MateriaPrima materia = materiaDAO.obtenerPorId(mn.getMateriaId());
        //     int cantidadNecesaria = (int) (mn.getCantidadNecesaria() * cantidad);
        //     int stockDisponible = materia.getCantidad();
        //     resultado.put(materia, stockDisponible - cantidadNecesaria);
        // }

        return resultado;
    }

    /**
     * Verifica si hay suficiente stock de una materia prima específica
     */
    public boolean verificarStockMateriaPrima(int materiaId, int cantidadNecesaria) {
        MateriaPrima materia = materiaDAO.obtenerPorId(materiaId);
        if (materia == null) {
            return false;
        }
        return materia.getCantidad() >= cantidadNecesaria;
    }

    /**
     * Verifica si hay suficiente stock de un producto para vender
     */
    public boolean verificarStockProducto(int productoId, int cantidadNecesaria) {
        Producto producto = productoDAO.obtenerPorId(productoId);
        if (producto == null) {
            return false;
        }
        return producto.getCantidad() >= cantidadNecesaria;
    }

    /**
     * Obtiene lista de materias primas con stock insuficiente para un producto
     */
    public List<String> obtenerMateriasFaltantes(int productoId, int cantidad) {
        List<String> faltantes = new ArrayList<>();
        Map<MateriaPrima, Integer> resultado = verificarStockMateriasPrimas(productoId, cantidad);

        for (Map.Entry<MateriaPrima, Integer> entry : resultado.entrySet()) {
            if (entry.getValue() < 0) {
                MateriaPrima m = entry.getKey();
                faltantes.add(String.format("%s (faltan %d unidades)",
                        m.getNombre(), Math.abs(entry.getValue())));
            }
        }
        return faltantes;
    }
}