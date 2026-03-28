package com.yupi.pos.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "productos")
public class Producto {

    // Usamos el código de barras (SKU) como nuestro ID principal
    @Id
    @Column(name = "sku", length = 50, nullable = false)
    private String sku;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    // BigDecimal es el estándar en Java para manejar dinero (evita errores de redondeo)
    @Column(name = "precio_venta", nullable = false)
    private BigDecimal precioVenta;

    @Column(name = "precio_costo", nullable = false)
    private BigDecimal precioCosto;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    // Constructores (Uno vacío es obligatorio para Spring Boot)
    public Producto() {
    }

    public Producto(String sku, String nombre, BigDecimal precioVenta, BigDecimal precioCosto, Integer stock) {
        this.sku = sku;
        this.nombre = nombre;
        this.precioVenta = precioVenta;
        this.precioCosto = precioCosto;
        this.stock = stock;
    }

    // Getters y Setters (Para que otras partes del código puedan leer y modificar los datos)
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }

    public BigDecimal getPrecioCosto() { return precioCosto; }
    public void setPrecioCosto(BigDecimal precioCosto) { this.precioCosto = precioCosto; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}