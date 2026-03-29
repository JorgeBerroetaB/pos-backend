package com.yupi.pos.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "total", nullable = false)
    private BigDecimal total;

    // 🔥 NUEVO: Estado de la venta para saber si fue cancelada
    @Column(name = "estado", nullable = false)
    private String estado = "COMPLETADA";

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    private List<PagoVenta> pagos = new ArrayList<>();

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    private List<DetalleVenta> detalles = new ArrayList<>();

    public Venta() {
        this.fecha = LocalDateTime.now();
    }

    public Venta(BigDecimal total) {
        this.fecha = LocalDateTime.now();
        this.total = total;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public List<PagoVenta> getPagos() { return pagos; }
    public void setPagos(List<PagoVenta> pagos) { this.pagos = pagos; }

    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }
}