/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.ucenfotec.soft2.ventas;

import cr.ac.ucenfotec.soft2.clientes.Cliente;
import cr.ac.ucenfotec.soft2.inventario.Producto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 *
 * @author Carlos Carballo Villalobos
 */
public class Venta {

    private String numeroVenta;
    private Cliente cliente;
    private ArrayList<DetalleVenta> detalles;
    private LocalDateTime fechaHora;
    private double subtotal;
    private double iva;
    private double total;
    private String usuarioVendedor;

    private static final double PORCENTAJE_IVA = 0.13;

    public Venta(String numeroVenta, Cliente cliente, String usuarioVendedor) {
        this.numeroVenta = numeroVenta;
        this.cliente = cliente;
        this.detalles = new ArrayList<>();
        this.fechaHora = LocalDateTime.now();
        this.subtotal = 0;
        this.iva = 0;
        this.total = 0;
        this.usuarioVendedor = usuarioVendedor;
    }

    public void agregarDetalle(DetalleVenta detalle) {
        detalles.add(detalle);
        calcularTotal();
    }

    public boolean validarStock() {
        for (DetalleVenta detalle : detalles) {
            if (detalle.getProducto().getCantidadStock() < detalle.getCantidad()) {
                return false;
            }
        }
        return true;
    }

    public void calcularTotal() {
        subtotal = 0;
        for (DetalleVenta detalle : detalles) {
            subtotal += detalle.getSubtotal();
        }
        iva = subtotal * PORCENTAJE_IVA;
        total = subtotal + iva;
    }

    public Factura finalizarVenta() {
        if (!validarStock()) {
            return null;
        }

        // Actualizar inventario
        for (DetalleVenta detalle : detalles) {
            Producto p = detalle.getProducto();
            p.setCantidadStock(p.getCantidadStock() - detalle.getCantidad());
        }

        // Generar factura
        Factura factura = new Factura(this);
        return factura;
    }

    public String obtenerResumen() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        sb.append("========================================\n");
        sb.append("         EL RINCÓN DEL CAFÉ\n");
        sb.append("========================================\n");
        sb.append("Venta #: ").append(numeroVenta).append("\n");
        sb.append("Fecha: ").append(fechaHora.format(formatter)).append("\n");
        sb.append("Cliente: ").append(cliente.getNombre()).append(" ").append(cliente.getApellido()).append("\n");
        sb.append("Cédula: ").append(cliente.getCedula()).append("\n");
        sb.append("Vendedor: ").append(usuarioVendedor).append("\n");
        sb.append("========================================\n");
        sb.append("PRODUCTOS:\n");
        sb.append("----------------------------------------\n");

        for (DetalleVenta detalle : detalles) {
            sb.append(String.format("%-20s x%d\n",
                    detalle.getProducto().getNombreProducto(),
                    detalle.getCantidad()));
            sb.append(String.format("  ₡%.2f x %d = ₡%.2f\n",
                    detalle.getPrecioUnitario(),
                    detalle.getCantidad(),
                    detalle.getSubtotal()));
        }

        sb.append("========================================\n");
        sb.append(String.format("Subtotal:        ₡%.2f\n", subtotal));
        sb.append(String.format("IVA (13%%):       ₡%.2f\n", iva));
        sb.append("========================================\n");
        sb.append(String.format("TOTAL:           ₡%.2f\n", total));
        sb.append("========================================\n");
        sb.append("      ¡Gracias por su compra!\n");
        sb.append("========================================\n");

        return sb.toString();
    }

    public String getNumeroVenta() {
        return numeroVenta;
    }

    public void setNumeroVenta(String numeroVenta) {
        this.numeroVenta = numeroVenta;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public ArrayList<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(ArrayList<DetalleVenta> detalles) {
        this.detalles = detalles;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getIva() {
        return iva;
    }

    public double getTotal() {
        return total;
    }

    public String getUsuarioVendedor() {
        return usuarioVendedor;
    }

    public void setUsuarioVendedor(String usuarioVendedor) {
        this.usuarioVendedor = usuarioVendedor;
    }

}
