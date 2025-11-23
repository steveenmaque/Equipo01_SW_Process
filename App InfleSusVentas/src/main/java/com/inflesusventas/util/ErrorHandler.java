package com.inflesusventas.util;

import javax.swing.*;
import java.awt.*;

public class ErrorHandler {

    public static void mostrarError(Component parent, String mensaje, Exception e) {
        JOptionPane.showMessageDialog(parent,
                mensaje + "\n\nDetalle: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void mostrarAdvertencia(Component parent, String mensaje) {
        JOptionPane.showMessageDialog(parent, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }

    public static void mostrarExito(Component parent, String mensaje) {
        JOptionPane.showMessageDialog(parent, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirmar(Component parent, String mensaje) {
        return JOptionPane.showConfirmDialog(parent, mensaje, "Confirmación",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public static int mostrarOpciones(Component parent, String mensaje, String[] opciones) {
        return JOptionPane.showOptionDialog(parent, mensaje, "Seleccione",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, opciones, opciones[0]);
    }
}
