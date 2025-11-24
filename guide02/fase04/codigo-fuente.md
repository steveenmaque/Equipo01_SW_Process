## Código fuente

Código fuente de las funciones más importantes del software. Para ello:

Modelos:
- Cotizacion
  ```java
  public class Cotizacion {
    private int numeroCotizacion;
    private LocalDate fecha;
    private Cliente cliente;
    private List<ProductoCotizacion> productos;
    private CondicionPago condicionPago;
    private int diasVigencia = 10; // Valor por defecto razonable
    private boolean mostrarConIGV;
    private String moneda = "PEN";
    private double tipoCambio = 3.85;

    public enum CondicionPago {
        CONTADO("Contado contra entrega"),
        CREDITO_15_DIAS("Crédito a 15 días"),
        CREDITO_30_DIAS("Crédito a 30 días"),
        ADELANTO_50("50% Adelanto, 50% Contra entrega");

        private final String descripcion;

        CondicionPago(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    public Cotizacion() {
        this.fecha = LocalDate.now();
    }

    // --- GETTERS Y SETTERS ---
    public int getNumeroCotizacion() { return numeroCotizacion; }
    public void setNumeroCotizacion(int numeroCotizacion) { this.numeroCotizacion = numeroCotizacion; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public List<ProductoCotizacion> getProductos() { return productos; }
    public void setProductos(List<ProductoCotizacion> productos) { this.productos = productos; }
    public CondicionPago getCondicionPago() { return condicionPago; }
    public void setCondicionPago(CondicionPago condicionPago) { this.condicionPago = condicionPago; }
    public int getDiasVigencia() { return diasVigencia; }
    public void setDiasVigencia(int diasVigencia) { this.diasVigencia = diasVigencia; }
    public boolean isMostrarConIGV() { return mostrarConIGV; }
    public void setMostrarConIGV(boolean mostrarConIGV) { this.mostrarConIGV = mostrarConIGV; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public double getTipoCambio() { return tipoCambio; }
    public void setTipoCambio(double tipoCambio) { this.tipoCambio = tipoCambio; }

    // --- MÉTODOS CALCULADOS ---
    public double getSubtotal() {
        if (productos == null) return 0.0;
        return productos.stream().mapToDouble(ProductoCotizacion::getSubtotal).sum();
    }

    public double getIGV() {
        return getSubtotal() * 0.18;
    }

    public double getTotal() {
        return getSubtotal() + getIGV();
    }

    public LocalDate getFechaVigencia() {
        return (fecha != null) ? fecha.plusDays(diasVigencia) : LocalDate.now().plusDays(diasVigencia);
    }

- Comprobante de pago:
    ```java
    public class ComprobanteElectronico {

    private String id;
    private LocalDateTime fechaEmision;
    private String rucCliente;
    private String razonSocialCliente;
    private String moneda;
    private List<ProductoCotizacion> items;
    private double subtotal;
    private double igv;
    private double total;
    private String condicionPago; // CONTADO / CREDITO / 50-50

    public ComprobanteElectronico() { }

    // getters / setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getRucCliente() {
        return rucCliente;
    }

    public void setRucCliente(String rucCliente) {
        this.rucCliente = rucCliente;
    }

    public String getRazonSocialCliente() {
        return razonSocialCliente;
    }

    public void setRazonSocialCliente(String razonSocialCliente) {
        this.razonSocialCliente = razonSocialCliente;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public List<ProductoCotizacion> getItems() {
        return items;
    }

    public void setItems(List<ProductoCotizacion> items) {
        this.items = items;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getIgv() {
        return igv;
    }

    public void setIgv(double igv) {
        this.igv = igv;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getCondicionPago() {
        return condicionPago;
    }

    public void setCondicionPago(String condicionPago) {
        this.condicionPago = condicionPago;
    }

    @Override
    public String toString() {
        return "ComprobanteElectronico{" +
                "id='" + id + '\'' +
                ", tipoComprobante='" + tipoComprobante + '\'' +
                ", fechaEmision=" + fechaEmision +
                ", rucCliente='" + rucCliente + '\'' +
                ", total=" + total +
                '}';
    }
- Guia de Remision:
  ```java
  public class GuiaRemision {

    public enum TipoRemitente {
        REMITENTE("Remitente"),
        DESTINATARIO("Destinatario");

        private final String descripcion;
        TipoRemitente(String descripcion) { this.descripcion = descripcion; }
        public String getDescripcion() { return descripcion; }
    }

    public enum MotivoTraslado {
        VENTA("01", "Venta"),
        COMPRA("02", "Compra"),
        TRASLADO_ENTRE_ESTABLECIMIENTOS("04", "Traslado entre establecimientos"),
        CONSIGNACION("08", "Consignación"),
        DEVOLUCION("09", "Devolución"),
        OTROS("13", "Otros");

        private final String codigo;
        private final String descripcion;

        MotivoTraslado(String codigo, String descripcion) {
            this.codigo = codigo;
            this.descripcion = descripcion;
        }

        public String getCodigo() { return codigo; }
        public String getDescripcion() { return descripcion; }
    }

    public enum TipoDocumento {
        RUC("6", "RUC"),
        DNI("1", "DNI"),
        CARNET_EXTRANJERIA("4", "Carnet de Extranjería");

        private final String codigo;
        private final String descripcion;

        TipoDocumento(String codigo, String descripcion) {
            this.codigo = codigo;
            this.descripcion = descripcion;
        }

        public String getCodigo() { return codigo; }
        public String getDescripcion() { return descripcion; }
    }

    // Datos básicos
    private String serieNumero;
    private LocalDate fechaEmision;

    // Paso 1: Configuración inicial
    private TipoRemitente tipoRemitente;
    private boolean operacionComercioExterior;
    private MotivoTraslado motivoTraslado;

    // Paso 2: Destinatario
    private TipoDocumento tipoDocumentoDestinatario;
    private String numeroDocumentoDestinatario;
    private String razonSocialDestinatario;

    // Paso 5: Bienes a trasladar
    private List<BienGuiaRemision> bienes;

    // Paso 6: Puntos de partida y llegada
    private String puntoPartida; // Dirección completa
    private String ubigeoPartida; // Código UBIGEO
    private String puntoLlegada;
    private String ubigeoLlegada;

    // Paso 7: Datos de transporte
    private DatosTransporte datosTransporte;

    // Datos del remitente (empresa)
    private String rucRemitente;
    private String razonSocialRemitente;

    // Constructor
    public GuiaRemision() {
        this.fechaEmision = LocalDate.now();
        this.tipoRemitente = TipoRemitente.REMITENTE;
        this.operacionComercioExterior = false;
        this.motivoTraslado = MotivoTraslado.VENTA;
        this.tipoDocumentoDestinatario = TipoDocumento.RUC;
        this.bienes = new ArrayList<>();
        this.datosTransporte = new DatosTransporte();
    }

    // Getters y Setters
    public String getSerieNumero() {
        return serieNumero;
    }

    public void setSerieNumero(String serieNumero) {
        this.serieNumero = serieNumero;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public TipoRemitente getTipoRemitente() {
        return tipoRemitente;
    }

    public void setTipoRemitente(TipoRemitente tipoRemitente) {
        this.tipoRemitente = tipoRemitente;
    }

    public boolean isOperacionComercioExterior() {
        return operacionComercioExterior;
    }

    public void setOperacionComercioExterior(boolean operacionComercioExterior) {
        this.operacionComercioExterior = operacionComercioExterior;
    }

    public MotivoTraslado getMotivoTraslado() {
        return motivoTraslado;
    }

    public void setMotivoTraslado(MotivoTraslado motivoTraslado) {
        this.motivoTraslado = motivoTraslado;
    }

    public TipoDocumento getTipoDocumentoDestinatario() {
        return tipoDocumentoDestinatario;
    }

    public void setTipoDocumentoDestinatario(TipoDocumento tipoDocumentoDestinatario) {
        this.tipoDocumentoDestinatario = tipoDocumentoDestinatario;
    }

    public String getNumeroDocumentoDestinatario() {
        return numeroDocumentoDestinatario;
    }

    public void setNumeroDocumentoDestinatario(String numeroDocumentoDestinatario) {
        this.numeroDocumentoDestinatario = numeroDocumentoDestinatario;
    }

    public String getRazonSocialDestinatario() {
        return razonSocialDestinatario;
    }

    public void setRazonSocialDestinatario(String razonSocialDestinatario) {
        this.razonSocialDestinatario = razonSocialDestinatario;
    }

    public List<BienGuiaRemision> getBienes() {
        return bienes;
    }

    public void setBienes(List<BienGuiaRemision> bienes) {
        this.bienes = bienes;
    }

    public String getPuntoPartida() {
        return puntoPartida;
    }

    public void setPuntoPartida(String puntoPartida) {
        this.puntoPartida = puntoPartida;
    }

    public String getUbigeoPartida() {
        return ubigeoPartida;
    }

    public void setUbigeoPartida(String ubigeoPartida) {
        this.ubigeoPartida = ubigeoPartida;
    }

    public String getPuntoLlegada() {
        return puntoLlegada;
    }

    public void setPuntoLlegada(String puntoLlegada) {
        this.puntoLlegada = puntoLlegada;
    }

    public String getUbigeoLlegada() {
        return ubigeoLlegada;
    }

    public void setUbigeoLlegada(String ubigeoLlegada) {
        this.ubigeoLlegada = ubigeoLlegada;
    }

    public DatosTransporte getDatosTransporte() {
        return datosTransporte;
    }

    public void setDatosTransporte(DatosTransporte datosTransporte) {
        this.datosTransporte = datosTransporte;
    }

    public String getRucRemitente() {
        return rucRemitente;
    }

    public void setRucRemitente(String rucRemitente) {
        this.rucRemitente = rucRemitente;
    }

    public String getRazonSocialRemitente() {
        return razonSocialRemitente;
    }

    public void setRazonSocialRemitente(String razonSocialRemitente) {
        this.razonSocialRemitente = razonSocialRemitente;
    }

    // Métodos auxiliares
    public double getPesoTotalCarga() {
        return bienes.stream()
                .mapToDouble(BienGuiaRemision::getPesoBrutoTotal)
                .sum();
    }

    public int getCantidadTotalBienes() {
        return bienes.stream()
                .mapToInt(BienGuiaRemision::getCantidad)
                .sum();
    }

    @Override
    public String toString() {
        return "Guía " + serieNumero + " - " + razonSocialDestinatario;
    }
