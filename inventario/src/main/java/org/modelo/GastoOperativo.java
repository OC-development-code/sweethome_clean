package org.modelo;

public class GastoOperativo {
    private int id;
    private String fecha;
    private String descripcion;
    private double monto;
    private String categoria;
    private Integer materiaId;
    private Integer cantidadComprada;
    private Integer usuarioId;

    public GastoOperativo() {}

    public GastoOperativo(String descripcion, double monto, String categoria) {
        this.descripcion = descripcion;
        this.monto = monto;
        this.categoria = categoria;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public Integer getMateriaId() { return materiaId; }
    public void setMateriaId(Integer materiaId) { this.materiaId = materiaId; }
    public Integer getCantidadComprada() { return cantidadComprada; }
    public void setCantidadComprada(Integer cantidadComprada) { this.cantidadComprada = cantidadComprada; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
}