package org.servicios;

import org.DAO.*;
import org.modelo.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReporteService {
    private final ProductoDAO productoDAO;
    private final MateriaPrimaDAO materiaDAO;
    private final GastoOperativoDAO gastoDAO;
    private final ReporteDAO reporteDAO;
    private final FacturaDAO facturaDAO;
    private final UsuarioDAO usuarioDAO;

    public ReporteService() {
        this.productoDAO = new ProductoDAO();
        this.materiaDAO = new MateriaPrimaDAO();
        this.gastoDAO = new GastoOperativoDAO();
        this.reporteDAO = new ReporteDAO();
        this.facturaDAO = new FacturaDAO();
        this.usuarioDAO = new UsuarioDAO();
    }

    public String generarYGuardarReporte(String tipo, String periodo, Integer usuarioId) {
        String contenido = generarReporte(tipo, periodo);
        Reporte reporte = new Reporte(tipo.toLowerCase(), contenido);
        reporte.setUsuarioId(usuarioId);
        reporteDAO.insertar(reporte);
        return contenido;
    }

    public List<Reporte> obtenerReportesPorCategoria(String categoria) {
        return reporteDAO.obtenerPorCategoria(categoria.toLowerCase());
    }

    public Reporte obtenerReportePorId(int id) {
        return reporteDAO.obtenerPorId(id);
    }

    public boolean eliminarReporte(int id) {
        return reporteDAO.eliminar(id);
    }

    private String generarReporte(String tipo, String periodo) {
        StringBuilder sb = new StringBuilder();
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());

        sb.append("=".repeat(80)).append("\n");
        sb.append("              SWEETHOME CLEAN - REPORTE DE ")
                .append(tipo.toUpperCase()).append("\n");
        sb.append("              Fecha: ").append(fecha).append("\n");
        sb.append("              Período: ").append(periodo).append("\n");
        sb.append("=".repeat(80)).append("\n\n");

        switch (tipo.toLowerCase()) {
            case "ventas": generarReporteVentas(sb); break;
            case "inventario": generarReporteInventario(sb); break;
            case "costos": generarReporteCostos(sb); break;
            case "stock": generarReporteStock(sb); break;
            default: sb.append("Tipo no reconocido.");
        }

        sb.append("\n").append("=".repeat(80)).append("\n");
        sb.append("Reporte generado automáticamente por SweetHome Clean v1.0\n");
        return sb.toString();
    }

    private void generarReporteVentas(StringBuilder sb) {
        sb.append("📊 REPORTE DE VENTAS\n");
        sb.append("-".repeat(80)).append("\n");
        sb.append(String.format("%-5s %-18s %-12s %-12s %-10s %-12s\n",
                "ID", "Cliente", "Fecha", "Total", "Estado", "Usuario"));
        sb.append("-".repeat(80)).append("\n");

        List<Factura> facturas = facturaDAO.obtenerTodas();
        double totalActivas = 0;
        for (Factura f : facturas) {
            String nombreUsuario = "";
            if (f.getUsuarioId() != null) {
                Usuario u = usuarioDAO.obtenerPorId(f.getUsuarioId());
                nombreUsuario = (u != null) ? u.getNomUsu() : "N/A";
            }
            String cliente = f.getNombreCliente() != null && f.getNombreCliente().length() > 18
                    ? f.getNombreCliente().substring(0, 15) + "..."
                    : f.getNombreCliente();
            String fecha = f.getFechaEmision() != null && f.getFechaEmision().length() >= 10
                    ? f.getFechaEmision().substring(0, 10) : "";
            sb.append(String.format("%-5d %-18s %-12s S/%-9.2f %-10s %-12s\n",
                    f.getId(), cliente, fecha, f.getTotal(), f.getEstado(), nombreUsuario));
            if ("ACTIVA".equals(f.getEstado())) totalActivas += f.getTotal();
        }

        sb.append("-".repeat(80)).append("\n");
        sb.append("Total de facturas: ").append(facturas.size()).append("\n");
        sb.append("Monto total (facturas ACTIVAS): S/. ").append(String.format("%.2f", totalActivas)).append("\n");
    }

    private void generarReporteInventario(StringBuilder sb) {
        sb.append("📦 REPORTE DE INVENTARIO\n");
        sb.append("-".repeat(80)).append("\n");
        sb.append("PRODUCTOS:\n");
        sb.append(String.format("%-5s %-30s %-12s %-12s\n", "ID", "Nombre", "Cantidad", "Precio"));
        sb.append("-".repeat(80)).append("\n");

        List<Producto> productos = productoDAO.obtenerTodos();
        double valorProductos = 0;
        for (Producto p : productos) {
            sb.append(String.format("%-5d %-30s %-12d S/%-9.2f\n",
                    p.getId(), p.getNombre(), p.getCantidad(), p.getPrecioVenta()));
            valorProductos += p.getCantidad() * p.getPrecioVenta();
        }

        sb.append("\nMATERIAS PRIMAS:\n");
        sb.append(String.format("%-5s %-30s %-12s %-12s\n", "ID", "Nombre", "Cantidad", "Costo Unit."));
        sb.append("-".repeat(80)).append("\n");

        List<MateriaPrima> materias = materiaDAO.obtenerTodos();
        double valorMaterias = 0;
        for (MateriaPrima m : materias) {
            sb.append(String.format("%-5d %-30s %-12d S/%-9.2f\n",
                    m.getId(), m.getNombre(), m.getCantidad(), m.getCostoUnitario()));
            valorMaterias += m.getCantidad() * m.getCostoUnitario();
        }

        sb.append("-".repeat(80)).append("\n");
        sb.append("Total de productos: ").append(productos.size())
                .append(" | Valor: S/. ").append(String.format("%.2f", valorProductos)).append("\n");
        sb.append("Total de materias primas: ").append(materias.size())
                .append(" | Valor: S/. ").append(String.format("%.2f", valorMaterias)).append("\n");
    }

    private void generarReporteCostos(StringBuilder sb) {
        sb.append("💰 REPORTE DE COSTOS\n");
        sb.append("-".repeat(80)).append("\n");

        List<MateriaPrima> materias = materiaDAO.obtenerTodos();
        double totalInventario = materias.stream()
                .mapToDouble(m -> m.getCantidad() * m.getCostoUnitario()).sum();

        sb.append(String.format("📦 COSTO INVENTARIO (materias primas): S/. %.2f\n\n", totalInventario));

        sb.append("📋 GASTOS POR CATEGORÍA:\n");
        List<GastoOperativo> gastos = gastoDAO.obtenerTodos();
        Map<String, Double> porCategoria = gastos.stream()
                .collect(Collectors.groupingBy(GastoOperativo::getCategoria,
                        Collectors.summingDouble(GastoOperativo::getMonto)));
        for (Map.Entry<String, Double> entry : porCategoria.entrySet()) {
            sb.append(String.format("   • %-25s: S/. %,.2f\n", entry.getKey(), entry.getValue()));
        }

        double totalGastos = gastos.stream().mapToDouble(GastoOperativo::getMonto).sum();
        sb.append("\n").append("-".repeat(80)).append("\n");
        sb.append(String.format("TOTAL GASTOS OPERATIVOS: S/. %.2f\n", totalGastos));
        sb.append(String.format("COSTO TOTAL (Inventario + Gastos): S/. %.2f\n",
                totalInventario + totalGastos));
    }

    private void generarReporteStock(StringBuilder sb) {
        sb.append("⚠️ REPORTE DE STOCK BAJO\n");
        sb.append("-".repeat(80)).append("\n");

        sb.append("PRODUCTOS CON STOCK BAJO:\n");
        sb.append(String.format("%-5s %-30s %-12s %-12s\n", "ID", "Nombre", "Stock", "Mínimo"));
        sb.append("-".repeat(80)).append("\n");

        List<Producto> productosBajo = productoDAO.obtenerProductosStockBajo();
        if (productosBajo.isEmpty()) sb.append("✅ Sin productos con stock bajo.\n");
        else for (Producto p : productosBajo) {
            sb.append(String.format("%-5d %-30s %-12d %-12d\n",
                    p.getId(), p.getNombre(), p.getCantidad(), p.getStockMinimo()));
        }

        sb.append("\nMATERIAS PRIMAS CON STOCK BAJO:\n");
        sb.append(String.format("%-5s %-30s %-12s %-12s\n", "ID", "Nombre", "Stock", "Mínimo"));
        sb.append("-".repeat(80)).append("\n");

        List<MateriaPrima> materiasBajo = materiaDAO.obtenerMateriasStockBajo();
        if (materiasBajo.isEmpty()) sb.append("✅ Sin materias primas con stock bajo.\n");
        else for (MateriaPrima m : materiasBajo) {
            sb.append(String.format("%-5d %-30s %-12d %-12d\n",
                    m.getId(), m.getNombre(), m.getCantidad(), m.getStockMinimo()));
        }

        sb.append("\n").append("-".repeat(80)).append("\n");
        sb.append("Productos con stock bajo: ").append(productosBajo.size()).append("\n");
        sb.append("Materias primas con stock bajo: ").append(materiasBajo.size()).append("\n");
    }
}