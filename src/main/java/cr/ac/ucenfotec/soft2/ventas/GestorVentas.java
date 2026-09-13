/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.ucenfotec.soft2.ventas;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 *
 * @author Carlos Carballo Villalobos
 */
public class GestorVentas {
    private ArrayList<Venta> ventas;
    private ArrayList<Factura> facturas;
    private int contadorVentas;

    public GestorVentas() {
        this.ventas = new ArrayList<>();
        this.facturas = new ArrayList<>();
        this.contadorVentas = 1;
    }
    
    public String generarNumeroVenta() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String fecha = LocalDateTime.now().format(formatter);
        String numero = String.format("%s-%04d", fecha, contadorVentas);
        contadorVentas++;
        return numero;
    }
    
    public void registrarVenta(Venta venta) {
        ventas.add(venta);
    }
    
    public void registrarFactura(Factura factura) {
        facturas.add(factura);
    }
    
    public Venta buscarVenta(String numeroVenta) {
        for (Venta v : ventas) {
            if (v.getNumeroVenta().equals(numeroVenta)) {
                return v;
            }
            
        }
        return null;
    }
    
    public Factura buscarFactura(String numeroFactura) {
        for (Factura f : facturas) {
            if (f.getNumeroFactura().equals(numeroFactura)) {
                return f;
            }
        }
        return null;
    }
    
    public ArrayList<Venta> getVentas() {
        return ventas;
    }

    public void setVentas(ArrayList<Venta> ventas) {
        this.ventas = ventas;
    }

    public ArrayList<Factura> getFacturas() {
        return facturas;
    }
}
