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
}