package org.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Factura {
    private int id;
    private String fechaEmision;
    private String nombreCliente;
    private double total;
    private Integer usuarioId;
    private String estado;

    public Factura() {
        this.estado = "ACTIVA";
        this.fechaEmision = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public Factura(String nombreCliente, Integer usuarioId) {
        this();
        this.nombreCliente = nombreCliente;
        this.usuarioId = usuarioId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(String fechaEmision) { this.fechaEmision = fechaEmision; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}