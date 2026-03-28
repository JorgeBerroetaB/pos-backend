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
@CrossOrigin(origins = "*") // ¡Súper importante para que Flutter pueda conectarse sin errores CORS!
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // GET: http://localhost:8080/api/productos (Devuelve todo el inventario)
    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodos() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    // GET: http://localhost:8080/api/productos/770123456 (Busca cuando se escanea un código)
    @GetMapping("/{sku}")
    public ResponseEntity<Producto> buscarPorSku(@PathVariable String sku) {
        Optional<Producto> producto = productoService.buscarPorSku(sku);

        // Si lo encuentra devuelve 200 OK, si no, devuelve 404 Not Found
        return producto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST: http://localhost:8080/api/productos (Crea un producto nuevo)
    @PostMapping
    public ResponseEntity<Producto> guardarProducto(@RequestBody Producto producto) {
        try {
            Producto productoGuardado = productoService.guardarProducto(producto);
            return new ResponseEntity<>(productoGuardado, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Si el precio es negativo (la regla que pusimos en el Service), devuelve un Bad Request 400
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT: http://localhost:8080/api/productos/770123456 (Actualiza un producto existente - ¡Lo nuevo!)
    @PutMapping("/{sku}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable String sku, @RequestBody Producto producto) {
        try {
            Producto productoActualizado = productoService.actualizarProducto(sku, producto);
            return ResponseEntity.ok(productoActualizado); // Devuelve 200 OK con el producto actualizado
        } catch (IllegalArgumentException e) {
            // Si el precio es negativo al actualizar
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            // Si no se encontró el SKU en la base de datos
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE: http://localhost:8080/api/productos/770123456 (Borra un producto)
    @DeleteMapping("/{sku}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable String sku) {
        productoService.eliminarProducto(sku);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // GET: http://localhost:8080/api/productos/buscar?termino=gaseosa
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarProductos(@RequestParam String termino) {
        return ResponseEntity.ok(productoService.buscarPorTermino(termino));
    }
}