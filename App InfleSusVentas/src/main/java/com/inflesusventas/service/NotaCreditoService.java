package com.inflesusventas.service;

import com.inflesusventas.controller.CotizacionController; // IMPORTANTE
import com.inflesusventas.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * Servicio de Notas de Crédito
 * Almacenamiento LOCAL (sin BD)
 * SUNAT SIMULADA
 */
@Service
public class NotaCreditoService {

   @Autowired
    private XmlGeneratorService xmlGeneratorService;
    @Autowired
    private PdfGeneratorService pdfGeneratorService;
    @Autowired
    private SunatApiService sunatApiService;
    @Autowired
    private JsonPersistenceService jsonService; // Para guardar JSON
    @Autowired
    private CotizacionController cotizacionController; // Para actualizar estado

    @Value("${empresa.ruc:20554524051}")
    private String rucEmisor;

    @Value("${empresa.razon_social:INFLE SUS VENTAS S.R.L.}")
    private String razonSocialEmisor;

    @Value("${serie.nota_credito:FC01}")
    private String serieNotaCredito;

    private List<NotaCredito> notasCreditoEnMemoria = new ArrayList<>();
    private int contadorNumero = 0;

    @PostConstruct
    public void init() {
        this.notasCreditoEnMemoria = jsonService.cargarNotasCredito();
        // Calcular el último correlativo para seguir la numeración
        if (!notasCreditoEnMemoria.isEmpty()) {
            this.contadorNumero = notasCreditoEnMemoria.get(notasCreditoEnMemoria.size() - 1).getNumero();
        }
        System.out.println("✅ Notas de Crédito cargadas: " + notasCreditoEnMemoria.size());
    }
    
    /**
     * Genera una Nota de Crédito Electrónica completa
     */
    /**
     * Genera una Nota de Crédito Electrónica completa
     * CORREGIDO: Ahora siempre intenta liberar la cotización asociada.
     */
    public String generarNotaCredito(NotaCredito nc) throws Exception {
        System.out.println("📄 Generando Nota de Crédito...");

        // 1. Validaciones
        validarDatosNotaCredito(nc);

        // 2. Asignar serie y número correlativo (en memoria)
        nc.setSerie(serieNotaCredito);
        contadorNumero++;
        nc.setNumero(contadorNumero);

        System.out.println("🛠 [Service] Serie asignada: " + nc.getSerie() + "-" + nc.getNumero());

        // 3. Calcular totales (si no están calculados)
        if (nc.getSubtotal() == 0) calcularTotales(nc);

        // 4. Generar XML
        System.out.println("🛠 [Service] Generando XML...");
        if (xmlGeneratorService == null)
            System.err.println("❌ [Service] xmlGeneratorService es NULL");
        String rutaXml = xmlGeneratorService.generarXMLNotaCredito(nc);
        System.out.println("✅ [Service] XML generado en: " + rutaXml);
        nc.setRutaXml(rutaXml);

        // 5. Generar PDF
        System.out.println("🛠 [Service] Generando PDF...");
        if (pdfGeneratorService == null)
            System.err.println("❌ [Service] pdfGeneratorService es NULL");
        String rutaPdf = pdfGeneratorService.generarPdfNotaCredito(nc);
        System.out.println("✅ [Service] PDF generado en: " + rutaPdf);
        nc.setRutaPdf(rutaPdf);

        // 6. Enviar a SUNAT (Simulado)
        System.out.println("🛠 [Service] Enviando a SUNAT...");
        if (sunatApiService == null)
            System.err.println("❌ [Service] sunatApiService es NULL");
        
        // Nota: Asegúrate de tener importado SunatApiService.RespuestaSUNAT
        SunatApiService.RespuestaSUNAT respuesta = sunatApiService.enviarNotaCredito(rutaXml);

        String mensaje;
        if (respuesta.isAceptado()) {
            nc.setEstadoSunat("ACEPTADO");
            nc.setCdr(respuesta.getCdr());
            System.out.println("✓ Aceptado por SUNAT (simulado)");
            mensaje = "Nota de Crédito generada y ACEPTADA por SUNAT.\nCDR: " + respuesta.getCdr();
        } else {
            nc.setEstadoSunat("RECHAZADO");
            mensaje = "Nota de Crédito generada pero RECHAZADA por SUNAT.\nMotivo: " + respuesta.getMensajeRespuesta();
        }
        System.out.println("✅ [Service] Respuesta SUNAT procesada: " + nc.getEstadoSunat());

        // 7. Guardar en memoria y persistir JSON
        notasCreditoEnMemoria.add(nc);
        jsonService.guardarNotasCredito(notasCreditoEnMemoria);

        // -----------------------------------------------------------------------
        // 8. LIBERAR COTIZACIÓN (CORREGIDO)
        // Antes había un IF que solo entraba si era "ANULACION". 
        // Lo hemos quitado para que SIEMPRE intente liberar la cotización si hay referencia.
        // -----------------------------------------------------------------------
        if (nc.getNumeroFacturaRef() != null && !nc.getNumeroFacturaRef().trim().isEmpty()) {
            System.out.println("🔄 [Service] Notificando al controlador para liberar cotización ref: " + nc.getNumeroFacturaRef());
            // Esto llamará al método blindado que pusimos en CotizacionController
            cotizacionController.anularCotizacionPorFactura(nc.getNumeroFacturaRef());
        }
        // -----------------------------------------------------------------------

        // 9. Mensaje de éxito final para la vista
        mensaje = String.format(
                "✓ Nota de Crédito Generada Exitosamente\n\n" +
                        "Serie-Número: %s-%08d\n" +
                        "Factura Referencia: %s\n" +
                        "Cliente: %s\n" +
                        "Total: S/ %.2f\n" +
                        "Estado SUNAT: %s\n\n" +
                        "Archivos generados:\n" +
                        "• XML: %s\n" +
                        "• PDF: %s",
                nc.getSerie(),
                nc.getNumero(),
                nc.getNumeroFacturaRef(),
                nc.getRazonSocialCliente(),
                nc.getTotal(),
                nc.getEstadoSunat(),
                rutaXml,
                rutaPdf);
            
        return mensaje;
    }

    /**
     * Valida los datos de la Nota de Crédito
     */
    public void validarDatosNotaCredito(NotaCredito nc) {
        List<String> errores = new ArrayList<>();

        if (nc.getNumeroFacturaRef() == null || nc.getNumeroFacturaRef().trim().isEmpty()) {
            errores.add("El número de factura de referencia es obligatorio");
        }

        if (nc.getMotivoSustento() == null || nc.getMotivoSustento().trim().isEmpty()) {
            errores.add("El motivo o sustento es obligatorio");
        } else if (nc.getMotivoSustento().trim().length() < 10) {
            errores.add("El motivo debe tener al menos 10 caracteres");
        }

        String[] tiposValidos = {
                "ANULACION DE LA OPERACION",
                "ANULACION POR ERROR EN EL RUC",
                "CORRECCION POR ERROR EN LA DESCRIPCION",
                "DESCUENTO GLOBAL",
                "DEVOLUCION TOTAL",
                "DEVOLUCION POR ITEM"
        };

        // Normalizar a mayúsculas para comparar
        String tipoActual = nc.getTipoNotaCredito() != null ? nc.getTipoNotaCredito().toUpperCase() : "";
        boolean tipoValido = Arrays.asList(tiposValidos).contains(tipoActual);

        // Si no coincide exactamente, permitimos pasar si no es vacío (flexibilidad)
        if (!tipoValido && tipoActual.isEmpty()) {
            errores.add("El tipo de nota de crédito no es válido");
        }

        if (nc.getItems() == null || nc.getItems().isEmpty()) {
            errores.add("La nota de crédito debe tener al menos un ítem");
        }

        if (!errores.isEmpty()) {
            throw new IllegalArgumentException("Errores de validación:\n" + String.join("\n", errores));
        }
    }

    /**
     * Calcula los totales de la NC
     */
    public void calcularTotales(NotaCredito nc) {
        double subtotal = 0.0;
        for (ItemNotaCredito item : nc.getItems()) {
            subtotal += item.getSubtotal();
        }
        nc.setSubtotal(subtotal);
        nc.setIgv(subtotal * 0.18);
        nc.setTotal(subtotal + (subtotal * 0.18));
    }

    /**
     * Obtiene todas las NC almacenadas en memoria
     */
    public List<NotaCredito> obtenerTodasLasNC() {
        return notasCreditoEnMemoria;
    }
    /**
     * Busca una NC por serie y número
     */
    public NotaCredito buscarPorSerieNumero(String serie, Integer numero) {
        return notasCreditoEnMemoria.stream()
                .filter(nc -> nc.getSerie().equals(serie) && nc.getNumero() == numero)
                .findFirst()
                .orElse(null);
    }
}
