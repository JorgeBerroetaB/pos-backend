package com.yupi.pos.repositories;

import com.yupi.pos.entities.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    // Ya trae listo el guardar, buscar y borrar por defecto
}