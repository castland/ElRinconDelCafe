/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.ucenfotec.soft2.ventas;

import cr.ac.ucenfotec.soft2.inventario.Producto;

/**
 *
 * @author Carlos Carballo Villalobos
 */
public class DetalleVenta {
    private Producto producto;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public DetalleVenta(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecioProducto();
        this.subtotal = precioUnitario * cantidad;
    }
    
    public void recalcularSubtotal() {
        this.subtotal = precioUnitario * cantidad;
    }
    
    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
        this.precioUnitario = producto.getPrecioProducto();
        recalcularSubtotal();
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        recalcularSubtotal();
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }
}
