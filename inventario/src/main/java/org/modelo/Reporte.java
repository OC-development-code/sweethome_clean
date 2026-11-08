package org.modelo;

public class Reporte {
    private int id;
    private String categoria;
    private String contenido;
    private String fechaGeneracion;
    private Integer usuarioId;

    public Reporte() {}

    public Reporte(String categoria, String contenido) {
        this.categoria = categoria;
        this.contenido = contenido;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public String getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(String fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    @Override
    public String toString() {
        return String.format("Reporte #%d - %s (%s)",
                id, categoria.toUpperCase(),
                fechaGeneracion != null && fechaGeneracion.length() >= 16 ? fechaGeneracion.substring(0, 16) : "Sin fecha");
    }
}