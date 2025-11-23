package com.inflesusventas.service;

import com.inflesusventas.model.Cliente;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClienteService {
    private List<Cliente> registroClientes = new ArrayList<>();
    private int contadorIds = 1;

    public ClienteService() {
        // Datos de prueba actualizados
        guardarCliente(new Cliente(0, "20123456789", "Empresa ABC S.A.C.", "Av. Siempre Viva 123", "999888777", "contacto@abc.com", "Juan Perez"));
        guardarCliente(new Cliente(0, "10987654321", "Bodega Don Pepe EIRL", "Jr. Lima 456", "987654321", "pepe@bodega.com", "Pepe Lucho"));
        guardarCliente(new Cliente(0, "20601234567", "Constructora Global", "Av. Arequipa 999", "01-444-5555", "ventas@global.com", "Ing. Maria Lopez"));
    }

    public List<Cliente> obtenerTodos() {
        return registroClientes;
    }

    public Cliente buscarPorId(int id) {
        return registroClientes.stream().filter(c -> c.getId() == id).findFirst().orElse(null);
    }

    public void guardarCliente(Cliente c) {
        if (c.getId() == 0) {
            c.setId(contadorIds++);
            registroClientes.add(c);
        } else {
            eliminarCliente(c.getId());
            registroClientes.add(c);
        }
    }

    public void eliminarCliente(int id) {
        registroClientes.removeIf(c -> c.getId() == id);
    }
}