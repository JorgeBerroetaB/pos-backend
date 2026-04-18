package com.yupi.pos.services;

import com.yupi.pos.entities.Producto;
import com.yupi.pos.repositories.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> buscarPorSku(String sku) {
        return productoRepository.findById(sku);
    }

    public Producto guardarProducto(Producto producto) {
        if (producto.getPrecioVenta().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("¡Error! El precio de venta no puede ser negativo.");
        }
        return productoRepository.save(producto);
    }

    // --- MÉTODO ACTUALIZADO PARA FACTURAS (IVA + Margen Dinámico) ---
    @Transactional
    public Producto actualizarDesdeFactura(String sku, double costoNeto, double cantidadComprada, double margenGanancia) {
        return productoRepository.findById(sku).map(producto -> {
            BigDecimal costoNetoBD = BigDecimal.valueOf(costoNeto);

            // 1. Actualizamos el costo base neto
            producto.setPrecioCosto(costoNetoBD);

            // 2. Calculamos el Costo Bruto (Costo Neto + 19% IVA)
            BigDecimal iva = new BigDecimal("1.19");
            BigDecimal costoBruto = costoNetoBD.multiply(iva);

            // 3. Calculamos el Precio de Venta sumando el margen ingresado (ej: 30%)
            // Ecuación: Costo Bruto * (1 + (margen / 100))
            BigDecimal factorMargen = BigDecimal.valueOf(1 + (margenGanancia / 100.0));
            BigDecimal nuevoPrecioVenta = costoBruto.multiply(factorMargen).setScale(0, RoundingMode.HALF_UP);

            producto.setPrecioVenta(nuevoPrecioVenta);

            // 4. Sumamos el stock
            int stockActual = producto.getStock() != null ? producto.getStock() : 0;
            producto.setStock(stockActual + (int) cantidadComprada);

            return guardarProducto(producto);
        }).orElseThrow(() -> new RuntimeException("No se pudo actualizar: Producto no encontrado SKU " + sku));
    }

    public Producto actualizarProducto(String sku, Producto detallesProducto) {
        return productoRepository.findById(sku).map(productoExistente -> {
            productoExistente.setNombre(detallesProducto.getNombre());
            productoExistente.setPrecioVenta(detallesProducto.getPrecioVenta());
            productoExistente.setPrecioCosto(detallesProducto.getPrecioCosto());
            productoExistente.setStock(detallesProducto.getStock());
            return guardarProducto(productoExistente);
        }).orElseThrow(() -> new RuntimeException("¡Error! No se encontró el producto con SKU: " + sku));
    }

    public void eliminarProducto(String sku) {
        productoRepository.deleteById(sku);
    }

    public List<Producto> buscarPorTermino(String termino) {
        return productoRepository.findByNombreContainingIgnoreCaseOrSkuContaining(termino, termino);
    }
}