package com.yupi.pos.controllers;

import com.yupi.pos.services.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/facturas")
@CrossOrigin(origins = "*") // Permite que Flutter se conecte sin bloqueos
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    // Esta es la URL a la que Flutter enviará el archivo: POST /api/facturas/subir
    @PostMapping("/subir")
    public ResponseEntity<?> subirFactura(@RequestParam("archivo") MultipartFile archivo) {
        try {
            // Le pasamos el archivo al servicio que creamos arriba
            List<Map<String, Object>> resultado = facturaService.procesarFacturaXml(archivo);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al leer el XML de la factura: " + e.getMessage());
        }
    }
}