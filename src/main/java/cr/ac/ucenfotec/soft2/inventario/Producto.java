/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.ucenfotec.soft2.inventario;

/**
 *
 * @author Kenner Gamboa Suarez
 */
public class Producto {
    private String codigoProducto;
    private String nombreProducto;
    private String categoriaProducto;
    private double precioProducto;
    private int cantidadStock;

    public Producto(String codigoProducto, String nombreProducto, String categoriaProducto, double precioProducto, int cantidadStock) {
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.categoriaProducto = categoriaProducto;
        this.precioProducto = precioProducto;
        this.cantidadStock = cantidadStock;
    }

    public int getCantidadStock() {
        return cantidadStock;
    }

    public void setCantidadStock(int cantidadStock) {
        this.cantidadStock = cantidadStock;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getCategoriaProducto() {
        return categoriaProducto;
    }

    public void setCategoriaProducto(String categoriaProducto) {
        this.categoriaProducto = categoriaProducto;
    }

    public double getPrecioProducto() {
        return precioProducto;
    }

    public void setPrecioProducto(double precioProducto) {
        this.precioProducto = precioProducto;
    }
    
    @Override
    public String toString() {
        return codigoProducto + " - " + nombreProducto + " (" + categoriaProducto + ") ₡" + precioProducto + " | Stock: " + cantidadStock;
    }
}
