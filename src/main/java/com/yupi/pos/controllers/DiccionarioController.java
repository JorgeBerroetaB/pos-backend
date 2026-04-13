package com.yupi.pos.controllers;

import com.yupi.pos.entities.DiccionarioProveedor;
import com.yupi.pos.repositories.DiccionarioProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diccionario")
@CrossOrigin(origins = "*") // Permite que Flutter se comunique sin bloqueos
public class DiccionarioController {

    @Autowired
    private DiccionarioProveedorRepository diccionarioRepo;

    // Esta es la ruta a la que llamará Flutter cuando presionemos "Enseñar a Pingu"
    @PostMapping("/aprender")
    public ResponseEntity<?> aprenderProducto(@RequestBody DiccionarioProveedor nuevoConocimiento) {
        try {
            // Guardamos la nueva relación en la base de datos
            DiccionarioProveedor guardado = diccionarioRepo.save(nuevoConocimiento);
            return ResponseEntity.ok(guardado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al aprender: " + e.getMessage());
        }
    }
}