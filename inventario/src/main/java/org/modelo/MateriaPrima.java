package org.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MateriaPrima {
    private int id;
    private String nombre;
    private String categoria;
    private int cantidad;
    private int stockMinimo;
    private double costoUnitario;
    private String fechaActualizacion;
    private Integer usuarioCreacionId;

    public MateriaPrima() {}

    public MateriaPrima(String nombre, String categoria, int cantidad, int stockMinimo, double costoUnitario) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.stockMinimo = stockMinimo;
        this.costoUnitario = costoUnitario;
        this.fechaActualizacion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }
    public double getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(double costoUnitario) { this.costoUnitario = costoUnitario; }
    public String getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(String fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    public Integer getUsuarioCreacionId() { return usuarioCreacionId; }
    public void setUsuarioCreacionId(Integer usuarioCreacionId) { this.usuarioCreacionId = usuarioCreacionId; }

    @Override
    public String toString() {
        return String.format("MateriaPrima{id=%d, nombre='%s', cantidad=%d, costoUnitario=%.2f}",
                id, nombre, cantidad, costoUnitario);
    }
}