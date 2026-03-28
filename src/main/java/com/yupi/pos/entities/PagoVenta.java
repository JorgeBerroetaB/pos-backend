package com.yupi.pos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class PagoVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "venta_id")
    @JsonIgnore // Para evitar bucles infinitos al devolver el JSON
    private Venta venta;

    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago;

    private BigDecimal monto;

    // Constructores
    public PagoVenta() {}

    public PagoVenta(Venta venta, MetodoPago metodoPago, BigDecimal monto) {
        this.venta = venta;
        this.metodoPago = metodoPago;
        this.monto = monto;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }

    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
}