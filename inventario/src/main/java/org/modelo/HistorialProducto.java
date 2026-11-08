package org.modelo;

public class HistorialProducto {
        private int id;
        private int productoId;
        private int cantidadAnterior;
        private int cantidadNueva;
        private String fechaMovimiento;
        private String justificacion;
        private String tipoMovimiento;
        private Integer usuarioId;

        public HistorialProducto() {}

        public HistorialProducto(int productoId, int cantidadAnterior, int cantidadNueva,
                                 String justificacion, String tipoMovimiento) {
                this.productoId = productoId;
                this.cantidadAnterior = cantidadAnterior;
                this.cantidadNueva = cantidadNueva;
                this.justificacion = justificacion;
                this.tipoMovimiento = tipoMovimiento;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public int getProductoId() { return productoId; }
        public void setProductoId(int productoId) { this.productoId = productoId; }
        public int getCantidadAnterior() { return cantidadAnterior; }
        public void setCantidadAnterior(int cantidadAnterior) { this.cantidadAnterior = cantidadAnterior; }
        public int getCantidadNueva() { return cantidadNueva; }
        public void setCantidadNueva(int cantidadNueva) { this.cantidadNueva = cantidadNueva; }
        public String getFechaMovimiento() { return fechaMovimiento; }
        public void setFechaMovimiento(String fechaMovimiento) { this.fechaMovimiento = fechaMovimiento; }
        public String getJustificacion() { return justificacion; }
        public void setJustificacion(String justificacion) { this.justificacion = justificacion; }
        public String getTipoMovimiento() { return tipoMovimiento; }
        public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }
        public Integer getUsuarioId() { return usuarioId; }
        public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
}