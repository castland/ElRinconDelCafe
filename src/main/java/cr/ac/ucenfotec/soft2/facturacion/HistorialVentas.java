/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.ucenfotec.soft2.facturacion;

/**
 *
 * @author kenner
 */
import cr.ac.ucenfotec.soft2.ventas.Factura;
import java.util.ArrayList;
import java.util.List;

public class HistorialVentas {

    private List<Factura> facturas;

    public HistorialVentas() {
        facturas = new ArrayList<>();
    }

    // Registrar una factura en el historial
    public void agregarFactura(Factura f) {
        facturas.add(f);
    }

    // Buscar factura por número
    public Factura buscarFactura(String numero) {
        for (Factura f : facturas) {
            if (f.getNumeroFactura().equals(numero)) {
                return f;
            }
        }
        System.out.println("Error: No se encontró la factura N° " + numero);
        return null;
    }

    public void mostrarHistorial() {
        System.out.println("=== Historial de Ventas ===");
        for (Factura f : facturas) {
            System.out.println(f.generarFacturaTexto());
            System.out.println("--------------------------");
        }
    }

    public List<Factura> getFacturas() {
        return facturas;
    }
}
