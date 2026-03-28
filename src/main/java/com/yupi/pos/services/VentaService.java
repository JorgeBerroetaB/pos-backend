package com.yupi.pos.services;

import com.yupi.pos.entities.DetalleVenta;
import com.yupi.pos.entities.Producto;
import com.yupi.pos.entities.Venta;
import com.yupi.pos.entities.MetodoPago;
import com.yupi.pos.repositories.ProductoRepository;
import com.yupi.pos.repositories.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;

    public VentaService(VentaRepository ventaRepository, ProductoRepository productoRepository) {
        this.ventaRepository = ventaRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public Venta procesarVenta(List<DetalleVenta> detallesRequest, String metodoPagoStr) {
        Venta nuevaVenta = new Venta();
        nuevaVenta.setFecha(LocalDateTime.now());

        // 1. Convertimos el String que manda Flutter al Enum de Java
        try {
            MetodoPago metodoEnum = MetodoPago.valueOf(metodoPagoStr.toUpperCase());
            nuevaVenta.setMetodoPago(metodoEnum);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Método de pago no válido: " + metodoPagoStr);
        }

        BigDecimal totalVenta = BigDecimal.ZERO;

        for (DetalleVenta detalle : detallesRequest) {
            Producto productoReal = productoRepository.findById(detalle.getProducto().getSku())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + detalle.getProducto().getSku()));

            if (productoReal.getStock() < detalle.getCantidad()) {
                throw new RuntimeException("¡Stock insuficiente para el producto: " + productoReal.getNombre() + "!");
            }

            productoReal.setStock(productoReal.getStock() - detalle.getCantidad());
            productoRepository.save(productoReal);

            detalle.setProducto(productoReal);
            detalle.setVenta(nuevaVenta);

            // ==================================================
            // ¡MAGIA DE LA BALANZA Y PRECIOS EDITADOS!
            // ==================================================
            // Si Flutter nos envió el subtotal (porque pesó en balanza o se editó a mano)
            if (detalle.getSubtotal() != null) {
                totalVenta = totalVenta.add(detalle.getSubtotal());
            } else {
                // Si no mandó subtotal, calculamos el normal de la base de datos
                BigDecimal cantidad = new BigDecimal(detalle.getCantidad());
                BigDecimal subtotalBD = productoReal.getPrecioVenta().multiply(cantidad);
                detalle.setSubtotal(subtotalBD);
                totalVenta = totalVenta.add(subtotalBD);
            }
        }

        // Asignamos los detalles y el total calculado a la venta principal
        nuevaVenta.setDetalles(detallesRequest);
        nuevaVenta.setTotal(totalVenta);

        // Guardamos la venta
        return ventaRepository.save(nuevaVenta);
    }

    public List<Venta> obtenerTodasLasVentas() {
        return ventaRepository.findAll();
    }
}