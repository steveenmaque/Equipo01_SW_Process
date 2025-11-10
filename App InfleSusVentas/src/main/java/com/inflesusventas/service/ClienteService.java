package com.inflesusventas.service;

import com.inflesusventas.model.Cliente;
import java.util.ArrayList;
import java.util.List;

public class ClienteService {
    private List<Cliente> registroClientes = new ArrayList<>();

    public ClienteService() {
        // Podemos precargar algunos datos de prueba
        registroClientes.add(new Cliente("20123456789", "Empresa ABC S.A.C.", "Av. Siempre Viva 123", "999888777", "contacto@abc.com", "Juan Perez"));
        registroClientes.add(new Cliente("10987654321", "Bodega Don Pepe", "Jr. Lima 456", "987654321", "pepe@bodega.com", "Pepe"));
    }

    // Método para obtener todo el registro
    public List<Cliente> obtenerTodos() {
        return registroClientes;
    }

    // Método para guardar uno nuevo en el registro
    public void guardarCliente(Cliente nuevoCliente) {
        registroClientes.add(nuevoCliente);
    }
}
