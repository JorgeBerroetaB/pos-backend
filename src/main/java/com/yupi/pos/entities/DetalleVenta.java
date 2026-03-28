package com.yupi.pos.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalles_venta")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación Muchos a Uno: Muchos detalles pertenecen a UNA Venta
    @ManyToOne
    @JoinColumn(name = "venta_id", nullable = false)
    @JsonIgnore // Esto evita un bucle infinito al enviar a Flutter
    private Venta venta;

    // Relación con tu producto existente (¡Aquí conectamos los módulos!)
    @ManyToOne
    @JoinColumn(name = "producto_sku", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    // Guardamos el subtotal (precio * cantidad) en el momento de la venta
    // Es vital guardarlo por si mañana el producto sube de precio, el historial no se altere
    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal;

    public DetalleVenta() {
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}