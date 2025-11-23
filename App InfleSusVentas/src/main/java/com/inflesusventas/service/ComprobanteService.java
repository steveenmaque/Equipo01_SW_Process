package com.inflesusventas.service;

import com.inflesusventas.model.ComprobanteElectronico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class ComprobanteService {

    @Autowired
    private JsonPersistenceService storageService;

    private List<ComprobanteElectronico> historialComprobantes = new ArrayList<>();

    // CARGAR AL INICIAR
    @PostConstruct
    public void init() {
        this.historialComprobantes = storageService.cargarComprobantes();
        System.out.println(" Comprobantes recuperados: " + historialComprobantes.size());
    }

    public void registrarComprobante(ComprobanteElectronico comprobante) {
        historialComprobantes.add(comprobante);
        storageService.guardarComprobantes(historialComprobantes);
    }

    public List<ComprobanteElectronico> listarTodos() {
        return historialComprobantes;
    }

    public ComprobanteElectronico buscarPorId(String id) {
        System.out.println("🔍 Buscando comprobante con ID: '" + id + "'");

        // 1. Búsqueda exacta
        ComprobanteElectronico encontrado = historialComprobantes.stream()
                .filter(c -> c.getId() != null && c.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);

        // 2. Si no encuentra, buscar por sufijo (ej: usuario ingresa "F001-00000045"
        // pero ID es "RUC-F001-00000045")
        if (encontrado == null) {
            encontrado = historialComprobantes.stream()
                    .filter(c -> c.getId() != null && c.getId().endsWith("-" + id))
                    .findFirst()
                    .orElse(null);
        }

        // 3. Si no encuentra, buscar si contiene el texto (búsqueda laxa)
        if (encontrado == null) {
            encontrado = historialComprobantes.stream()
                    .filter(c -> c.getId() != null && c.getId().contains(id))
                    .findFirst()
                    .orElse(null);
        }

        if (encontrado == null) {
            System.out.println("⚠️ No encontrado. IDs disponibles en memoria (" + historialComprobantes.size() + "):");
            historialComprobantes.forEach(c -> System.out.println("   - '" + c.getId() + "'"));
        } else {
            System.out.println("✅ Comprobante encontrado: " + encontrado.getId() + " ("
                    + encontrado.getRazonSocialCliente() + ")");
        }

        return encontrado;
    }
}