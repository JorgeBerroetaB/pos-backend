package com.yupi.pos.services;

import com.yupi.pos.controllers.VentaController.PagoRequestDTO;
import com.yupi.pos.entities.DetalleVenta;
import com.yupi.pos.entities.PagoVenta;
import com.yupi.pos.entities.Producto;
import com.yupi.pos.entities.Venta;
import com.yupi.pos.entities.MetodoPago;
import com.yupi.pos.repositories.ProductoRepository;
import com.yupi.pos.repositories.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public Venta procesarVenta(List<DetalleVenta> detallesRequest, List<PagoRequestDTO> pagosRequest) {
        Venta nuevaVenta = new Venta();
        nuevaVenta.setFecha(LocalDateTime.now());

        BigDecimal totalVenta = BigDecimal.ZERO;

        // 1. Procesar todos los productos (Detalles)
        for (DetalleVenta detalle : detallesRequest) {
            Producto productoReal = productoRepository.findById(detalle.getProducto().getSku())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + detalle.getProducto().getSku()));

            // Se omite la validación de stock para poder vender en negativo
            productoReal.setStock(productoReal.getStock() - detalle.getCantidad());
            productoRepository.save(productoReal);

            detalle.setProducto(productoReal);
            detalle.setVenta(nuevaVenta);

            // Calcular Subtotales
            if (detalle.getSubtotal() != null) {
                totalVenta = totalVenta.add(detalle.getSubtotal());
            } else {
                BigDecimal cantidad = new BigDecimal(detalle.getCantidad());
                BigDecimal subtotalBD = productoReal.getPrecioVenta().multiply(cantidad);
                detalle.setSubtotal(subtotalBD);
                totalVenta = totalVenta.add(subtotalBD);
            }
        }

        nuevaVenta.setDetalles(detallesRequest);
        nuevaVenta.setTotal(totalVenta);

        // ==========================================
        // 🔥 NUEVO: PROCESAR LISTA DE PAGOS DIVIDIDOS 🔥
        // ==========================================
        List<PagoVenta> listaPagos = new ArrayList<>();

        if (pagosRequest != null && !pagosRequest.isEmpty()) {
            for (PagoRequestDTO pagoDTO : pagosRequest) {
                // Solo guardamos los métodos en los que ingresaron dinero (> 0)
                if (pagoDTO.getMonto() != null && pagoDTO.getMonto().compareTo(BigDecimal.ZERO) > 0) {
                    try {
                        MetodoPago metodoEnum = MetodoPago.valueOf(pagoDTO.getMetodoPago().toUpperCase());
                        PagoVenta nuevoPago = new PagoVenta(nuevaVenta, metodoEnum, pagoDTO.getMonto());
                        listaPagos.add(nuevoPago);
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Método de pago no válido: " + pagoDTO.getMetodoPago());
                    }
                }
            }
        } else {
            throw new RuntimeException("Debe ingresar al menos un método de pago.");
        }

        nuevaVenta.setPagos(listaPagos);

        // Guardamos la venta completa (Spring Boot guardará en cascada los detalles y los pagos)
        return ventaRepository.save(nuevaVenta);
    }

    public List<Venta> obtenerTodasLasVentas() {
        return ventaRepository.findAll();
    }
}