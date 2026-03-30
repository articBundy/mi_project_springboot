package com.factura;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Permite llamadas desde cualquier HTML
public class InvoiceController {

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generate(@RequestBody Map<String, Object> payload) {
        try {
            // 1. Cargar Plantilla
            InputStream stream = getClass().getResourceAsStream("/reports/plantilla.jrxml");
            JasperReport report = JasperCompileManager.compileReport(stream);

            // 2. Extraer datos del JSON
            String cliente = (String) payload.get("clienteNombre");
            // suprimir warnings
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> itemsRaw = (List<Map<String, Object>>) payload.get("items");

            List<InvoiceData> itemList = new ArrayList<>();
            for (Map<String, Object> item : itemsRaw) {
                itemList.add(new InvoiceData(
                        (String) item.get("descripcion"),
                        Double.valueOf(item.get("precio").toString())));
            }

            // 3. Parámetros y DataSource
            Map<String, Object> params = new HashMap<>();
            params.put("clienteNombre", cliente);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(itemList);

            // 4. Llenar y Exportar
            JasperPrint print = JasperFillManager.fillReport(report, params, dataSource);
            byte[] pdf = JasperExportManager.exportReportToPdf(print);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
