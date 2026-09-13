/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.ucenfotec.soft2.app;

import javax.swing.Icon;
import javax.swing.JOptionPane;

/**
 *
 * @author Carlos Carballo Villalobos
 */
public class Utilities {
    
    public static boolean preguntarSioNo(String pregunta, String titulo, java.awt.Component padre) {
    boolean salida = false;
    // 1. Define las etiquetas de los botones en espa�ol
    Object[] opcionesPersonalizadas = {"Si", "No"};

    // Puedes usar null si no quieres un icono o una de las constantes de JOptionPane
    Icon icono = null;

    // 2. Llama a showOptionDialog para usar las etiquetas
    int respuesta = JOptionPane.showOptionDialog(
            padre, // Componente padre (null para centrar)
            pregunta, // Mensaje
            titulo, // T�tulo de la ventana
            JOptionPane.YES_NO_OPTION, // Tipo de botones (define el comportamiento)
            JOptionPane.QUESTION_MESSAGE, // Tipo de mensaje/icono (Pregunta)
            icono, // Icono a mostrar
            opcionesPersonalizadas, // *** ARRAY DE ETIQUETAS PERSONALIZADAS ***
            opcionesPersonalizadas[0] // Opci�n por defecto (Aqu� es "S�")
    );

    // 3. Manejar el resultado
    // Aunque los textos son personalizados, el valor retornado sigue siendo un entero:
    // 0 -> Opci�n 1 ("S�")
    // 1 -> Opci�n 2 ("No")
    // -1 -> Cerrar Ventana (X)
    salida = respuesta == 0;
    return salida;
    }
    
    
    
}
