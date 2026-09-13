/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.ucenfotec.soft2.ventas;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Carlos Carballo Villalobos
 */
public class Factura {
    private String numeroFactura;
    private Venta venta;
    private LocalDateTime fechaEmision;
    private String estado;

    public Factura(Venta venta) {
        this.venta = venta;
        this.numeroFactura = generarNumeroFactura();
        this.fechaEmision = LocalDateTime.now();
        this.estado = "PAGADA";
    }
    
    private String generarNumeroFactura() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "FAC-" + LocalDateTime.now().format(formatter);
    }
    
    public String generarFacturaTexto() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        
        sb.append("══════════════════════════════════════════\n");
        sb.append("          EL RINCÓN DEL CAFÉ         \n");
        sb.append("          FACTURA ELECTRÓNICA        \n");
        sb.append("══════════════════════════════════════════\n\n");
        
        sb.append("Factura #: ").append(numeroFactura).append("\n");
        sb.append("Venta #: ").append(venta.getNumeroVenta()).append("\n");
        sb.append("Fecha Emisión: ").append(fechaEmision.format(formatter)).append("\n");
        sb.append("Estado: ").append(estado).append("\n\n");
        
        sb.append("----------------------------------------\n");
        sb.append("DATOS DEL CLIENTE\n");
        sb.append("----------------------------------------\n");
        sb.append("Nombre: ").append(venta.getCliente().getNombre()).append(" ");
        sb.append(venta.getCliente().getApellido()).append("\n");
        sb.append("Cédula: ").append(venta.getCliente().getCedula()).append("\n");
        sb.append("Correo: ").append(venta.getCliente().getCorreo()).append("\n\n");
        
        sb.append("----------------------------------------\n");
        sb.append("DETALLE DE PRODUCTOS\n");
        sb.append("----------------------------------------\n");
        
        for (DetalleVenta detalle : venta.getDetalles()) {
            sb.append(String.format("%-25s x%d\n", 
                detalle.getProducto().getNombreProducto(), 
                detalle.getCantidad()));
            sb.append(String.format("  Precio Unit: ₡%.2f\n", detalle.getPrecioUnitario()));
            sb.append(String.format("  Subtotal: ₡%.2f\n\n", detalle.getSubtotal()));
        }
        
        sb.append("========================================\n");
        sb.append(String.format("Subtotal:           ₡%.2f\n", venta.getSubtotal()));
        sb.append(String.format("IVA (13%%):          ₡%.2f\n", venta.getIva()));
        sb.append("========================================\n");
        sb.append(String.format("TOTAL A PAGAR:      ₡%.2f\n", venta.getTotal()));
        sb.append("========================================\n\n");
        
        sb.append("Atendido por: ").append(venta.getUsuarioVendedor()).append("\n");
        sb.append("\n¡Gracias por su preferencia!\n");
        sb.append("Vuelva pronto\n");
        
        return sb.toString();
    }
    
    public void anularFactura() {
        this.estado = "ANULADA";
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public Venta getVenta() {
        return venta;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
        
}
