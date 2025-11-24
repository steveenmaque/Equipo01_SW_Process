package com.inflesusventas.service;

import com.inflesusventas.model.BienGuiaRemision;
import com.inflesusventas.model.GuiaRemision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

/**
 * Servicio para gestión de documentos de Guía de Remisión
 * - Genera XML (Lógica interna para SUNAT)
 * - Genera PDF (Delegando al PdfGeneratorService para diseño corporativo)
 */
@Service
public class GuiaRemisionService {

    private static final String DIR_XML = "App InfleSusVentas/documentos/guias_remision/xml/";
    
    // Inyectamos el servicio profesional de PDFs
    private final PdfGeneratorService pdfGeneratorService;

    @Autowired
    public GuiaRemisionService(PdfGeneratorService pdfGeneratorService) {
        this.pdfGeneratorService = pdfGeneratorService;
        crearDirectorios();
    }

    /**
     * Crea los directorios necesarios para los XML
     * (Los directorios de PDF los gestiona PdfGeneratorService)
     */
    private void crearDirectorios() {
        try {
            Files.createDirectories(Paths.get(DIR_XML));
            System.out.println("✓ Directorios de guías de remisión (XML) verificados");
        } catch (IOException e) {
            System.err.println("✗ Error al crear directorios: " + e.getMessage());
        }
    }

    /**
     * Genera el XML de la guía de remisión según formato UBL 2.1 (SUNAT)
     * Este método contiene toda la lógica de construcción del XML.
     */
    public String generarXML(GuiaRemision guia) throws IOException {
        String nombreArchivo = guia.getSerieNumero().replace("-", "") + ".xml";
        String rutaCompleta = DIR_XML + nombreArchivo;

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<DespatchAdvice xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:DespatchAdvice-2\">\n");

        // UBL Version
        xml.append("  <UBLVersionID>2.1</UBLVersionID>\n");
        xml.append("  <CustomizationID>2.0</CustomizationID>\n");

        // ID de la guía
        xml.append("  <ID>").append(guia.getSerieNumero()).append("</ID>\n");

        // Fecha de emisión
        String fechaEmision = guia.getFechaEmision().format(DateTimeFormatter.ISO_LOCAL_DATE);
        xml.append("  <IssueDate>").append(fechaEmision).append("</IssueDate>\n");

        // Tipo de documento: 09 (Guía de Remisión Remitente)
        xml.append("  <DespatchAdviceTypeCode>09</DespatchAdviceTypeCode>\n");

        // Motivo de traslado
        xml.append("  <Note>").append(guia.getMotivoTraslado().getDescripcion()).append("</Note>\n");

        // Remitente
        xml.append("  <DespatchSupplierParty>\n");
        xml.append("    <Party>\n");
        xml.append("      <PartyIdentification>\n");
        xml.append("        <ID schemeID=\"6\">").append(guia.getRucRemitente()).append("</ID>\n");
        xml.append("      </PartyIdentification>\n");
        xml.append("      <PartyLegalEntity>\n");
        xml.append("        <RegistrationName>").append(guia.getRazonSocialRemitente()).append("</RegistrationName>\n");
        xml.append("      </PartyLegalEntity>\n");
        xml.append("    </Party>\n");
        xml.append("  </DespatchSupplierParty>\n");

        // Destinatario
        xml.append("  <DeliveryCustomerParty>\n");
        xml.append("    <Party>\n");
        xml.append("      <PartyIdentification>\n");
        // Validación básica por si es null el tipo de documento
        String tipoDoc = (guia.getTipoDocumentoDestinatario() != null) ? guia.getTipoDocumentoDestinatario().getCodigo() : "6";
        xml.append("        <ID schemeID=\"").append(tipoDoc).append("\">")
                .append(guia.getNumeroDocumentoDestinatario()).append("</ID>\n");
        xml.append("      </PartyIdentification>\n");
        xml.append("      <PartyLegalEntity>\n");
        xml.append("        <RegistrationName>").append(guia.getRazonSocialDestinatario()).append("</RegistrationName>\n");
        xml.append("      </PartyLegalEntity>\n");
        xml.append("    </Party>\n");
        xml.append("  </DeliveryCustomerParty>\n");

        // Envío (Shipment)
        xml.append("  <Shipment>\n");
        xml.append("    <ID>1</ID>\n");

        // Peso bruto total
        double pesoTotal = guia.getPesoTotalCarga();
        xml.append("    <GrossWeightMeasure unitCode=\"KGM\">").append(String.format("%.2f", pesoTotal).replace(",", ".")).append("</GrossWeightMeasure>\n");

        // Punto de partida
        xml.append("    <OriginAddress>\n");
        xml.append("      <AddressLine><Line>").append(guia.getPuntoPartida()).append("</Line></AddressLine>\n");
        xml.append("    </OriginAddress>\n");

        // Punto de llegada
        xml.append("    <DeliveryAddress>\n");
        xml.append("      <AddressLine><Line>").append(guia.getPuntoLlegada()).append("</Line></AddressLine>\n");
        xml.append("    </DeliveryAddress>\n");

        // Datos de transporte
        xml.append("    <TransportHandlingUnit>\n");
        xml.append("      <TransportMeans>\n");
        xml.append("        <RoadTransport>\n");
        xml.append("          <LicensePlateID>").append(guia.getDatosTransporte().getNumeroPlaca()).append("</LicensePlateID>\n");
        xml.append("        </RoadTransport>\n");
        xml.append("      </TransportMeans>\n");
        xml.append("    </TransportHandlingUnit>\n");

        // Conductor
        xml.append("    <DriverPerson>\n");
        xml.append("      <ID schemeID=\"1\">").append(guia.getDatosTransporte().getDniConductor()).append("</ID>\n");
        xml.append("      <FirstName>").append(guia.getDatosTransporte().getNombreConductor()).append("</FirstName>\n");
        xml.append("      <FamilyName>").append(guia.getDatosTransporte().getApellidosConductor()).append("</FamilyName>\n");
        xml.append("      <JobTitle>Principal</JobTitle>\n");
        xml.append("      <IdentityDocumentReference>\n");
        xml.append("        <ID>").append(guia.getDatosTransporte().getNumeroLicencia()).append("</ID>\n");
        xml.append("      </IdentityDocumentReference>\n");
        xml.append("    </DriverPerson>\n");

        xml.append("  </Shipment>\n");

        // Líneas de detalle (bienes)
        for (int i = 0; i < guia.getBienes().size(); i++) {
            BienGuiaRemision bien = guia.getBienes().get(i);
            xml.append("  <DespatchLine>\n");
            xml.append("    <ID>").append(i + 1).append("</ID>\n");
            xml.append("    <DeliveredQuantity unitCode=\"").append(bien.getUnidadMedida()).append("\">")
                    .append(bien.getCantidad()).append("</DeliveredQuantity>\n");
            xml.append("    <Item>\n");
            xml.append("      <Description>").append(bien.getDescripcionDetallada()).append("</Description>\n");
            xml.append("      <SellersItemIdentification>\n");
            xml.append("        <ID>").append(bien.getCodigoBien()).append("</ID>\n");
            xml.append("      </SellersItemIdentification>\n");
            xml.append("    </Item>\n");
            xml.append("  </DespatchLine>\n");
        }

        xml.append("</DespatchAdvice>");

        // Escribir archivo
        try (FileWriter writer = new FileWriter(rutaCompleta)) {
            writer.write(xml.toString());
        }

        System.out.println("✓ XML guardado correctamente en: " + rutaCompleta);
        return rutaCompleta;
    }

    /**
     * Genera el PDF delegando la tarea al servicio profesional PdfGeneratorService.
     * Esto asegura que el diseño sea idéntico al de las cotizaciones y facturas.
     */
    public String generarPDF(GuiaRemision guia) throws IOException {
        try {
            // Aquí conectamos con el servicio que tiene los estilos corporativos
            System.out.println("🔄 Generando PDF corporativo para Guía: " + guia.getSerieNumero());
            return pdfGeneratorService.generarPdfGuiaRemision(guia);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Error crítico al generar PDF de Guía: " + e.getMessage());
        }
    }
}