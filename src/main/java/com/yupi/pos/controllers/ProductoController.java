package com.yupi.pos.controllers;

import com.yupi.pos.entities.Producto;
import com.yupi.pos.services.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodos() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    @GetMapping("/{sku}")
    public ResponseEntity<Producto> buscarPorSku(@PathVariable String sku) {
        Optional<Producto> producto = productoService.buscarPorSku(sku);
        return producto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Producto> guardarProducto(@RequestBody Producto producto) {
        try {
            Producto productoGuardado = productoService.guardarProducto(producto);
            return new ResponseEntity<>(productoGuardado, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{sku}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable String sku, @RequestBody Producto producto) {
        try {
            Producto productoActualizado = productoService.actualizarProducto(sku, producto);
            return ResponseEntity.ok(productoActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- ENDPOINT ACTUALIZADO PARA FACTURAS (Ahora recibe costo, cantidad y margen) ---
    @PutMapping("/{sku}/actualizar-desde-factura")
    public ResponseEntity<?> actualizarDesdeFactura(@PathVariable String sku,
                                                    @RequestParam double costo,
                                                    @RequestParam double cantidad,
                                                    @RequestParam double margen) {
        try {
            Producto productoActualizado = productoService.actualizarDesdeFactura(sku, costo, cantidad, margen);
            return ResponseEntity.ok(productoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{sku}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable String sku) {
        productoService.eliminarProducto(sku);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarProductos(@RequestParam String termino) {
        return ResponseEntity.ok(productoService.buscarPorTermino(termino));
    }
}