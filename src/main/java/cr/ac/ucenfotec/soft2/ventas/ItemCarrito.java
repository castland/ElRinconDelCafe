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
public class ItemCarrito {
    private Producto producto;
    private int cantidad;
    
    public ItemCarrito(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }
    
    public double getSubtotal() {
        return producto.getPrecioProducto() * cantidad;
    }
    
    public DetalleVenta toDetalleVenta() {
        return new DetalleVenta(producto, cantidad);
    }
    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
