package com.yupi.pos.services;

import com.yupi.pos.entities.DiccionarioProveedor;
import com.yupi.pos.repositories.DiccionarioProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FacturaService {

    // Inyectamos nuestro diccionario
    @Autowired
    private DiccionarioProveedorRepository diccionarioRepo;

    public List<Map<String, Object>> procesarFacturaXml(MultipartFile archivo) throws Exception {
        List<Map<String, Object>> productosFactura = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(archivo.getInputStream());
        document.getDocumentElement().normalize();

        String rutProveedor = "DESCONOCIDO";
        NodeList emisorList = document.getElementsByTagName("Emisor");
        if (emisorList.getLength() > 0) {
            Element emisor = (Element) emisorList.item(0);
            rutProveedor = emisor.getElementsByTagName("RUTEmisor").item(0).getTextContent();
        }

        NodeList detalles = document.getElementsByTagName("Detalle");
        for (int i = 0; i < detalles.getLength(); i++) {
            Element detalle = (Element) detalles.item(i);

            String nombreItem = detalle.getElementsByTagName("NmbItem").item(0).getTextContent();
            String cantidad = detalle.getElementsByTagName("QtyItem").item(0).getTextContent();
            String precio = detalle.getElementsByTagName("PrcItem").item(0).getTextContent();

            Map<String, Object> item = new HashMap<>();
            item.put("rutProveedor", rutProveedor);
            item.put("nombreItemProveedor", nombreItem);
            item.put("cantidadComprada", Double.parseDouble(cantidad));
            item.put("precioCostoNeto", Double.parseDouble(precio));

            // 🔥 LA MAGIA: Verificamos si ya existe en el diccionario 🔥
            Optional<DiccionarioProveedor> match = diccionarioRepo.findByRutProveedorAndNombreItemProveedor(rutProveedor, nombreItem);

            if (match.isPresent()) {
                item.put("estado", "CONOCIDO");
                item.put("skuAsociado", match.get().getSkuInterno());
            } else {
                item.put("estado", "DESCONOCIDO");
            }

            productosFactura.add(item);
        }

        return productosFactura;
    }
}