/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.ucenfotec.soft2.inventario;

import java.util.ArrayList;

/**
 *
 * @author Kenner Gamboa Suarez
 */
public class Inventario {
    private ArrayList<Producto> listaProductos;

    public Inventario() {
        listaProductos = new ArrayList<>();
        precargarProductos();
    }
    
    public void precargarProductos() {
        // Café
        listaProductos.add(new Producto("CAF-001", "Café Negro", "Café", 1200, 50));
        listaProductos.add(new Producto("CAF-002", "Capuchino", "Café", 1500, 45));
        listaProductos.add(new Producto("CAF-003", "Latte", "Café", 1600, 40));
        listaProductos.add(new Producto("CAF-004", "Espresso", "Café", 1000, 60));
        listaProductos.add(new Producto("CAF-005", "Americano", "Café", 1300, 55));
        
        // Bebidas Frías
        listaProductos.add(new Producto("BEB-001", "Té Frío", "Bebida Fría", 1000, 30));
        listaProductos.add(new Producto("BEB-002", "Frappé de Chocolate", "Bebida Fría", 1800, 25));
        listaProductos.add(new Producto("BEB-003", "Smoothie de Fresa", "Bebida Fría", 2000, 20));
        listaProductos.add(new Producto("BEB-004", "Limonada Natural", "Bebida Fría", 1200, 35));
        listaProductos.add(new Producto("BEB-005", "Café Frío", "Bebida Fría", 1500, 28));
        
        // Pastelería
        listaProductos.add(new Producto("PAS-001", "Croissant", "Pastelería", 1300, 40));
        listaProductos.add(new Producto("PAS-002", "Brownie", "Pastelería", 1500, 35));
        listaProductos.add(new Producto("PAS-003", "Cheesecake", "Pastelería", 2200, 20));
        listaProductos.add(new Producto("PAS-004", "Muffin de Arándanos", "Pastelería", 1400, 30));
        listaProductos.add(new Producto("PAS-005", "Galleta de Chocolate", "Pastelería", 800, 50));
    }
    
    public void agregarProducto(Producto nuevo) {
        listaProductos.add(nuevo);
    }
    
    public Producto buscarProducto(String codigo) {
        for (Producto p : listaProductos) {
            if (p.getCodigoProducto().equalsIgnoreCase(codigo)) {
                return p;
            }
        }
        return null;
    }
    
    public ArrayList<Producto> obtenerTodosLosProductos() {
        return listaProductos;
    }

    public ArrayList<Producto> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(ArrayList<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }
}
