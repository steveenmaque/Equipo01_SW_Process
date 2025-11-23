package com.inflesusventas.service;

import com.inflesusventas.model.Cotizacion;
import com.inflesusventas.model.Cotizacion;
import com.inflesusventas.model.ProductoCotizacion;
import com.inflesusventas.model.NotaCredito;
import com.inflesusventas.model.ItemNotaCredito;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Servicio para generar archivos XML de facturas electrónicas
 * Compatible con estándar UBL 2.1 (SUNAT Perú)
 * 
 * Ruta: src/main/java/com/inflesusventas/service/XMLGeneratorService.java
 */
@Service
public class XmlGeneratorService {

    private static final String DIRECTORIO_XML = "documentos/xml/facturas/";
    private static final String CONFIG_FILE = "src/main/resources/config/empresa.properties";

    private Properties configuracion;

    public XmlGeneratorService() {
        crearDirectorios();
        cargarConfiguracion();
    }

    /**
     * Crea los directorios necesarios para XMLs
     */
    private void crearDirectorios() {
        try {
            Files.createDirectories(Paths.get(DIRECTORIO_XML));
            System.out.println("✓ Directorios XML creados");
        } catch (Exception e) {
            System.err.println("✗ Error al crear directorios XML: " + e.getMessage());
        }
    }

    /**
     * Carga configuración de la empresa
     */
    private void cargarConfiguracion() {
        configuracion = new Properties();
        try {
            File configFile = new File(CONFIG_FILE);
            if (configFile.exists()) {
                configuracion.load(new FileInputStream(configFile));
                System.out.println("✓ Configuración cargada para XML");
            } else {
                // Valores por defecto
                configuracion.setProperty("empresa.ruc", "20123456789");
                configuracion.setProperty("empresa.razon_social", "InfleSusVentas SRL");
                configuracion.setProperty("empresa.direccion", "Av. Ejemplo 123, Lima, Perú");
                System.out.println("⚠ Usando configuración por defecto");
            }
        } catch (Exception e) {
            System.err.println("✗ Error al cargar configuración: " + e.getMessage());
        }
    }

    /**
     * Genera XML de factura electrónica desde una cotización
     * 
     * @param cotizacion   Cotización base
     * @param datosFactura Datos adicionales de la factura (forma pago, detracción,
     *                     etc.)
     * @return Ruta completa del archivo XML generado
     */
    public String generarXMLFactura(Cotizacion cotizacion, DatosFacturaElectronica datosFactura) throws Exception {

        // Nombre del archivo
        String nombreArchivo = String.format("%s-%s-%08d.xml",
                datosFactura.rucEmisor,
                datosFactura.serie,
                datosFactura.numero);

        String rutaCompleta = DIRECTORIO_XML + nombreArchivo;

        // Crear documento XML
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        // Elemento raíz: Invoice (según UBL 2.1)
        Element invoice = doc.createElement("Invoice");
        doc.appendChild(invoice);

        // Namespace UBL 2.1
        invoice.setAttribute("xmlns", "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2");
        invoice.setAttribute("xmlns:cac", "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2");
        invoice.setAttribute("xmlns:cbc", "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2");

        // UBLVersionID
        agregarElemento(doc, invoice, "cbc:UBLVersionID", "2.1");

        // ID de la factura (Serie-Número)
        agregarElemento(doc, invoice, "cbc:ID", datosFactura.serie + "-" + String.format("%08d", datosFactura.numero));

        // Fecha de emisión
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        agregarElemento(doc, invoice, "cbc:IssueDate", sdf.format(datosFactura.fechaEmision));

        // Tipo de factura (01 = Factura)
        agregarElemento(doc, invoice, "cbc:InvoiceTypeCode", "01");

        // Moneda
        agregarElemento(doc, invoice, "cbc:DocumentCurrencyCode", datosFactura.moneda);

        // === EMISOR (AccountingSupplierParty) ===
        Element emisor = doc.createElement("cac:AccountingSupplierParty");
        invoice.appendChild(emisor);

        Element partyEmisor = doc.createElement("cac:Party");
        emisor.appendChild(partyEmisor);

        // RUC del emisor
        Element partyIdEmisor = doc.createElement("cac:PartyIdentification");
        partyEmisor.appendChild(partyIdEmisor);
        agregarElemento(doc, partyIdEmisor, "cbc:ID", datosFactura.rucEmisor);

        // Razón social del emisor
        Element partyNameEmisor = doc.createElement("cac:PartyName");
        partyEmisor.appendChild(partyNameEmisor);
        agregarElemento(doc, partyNameEmisor, "cbc:Name", datosFactura.razonSocialEmisor);

        // === CLIENTE (AccountingCustomerParty) ===
        Element cliente = doc.createElement("cac:AccountingCustomerParty");
        invoice.appendChild(cliente);

        Element partyCliente = doc.createElement("cac:Party");
        cliente.appendChild(partyCliente);

        // RUC del cliente
        Element partyIdCliente = doc.createElement("cac:PartyIdentification");
        partyCliente.appendChild(partyIdCliente);
        agregarElemento(doc, partyIdCliente, "cbc:ID", datosFactura.rucCliente);

        // Razón social del cliente
        Element partyNameCliente = doc.createElement("cac:PartyName");
        partyCliente.appendChild(partyNameCliente);
        agregarElemento(doc, partyNameCliente, "cbc:Name", datosFactura.razonSocialCliente);

        // === FORMA DE PAGO ===
        Element paymentMeans = doc.createElement("cac:PaymentMeans");
        invoice.appendChild(paymentMeans);
        agregarElemento(doc, paymentMeans, "cbc:PaymentMeansCode", datosFactura.esCredito ? "Credito" : "Contado");

        // Si es crédito, agregar términos de pago
        if (datosFactura.esCredito) {
            Element paymentTerms = doc.createElement("cac:PaymentTerms");
            invoice.appendChild(paymentTerms);
            agregarElemento(doc, paymentTerms, "cbc:PaymentDueDate", sdf.format(datosFactura.fechaVencimiento));
            agregarElemento(doc, paymentTerms, "cbc:Amount", String.format("%.2f", datosFactura.montoCuota));
        }

        // === DETRACCIÓN (si aplica) ===
        if (datosFactura.montoDetraccion > 0) {
            Element detraccion = doc.createElement("cac:PaymentTerms");
            invoice.appendChild(detraccion);
            agregarElemento(doc, detraccion, "cbc:ID", "Detraccion");
            agregarElemento(doc, detraccion, "cbc:PaymentPercent",
                    String.format("%.2f", datosFactura.porcentajeDetraccion));
            agregarElemento(doc, detraccion, "cbc:Amount", String.format("%.2f", datosFactura.montoDetraccion));
            if (datosFactura.cuentaBancoNacion != null && !datosFactura.cuentaBancoNacion.isEmpty()) {
                agregarElemento(doc, detraccion, "cbc:PaymentMeansID", datosFactura.cuentaBancoNacion);
            }
        }

        // === ITEMS / LÍNEAS DE LA FACTURA ===
        int lineaNum = 1;
        for (ProductoCotizacion producto : cotizacion.getProductos()) {
            Element invoiceLine = doc.createElement("cac:InvoiceLine");
            invoice.appendChild(invoiceLine);

            // Número de línea
            agregarElemento(doc, invoiceLine, "cbc:ID", String.valueOf(lineaNum++));

            // Cantidad
            Element quantity = doc.createElement("cbc:InvoicedQuantity");
            quantity.setAttribute("unitCode", producto.getUnidadMedida() != null ? producto.getUnidadMedida() : "NIU");
            quantity.setTextContent(String.valueOf(producto.getCantidad()));
            invoiceLine.appendChild(quantity);

            // Monto de la línea (con IGV)
            agregarElemento(doc, invoiceLine, "cbc:LineExtensionAmount", String.format("%.2f", producto.getSubtotal()));

            // Precio unitario
            Element price = doc.createElement("cac:Price");
            invoiceLine.appendChild(price);
            agregarElemento(doc, price, "cbc:PriceAmount", String.format("%.2f", producto.getPrecioBase()));

            // Descripción del item
            Element item = doc.createElement("cac:Item");
            invoiceLine.appendChild(item);
            agregarElemento(doc, item, "cbc:Description", producto.getDescripcion());

            // Código del producto (si existe)
            if (producto.getCodigo() != null && !producto.getCodigo().isEmpty()) {
                Element sellersItemId = doc.createElement("cac:SellersItemIdentification");
                item.appendChild(sellersItemId);
                agregarElemento(doc, sellersItemId, "cbc:ID", producto.getCodigo());
            }
        }

        // === TOTALES ===
        Element legalMonetaryTotal = doc.createElement("cac:LegalMonetaryTotal");
        invoice.appendChild(legalMonetaryTotal);

        // Subtotal (sin IGV)
        agregarElemento(doc, legalMonetaryTotal, "cbc:LineExtensionAmount",
                String.format("%.2f", cotizacion.getSubtotal()));

        // Total con impuestos
        agregarElemento(doc, legalMonetaryTotal, "cbc:TaxInclusiveAmount",
                String.format("%.2f", cotizacion.getTotal()));

        // Monto a pagar (después de detracción si aplica)
        double montoPagar = cotizacion.getTotal() - datosFactura.montoDetraccion;
        agregarElemento(doc, legalMonetaryTotal, "cbc:PayableAmount",
                String.format("%.2f", montoPagar));

        // === IMPUESTOS (IGV) ===
        Element taxTotal = doc.createElement("cac:TaxTotal");
        invoice.appendChild(taxTotal);

        agregarElemento(doc, taxTotal, "cbc:TaxAmount", String.format("%.2f", cotizacion.getIGV()));

        Element taxSubtotal = doc.createElement("cac:TaxSubtotal");
        taxTotal.appendChild(taxSubtotal);

        agregarElemento(doc, taxSubtotal, "cbc:TaxableAmount", String.format("%.2f", cotizacion.getSubtotal()));
        agregarElemento(doc, taxSubtotal, "cbc:TaxAmount", String.format("%.2f", cotizacion.getIGV()));

        Element taxCategory = doc.createElement("cac:TaxCategory");
        taxSubtotal.appendChild(taxCategory);

        Element taxScheme = doc.createElement("cac:TaxScheme");
        taxCategory.appendChild(taxScheme);
        agregarElemento(doc, taxScheme, "cbc:ID", "1000"); // Código IGV
        agregarElemento(doc, taxScheme, "cbc:Name", "IGV");
        agregarElemento(doc, taxScheme, "cbc:TaxTypeCode", "VAT");

        // Escribir el XML a archivo
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(rutaCompleta));
        transformer.transform(source, result);

        System.out.println("✓ XML generado: " + nombreArchivo);
        return rutaCompleta;
    }

    /**
     * Genera XML de Nota de Crédito
     */
    public String generarXMLNotaCredito(NotaCredito nc) throws Exception {
        String nombreArchivo = String.format("%s-%s-%08d.xml",
                "20554524051", // RUC Emisor fijo por ahora
                nc.getSerie(),
                nc.getNumero());

        String rutaCompleta = "documentos/xml/notas_credito/" + nombreArchivo;
        Files.createDirectories(Paths.get("documentos/xml/notas_credito/"));

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        // Root: CreditNote
        Element root = doc.createElement("CreditNote");
        doc.appendChild(root);

        root.setAttribute("xmlns", "urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2");
        root.setAttribute("xmlns:cac", "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2");
        root.setAttribute("xmlns:cbc", "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2");

        agregarElemento(doc, root, "cbc:UBLVersionID", "2.1");
        agregarElemento(doc, root, "cbc:CustomizationID", "2.0");
        agregarElemento(doc, root, "cbc:ID", nc.getSerie() + "-" + String.format("%08d", nc.getNumero()));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaEmision = (nc.getFechaEmision() != null) ? nc.getFechaEmision().toString() : sdf.format(new Date());
        agregarElemento(doc, root, "cbc:IssueDate", fechaEmision);

        agregarElemento(doc, root, "cbc:DocumentCurrencyCode", nc.getMoneda() != null ? nc.getMoneda() : "PEN");

        // DiscrepancyResponse (Motivo)
        Element discrepancy = doc.createElement("cac:DiscrepancyResponse");
        root.appendChild(discrepancy);
        agregarElemento(doc, discrepancy, "cbc:ReferenceID", nc.getNumeroFacturaRef());
        agregarElemento(doc, discrepancy, "cbc:ResponseCode", "01"); // Código genérico anulación
        agregarElemento(doc, discrepancy, "cbc:Description", nc.getMotivoSustento());

        // Documento Referencia
        Element billingRef = doc.createElement("cac:BillingReference");
        root.appendChild(billingRef);
        Element invoiceRef = doc.createElement("cac:InvoiceDocumentReference");
        billingRef.appendChild(invoiceRef);
        agregarElemento(doc, invoiceRef, "cbc:ID", nc.getNumeroFacturaRef());
        agregarElemento(doc, invoiceRef, "cbc:DocumentTypeCode", "01");

        // Emisor
        Element emisor = doc.createElement("cac:AccountingSupplierParty");
        root.appendChild(emisor);
        Element partyEmisor = doc.createElement("cac:Party");
        emisor.appendChild(partyEmisor);
        Element partyIdEmisor = doc.createElement("cac:PartyIdentification");
        partyEmisor.appendChild(partyIdEmisor);
        agregarElemento(doc, partyIdEmisor, "cbc:ID", "20554524051");
        Element partyNameEmisor = doc.createElement("cac:PartyName");
        partyEmisor.appendChild(partyNameEmisor);
        agregarElemento(doc, partyNameEmisor, "cbc:Name", "INFLE SUS VENTAS S.R.L.");

        // Cliente
        Element cliente = doc.createElement("cac:AccountingCustomerParty");
        root.appendChild(cliente);
        Element partyCliente = doc.createElement("cac:Party");
        cliente.appendChild(partyCliente);
        Element partyIdCliente = doc.createElement("cac:PartyIdentification");
        partyCliente.appendChild(partyIdCliente);
        agregarElemento(doc, partyIdCliente, "cbc:ID", nc.getRucCliente());
        Element partyNameCliente = doc.createElement("cac:PartyName");
        partyCliente.appendChild(partyNameCliente);
        agregarElemento(doc, partyNameCliente, "cbc:Name", nc.getRazonSocialCliente());

        // Totales
        Element legalMonetaryTotal = doc.createElement("cac:LegalMonetaryTotal");
        root.appendChild(legalMonetaryTotal);
        agregarElemento(doc, legalMonetaryTotal, "cbc:PayableAmount", String.format("%.2f", nc.getTotal()));

        // Items
        int idLinea = 1;
        for (ItemNotaCredito item : nc.getItems()) {
            Element line = doc.createElement("cac:CreditNoteLine");
            root.appendChild(line);

            agregarElemento(doc, line, "cbc:ID", String.valueOf(idLinea++));

            Element quantity = doc.createElement("cbc:CreditedQuantity");
            quantity.setAttribute("unitCode", item.getUnidadMedida() != null ? item.getUnidadMedida() : "NIU");
            quantity.setTextContent(String.valueOf(item.getCantidad()));
            line.appendChild(quantity);

            agregarElemento(doc, line, "cbc:LineExtensionAmount", String.format("%.2f", item.getSubtotal()));

            Element pricing = doc.createElement("cac:PricingReference");
            line.appendChild(pricing);
            Element altCondition = doc.createElement("cac:AlternativeConditionPrice");
            pricing.appendChild(altCondition);
            Element priceAmount = doc.createElement("cbc:PriceAmount");
            priceAmount.setAttribute("currencyID", nc.getMoneda() != null ? nc.getMoneda() : "PEN");
            priceAmount.setTextContent(String.format("%.2f", item.getValorUnitario()));
            altCondition.appendChild(priceAmount);

            Element itemTag = doc.createElement("cac:Item");
            line.appendChild(itemTag);
            agregarElemento(doc, itemTag, "cbc:Description", item.formatearDescripcionParaPDF());

            Element price = doc.createElement("cac:Price");
            line.appendChild(price);
            agregarElemento(doc, price, "cbc:PriceAmount", String.format("%.2f", item.getValorUnitario()));
        }

        // Guardar
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(rutaCompleta));
        transformer.transform(source, result);

        return rutaCompleta;
    }

    /**
     * Método auxiliar para agregar elementos XML
     */
    private void agregarElemento(Document doc, Element padre, String nombreElemento, String valor) {
        Element elemento = doc.createElement(nombreElemento);
        elemento.setTextContent(valor);
        padre.appendChild(elemento);
    }

    /**
     * Clase interna para datos de factura electrónica
     */
    public static class DatosFacturaElectronica {
        // Identificación
        public String serie = "F001";
        public int numero = 1;
        public Date fechaEmision = new Date();
        public String moneda = "PEN"; // PEN, USD, EUR

        // Emisor
        public String rucEmisor;
        public String razonSocialEmisor;

        // Cliente
        public String rucCliente;
        public String razonSocialCliente;

        // Detracción
        public double porcentajeDetraccion = 0;
        public double montoDetraccion = 0;
        public String cuentaBancoNacion = "";

        // Crédito
        public boolean esCredito = false;
        public double montoNetoPendiente = 0;
        public double montoCuota = 0;
        public Date fechaVencimiento = new Date();
    }
}