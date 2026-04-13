package com.yupi.pos.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "diccionario_proveedor")
public class DiccionarioProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El RUT de la empresa que te vende (ej: 76.123.456-7)
    @Column(nullable = false)
    private String rutProveedor;

    // El nombre raro que viene en la factura XML (ej: "CC RETORNABLE 2000CC")
    @Column(nullable = false)
    private String nombreItemProveedor;

    // El SKU de TU sistema al que corresponde (ej: "12345")
    @Column(nullable = false)
    private String skuInterno;

    // Constructores vacíos y con parámetros (Obligatorio para Spring Boot)
    public DiccionarioProveedor() {}

    public DiccionarioProveedor(String rutProveedor, String nombreItemProveedor, String skuInterno) {
        this.rutProveedor = rutProveedor;
        this.nombreItemProveedor = nombreItemProveedor;
        this.skuInterno = skuInterno;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRutProveedor() { return rutProveedor; }
    public void setRutProveedor(String rutProveedor) { this.rutProveedor = rutProveedor; }

    public String getNombreItemProveedor() { return nombreItemProveedor; }
    public void setNombreItemProveedor(String nombreItemProveedor) { this.nombreItemProveedor = nombreItemProveedor; }

    public String getSkuInterno() { return skuInterno; }
    public void setSkuInterno(String skuInterno) { this.skuInterno = skuInterno; }
}