package com.yupi.pos.repositories;

// ¡AQUÍ ESTÁ LA MAGIA! Cambiamos .models por .entities 👇
import com.yupi.pos.entities.DiccionarioProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiccionarioProveedorRepository extends JpaRepository<DiccionarioProveedor, Long> {

    // Esta función es MAGIA: Buscará si ya conocemos este producto de este proveedor en específico
    Optional<DiccionarioProveedor> findByRutProveedorAndNombreItemProveedor(String rutProveedor, String nombreItemProveedor);
}