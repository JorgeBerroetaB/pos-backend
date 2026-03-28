package com.yupi.pos.services;

import com.yupi.pos.entities.Producto;
import com.yupi.pos.repositories.ProductoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    // Inyección de dependencias: Le pasamos el Repository a nuestro Service
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // 1. Obtener todos los productos (Para el panel de inventario en Flutter)
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    // 2. Buscar un producto por código de barras (Cuando el cajero escanea)
    // Usamos Optional porque el producto podría no existir en la base de datos
    public Optional<Producto> buscarPorSku(String sku) {
        return productoRepository.findById(sku);
    }

    // 3. Crear producto (Aquí metemos las reglas de Yupi)
    public Producto guardarProducto(Producto producto) {
        // Regla de negocio: No se puede vender algo a precio negativo
        if (producto.getPrecioVenta().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("¡Error! El precio de venta no puede ser negativo.");
        }

        // Si todo está correcto, le decimos al Repository que lo guarde
        return productoRepository.save(producto);
    }

    // 4. Actualizar un producto existente (¡Lo nuevo!)
    public Producto actualizarProducto(String sku, Producto detallesProducto) {
        // Buscamos si el producto existe primero
        return productoRepository.findById(sku).map(productoExistente -> {
            // Actualizamos los campos
            productoExistente.setNombre(detallesProducto.getNombre());
            productoExistente.setPrecioVenta(detallesProducto.getPrecioVenta());
            productoExistente.setPrecioCosto(detallesProducto.getPrecioCosto());
            productoExistente.setStock(detallesProducto.getStock());

            // Reutilizamos tu método guardarProducto para que también valide que el precio no sea negativo al actualizar
            return guardarProducto(productoExistente);
        }).orElseThrow(() -> new RuntimeException("¡Error! No se encontró el producto con SKU: " + sku));
    }

    // 5. Eliminar un producto
    public void eliminarProducto(String sku) {
        productoRepository.deleteById(sku);
    }

    // Buscar por cualquier término (nombre o pedazo de SKU)
    public List<Producto> buscarPorTermino(String termino) {
        return productoRepository.findByNombreContainingIgnoreCaseOrSkuContaining(termino, termino);
    }
}