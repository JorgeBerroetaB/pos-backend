package com.yupi.pos.controllers;

import com.yupi.pos.entities.DetalleVenta;
import com.yupi.pos.entities.Venta;
import com.yupi.pos.services.VentaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*") // ¡Magia para que Flutter no tenga problemas de conexión!
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    // --- ¡NUEVO! Clase para recibir cada pago individual (Ej: "EFECTIVO" -> 5000) ---
    public static class PagoRequestDTO {
        private String metodoPago;
        private BigDecimal monto;

        public String getMetodoPago() { return metodoPago; }
        public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
        public BigDecimal getMonto() { return monto; }
        public void setMonto(BigDecimal monto) { this.monto = monto; }
    }

    // --- ACTUALIZADO: Ahora recibe una lista de pagos en lugar de un solo String ---
    public static class VentaRequestDTO {
        private List<DetalleVenta> detalles;
        private List<PagoRequestDTO> pagos;

        public List<DetalleVenta> getDetalles() { return detalles; }
        public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }
        public List<PagoRequestDTO> getPagos() { return pagos; }
        public void setPagos(List<PagoRequestDTO> pagos) { this.pagos = pagos; }
    }

    // 1. POST: http://localhost:8080/api/ventas (Para cobrar una venta nueva)
    @PostMapping
    public ResponseEntity<?> registrarVenta(@RequestBody VentaRequestDTO request) {
        try {
            // Pasamos los detalles y la LISTA DE PAGOS al Service
            Venta nuevaVenta = ventaService.procesarVenta(request.getDetalles(), request.getPagos());
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