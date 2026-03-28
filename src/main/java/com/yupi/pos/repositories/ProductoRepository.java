package com.yupi.pos.repositories;

import com.yupi.pos.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, String> {

    // ¡La magia de Spring Data JPA!
    List<Producto> findByNombreContainingIgnoreCaseOrSkuContaining(String nombre, String sku);

}