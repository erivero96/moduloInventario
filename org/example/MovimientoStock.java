package org.example;

import java.time.LocalDateTime;

public class MovimientoStock {
    private String id;
    private int codigoProducto;
    private LocalDateTime fecha;
    private int cantidad;
    private String motivo;
    private String usuarioId;

    // Constructor
    public MovimientoStock(String id, int codigoProducto, LocalDateTime fecha, int cantidad, String motivo, String usuarioId) {
        this.id = id;
        this.codigoProducto = codigoProducto;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.motivo = motivo;
        this.usuarioId = usuarioId;
    }

    // Getters
    public String getId() {
        return id;
    }

    public int getCodigoProducto() {
        return codigoProducto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setCodigoProducto(int codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    // toString para debug
    @Override
    public String toString() {
        return "MovimientoStock{" +
                "id='" + id + '\'' +
                ", codigoProducto=" + codigoProducto +
                ", fecha=" + fecha +
                ", cantidad=" + cantidad +
                ", motivo='" + motivo + '\'' +
                ", usuarioId='" + usuarioId + '\'' +
                '}';
    }
}
