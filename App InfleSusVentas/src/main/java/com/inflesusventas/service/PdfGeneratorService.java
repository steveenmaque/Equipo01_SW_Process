package com.inflesusventas.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Date;
import org.springframework.stereotype.Service;

import com.inflesusventas.model.Cotizacion;
import com.inflesusventas.model.ProductoCotizacion;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;

/**
 * Servicio para generación de PDFs profesionales con diseño corporativo
 * Incluye: Logo, encabezados, tablas formateadas, colores corporativos
 * 
 * Ruta: src/main/java/com/inflesusventas/service/PdfGeneratorService.java
 */
@Service
public class PdfGeneratorService {
    
    private static final String DIRECTORIO_PDF = "documentos/pdf/cotizaciones/";
    private static final String CONFIG_FILE = "src/main/resources/config/empresa.properties";
    private static final String LOGO_PATH = "src/main/resources/images/logo_empresa.png";
    
    // Colores corporativos (se cargan desde properties)
    private Color colorPrimario = new DeviceRgb(102, 126, 234);  // #667eea
    private Color colorSecundario = new DeviceRgb(118, 75, 162); // #764ba2
    private Color colorGrisClaro = new DeviceRgb(248, 249, 250);
    
    private Properties configuracion;
    
    public PdfGeneratorService() {
        crearDirectorios();
        cargarConfiguracion();
    }
    
    /**
     * Crea los directorios necesarios para PDFs
     */
    private void crearDirectorios() {
        try {
            Files.createDirectories(Paths.get(DIRECTORIO_PDF));
            Files.createDirectories(Paths.get("src/main/resources/images"));
            System.out.println("✓ Directorios PDF creados");
        } catch (IOException e) {
            System.err.println("✗ Error al crear directorios PDF: " + e.getMessage());
        }
    }
    
    /**
     * Carga configuración de la empresa desde properties
     */
    private void cargarConfiguracion() {
        configuracion = new Properties();
        try {
            File configFile = new File(CONFIG_FILE);
            if (configFile.exists()) {
                configuracion.load(new FileInputStream(configFile));
                System.out.println("✓ Configuración de empresa cargada");
            } else {
                // Valores por defecto
                configuracion.setProperty("empresa.ruc", "20123456789");
                configuracion.setProperty("empresa.razon_social", "InfleSusVentas SRL");
                configuracion.setProperty("empresa.direccion", "Av. Ejemplo 123, Lima, Perú");
                configuracion.setProperty("empresa.telefono", "01-1234567");
                configuracion.setProperty("empresa.email", "ventas@inflesusventas.com");
                System.out.println("⚠ Usando configuración por defecto");
            }
        } catch (IOException e) {
            System.err.println("✗ Error al cargar configuración: " + e.getMessage());
        }
    }
    
    /**
     * Genera PDF profesional de cotización con diseño corporativo
     */
    public String generarPdfCotizacion(Cotizacion cotizacion) throws Exception {
        String nombreArchivo = String.format("COT-%04d_%s.pdf",
            cotizacion.getNumeroCotizacion(),
            cotizacion.getFecha().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        
        String rutaCompleta = DIRECTORIO_PDF + nombreArchivo;
        
        PdfWriter writer = new PdfWriter(rutaCompleta);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);
        
        // Márgenes ajustados para aprovechar espacio
        document.setMargins(30, 30, 30, 30);
        
        // 1. ENCABEZADO
        agregarEncabezadoConLogo(document, cotizacion);
        agregarLineaSeparadora(document, colorPrimario);
        
        // 2. DATOS
        agregarInformacionCotizacion(document, cotizacion);
        agregarDatosCliente(document, cotizacion);
        
        // 3. PRODUCTOS (Con descripción larga ajustada)
        agregarTablaProductos(document, cotizacion);
        
        // 4. MONTOS Y BANCOS (Lado a lado o secuencial)
        // Primero el resumen de dinero a la derecha
        agregarResumenMontos(document, cotizacion);
        
        // Luego la tabla de bancos (Clave para tu diseño)
        agregarDatosBancarios(document);
        
        // 5. PIE DE PÁGINA (Condiciones y Garantía)
        agregarCondicionesLegales(document, cotizacion);
        
        document.close();
        
        System.out.println("✓ PDF generado: " + nombreArchivo);
        return rutaCompleta;
    }
    
    /**
     * Agrega encabezado con logo de la empresa
     */
    private void agregarEncabezadoConLogo(Document document, Cotizacion cotizacion) {
        try {
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{2, 3}));
            headerTable.setWidth(UnitValue.createPercentValue(100));
            
            // Logo (lado izquierdo)
            Cell logoCell = new Cell();
            File logoFile = new File(LOGO_PATH);
            if (logoFile.exists()) {
                Image logo = new Image(ImageDataFactory.create(LOGO_PATH));
                logo.setWidth(120);
                logo.setHeight(60);
                logoCell.add(logo);
            } else {
                // Si no hay logo, mostrar nombre de la empresa
                Paragraph nombreEmpresa = new Paragraph(configuracion.getProperty("empresa.razon_social", "InfleSusVentas SRL"))
                    .setFontSize(18)
                    .setBold()
                    .setFontColor(colorPrimario);
                logoCell.add(nombreEmpresa);
            }
            logoCell.setBorder(Border.NO_BORDER);
            logoCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
            
            // Información de la empresa (lado derecho)
            Cell infoCell = new Cell();
            infoCell.add(new Paragraph(configuracion.getProperty("empresa.razon_social", "InfleSusVentas SRL"))
                .setFontSize(14)
                .setBold()
                .setFontColor(colorPrimario));
            infoCell.add(new Paragraph("RUC: " + configuracion.getProperty("empresa.ruc", "20123456789"))
                .setFontSize(10));
            infoCell.add(new Paragraph(configuracion.getProperty("empresa.direccion", ""))
                .setFontSize(9));
            infoCell.add(new Paragraph("Teléfono: " + configuracion.getProperty("empresa.telefono", ""))
                .setFontSize(9));
            infoCell.add(new Paragraph("Email: " + configuracion.getProperty("empresa.email", ""))
                .setFontSize(9));
            infoCell.setBorder(Border.NO_BORDER);
            infoCell.setTextAlignment(TextAlignment.RIGHT);
            infoCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
            
            headerTable.addCell(logoCell);
            headerTable.addCell(infoCell);
            
            document.add(headerTable);
            document.add(new Paragraph("\n"));
            
        } catch (Exception e) {
            System.err.println("✗ Error al agregar encabezado: " + e.getMessage());
        }
    }
    
    /**
     * Agrega línea separadora decorativa
     */
    

    private void agregarLineaSeparadora(Document document, Color color) {
        SolidLine linea = new SolidLine(1f); // 1f es el grosor de la línea
        linea.setColor(color);
        LineSeparator ls = new LineSeparator(linea);
        ls.setMarginTop(5); // Opcional: un poco de espacio arriba
        ls.setMarginBottom(5); // Opcional: un poco de espacio abajo

        document.add(ls);
    }
    
    /**
     * Agrega información general de la cotización
     */
    private void agregarInformacionCotizacion(Document document, Cotizacion cotizacion) {
        // Título centrado
        Paragraph titulo = new Paragraph("COTIZACIÓN DE VENTA")
            .setFontSize(20)
            .setBold()
            .setFontColor(colorPrimario)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(titulo);
        
        // Número y fecha
        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        infoTable.setWidth(UnitValue.createPercentValue(100));
        
        Cell nroCell = new Cell();
        nroCell.add(new Paragraph("Nº Cotización:")
            .setBold()
            .setFontSize(11));
        nroCell.add(new Paragraph(String.format("COT-%04d", cotizacion.getNumeroCotizacion()))
            .setFontSize(14)
            .setBold()
            .setFontColor(colorSecundario));
        nroCell.setBackgroundColor(colorGrisClaro);
        nroCell.setTextAlignment(TextAlignment.CENTER);
        nroCell.setPadding(10);
        
        Cell fechaCell = new Cell();
        fechaCell.add(new Paragraph("Fecha de Emisión:")
            .setBold()
            .setFontSize(11));
        fechaCell.add(new Paragraph(cotizacion.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
            .setFontSize(14)
            .setBold());
        fechaCell.setBackgroundColor(colorGrisClaro);
        fechaCell.setTextAlignment(TextAlignment.CENTER);
        fechaCell.setPadding(10);
        
        infoTable.addCell(nroCell);
        infoTable.addCell(fechaCell);
        
        document.add(infoTable);
        document.add(new Paragraph("\n"));
    }
    
    /**
     * Agrega datos del cliente
     */
    private void agregarDatosCliente(Document document, Cotizacion cotizacion) {
        Paragraph tituloCliente = new Paragraph("DATOS DEL CLIENTE")
            .setFontSize(12)
            .setBold()
            .setFontColor(colorPrimario);
        document.add(tituloCliente);
        
        Table clienteTable = new Table(UnitValue.createPercentArray(new float[]{1, 3}));
        clienteTable.setWidth(UnitValue.createPercentValue(100));
        
        agregarFilaCliente(clienteTable, "RUC:", cotizacion.getCliente().getRuc());
        agregarFilaCliente(clienteTable, "Razón Social:", cotizacion.getCliente().getRazonSocial());
        
        if (cotizacion.getCliente().getNombreContacto() != null) {
            agregarFilaCliente(clienteTable, "Contacto:", cotizacion.getCliente().getNombreContacto());
        }
        if (cotizacion.getCliente().getTelefono() != null) {
            agregarFilaCliente(clienteTable, "Teléfono:", cotizacion.getCliente().getTelefono());
        }
        if (cotizacion.getCliente().getEmail() != null) {
            agregarFilaCliente(clienteTable, "Email:", cotizacion.getCliente().getEmail());
        }
        
        document.add(clienteTable);
        document.add(new Paragraph("\n"));
    }
    
        private void agregarFilaCliente(Table table, String label, Object valor) {
        Cell labelCell = new Cell();
        labelCell.add(new Paragraph(label).setBold());
        labelCell.setBackgroundColor(colorGrisClaro);
        labelCell.setBorder(Border.NO_BORDER); // Opcional: se ve mejor sin bordes internos
        labelCell.setPadding(5); // Un poco menos de padding queda mejor

        Cell valorCell = new Cell();
        // CORRECCIÓN AQUÍ: Convertimos el objeto a String de forma segura
        String textoValor = (valor != null) ? valor.toString() : "-";
        valorCell.add(new Paragraph(textoValor));
        valorCell.setBorder(Border.NO_BORDER);
        valorCell.setPadding(5);

        table.addCell(labelCell);
        table.addCell(valorCell);
    }
        
    /**
     * Agrega tabla de productos con diseño profesional
     */
        private void agregarTablaProductos(Document document, Cotizacion cotizacion) {
            // Título
            document.add(new Paragraph("DETALLE DE PRODUCTOS / SERVICIOS")
                .setFontSize(11).setBold().setFontColor(colorPrimario));
            
            // ANCHOS AJUSTADOS: Damos 50% de ancho a la descripción para el texto largo
            // Columnas: Item, Cant, Und, Descripción, P.Unit, Total
            float[] columnWidths = {1, 1, 1, 6, 2, 2}; 
            Table productosTable = new Table(UnitValue.createPercentArray(columnWidths));
            productosTable.setWidth(UnitValue.createPercentValue(100));
            
            // Encabezados
            String[] headers = {"N.", "Cant.", "Und.", "Descripción Técnica", "P. Unit", "Total"};
            for (String header : headers) {
                Cell cell = new Cell().add(new Paragraph(header).setBold().setFontSize(9).setFontColor(ColorConstants.WHITE));
                cell.setBackgroundColor(colorPrimario);
                cell.setTextAlignment(TextAlignment.CENTER);
                productosTable.addHeaderCell(cell);
            }
            
            int item = 1;
            for (ProductoCotizacion producto : cotizacion.getProductos()) {
                // Filas con alineación TOP para que el texto largo se vea bien
                productosTable.addCell(crearCeldaCuerpo(String.valueOf(item++), TextAlignment.CENTER));
                productosTable.addCell(crearCeldaCuerpo(String.valueOf(producto.getCantidad()), TextAlignment.CENTER));
                productosTable.addCell(crearCeldaCuerpo("UND", TextAlignment.CENTER)); // O producto.getUnidad()
                
                // AQUÍ ESTÁ EL TRUCO: Alineación IZQUIERDA para el texto largo
                productosTable.addCell(crearCeldaCuerpo(producto.getDescripcion(), TextAlignment.LEFT));
                
                productosTable.addCell(crearCeldaCuerpo(String.format("S/ %.2f", producto.getPrecioBase()), TextAlignment.RIGHT));
                productosTable.addCell(crearCeldaCuerpo(String.format("S/ %.2f", producto.getSubtotal()), TextAlignment.RIGHT));
            }
            
            document.add(productosTable);
            document.add(new Paragraph("\n")); // Espacio
        }

        private void agregarDatosBancarios(Document document) {
        document.add(new Paragraph("INFORMACIÓN DE PAGO")
            .setFontSize(10).setBold().setFontColor(colorSecundario));

        // Tabla simple para bancos
        Table bancoTable = new Table(UnitValue.createPercentArray(new float[]{2, 3, 4}));
        bancoTable.setWidth(UnitValue.createPercentValue(100));
        
        // Estilo encabezado gris
        Color grisHeader = new DeviceRgb(240, 240, 240);
        String[] headers = {"Banco", "N° Cuenta", "Titular / Detalle"};
        for(String h : headers) {
            bancoTable.addHeaderCell(new Cell().add(new Paragraph(h).setBold().setFontSize(8))
                .setBackgroundColor(grisHeader));
        }

        // Fila 1: BBVA
        bancoTable.addCell(crearCeldaBanco(configuracion.getProperty("banco.bbva.nombre", "BBVA")));
        bancoTable.addCell(crearCeldaBanco(configuracion.getProperty("banco.bbva.cuenta", "-")));
        bancoTable.addCell(crearCeldaBanco(configuracion.getProperty("banco.bbva.titular", "")));

        // Fila 2: Nación
        bancoTable.addCell(crearCeldaBanco(configuracion.getProperty("banco.nacion.nombre", "Banco Nación")));
        bancoTable.addCell(crearCeldaBanco(configuracion.getProperty("banco.nacion.cuenta", "-")));
        bancoTable.addCell(crearCeldaBanco("Cuenta de Detracciones"));

        document.add(bancoTable);
        document.add(new Paragraph("\n"));
        }

        private void agregarCondicionesLegales(Document document, Cotizacion cotizacion) {
            // Contenedor visual (Cuadro gris claro)
            Table condicionesTable = new Table(1);
            condicionesTable.setWidth(UnitValue.createPercentValue(100));
            
            Cell cell = new Cell();
            cell.setBackgroundColor(new DeviceRgb(250, 250, 250)); // Gris muy suave
            cell.setBorder(Border.NO_BORDER);
            cell.setPadding(10);
            
            // 1. Validez y Tiempos (Negrita)
            cell.add(new Paragraph("CONDICIONES COMERCIALES:")
                .setBold().setFontSize(9).setFontColor(colorPrimario));
                
            cell.add(new Paragraph("• Validez de la oferta: " + configuracion.getProperty("condicion.validez_oferta", "30 días"))
                .setFontSize(8));
            cell.add(new Paragraph("• Tiempo de entrega: " + configuracion.getProperty("condicion.tiempo_entrega_defecto", "5-7 días"))
                .setFontSize(8));
                
            // 2. Garantía (Itálica)
            cell.add(new Paragraph("\n" + configuracion.getProperty("texto.garantia", ""))
                .setFontSize(8).setItalic());
                
            // 3. Requisitos Técnicos
            cell.add(new Paragraph("NOTAS IMPORTANTES:")
                .setBold().setFontSize(9).setMarginTop(5));
            
            cell.add(new Paragraph("- " + configuracion.getProperty("texto.requisito.electrico", ""))
                .setFontSize(8));
            cell.add(new Paragraph("- " + configuracion.getProperty("texto.requisito.arte", ""))
                .setFontSize(8));
            cell.add(new Paragraph("- " + configuracion.getProperty("texto.nota.motor", ""))
                .setFontSize(8));
                
            condicionesTable.addCell(cell);
            document.add(condicionesTable);
            
            // Firma y cierre final
            document.add(new Paragraph("\n\n" + configuracion.getProperty("empresa.representante", "Ventas"))
                .setTextAlignment(TextAlignment.CENTER).setBold());
            document.add(new Paragraph("Departamento de Ventas - " + configuracion.getProperty("empresa.nombre_comercial"))
                .setTextAlignment(TextAlignment.CENTER).setFontSize(8));
        }

    // Helper mejorado para celdas
    private Cell crearCeldaCuerpo(String texto, TextAlignment alineacion) {
        return new Cell()
            .add(new Paragraph(texto).setFontSize(9)) // Fuente un poco más chica
            .setTextAlignment(alineacion)
            .setPadding(4)
            .setVerticalAlignment(VerticalAlignment.TOP); // Clave para descripciones largas
    }
    
    private Cell crearCeldaProducto(String texto, TextAlignment alignment) {
        Cell cell = new Cell();
        cell.add(new Paragraph(texto));
        cell.setTextAlignment(alignment);
        cell.setPadding(8);
        return cell;
    }
    
    /**
     * Agrega resumen de montos
     */
    private void agregarResumenMontos(Document document, Cotizacion cotizacion) {
        Table resumenTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}));
        resumenTable.setWidth(UnitValue.createPercentValue(50));
        resumenTable.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.RIGHT);
        
        // Subtotal
        agregarFilaMonto(resumenTable, "Subtotal:", String.format("S/ %.2f", cotizacion.getSubtotal()), false);
        
        // IGV
        agregarFilaMonto(resumenTable, "IGV (18%):", String.format("S/ %.2f", cotizacion.getIGV()), false);
        
        // Total
        agregarFilaMonto(resumenTable, "TOTAL:", String.format("S/ %.2f", cotizacion.getTotal()), true);
        
        document.add(resumenTable);
        document.add(new Paragraph("\n"));
    }
    
    private void agregarFilaMonto(Table table, String label, String valor, boolean esTotal) {
        Cell labelCell = new Cell();
        labelCell.add(new Paragraph(label).setBold());
        if (esTotal) {
            labelCell.setBackgroundColor(colorPrimario);
            labelCell.setFontColor(ColorConstants.WHITE);
        }
        labelCell.setTextAlignment(TextAlignment.RIGHT);
        labelCell.setPadding(10);
        
        Cell valorCell = new Cell();
        Paragraph valorPara = new Paragraph(valor).setBold();
        if (esTotal) {
            valorPara.setFontSize(14);
            valorCell.setBackgroundColor(colorPrimario);
            valorCell.setFontColor(ColorConstants.WHITE);
        }
        valorCell.add(valorPara);
        valorCell.setTextAlignment(TextAlignment.RIGHT);
        valorCell.setPadding(10);
        
        table.addCell(labelCell);
        table.addCell(valorCell);
    }
    
    /**
     * Agrega condiciones y vigencia
     */
    private void agregarCondicionesVigencia(Document document, Cotizacion cotizacion) {
        Table condicionesTable = new Table(2);
        condicionesTable.setWidth(UnitValue.createPercentValue(100));
        
        Cell condPagoCell = new Cell();
        condPagoCell.add(new Paragraph("Condición de Pago:").setBold());
        condPagoCell.add(new Paragraph(cotizacion.getCondicionPago().getDescripcion()));
        condPagoCell.setBackgroundColor(colorGrisClaro);
        condPagoCell.setPadding(10);
        
        Cell vigenciaCell = new Cell();
        vigenciaCell.add(new Paragraph("Vigencia:").setBold());
        vigenciaCell.add(new Paragraph("Válida hasta el " + 
            cotizacion.getFechaVigencia().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        vigenciaCell.setBackgroundColor(colorGrisClaro);
        vigenciaCell.setPadding(10);
        
        condicionesTable.addCell(condPagoCell);
        condicionesTable.addCell(vigenciaCell);
        
        document.add(condicionesTable);
    }
    
    /**
     * Agrega pie de página
     */
    private void agregarPiePagina(Document document) {
        document.add(new Paragraph("\n\n"));
        
        Paragraph footer = new Paragraph(
            "InfleSusVentas SRL - Venta de Inflables Publicitarios\n" +
            configuracion.getProperty("empresa.direccion", "") + " | " +
            "Tel: " + configuracion.getProperty("empresa.telefono", "") + " | " +
            configuracion.getProperty("empresa.email", "")
        )
        .setFontSize(8)
        .setTextAlignment(TextAlignment.CENTER)
        .setFontColor(ColorConstants.GRAY);
        
        document.add(footer);
    }

    public void generarCotizacionPdf(Cotizacion cotizacionActual, String rutaCompleta) {
        // TODo Auto- generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generarCotizacionPdf'");
    }

    /**
     * Genera PDF de factura electrónica con diseño profesional
     * Reutiliza el estilo de las cotizaciones
     */
    public String generarPdfFactura(Cotizacion cotizacion, DatosFacturaPDF datosFactura) throws Exception {
        
        String nombreArchivo = String.format("%s-%s-%08d.pdf",
            datosFactura.rucEmisor,
            datosFactura.serie,
            datosFactura.numero);
        
        String rutaCompleta = "documentos/pdf/facturas/" + nombreArchivo;
        
        // Crear directorio si no existe
        Files.createDirectories(Paths.get("documentos/pdf/facturas/"));
        
        // Crear el PDF
        PdfWriter writer = new PdfWriter(rutaCompleta);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(40, 40, 40, 40);
        
        // === CONTENIDO ===
        agregarEncabezadoFactura(document, datosFactura);
        agregarLineaSeparadora(document, colorPrimario);
        agregarInformacionFactura(document, datosFactura);
        agregarDatosClienteFactura(document, cotizacion, datosFactura);
        agregarTablaProductosFactura(document, cotizacion);
        agregarResumenMontosFactura(document, cotizacion, datosFactura);
        
        // Detracción y Crédito (si aplican)
        if (datosFactura.montoDetraccion > 0) {
            agregarInfoDetraccion(document, datosFactura);
        }
        if (datosFactura.esCredito) {
            agregarInfoCredito(document, datosFactura);
        }
        
        agregarPieFactura(document);
        
        document.close();
        
        System.out.println("✓ PDF Factura generado: " + nombreArchivo);
        return rutaCompleta;
    }

    /**
     * Encabezado específico para facturas
     */
    private void agregarEncabezadoFactura(Document document, DatosFacturaPDF datos) {
        try {
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{2, 3}));
            headerTable.setWidth(UnitValue.createPercentValue(100));
            
            // Logo (reutiliza el código existente de cotizaciones)
            Cell logoCell = new Cell();
            File logoFile = new File(LOGO_PATH);
            if (logoFile.exists()) {
                Image logo = new Image(ImageDataFactory.create(LOGO_PATH));
                logo.setWidth(120);
                logo.setHeight(60);
                logoCell.add(logo);
            } else {
                Paragraph nombreEmpresa = new Paragraph(datos.razonSocialEmisor)
                    .setFontSize(18)
                    .setBold()
                    .setFontColor(colorPrimario);
                logoCell.add(nombreEmpresa);
            }
            logoCell.setBorder(Border.NO_BORDER);
            logoCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
            
            // Información de la empresa
            Cell infoCell = new Cell();
            infoCell.add(new Paragraph(datos.razonSocialEmisor)
                .setFontSize(14)
                .setBold()
                .setFontColor(colorPrimario));
            infoCell.add(new Paragraph("RUC: " + datos.rucEmisor).setFontSize(10));
            infoCell.add(new Paragraph(configuracion.getProperty("empresa.direccion", "")).setFontSize(9));
            infoCell.add(new Paragraph("Tel: " + configuracion.getProperty("empresa.telefono", "")).setFontSize(9));
            infoCell.add(new Paragraph("Email: " + configuracion.getProperty("empresa.email", "")).setFontSize(9));
            infoCell.setBorder(Border.NO_BORDER);
            infoCell.setTextAlignment(TextAlignment.RIGHT);
            infoCell.setVerticalAlignment(VerticalAlignment.MIDDLE);
            
            headerTable.addCell(logoCell);
            headerTable.addCell(infoCell);
            
            document.add(headerTable);
            document.add(new Paragraph("\n"));
            
        } catch (Exception e) {
            System.err.println("✗ Error al agregar encabezado: " + e.getMessage());
        }
    }

    /**
     * Información de la factura (similar a cotización pero con "FACTURA ELECTRÓNICA")
     */
    private void agregarInformacionFactura(Document document, DatosFacturaPDF datos) {
        // Título centrado
        Paragraph titulo = new Paragraph("FACTURA ELECTRÓNICA")
            .setFontSize(20)
            .setBold()
            .setFontColor(colorPrimario)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(titulo);
        
        // Número y fecha
        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        infoTable.setWidth(UnitValue.createPercentValue(100));
        
        Cell nroCell = new Cell();
        nroCell.add(new Paragraph("Serie - Número:")
            .setBold()
            .setFontSize(11));
        nroCell.add(new Paragraph(datos.serie + "-" + String.format("%08d", datos.numero))
            .setFontSize(14)
            .setBold()
            .setFontColor(colorSecundario));
        nroCell.setBackgroundColor(colorGrisClaro);
        nroCell.setTextAlignment(TextAlignment.CENTER);
        nroCell.setPadding(10);
        
        Cell fechaCell = new Cell();
        fechaCell.add(new Paragraph("Fecha de Emisión:")
            .setBold()
            .setFontSize(11));
        fechaCell.add(new Paragraph(new SimpleDateFormat("dd/MM/yyyy").format(datos.fechaEmision))
            .setFontSize(14)
            .setBold());
        fechaCell.setBackgroundColor(colorGrisClaro);
        fechaCell.setTextAlignment(TextAlignment.CENTER);
        fechaCell.setPadding(10);
        
        infoTable.addCell(nroCell);
        infoTable.addCell(fechaCell);
        
        document.add(infoTable);
        document.add(new Paragraph("\n"));
    }

    /**
     * Datos del cliente (adaptado para facturas)
     */
    private void agregarDatosClienteFactura(Document document, Cotizacion cotizacion, DatosFacturaPDF datos) {
        Paragraph tituloCliente = new Paragraph("DATOS DEL CLIENTE")
            .setFontSize(12)
            .setBold()
            .setFontColor(colorPrimario);
        document.add(tituloCliente);
        
        Table clienteTable = new Table(UnitValue.createPercentArray(new float[]{1, 3}));
        clienteTable.setWidth(UnitValue.createPercentValue(100));
        
        agregarFilaCliente(clienteTable, "RUC:", datos.rucCliente);
        agregarFilaCliente(clienteTable, "Razón Social:", datos.razonSocialCliente);
        
        if (cotizacion.getCliente().getNombreContacto() != null) {
            agregarFilaCliente(clienteTable, "Contacto:", cotizacion.getCliente().getNombreContacto());
        }
        if (cotizacion.getCliente().getTelefono() != null) {
            agregarFilaCliente(clienteTable, "Teléfono:", cotizacion.getCliente().getTelefono());
        }
        
        document.add(clienteTable);
        document.add(new Paragraph("\n"));
    }

    /**
     * Tabla de productos (reutiliza lógica de cotización)
     */
    private void agregarTablaProductosFactura(Document document, Cotizacion cotizacion) {
        Paragraph tituloProductos = new Paragraph("DETALLE DE LA FACTURA")
            .setFontSize(12)
            .setBold()
            .setFontColor(colorPrimario);
        document.add(tituloProductos);
        
        Table productosTable = new Table(UnitValue.createPercentArray(new float[]{1, 4, 1, 1.5f, 1.5f}));
        productosTable.setWidth(UnitValue.createPercentValue(100));
        
        // Encabezados
        String[] headers = {"Código", "Descripción", "Cant.", "V. Unit. (S/)", "Subtotal (S/)"};
        for (String header : headers) {
            Cell headerCell = new Cell();
            headerCell.add(new Paragraph(header).setBold().setFontColor(ColorConstants.WHITE));
            headerCell.setBackgroundColor(colorPrimario);
            headerCell.setTextAlignment(TextAlignment.CENTER);
            headerCell.setPadding(10);
            productosTable.addHeaderCell(headerCell);
        }
        
        // Filas de productos
        for (ProductoCotizacion producto : cotizacion.getProductos()) {
            productosTable.addCell(crearCeldaProducto(
                producto.getCodigo() != null ? producto.getCodigo() : "-", 
                TextAlignment.CENTER));
            productosTable.addCell(crearCeldaProducto(producto.getDescripcion(), TextAlignment.LEFT));
            productosTable.addCell(crearCeldaProducto(String.valueOf(producto.getCantidad()), TextAlignment.CENTER));
            productosTable.addCell(crearCeldaProducto(String.format("%.2f", producto.getPrecioBase()), TextAlignment.RIGHT));
            productosTable.addCell(crearCeldaProducto(String.format("%.2f", producto.getSubtotal()), TextAlignment.RIGHT));
        }
        
        document.add(productosTable);
        document.add(new Paragraph("\n"));
    }

    /**
     * Resumen de montos con detracción
     */
    private void agregarResumenMontosFactura(Document document, Cotizacion cotizacion, DatosFacturaPDF datos) {
        Table resumenTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}));
        resumenTable.setWidth(UnitValue.createPercentValue(50));
        resumenTable.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.RIGHT);
        
        // Subtotal
        agregarFilaMonto(resumenTable, "Subtotal:", String.format("S/ %.2f", cotizacion.getSubtotal()), false);
        
        // IGV
        agregarFilaMonto(resumenTable, "IGV (18%):", String.format("S/ %.2f", cotizacion.getIGV()), false);
        
        // Total
        agregarFilaMonto(resumenTable, "TOTAL:", String.format("S/ %.2f", cotizacion.getTotal()), true);
        
        // Detracción (si aplica)
        if (datos.montoDetraccion > 0) {
            agregarFilaMonto(resumenTable, "Detracción (" + String.format("%.0f", datos.porcentajeDetraccion) + "%):", 
                String.format("S/ %.2f", datos.montoDetraccion), false);
            
            double montoPagar = cotizacion.getTotal() - datos.montoDetraccion;
            agregarFilaMonto(resumenTable, "MONTO A PAGAR:", String.format("S/ %.2f", montoPagar), true);
        }
        
        document.add(resumenTable);
        document.add(new Paragraph("\n"));
    }

    /**
     * Información de detracción
     */
    private void agregarInfoDetraccion(Document document, DatosFacturaPDF datos) {
        Paragraph titulo = new Paragraph("INFORMACIÓN DE DETRACCIÓN")
            .setFontSize(11)
            .setBold()
            .setFontColor(colorSecundario);
        document.add(titulo);
        
        Table detTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}));
        detTable.setWidth(UnitValue.createPercentValue(70));
        
        agregarFilaCliente(detTable, "Tipo:", "037 - Demás servicios gravados con el IGV");
        agregarFilaCliente(detTable, "Porcentaje:", String.format("%.0f%%", datos.porcentajeDetraccion));
        agregarFilaCliente(detTable, "Monto:", String.format("S/ %.2f", datos.montoDetraccion));
        
        if (datos.cuentaBancoNacion != null && !datos.cuentaBancoNacion.isEmpty()) {
            agregarFilaCliente(detTable, "Cuenta BN:", datos.cuentaBancoNacion);
        }
        
        document.add(detTable);
        document.add(new Paragraph("\n"));
    }

        private void agregarCuentasBancarias(Document document) {
        // Título pequeño
        document.add(new Paragraph("Información de Pago:").setBold().setFontSize(10));

        // Tabla de 3 columnas: Banco, Cuenta, CCI (ajusta según necesites)
        Table bancoTable = new Table(UnitValue.createPercentArray(new float[]{2, 3, 3}));
        bancoTable.setWidth(UnitValue.createPercentValue(90)); // Un poco más angosta que el total
        
        // Estilo de encabezado de banco
        Color grisOscuro = new DeviceRgb(230, 230, 230);
        
        bancoTable.addHeaderCell(new Cell().add(new Paragraph("Banco").setBold().setFontSize(8)).setBackgroundColor(grisOscuro));
        bancoTable.addHeaderCell(new Cell().add(new Paragraph("N° Cuenta").setBold().setFontSize(8)).setBackgroundColor(grisOscuro));
        bancoTable.addHeaderCell(new Cell().add(new Paragraph("Titular").setBold().setFontSize(8)).setBackgroundColor(grisOscuro));

        // Fila 1: BBVA (Datos desde tu properties o hardcoded si son fijos)
        bancoTable.addCell(crearCeldaBanco("BBVA Continental"));
        bancoTable.addCell(crearCeldaBanco(configuracion.getProperty("empresa.banco.bbva", "0011-xxxx-xxxx"))); 
        bancoTable.addCell(crearCeldaBanco(configuracion.getProperty("empresa.razon_social")));

        // Fila 2: Banco de la Nación (Detracción)
        bancoTable.addCell(crearCeldaBanco("Banco de la Nación (Detracción)"));
        bancoTable.addCell(crearCeldaBanco(configuracion.getProperty("empresa.banco.nacion", "00-006-038646")));
        bancoTable.addCell(crearCeldaBanco(configuracion.getProperty("empresa.razon_social")));

        document.add(bancoTable);
        document.add(new Paragraph("\n"));
    }

        private Cell crearCeldaBanco(String texto) {
            return new Cell().add(new Paragraph(texto).setFontSize(8)).setPadding(3).setBorder(new SolidBorder(ColorConstants.GRAY, 0.5f));
        }

    /**
     * Información de crédito
     */
    private void agregarInfoCredito(Document document, DatosFacturaPDF datos) {
        Paragraph titulo = new Paragraph("CONDICIÓN DE PAGO: CRÉDITO")
            .setFontSize(11)
            .setBold()
            .setFontColor(colorSecundario);
        document.add(titulo);
        
        Table creditoTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}));
        creditoTable.setWidth(UnitValue.createPercentValue(60));
        
        agregarFilaCliente(creditoTable, "Monto Pendiente:", String.format("S/ %.2f", datos.montoNetoPendiente));
        agregarFilaCliente(creditoTable, "Monto Cuota:", String.format("S/ %.2f", datos.montoCuota));
        agregarFilaCliente(creditoTable, "Vencimiento:", 
            new SimpleDateFormat("dd/MM/yyyy").format(datos.fechaVencimiento));
        
        document.add(creditoTable);
        document.add(new Paragraph("\n"));
    }

    /**
     * Pie de página específico para facturas
     */
    private void agregarPieFactura(Document document) {
        document.add(new Paragraph("\n"));
        
        // Observaciones
        Paragraph obs = new Paragraph("Documento emitido de acuerdo a la normativa SUNAT vigente")
            .setFontSize(8)
            .setItalic()
            .setTextAlignment(TextAlignment.CENTER);
        document.add(obs);
        
        // Info de contacto
        Paragraph footer = new Paragraph(
            configuracion.getProperty("empresa.razon_social", "InfleSusVentas SRL") + "\n" +
            configuracion.getProperty("empresa.direccion", "") + " | " +
            "Tel: " + configuracion.getProperty("empresa.telefono", "") + " | " +
            configuracion.getProperty("empresa.email", "")
        )
        .setFontSize(8)
        .setTextAlignment(TextAlignment.CENTER)
        .setFontColor(ColorConstants.GRAY);
        
        document.add(footer);
    }

    /**
     * Clase interna para datos de factura en PDF
     */
    public static class DatosFacturaPDF {
        // Identificación
        public String serie = "F001";
        public int numero = 1;
        public Date fechaEmision = new Date();
        public String moneda = "PEN";
        
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