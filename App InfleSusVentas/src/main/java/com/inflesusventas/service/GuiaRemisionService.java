package com.inflesusventas.service;

import com.inflesusventas.model.GuiaRemision;
import com.inflesusventas.model.BienGuiaRemision;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

/**
 * Servicio para generación de documentos de Guía de Remisión
 * Ruta: src/main/java/com/inflesusventas/service/GuiaRemisionService.java
 */
@Service
public class GuiaRemisionService {

    private static final String DIR_XML = "documentos/guias_remision/xml/";
    private static final String DIR_PDF = "documentos/guias_remision/pdf/";

    public GuiaRemisionService() {
        crearDirectorios();
    }

    /**
     * Crea los directorios necesarios
     */
    private void crearDirectorios() {
        try {
            Files.createDirectories(Paths.get(DIR_XML));
            Files.createDirectories(Paths.get(DIR_PDF));
            System.out.println("✓ Directorios de guías de remisión creados");
        } catch (IOException e) {
            System.err.println("✗ Error al crear directorios: " + e.getMessage());
        }
    }

    /**
     * Genera el XML de la guía de remisión según formato SUNAT
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
        xml.append("        <ID schemeID=\"").append(guia.getTipoDocumentoDestinatario().getCodigo()).append("\">")
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
        xml.append("    <GrossWeightMeasure unitCode=\"KGM\">").append(String.format("%.2f", pesoTotal)).append("</GrossWeightMeasure>\n");

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

        System.out.println("✓ XML guardado: " + rutaCompleta);
        return rutaCompleta;
    }

    /**
     * Genera el PDF de la guía de remisión (versión simplificada)
     */
    public String generarPDF(GuiaRemision guia) throws IOException {
        String nombreArchivo = guia.getSerieNumero().replace("-", "") + ".pdf";
        String rutaCompleta = DIR_PDF + nombreArchivo;

        // TODO: Implementar generación de PDF con iText (similar a PdfGeneratorService)
        // Por ahora, generamos un archivo de texto con la información

        StringBuilder contenido = new StringBuilder();
        contenido.append("═══════════════════════════════════════════════════════════\n");
        contenido.append("              GUÍA DE REMISIÓN ELECTRÓNICA\n");
        contenido.append("═══════════════════════════════════════════════════════════\n\n");

        contenido.append("Nº: ").append(guia.getSerieNumero()).append("\n");
        contenido.append("Fecha de Emisión: ").append(guia.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
        contenido.append("Motivo de Traslado: ").append(guia.getMotivoTraslado().getDescripcion()).append("\n\n");

        contenido.append("───────────────────────────────────────────────────────────\n");
        contenido.append("REMITENTE:\n");
        contenido.append("───────────────────────────────────────────────────────────\n");
        contenido.append("RUC: ").append(guia.getRucRemitente()).append("\n");
        contenido.append("Razón Social: ").append(guia.getRazonSocialRemitente()).append("\n\n");

        contenido.append("───────────────────────────────────────────────────────────\n");
        contenido.append("DESTINATARIO:\n");
        contenido.append("───────────────────────────────────────────────────────────\n");
        contenido.append("Tipo Doc.: ").append(guia.getTipoDocumentoDestinatario().getDescripcion()).append("\n");
        contenido.append("Nº Documento: ").append(guia.getNumeroDocumentoDestinatario()).append("\n");
        contenido.append("Razón Social: ").append(guia.getRazonSocialDestinatario()).append("\n\n");

        contenido.append("───────────────────────────────────────────────────────────\n");
        contenido.append("BIENES A TRASLADAR:\n");
        contenido.append("───────────────────────────────────────────────────────────\n");
        for (BienGuiaRemision bien : guia.getBienes()) {
            contenido.append(String.format("• %s - %s (Cant: %d, Peso: %.2f KG)\n",
                    bien.getCodigoBien(),
                    bien.getDescripcionDetallada(),
                    bien.getCantidad(),
                    bien.getPesoBrutoTotal()));
        }
        contenido.append("\nPeso Total: ").append(String.format("%.2f KG", guia.getPesoTotalCarga())).append("\n\n");

        contenido.append("───────────────────────────────────────────────────────────\n");
        contenido.append("DATOS DE TRASLADO:\n");
        contenido.append("───────────────────────────────────────────────────────────\n");
        contenido.append("Punto de Partida: ").append(guia.getPuntoPartida()).append("\n");
        contenido.append("Punto de Llegada: ").append(guia.getPuntoLlegada()).append("\n");
        contenido.append("Fecha de Inicio: ").append(guia.getDatosTransporte().getFechaInicioTraslado()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");

        contenido.append("───────────────────────────────────────────────────────────\n");
        contenido.append("DATOS DE TRANSPORTE:\n");
        contenido.append("───────────────────────────────────────────────────────────\n");
        contenido.append("Tipo: ").append(guia.getDatosTransporte().getTipoTransporte().getDescripcion()).append("\n");
        contenido.append("Placa: ").append(guia.getDatosTransporte().getNumeroPlaca()).append("\n");
        contenido.append("Conductor: ").append(guia.getDatosTransporte().getNombreConductor())
                .append(" ").append(guia.getDatosTransporte().getApellidosConductor()).append("\n");
        contenido.append("Licencia: ").append(guia.getDatosTransporte().getNumeroLicencia()).append("\n\n");

        contenido.append("═══════════════════════════════════════════════════════════\n");
        contenido.append("           InfleSusVentas SRL - Sistema de Gestión\n");
        contenido.append("═══════════════════════════════════════════════════════════\n");

        // Guardar como archivo de texto (cambiar extensión a .txt temporalmente)
        String rutaTxt = rutaCompleta.replace(".pdf", ".txt");
        Files.writeString(Paths.get(rutaTxt), contenido.toString());

        System.out.println("✓ Documento generado: " + rutaTxt);
        return rutaTxt;
    }
}