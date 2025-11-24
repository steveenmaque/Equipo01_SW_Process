package com.inflesusventas.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Date;
import org.springframework.stereotype.Service;

import com.inflesusventas.model.Cotizacion;
import com.inflesusventas.model.ProductoCotizacion;
import com.inflesusventas.model.NotaCredito;
import com.inflesusventas.model.ItemNotaCredito;
import com.itextpdf.layout.properties.HorizontalAlignment;
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
    private Color colorPrimario = new DeviceRgb(102, 126, 234); // #667eea
    private Color colorSecundario = new DeviceRgb(118, 75, 162); // #764ba2
    private Color colorGrisClaro = new DeviceRgb(248, 249, 250);

    private Properties configuracion;

    public PdfGeneratorService() {
        crearDirectorios();
        cargarConfiguracion();
    }

    private void crearDirectorios() {
        try {
            // 1. Directorio principal para Cotizaciones (definido en la constante)
            Files.createDirectories(Paths.get(DIRECTORIO_PDF));
            
            // 2. Directorio para Notas de Crédito
            Files.createDirectories(Paths.get("documentos/pdf/notas_credito/"));

            // 3. Directorio para Facturas (por seguridad, para futuras facturas)
            Files.createDirectories(Paths.get("documentos/pdf/facturas/"));
            
            // 4. Directorio de recursos (donde debe estar tu logo)
            Files.createDirectories(Paths.get("src/main/resources/images"));
            
            System.out.println("✓ Directorios de almacenamiento PDF verificados y creados correctamente.");
        } catch (IOException e) {
            System.err.println("✗ Error crítico al crear los directorios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarConfiguracion() {
        configuracion = new Properties();
        try {
            File configFile = new File(CONFIG_FILE);
            if (configFile.exists()) {
                // Carga los datos reales del archivo empresa.properties
                configuracion.load(new FileInputStream(configFile));
                System.out.println("✓ Configuración de empresa cargada correctamente desde: " + CONFIG_FILE);
            } else {
                // Valores de respaldo por si se borra el archivo accidentalmente
                configuracion.setProperty("empresa.ruc", "20554542051");
                configuracion.setProperty("empresa.razon_social", "INFLE SUS VENTAS SRL");
                configuracion.setProperty("empresa.direccion", "Calle San Francisco Mz 1-4 Lt 13A CP. Zapallal Alto");
                System.out.println("⚠ ADVERTENCIA: No se encontró empresa.properties. Usando configuración básica por defecto.");
            }
        } catch (IOException e) {
            System.err.println("✗ Error crítico al cargar configuración: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Genera PDF de Cotización con el diseño específico solicitado (Estilo "Globo")
     */
    public String generarPdfCotizacion(Cotizacion cotizacion) throws Exception {
        // Definir nombre del archivo: COT-0001_20251123.pdf
        String nombreArchivo = String.format("COT-%04d_%s.pdf",
                cotizacion.getNumeroCotizacion(),
                cotizacion.getFecha().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        String rutaCompleta = DIRECTORIO_PDF + nombreArchivo;

        // Inicializar el escritor PDF
        PdfWriter writer = new PdfWriter(rutaCompleta);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(30, 30, 30, 30); // Márgenes estándar

        // 1. ENCABEZADO COMÚN (Logo y datos de InfleSusVentas)
        agregarEncabezadoConLogo(document); 
        agregarLineaSeparadora(document, colorPrimario);

        // 2. CABECERA ESPECÍFICA (Aquí está el cambio clave: Cliente + Bancos arriba)
        agregarCabeceraDatosCotizacion(document, cotizacion);

        // 3. TABLA DE PRODUCTOS (Con los títulos exactos de tu PDF: "UNID. DE MEDIDA", etc.)
        agregarTablaProductosCotizacion(document, cotizacion);

        // 4. TOTALES (El cuadro de resumen de montos alineado a la derecha)
        agregarResumenMontos(document, cotizacion);

        // 5. PIE LEGAL (Condiciones, Garantía y Notas Técnicas al final)
        agregarCondicionesLegalesCotizacion(document, cotizacion);

        // Cerrar y guardar
        document.close();
        System.out.println("✓ PDF Cotización generado correctamente: " + nombreArchivo);
        return rutaCompleta;
    }

    /**
     * Agrega encabezado con logo de la empresa
     */
    /**
     * ENCABEZADO UNIFICADO: Lee los datos de empresa.properties.
     * Se usa para todos los documentos (Cotización, Factura, Nota de Crédito).
     */
    private void agregarEncabezadoConLogo(Document document) {
        try {
            // Tabla de 2 columnas: Logo (2 partes) y Texto (3 partes)
            Table headerTable = new Table(UnitValue.createPercentArray(new float[] { 2, 3 }));
            headerTable.setWidth(UnitValue.createPercentValue(100));

            // 1. LOGO (Columna Izquierda)
            Cell logoCell = new Cell();
            File logoFile = new File(LOGO_PATH);
            if (logoFile.exists()) {
                Image logo = new Image(ImageDataFactory.create(LOGO_PATH));
                // Ajuste dinámico del logo para que no rompa el header
                logo.setAutoScale(true); 
                logo.setMaxHeight(60); 
                logoCell.add(logo);
            } else {
                // Texto de respaldo si no hay imagen
                Paragraph nombreEmpresa = new Paragraph(configuracion.getProperty("empresa.razon_social"))
                        .setFontSize(18).setBold().setFontColor(colorPrimario);
                logoCell.add(nombreEmpresa);
            }
            logoCell.setBorder(Border.NO_BORDER);
            logoCell.setVerticalAlignment(VerticalAlignment.MIDDLE);

            // 2. INFO EMPRESA (Columna Derecha)
            Cell infoCell = new Cell();
            // Nombre Comercial o Razón Social destacado
            infoCell.add(new Paragraph(configuracion.getProperty("empresa.razon_social"))
                    .setFontSize(14).setBold().setFontColor(colorPrimario));
            
            // RUC y Datos de contacto
            infoCell.add(new Paragraph("RUC: " + configuracion.getProperty("empresa.ruc"))
                    .setFontSize(10).setBold());
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
        } catch (Exception e) {
            System.err.println("✗ Error al agregar encabezado: " + e.getMessage());
        }
    }

    private void agregarLineaSeparadora(Document document, Color color) {
        // Crear una línea sólida de 1 punto de grosor
        SolidLine linea = new SolidLine(1f);
        linea.setColor(color);
        
        // Crear el elemento separador basado en esa línea
        LineSeparator ls = new LineSeparator(linea);
        
        // Darle un poco de aire arriba y abajo para que no quede pegado al texto
        ls.setMarginTop(5);
        ls.setMarginBottom(5);
        
        document.add(ls);
    }

    private void agregarCondicionesLegalesCotizacion(Document document, Cotizacion cotizacion) {
        // Contenedor simple sin bordes
        Table condiciones = new Table(1);
        condiciones.setWidth(UnitValue.createPercentValue(100));
        condiciones.setMarginTop(10); // Separación de los totales

        Cell celda = new Cell();
        celda.setBorder(Border.NO_BORDER);
        float fontSize = 9f;

        // 1. Datos básicos (Pago, Tiempo, IGV)
        celda.add(new Paragraph("Condición de pago: " + cotizacion.getCondicionPago().getDescripcion())
                .setFontSize(fontSize).setBold());
        
        celda.add(new Paragraph("Tiempo de entrega: " + configuracion.getProperty("condicion.tiempo_entrega_defecto", "5 a 7 días"))
                .setFontSize(fontSize));
        
        celda.add(new Paragraph("Incluye IGV 18%: SI").setFontSize(fontSize));

        // 2. Incluye (traído de properties)
        String incluye = configuracion.getProperty("condicion.incluye", "");
        if (!incluye.isEmpty()) {
            celda.add(new Paragraph("Incluye: " + incluye).setFontSize(fontSize).setMarginTop(4));
        }

        // 3. Marca y Procedencia
        celda.add(new Paragraph("Marca y procedencia: Producto Nacional de " + configuracion.getProperty("empresa.razon_social"))
                .setFontSize(fontSize));

        // 4. Garantía (traído de properties)
        String garantia = configuracion.getProperty("condicion.garantia", "");
        if (!garantia.isEmpty()) {
            celda.add(new Paragraph("Garantía: " + garantia).setFontSize(fontSize));
        }

        // 5. Notas Técnicas (Motor y Artes)
        celda.add(new Paragraph("\n NOTAS TÉCNICAS:").setBold().setFontSize(fontSize));
        
        // Verificamos que existan las propiedades antes de agregarlas
        String notaMotor = configuracion.getProperty("texto.nota.motor", "");
        if (!notaMotor.isEmpty()) celda.add(new Paragraph("- " + notaMotor).setFontSize(fontSize));
        
        String notaElectrica = configuracion.getProperty("texto.requisito.electrico", "");
        if (!notaElectrica.isEmpty()) celda.add(new Paragraph("- " + notaElectrica).setFontSize(fontSize));
        
        String notaArte = configuracion.getProperty("texto.requisito.arte", "");
        if (!notaArte.isEmpty()) celda.add(new Paragraph("- " + notaArte).setFontSize(fontSize));

        condiciones.addCell(celda);
        document.add(condiciones);
        
        // 6. Firma final (Atentamente...)
        document.add(new Paragraph("\n\nAtentamente,\n\nArea de Ventas\n" + configuracion.getProperty("empresa.razon_social"))
                .setTextAlignment(TextAlignment.CENTER).setFontSize(10).setBold());
    }
   
    private void agregarCabeceraDatosCotizacion(Document document, Cotizacion cotizacion) {
        // 1. ASUNTO
        document.add(new Paragraph("Asunto: Cotización de Inflable Publicitario")
                .setBold().setFontSize(11).setUnderline().setMarginBottom(10));

        // 2. TABLA DE DATOS (Cliente + Fecha + Número)
        Table tableDatos = new Table(UnitValue.createPercentArray(new float[]{1, 4}));
        tableDatos.setWidth(UnitValue.createPercentValue(100));

        // Datos del Cliente
        agregarFilaDatoCotizacion(tableDatos, "Cliente:", cotizacion.getCliente().getRazonSocial());
        agregarFilaDatoCotizacion(tableDatos, "N.º RUC:", cotizacion.getCliente().getRuc());
        agregarFilaDatoCotizacion(tableDatos, "Atención:", cotizacion.getCliente().getNombreContacto() != null ? cotizacion.getCliente().getNombreContacto() : "-");
        
        String telefono = cotizacion.getCliente().getTelefono() != null ? cotizacion.getCliente().getTelefono() : "-";
        agregarFilaDatoCotizacion(tableDatos, "Teléfono:", telefono);

        // Agregamos Fecha y Número aquí mismo para mantener el orden del documento
        agregarFilaDatoCotizacion(tableDatos, "Fecha:", cotizacion.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        agregarFilaDatoCotizacion(tableDatos, "Nº Cotización:", String.format("COT-%04d", cotizacion.getNumeroCotizacion()));
        
        document.add(tableDatos);

        // 3. CUENTA BBVA (Texto suelto debajo de los datos)
        String cuentaBBVA = configuracion.getProperty("banco.bbva.nombre", "Banco BBVA") + ": " + 
                            configuracion.getProperty("banco.bbva.cuenta", "");
        document.add(new Paragraph(cuentaBBVA).setFontSize(10).setBold().setMarginTop(8));
        
        // 4. TABLA DE DETRACCIÓN Y VALIDEZ (3 columnas con encabezado gris)
        Table tableBancos = new Table(UnitValue.createPercentArray(new float[]{1.5f, 1, 1}));
        tableBancos.setWidth(UnitValue.createPercentValue(100));
        tableBancos.setMarginTop(5);
        tableBancos.setMarginBottom(15);
        
        Color grisClaro = new DeviceRgb(240, 240, 240);
        
        // Encabezados
        tableBancos.addHeaderCell(new Cell().add(new Paragraph("Cuenta de detracción Banco de la Nación").setBold().setFontSize(8)).setBackgroundColor(grisClaro));
        tableBancos.addHeaderCell(new Cell().add(new Paragraph("Validez de la cotización").setBold().setFontSize(8)).setBackgroundColor(grisClaro));
        tableBancos.addHeaderCell(new Cell().add(new Paragraph("CCI").setBold().setFontSize(8)).setBackgroundColor(grisClaro));

        // Valores (desde properties)
        tableBancos.addCell(new Cell().add(new Paragraph(configuracion.getProperty("banco.nacion.cuenta", "-"))).setFontSize(9));
        tableBancos.addCell(new Cell().add(new Paragraph(configuracion.getProperty("condicion.validez_oferta", "30 días"))).setFontSize(9));
        tableBancos.addCell(new Cell().add(new Paragraph(configuracion.getProperty("banco.bbva.cci", "-"))).setFontSize(9));

        document.add(tableBancos);
    }

        private void agregarFilaDatoCotizacion(Table table, String label, String valor) {
        // Celda Etiqueta (Negrita, sin fondo, padding ajustado)
        Cell celdaLabel = new Cell();
        celdaLabel.add(new Paragraph(label).setBold().setFontSize(10));
        celdaLabel.setBorder(Border.NO_BORDER);
        celdaLabel.setPadding(1); // Padding reducido para que quede compacto
        
        // Celda Valor (Texto normal)
        Cell celdaValor = new Cell();
        celdaValor.add(new Paragraph(valor).setFontSize(10));
        celdaValor.setBorder(Border.NO_BORDER);
        celdaValor.setPadding(1);

        table.addCell(celdaLabel);
        table.addCell(celdaValor);
    }

    /**
     * Tabla de productos exclusiva para Cotización (Estilo "Globo")
     */
    private void agregarTablaProductosCotizacion(Document document, Cotizacion cotizacion) {
        // Definimos anchos de columna: La descripción (índice 3) es la más ancha
        float[] columnWidths = { 0.5f, 0.8f, 1.5f, 5, 1.5f, 1.5f };
        Table productosTable = new Table(UnitValue.createPercentArray(columnWidths));
        productosTable.setWidth(UnitValue.createPercentValue(100));

        // 1. ENCABEZADOS (Texto Blanco sobre Fondo Azul Corporativo)
        String[] headers = { "N.", "CANT.", "UNID. DE MEDIDA", "DESCRIPCIÓN", "PRECIO UNITARIO", "TOTAL" };
        
        for (String header : headers) {
            Cell cell = new Cell().add(new Paragraph(header).setBold().setFontSize(7).setFontColor(ColorConstants.WHITE));
            cell.setBackgroundColor(colorPrimario); 
            cell.setTextAlignment(TextAlignment.CENTER);
            cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
            cell.setPadding(4); // Un poco de aire en el encabezado
            productosTable.addHeaderCell(cell);
        }

        // 2. LLENADO DE DATOS
        int item = 1;
        for (ProductoCotizacion producto : cotizacion.getProductos()) {
            // N.
            productosTable.addCell(crearCeldaCuerpo(String.valueOf(item++), TextAlignment.CENTER));
            
            // CANT.
            productosTable.addCell(crearCeldaCuerpo(String.valueOf(producto.getCantidad()), TextAlignment.CENTER));
            
            // UNID. (Usamos "UNIDAD" fijo o lo que venga del objeto si prefieres)
            String unidad = (producto.getUnidadMedida() != null && !producto.getUnidadMedida().isEmpty()) 
                            ? producto.getUnidadMedida() : "UNIDAD";
            productosTable.addCell(crearCeldaCuerpo(unidad, TextAlignment.CENTER)); 
            
            // DESCRIPCIÓN (Alineada a la izquierda)
            productosTable.addCell(crearCeldaCuerpo(producto.getDescripcion(), TextAlignment.LEFT));

            // PRECIO UNITARIO (Alineado a la derecha)
            productosTable.addCell(crearCeldaCuerpo(String.format("S/ %.2f", producto.getPrecioBase()), TextAlignment.RIGHT));
            
            // TOTAL (Alineado a la derecha)
            productosTable.addCell(crearCeldaCuerpo(String.format("S/ %.2f", producto.getSubtotal()), TextAlignment.RIGHT));
        }

        document.add(productosTable);
        document.add(new Paragraph("\n")); // Espacio después de la tabla
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
        float[] columnWidths = { 1, 1, 1, 6, 2, 2 };
        Table productosTable = new Table(UnitValue.createPercentArray(columnWidths));
        productosTable.setWidth(UnitValue.createPercentValue(100));

        // Encabezados
        String[] headers = { "N.", "Cant.", "Und.", "Descripción Técnica", "P. Unit", "Total" };
        for (String header : headers) {
            Cell cell = new Cell()
                    .add(new Paragraph(header).setBold().setFontSize(9).setFontColor(ColorConstants.WHITE));
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

            productosTable
                    .addCell(crearCeldaCuerpo(String.format("S/ %.2f", producto.getPrecioBase()), TextAlignment.RIGHT));
            productosTable
                    .addCell(crearCeldaCuerpo(String.format("S/ %.2f", producto.getSubtotal()), TextAlignment.RIGHT));
        }

        document.add(productosTable);
        document.add(new Paragraph("\n")); // Espacio
    }

    private void agregarDatosBancarios(Document document) {
        document.add(new Paragraph("INFORMACIÓN DE PAGO")
                .setFontSize(10).setBold().setFontColor(colorSecundario));

        // Tabla simple para bancos
        Table bancoTable = new Table(UnitValue.createPercentArray(new float[] { 2, 3, 4 }));
        bancoTable.setWidth(UnitValue.createPercentValue(100));

        // Estilo encabezado gris
        Color grisHeader = new DeviceRgb(240, 240, 240);
        String[] headers = { "Banco", "N° Cuenta", "Titular / Detalle" };
        for (String h : headers) {
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
        // Contenedor simple sin bordes (ancho 100%)
        Table condiciones = new Table(1);
        condiciones.setWidth(UnitValue.createPercentValue(100));
        condiciones.setMarginTop(10);

        Cell celda = new Cell();
        celda.setBorder(Border.NO_BORDER);
        float fontSize = 9f;

        // 1. Condiciones Comerciales Básicas
        celda.add(new Paragraph("Condición de pago: " + cotizacion.getCondicionPago().getDescripcion())
                .setFontSize(fontSize).setBold());
        celda.add(new Paragraph("Tiempo de entrega: " + configuracion.getProperty("condicion.tiempo_entrega_defecto", "5 a 7 días"))
                .setFontSize(fontSize));
        celda.add(new Paragraph("Incluye IGV 18%: SI").setFontSize(fontSize));

        // 2. Qué incluye (desde properties)
        String incluye = configuracion.getProperty("condicion.incluye", "");
        if (!incluye.isEmpty()) {
            celda.add(new Paragraph("Incluye: " + incluye)
                    .setFontSize(fontSize).setMarginTop(4));
        }

        // 3. Marca y Procedencia
        celda.add(new Paragraph("Marca y procedencia: Producto Nacional de " + configuracion.getProperty("empresa.razon_social"))
                .setFontSize(fontSize));

        // 4. Garantía
        String garantia = configuracion.getProperty("condicion.garantia", "");
        if (!garantia.isEmpty()) {
            celda.add(new Paragraph("Garantía: " + garantia).setFontSize(fontSize));
        }

        // 5. Notas Técnicas (Motor, Eléctrico, Artes)
        celda.add(new Paragraph("\nNOTAS TÉCNICAS:").setBold().setFontSize(fontSize));
        celda.add(new Paragraph("- " + configuracion.getProperty("texto.nota.motor", ""))
                .setFontSize(fontSize));
        celda.add(new Paragraph("- " + configuracion.getProperty("texto.requisito.electrico", ""))
                .setFontSize(fontSize));
        celda.add(new Paragraph("- " + configuracion.getProperty("texto.requisito.arte", ""))
                .setFontSize(fontSize));

        condiciones.addCell(celda);
        document.add(condiciones);
        
        // 6. Firma / Cierre "Atentamente"
        document.add(new Paragraph("\n\nAtentamente,\n\nArea de Ventas\n" + configuracion.getProperty("empresa.razon_social"))
                .setTextAlignment(TextAlignment.CENTER).setFontSize(10).setBold());
    }

    // Helper para crear celdas de la tabla de productos rápidamente
    private Cell crearCeldaCuerpo(String texto, TextAlignment alineacion) {
        return new Cell()
                .add(new Paragraph(texto).setFontSize(8)) // Fuente pequeña para que quepa todo
                .setTextAlignment(alineacion)
                .setPadding(3)
                .setVerticalAlignment(VerticalAlignment.TOP);
    }

        private Cell crearCeldaProducto(String texto, TextAlignment alineacion) {
        Cell cell = new Cell();
        // Usamos una fuente tamaño 9 para que la información quepa bien
        cell.add(new Paragraph(texto != null ? texto : "").setFontSize(9));
        cell.setTextAlignment(alineacion);
        cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        cell.setPadding(4); // Un poco de espacio interno para que no se vea apretado
        return cell;
    }
    /**
     * Agrega resumen de montos
     */
    /**
     * Agrega el cuadro de totales (Subtotal, IGV, Total) alineado a la derecha.
     */
    private void agregarResumenMontos(Document document, Cotizacion cotizacion) {
        // Tabla de 2 columnas (Etiqueta y Valor)
        // Ancho 50% del documento, alineada a la DERECHA
        Table resumenTable = new Table(UnitValue.createPercentArray(new float[] { 3, 1 }));
        resumenTable.setWidth(UnitValue.createPercentValue(50));
        resumenTable.setHorizontalAlignment(HorizontalAlignment.RIGHT);
        resumenTable.setMarginTop(5);

        // 1. Subtotal
        agregarFilaMonto(resumenTable, "Subtotal:", String.format("S/ %.2f", cotizacion.getSubtotal()), false);

        // 2. IGV
        agregarFilaMonto(resumenTable, "IGV (18%):", String.format("S/ %.2f", cotizacion.getIGV()), false);

        // 3. TOTAL (Destacado)
        agregarFilaMonto(resumenTable, "TOTAL:", String.format("S/ %.2f", cotizacion.getTotal()), true);

        document.add(resumenTable);
        document.add(new Paragraph("\n"));
    }

    /**
     * Helper para las filas de montos.
     * Si 'esTotal' es true, aplica fondo azul y texto blanco.
     */
    private void agregarFilaMonto(Table table, String label, String valor, boolean esTotal) {
        // Celda Etiqueta (Ej: "Subtotal:")
        Cell labelCell = new Cell();
        labelCell.add(new Paragraph(label).setBold().setFontSize(10));
        labelCell.setTextAlignment(TextAlignment.RIGHT);
        labelCell.setPadding(5);
        
        // Celda Valor (Ej: "S/ 100.00")
        Cell valorCell = new Cell();
        Paragraph pValor = new Paragraph(valor).setBold().setFontSize(10);
        valorCell.add(pValor);
        valorCell.setTextAlignment(TextAlignment.RIGHT);
        valorCell.setPadding(5);

        // Estilo especial para la fila del TOTAL FINAL
        if (esTotal) {
            // Fondo Azul Corporativo
            labelCell.setBackgroundColor(colorPrimario);
            valorCell.setBackgroundColor(colorPrimario);
            
            // Texto Blanco
            labelCell.setFontColor(ColorConstants.WHITE);
            valorCell.setFontColor(ColorConstants.WHITE);
            
            // Fuente un poco más grande
            pValor.setFontSize(12);
        } else {
            // Bordes simples para las otras filas
            // (Opcional: Si quieres sin bordes, usa Border.NO_BORDER)
            labelCell.setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
            valorCell.setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f));
        }

        table.addCell(labelCell);
        table.addCell(valorCell);
    }

    /**
     * Agrega condiciones y vigencia
     */
    private void agregarCondicionesVigencia(Document document, Cotizacion cotizacion) {
        Table condicionesTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        condicionesTable.setWidth(UnitValue.createPercentValue(100));
        condicionesTable.setMarginTop(10);

        // 1. Condición de Pago
        Cell condPagoCell = new Cell();
        condPagoCell.add(new Paragraph("Condición de Pago:").setBold().setFontSize(9));
        // Obtiene la descripción del enum (ej: "Contado contra entrega")
        condPagoCell.add(new Paragraph(cotizacion.getCondicionPago().getDescripcion()).setFontSize(9));
        condPagoCell.setBackgroundColor(colorGrisClaro);
        condPagoCell.setBorder(Border.NO_BORDER);
        condPagoCell.setPadding(8);

        // 2. Vigencia / Validez
        Cell vigenciaCell = new Cell();
        vigenciaCell.add(new Paragraph("Validez de la oferta:").setBold().setFontSize(9));
        
        // Usamos la propiedad configurada o un valor calculado
        String validez = configuracion.getProperty("condicion.validez_oferta", "15 días");
        
        // Opcional: Si quisieras calcular la fecha exacta sumando días:
        // String fechaVenc = cotizacion.getFechaVigencia().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        
        vigenciaCell.add(new Paragraph(validez).setFontSize(9));
        vigenciaCell.setBackgroundColor(colorGrisClaro);
        vigenciaCell.setBorder(Border.NO_BORDER);
        vigenciaCell.setPadding(8);

        // Agregamos celdas con un pequeño espacio entre ellas (usando bordes blancos si fuera una tabla compleja, 
        // pero aquí es una tabla simple de 2 celdas pegadas).
        // Para separarlas visualmente, podrías usar una tabla de 3 columnas {1, 0.1, 1} con la del medio vacía.
        // Aquí usamos la versión estándar pegada:
        condicionesTable.addCell(condPagoCell);
        
        // Celda vacía para separar (truco visual)
        Cell espacio = new Cell().setBorder(Border.NO_BORDER);
        // Si quisieras separarlas, cambia la definición de la tabla arriba a new float[]{1, 0.1f, 1}
        
        condicionesTable.addCell(vigenciaCell);

        document.add(condicionesTable);
        document.add(new Paragraph("\n"));
    }

    /**
     * Agrega pie de página
     */
        private void agregarPiePagina(Document document) {
        // Un poco de espacio antes del pie
        document.add(new Paragraph("\n\n"));

        // Construimos el texto con los datos de empresa.properties
        String textoFooter = configuracion.getProperty("empresa.razon_social", "INFLE SUS VENTAS SRL") + 
                             " - Venta de Inflables Publicitarios\n" +
                             configuracion.getProperty("empresa.direccion", "") + " | " +
                             "Tel: " + configuracion.getProperty("empresa.telefono", "") + " | " +
                             configuracion.getProperty("empresa.email", "");

        Paragraph footer = new Paragraph(textoFooter)
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY);

        document.add(footer);
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
            Table headerTable = new Table(UnitValue.createPercentArray(new float[] { 2, 3 }));
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
     * Información de la factura (similar a cotización pero con "FACTURA
     * ELECTRÓNICA")
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
        Table infoTable = new Table(UnitValue.createPercentArray(new float[] { 1, 1 }));
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

    private void agregarFilaCliente(Table table, String label, Object valor) {
        Cell labelCell = new Cell();
        labelCell.add(new Paragraph(label).setBold().setFontSize(9));
        labelCell.setBackgroundColor(colorGrisClaro); // Fondo gris suave
        labelCell.setBorder(Border.NO_BORDER); 
        labelCell.setPadding(5); 

        Cell valorCell = new Cell();
        // Conversión segura a String para evitar errores si es null
        String textoValor = (valor != null) ? valor.toString() : "-";
        valorCell.add(new Paragraph(textoValor).setFontSize(9));
        valorCell.setBorder(Border.NO_BORDER);
        valorCell.setPadding(5);

        table.addCell(labelCell);
        table.addCell(valorCell);
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

        Table clienteTable = new Table(UnitValue.createPercentArray(new float[] { 1, 3 }));
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

        Table productosTable = new Table(UnitValue.createPercentArray(new float[] { 1, 4, 1, 1.5f, 1.5f }));
        productosTable.setWidth(UnitValue.createPercentValue(100));

        // Encabezados
        String[] headers = { "Código", "Descripción", "Cant.", "V. Unit. (S/)", "Subtotal (S/)" };
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
            productosTable
                    .addCell(crearCeldaProducto(String.format("%.2f", producto.getPrecioBase()), TextAlignment.RIGHT));
            productosTable
                    .addCell(crearCeldaProducto(String.format("%.2f", producto.getSubtotal()), TextAlignment.RIGHT));
        }

        document.add(productosTable);
        document.add(new Paragraph("\n"));
    }

    /**
     * Resumen de montos con detracción
     */
    private void agregarResumenMontosFactura(Document document, Cotizacion cotizacion, DatosFacturaPDF datos) {
        Table resumenTable = new Table(UnitValue.createPercentArray(new float[] { 3, 1 }));
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

        Table detTable = new Table(UnitValue.createPercentArray(new float[] { 1, 2 }));
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
        Table bancoTable = new Table(UnitValue.createPercentArray(new float[] { 2, 3, 3 }));
        bancoTable.setWidth(UnitValue.createPercentValue(90)); // Un poco más angosta que el total

        // Estilo de encabezado de banco
        Color grisOscuro = new DeviceRgb(230, 230, 230);

        bancoTable.addHeaderCell(
                new Cell().add(new Paragraph("Banco").setBold().setFontSize(8)).setBackgroundColor(grisOscuro));
        bancoTable.addHeaderCell(
                new Cell().add(new Paragraph("N° Cuenta").setBold().setFontSize(8)).setBackgroundColor(grisOscuro));
        bancoTable.addHeaderCell(
                new Cell().add(new Paragraph("Titular").setBold().setFontSize(8)).setBackgroundColor(grisOscuro));

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
        return new Cell().add(new Paragraph(texto).setFontSize(8)).setPadding(3)
                .setBorder(new SolidBorder(ColorConstants.GRAY, 0.5f));
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

        Table creditoTable = new Table(UnitValue.createPercentArray(new float[] { 1, 2 }));
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
                        configuracion.getProperty("empresa.email", ""))
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

    /**
     * Genera PDF de Nota de Crédito
     */
    public String generarPdfNotaCredito(NotaCredito nc) throws Exception {
        String nombreArchivo = String.format("%s-%s-%08d.pdf",
                "20554524051",
                nc.getSerie(),
                nc.getNumero());

        // Crear ruta absoluta para evitar error -600 en macOS
        Path directorioBase = Paths.get("documentos/pdf/notas_credito/").toAbsolutePath();
        Files.createDirectories(directorioBase);

        Path archivoPath = directorioBase.resolve(nombreArchivo);
        String rutaCompleta = archivoPath.toString();

        PdfWriter writer = new PdfWriter(rutaCompleta);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(40, 40, 40, 40);

        // ENCABEZADO
        agregarEncabezadoNC(document);

        // TÍTULO
        Paragraph titulo = new Paragraph("NOTA DE CRÉDITO ELECTRÓNICA")
                .setFontSize(20).setBold()
                .setFontColor(colorPrimario)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(titulo);
        document.add(new Paragraph("\n"));

        // SERIE-NÚMERO
        Table infoTable = new Table(2);
        infoTable.setWidth(UnitValue.createPercentValue(100));

        Cell serieCell = new Cell();
        serieCell.add(new Paragraph("Serie - Número:").setBold());
        serieCell.add(new Paragraph(nc.getSerie() + "-" + String.format("%08d", nc.getNumero()))
                .setFontSize(14).setBold());
        serieCell.setBackgroundColor(colorGrisClaro);
        serieCell.setTextAlignment(TextAlignment.CENTER);

        Cell fechaCell = new Cell();
        fechaCell.add(new Paragraph("Fecha de Emisión:").setBold());
        String fechaEmisionStr = (nc.getFechaEmision() != null)
                ? nc.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "SIN FECHA";
        fechaCell.add(new Paragraph(fechaEmisionStr).setFontSize(14).setBold());
        fechaCell.setBackgroundColor(colorGrisClaro);
        fechaCell.setTextAlignment(TextAlignment.CENTER);

        infoTable.addCell(serieCell);
        infoTable.addCell(fechaCell);
        document.add(infoTable);
        document.add(new Paragraph("\n"));

        // DOCUMENTO QUE MODIFICA
        Paragraph tituloMod = new Paragraph("DOCUMENTO QUE MODIFICA")
                .setFontSize(12).setBold().setFontColor(colorPrimario);
        document.add(tituloMod);

        Table modTable = new Table(2);
        modTable.setWidth(UnitValue.createPercentValue(100));

        agregarFilaPDF(modTable, "Factura Electrónica:",
                nc.getNumeroFacturaRef() != null ? nc.getNumeroFacturaRef() : "-");
        agregarFilaPDF(modTable, "Señor(es):", nc.getRazonSocialCliente() != null ? nc.getRazonSocialCliente() : "-");
        agregarFilaPDF(modTable, "RUC:", nc.getRucCliente() != null ? nc.getRucCliente() : "-");
        agregarFilaPDF(modTable, "Tipo de Moneda:", nc.getMoneda() != null ? nc.getMoneda() : "PEN");

        document.add(modTable);
        document.add(new Paragraph("\n"));

        // MOTIVO
        Paragraph tituloMotivo = new Paragraph("MOTIVO O SUSTENTO")
                .setFontSize(12).setBold().setFontColor(colorPrimario);
        document.add(tituloMotivo);

        String motivoTexto = (nc.getMotivoSustento() != null) ? nc.getMotivoSustento().toUpperCase() : "SIN MOTIVO";
        Paragraph motivo = new Paragraph(motivoTexto)
                .setBackgroundColor(colorGrisClaro).setPadding(10);
        document.add(motivo);
        document.add(new Paragraph("\n"));

        // DETALLE
        Paragraph tituloDetalle = new Paragraph("DETALLE")
                .setFontSize(12).setBold().setFontColor(colorPrimario);
        document.add(tituloDetalle);

        Table detalleTable = new Table(5);
        detalleTable.setWidth(UnitValue.createPercentValue(100));

        String[] headers = { "UM", "Cant", "Descripción", "V.Unit", "Subtotal" };
        for (String h : headers) {
            Cell hCell = new Cell();
            hCell.add(new Paragraph(h).setBold().setFontColor(ColorConstants.WHITE));
            hCell.setBackgroundColor(colorPrimario);
            hCell.setTextAlignment(TextAlignment.CENTER);
            detalleTable.addHeaderCell(hCell);
        }

        for (ItemNotaCredito item : nc.getItems()) {
            detalleTable.addCell(
                    new Cell().add(new Paragraph(item.getUnidadMedida() != null ? item.getUnidadMedida() : "NIU")));
            detalleTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getCantidad()))));
            detalleTable.addCell(new Cell().add(new Paragraph(item.formatearDescripcionParaPDF())));
            detalleTable.addCell(new Cell().add(new Paragraph(String.format("%.2f", item.getValorUnitario()))));
            detalleTable.addCell(new Cell().add(new Paragraph(String.format("%.2f", item.getSubtotal()))));
        }

        document.add(detalleTable);
        document.add(new Paragraph("\n"));

        // TOTALES
        Table totalesTable = new Table(2);
        totalesTable.setWidth(UnitValue.createPercentValue(50));
        totalesTable.setHorizontalAlignment(HorizontalAlignment.RIGHT);

        agregarFilaMontoPDF(totalesTable, "Sub Total:", String.format("S/ %.2f", nc.getSubtotal()));
        agregarFilaMontoPDF(totalesTable, "IGV (18%):", String.format("S/ %.2f", nc.getIgv()));

        Cell totalLabelCell = new Cell();
        totalLabelCell.add(new Paragraph("TOTAL:").setBold().setFontColor(ColorConstants.WHITE));
        totalLabelCell.setBackgroundColor(colorPrimario);

        Cell totalValueCell = new Cell();
        totalValueCell.add(new Paragraph(String.format("S/ %.2f", nc.getTotal()))
                .setBold().setFontSize(14).setFontColor(ColorConstants.WHITE));
        totalValueCell.setBackgroundColor(colorPrimario);

        totalesTable.addCell(totalLabelCell);
        totalesTable.addCell(totalValueCell);

        document.add(totalesTable);

        document.close();
        return rutaCompleta;
    }

    private void agregarEncabezadoNC(Document document) {
        Paragraph empresa = new Paragraph("INFLE SUS VENTAS S.R.L.")
                .setFontSize(14).setBold();
        Paragraph ruc = new Paragraph("RUC: 20554524051").setFontSize(10);
        document.add(empresa);
        document.add(ruc);
        document.add(new Paragraph("\n"));
    }

    private void agregarFilaPDF(Table table, String label, String valor) {
        Cell labelCell = new Cell();
        labelCell.add(new Paragraph(label).setBold());
        labelCell.setBackgroundColor(colorGrisClaro);

        Cell valorCell = new Cell();
        valorCell.add(new Paragraph(valor));

        table.addCell(labelCell);
        table.addCell(valorCell);
    }

    private void agregarFilaMontoPDF(Table table, String label, String valor) {
        table.addCell(new Cell().add(new Paragraph(label).setBold()));
        table.addCell(new Cell().add(new Paragraph(valor)));
    }
}