package com.yupi.pos.controllers;

import com.yupi.pos.entities.DetalleVenta;
import com.yupi.pos.entities.Venta;
import com.yupi.pos.services.VentaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*") // ¡Magia para que Flutter no tenga problemas de conexión!
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    // --- ¡NUEVO! Creamos este "molde" para atrapar el JSON de Flutter ---
    public static class VentaRequestDTO {
        private List<DetalleVenta> detalles;
        private String metodoPago;

        // Getters y Setters necesarios para que Spring Boot pueda leer el JSON
        public List<DetalleVenta> getDetalles() { return detalles; }
        public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }
        public String getMetodoPago() { return metodoPago; }
        public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    }

    // 1. POST: http://localhost:8080/api/ventas (Para cobrar una venta nueva)
    @PostMapping
    public ResponseEntity<?> registrarVenta(@RequestBody VentaRequestDTO request) {
        try {
            // ¡OJO AQUÍ! Ahora le pasamos ambas cosas al Service: los detalles y el método
            Venta nuevaVenta = ventaService.procesarVenta(request.getDetalles(), request.getMetodoPago());
            return new ResponseEntity<>(nuevaVenta, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. GET: http://localhost:8080/api/ventas (Para ver el historial de tickets)
    @GetMapping
    public ResponseEntity<List<Venta>> obtenerTodas() {
        return ResponseEntity.ok(ventaService.obtenerTodasLasVentas());
    }
}