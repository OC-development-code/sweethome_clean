package org.modelo;

public class HistorialMateria {
        private int id;
        private int materiaId;
        private int cantidadAnterior;
        private int cantidadNueva;
        private String fechaMovimiento;
        private String justificacion;
        private String tipoMovimiento;
        private Integer usuarioId;

        public HistorialMateria() {}

        public HistorialMateria(int materiaId, int cantidadAnterior, int cantidadNueva,
                                String justificacion, String tipoMovimiento) {
                this.materiaId = materiaId;
                this.cantidadAnterior = cantidadAnterior;
                this.cantidadNueva = cantidadNueva;
                this.justificacion = justificacion;
                this.tipoMovimiento = tipoMovimiento;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public int getMateriaId() { return materiaId; }
        public void setMateriaId(int materiaId) { this.materiaId = materiaId; }
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