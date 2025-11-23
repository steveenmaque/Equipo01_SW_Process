package com.inflesusventas.view.notacredito;

import com.inflesusventas.model.NotaCredito;
import com.inflesusventas.model.ItemNotaCredito;
import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class NotaCreditoPaso3View extends JPanel {

    private JTextArea txtVistaPrevia;

    public NotaCreditoPaso3View() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JLabel lblTitulo = new JLabel("Paso 3: Vista Previa y Emisión");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblTitulo, BorderLayout.NORTH);

        txtVistaPrevia = new JTextArea();
        txtVistaPrevia.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtVistaPrevia.setEditable(false);

        JScrollPane scroll = new JScrollPane(txtVistaPrevia);
        add(scroll, BorderLayout.CENTER);
    }

    public void cargarVistaPreviaNc(NotaCredito nc) {
        StringBuilder sb = new StringBuilder();
        sb.append("==================================================\n");
        sb.append("         VISTA PREVIA DE NOTA DE CRÉDITO          \n");
        sb.append("==================================================\n\n");

        sb.append("EMISOR: INFLE SUS VENTAS S.R.L.\n");
        sb.append("RUC: 20554524051\n\n");

        sb.append("CLIENTE:\n");
        sb.append("Razón Social: ").append(nc.getRazonSocialCliente()).append("\n");
        sb.append("RUC: ").append(nc.getRucCliente()).append("\n\n");

        sb.append("DATOS DEL DOCUMENTO:\n");
        sb.append("Tipo: ").append(nc.getTipoNotaCredito()).append("\n");
        sb.append("Factura Ref: ").append(nc.getNumeroFacturaRef()).append("\n");
        sb.append("Fecha Emisión: ").append(nc.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .append("\n");
        sb.append("Motivo: ").append(nc.getMotivoSustento()).append("\n\n");

        sb.append("DETALLE DE ÍTEMS:\n");
        sb.append(String.format("%-5s %-40s %-10s %-10s\n", "CANT", "DESCRIPCIÓN", "P.UNIT", "SUBTOTAL"));
        sb.append("----------------------------------------------------------------------\n");

        double subtotal = 0;
        for (ItemNotaCredito item : nc.getItems()) {
            String desc = item.formatearDescripcionParaPDF();
            if (desc.length() > 38)
                desc = desc.substring(0, 38) + "..";

            sb.append(String.format("%-5d %-40s %-10.2f %-10.2f\n",
                    item.getCantidad(),
                    desc,
                    item.getValorUnitario(),
                    item.getSubtotal()));

            subtotal += item.getSubtotal();
        }
        sb.append("----------------------------------------------------------------------\n");

        double igv = subtotal * 0.18;
        double total = subtotal + igv;

        sb.append(String.format("%60s %10.2f\n", "SUBTOTAL:", subtotal));
        sb.append(String.format("%60s %10.2f\n", "IGV (18%):", igv));
        sb.append(String.format("%60s %10.2f\n", "TOTAL:", total));

        txtVistaPrevia.setText(sb.toString());
        txtVistaPrevia.setCaretPosition(0);
    }
}
