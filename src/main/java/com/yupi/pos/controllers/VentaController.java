package com.yupi.pos.controllers;

import com.yupi.pos.entities.DetalleVenta;
import com.yupi.pos.entities.Venta;
import com.yupi.pos.repositories.VentaRepository;
import com.yupi.pos.services.VentaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*")
public class VentaController {

    private final VentaService ventaService;
    private final VentaRepository ventaRepository;

    public VentaController(VentaService ventaService, VentaRepository ventaRepository) {
        this.ventaService = ventaService;
        this.ventaRepository = ventaRepository;
    }

    public static class PagoRequestDTO {
        private String metodoPago;
        private BigDecimal monto;

        public String getMetodoPago() { return metodoPago; }
        public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
        public BigDecimal getMonto() { return monto; }
        public void setMonto(BigDecimal monto) { this.monto = monto; }
    }

    public static class VentaRequestDTO {
        private List<DetalleVenta> detalles;
        private List<PagoRequestDTO> pagos;

        public List<DetalleVenta> getDetalles() { return detalles; }
        public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }
        public List<PagoRequestDTO> getPagos() { return pagos; }
        public void setPagos(List<PagoRequestDTO> pagos) { this.pagos = pagos; }
    }

    @PostMapping
    public ResponseEntity<?> registrarVenta(@RequestBody VentaRequestDTO request) {
        try {
            Venta nuevaVenta = ventaService.procesarVenta(request.getDetalles(), request.getPagos());
            return new ResponseEntity<>(nuevaVenta, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Venta>> obtenerTodas() {
        return ResponseEntity.ok(ventaService.obtenerTodasLasVentas());
    }

    // ==========================================
    // 🔥 RUTA: PARA CANCELAR UNA VENTA (MÉTODO PUT) 🔥
    // ==========================================
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarVenta(@PathVariable Long id) {
        try {
            Venta ventaCancelada = ventaService.cancelarVenta(id);
            return ResponseEntity.ok(ventaCancelada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==========================================
    // 🔥 RUTA: PARA ELIMINAR UNA VENTA (AHORA HACE SOFT DELETE) 🔥
    // ==========================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarVenta(@PathVariable Long id) {
        try {
            // 🔥 AHORA LLAMAMOS AL SERVICIO PARA QUE SOLO LA TACHE Y DEVUELVA STOCK 🔥
            ventaService.cancelarVenta(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}