/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.ucenfotec.soft2.reportes;

/**
 *
 * @author kenner
 */
import cr.ac.ucenfotec.soft2.facturacion.HistorialVentas;
import cr.ac.ucenfotec.soft2.ventas.Factura;
import java.util.List;

public class ReporteVentas {

    private HistorialVentas historial;

    public ReporteVentas(HistorialVentas historial) {
        this.historial = historial;
    }

    // Total de ventas registradas
    public void generarReporteCantidadVentas() {
        List<Factura> facturas = historial.getFacturas();
        System.out.println("Total de ventas realizadas: " + facturas.size());
    }

    // Total facturado incluyendo IVA
    public void generarReporteTotalFacturado() {
        double totalGeneral = 0;
        for (Factura f : historial.getFacturas()) {
            totalGeneral += f.getVenta().getTotal();
        }
        System.out.println("Total facturado: " + totalGeneral);
    }

    // Listado de clientes atendidos
    public void generarReporteClientes() {
        System.out.println("Clientes atendidos:");
        for (Factura f : historial.getFacturas()) {
            System.out.println("- " + f.getVenta().getCliente().getNombre() + " (" + f.getVenta().getCliente().getCedula() + ")");
        }
    }
}
