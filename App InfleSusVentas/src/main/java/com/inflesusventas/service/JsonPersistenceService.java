package com.inflesusventas.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inflesusventas.model.ComprobanteElectronico;
import com.inflesusventas.model.Cotizacion;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class JsonPersistenceService {

    private final String FILE_COTIZACIONES = "data_cotizaciones.json";
    private final String FILE_COMPROBANTES = "data_comprobantes.json";
    
    private ObjectMapper mapper;

    public JsonPersistenceService() {
        this.mapper = new ObjectMapper();
        // Módulo importante para que Jackson entienda las Fechas de Java 8 (LocalDateTime)
        this.mapper.registerModule(new JavaTimeModule()); 
    }

    // --- COTIZACIONES ---
    public void guardarCotizaciones(List<Cotizacion> lista) {
        try {
            mapper.writeValue(new File(FILE_COTIZACIONES), lista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Cotizacion> cargarCotizaciones() {
        File file = new File(FILE_COTIZACIONES);
        
        // Depuración: Imprimir ruta absoluta para saber dónde busca el archivo
        System.out.println("📂 Buscando archivo en: " + file.getAbsolutePath());

        if (!file.exists()) {
            System.out.println("⚠️ El archivo JSON no existe aún.");
            return new ArrayList<>();
        }
        
        try {
            List<Cotizacion> lista = mapper.readValue(file, new TypeReference<List<Cotizacion>>() {});
            System.out.println("✅ JSON Leído correctamente. Elementos: " + lista.size());
            return lista;
        } catch (IOException e) {
            System.err.println("❌ Error leyendo JSON: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // --- COMPROBANTES (Esto alimenta la vista de Clientes/Ventas) ---
    public void guardarComprobantes(List<ComprobanteElectronico> lista) {
        try {
            mapper.writeValue(new File(FILE_COMPROBANTES), lista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ComprobanteElectronico> cargarComprobantes() {
        File file = new File(FILE_COMPROBANTES);
        if (!file.exists()) return new ArrayList<>();
        try {
            return mapper.readValue(file, new TypeReference<List<ComprobanteElectronico>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}