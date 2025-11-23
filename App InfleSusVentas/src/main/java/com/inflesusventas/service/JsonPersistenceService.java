package com.inflesusventas.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature; // IMPORTANTE
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inflesusventas.model.ComprobanteElectronico;
import com.inflesusventas.model.Cotizacion;
import com.inflesusventas.model.NotaCredito;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class JsonPersistenceService {

    private final String FILE_COTIZACIONES = "data_cotizaciones.json";
    private final String FILE_COMPROBANTES = "data_comprobantes.json";
     private final String FILE_NOTAS_CREDITO = "data_notas_credito.json";

    private ObjectMapper mapper;

    public JsonPersistenceService() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        // ESTO ES CRUCIAL: Evita que falle si el JSON tiene campos viejos que ya no existen en Java
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private File getArchivo(String nombreArchivo) {
        // Usamos ruta absoluta para no perdernos
        String rutaAbsoluta = Paths.get("").toAbsolutePath().toString();
        File archivo = new File(rutaAbsoluta + File.separator + nombreArchivo);
        System.out.println("📂 Ruta del archivo " + nombreArchivo + ": " + archivo.getAbsolutePath());
        return archivo;
    }

    // --- COTIZACIONES ---
    public void guardarCotizaciones(List<Cotizacion> lista) {
        try {
            mapper.writeValue(getArchivo(FILE_COTIZACIONES), lista);
            System.out.println("💾 Guardadas " + lista.size() + " cotizaciones en disco.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Cotizacion> cargarCotizaciones() {
        File file = getArchivo(FILE_COTIZACIONES);
        if (!file.exists()) {
            System.out.println("⚠️ Archivo no encontrado: Se creará uno nuevo al guardar.");
            return new ArrayList<>();
        }
        try {
            List<Cotizacion> lista = mapper.readValue(file, new TypeReference<List<Cotizacion>>() {});
            System.out.println("✅ CARGADAS " + lista.size() + " COTIZACIONES CORRECTAMENTE.");
            return lista;
        } catch (IOException e) {
            System.err.println("❌ ERROR GRAVE LEYENDO JSON: " + e.getMessage());
            e.printStackTrace(); // Esto imprimirá el error exacto en rojo
            return new ArrayList<>();
        }
    }

    // --- COMPROBANTES ---
    public void guardarComprobantes(List<ComprobanteElectronico> lista) {
        try {
            mapper.writeValue(getArchivo(FILE_COMPROBANTES), lista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ComprobanteElectronico> cargarComprobantes() {
        File file = getArchivo(FILE_COMPROBANTES);
        if (!file.exists()) return new ArrayList<>();
        try {
            return mapper.readValue(file, new TypeReference<List<ComprobanteElectronico>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // --- NOTAS DE CRÉDITO ---
     public void guardarNotasCredito(List<NotaCredito> lista) {
        try {
            mapper.writeValue(getArchivo(FILE_NOTAS_CREDITO), lista);
            System.out.println("💾 Guardadas " + lista.size() + " notas de crédito.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<NotaCredito> cargarNotasCredito() {
        File file = getArchivo(FILE_NOTAS_CREDITO);
        if (!file.exists()) return new ArrayList<>();
        try {
            return mapper.readValue(file, new TypeReference<List<NotaCredito>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}