package org.servicios;

import org.DAO.MateriaPrimaDAO;
import org.DAO.GastoOperativoDAO;
import org.modelo.MateriaPrima;
import org.modelo.GastoOperativo;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Servicio para generar reportes de costos y gastos
 */
public class ReporteCostosService {
    private MateriaPrimaDAO materiaDAO;
    private GastoOperativoDAO gastoDAO;

    public ReporteCostosService() {
        this.materiaDAO = new MateriaPrimaDAO();
        this.gastoDAO = new GastoOperativoDAO();
    }

    /**
     * Calcula el costo total de inventario de materias primas
     */
    public double calcularCostoTotalInventario() {
        List<MateriaPrima> materias = materiaDAO.obtenerTodos();
        double total = 0;
        for (MateriaPrima m : materias) {
            total += m.getCantidad() * m.getCostoUnitario();
        }
        return total;
    }

    /**
     * Calcula el costo por categoría de materias primas
     */
    public Map<String, Double> calcularCostoPorCategoria() {
        Map<String, Double> costosPorCategoria = new HashMap<>();
        List<MateriaPrima> materias = materiaDAO.obtenerTodos();

        for (MateriaPrima m : materias) {
            String categoria = m.getCategoria() != null ? m.getCategoria() : "Sin categoría";
            double costo = m.getCantidad() * m.getCostoUnitario();
            costosPorCategoria.put(categoria,
                    costosPorCategoria.getOrDefault(categoria, 0.0) + costo);
        }
        return costosPorCategoria;
    }

    /**
     * Calcula las pérdidas totales por merma/eliminación
     */
    public double calcularPerdidasTotales() {
        // Implementar cuando se tenga la tabla de gastos operativos completa
        return gastoDAO.calcularTotalPorCategoria("Merma");
    }

    /**
     * Genera resumen completo de costos
     */
    public Map<String, Object> generarResumenCostos() {
        Map<String, Object> resumen = new HashMap<>();
        resumen.put("costoTotalInventario", calcularCostoTotalInventario());
        resumen.put("costosPorCategoria", calcularCostoPorCategoria());
        resumen.put("perdidasTotales", calcularPerdidasTotales());
        return resumen;
    }
}