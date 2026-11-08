package org.modelo;

public class Usuario {
    private int id;
    private String nomUsu;
    private String pass;
    private String rol;
    private String fechaCreacion;

    public Usuario() {}

    public Usuario(String nomUsu, String pass, String rol) {
        this.nomUsu = nomUsu;
        this.pass = pass;
        this.rol = rol;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNomUsu() { return nomUsu; }
    public void setNomUsu(String nomUsu) { this.nomUsu = nomUsu; }
    public String getPass() { return pass; }
    public void setPass(String pass) { this.pass = pass; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public boolean esAdmin() { return "admin".equalsIgnoreCase(rol); }
    public boolean puedeEliminar() { return esAdmin() || "sweethome".equalsIgnoreCase(nomUsu); }

    @Override
    public String toString() {
        return String.format("Usuario{id=%d, nomUsu='%s', rol='%s'}", id, nomUsu, rol);
    }
}