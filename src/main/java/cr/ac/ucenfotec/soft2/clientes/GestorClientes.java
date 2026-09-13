/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.ucenfotec.soft2.clientes;

import java.util.ArrayList;

/**
 *
 * @author Carlos Carballo Villalobos
 */
public class GestorClientes {

    private ArrayList<Cliente> clientes;

    public GestorClientes() {
        clientes = new ArrayList<>();

        // LISTA FALSA
        clientes.add(new Cliente("Ana", "García", "301450789", "ana.garcia@email.com"));
        clientes.add(new Cliente("Luis", "Rodríguez", "205670123", "luis.rodriguez@email.com"));
        clientes.add(new Cliente("María", "Fernández", "108920456", "maria.fernandez@email.com"));
    }

    public void agregarCliente(String nombre, String apellido, String cedula, String correo) {
        clientes.add(new Cliente(nombre, apellido, cedula, correo));
    }
    
    // FUNCION LAMBDA SIMPLICAIFA AL BUCLE FOR
    
    public boolean eliminarCliente(String cedula) {
        return clientes.removeIf(cliente -> cliente.getCedula().equals(cedula));
    }

    /**
     * public boolean eliminarCliente(String cedula) { 
     *  for (Cliente cliente : clientes) { 
     *      if (cliente.getCedula().equals(cedula)) {
     *          clientes.remove(cliente); 
     *          return true; 
     *      } 
     *  } 
     *  return false; 
     * }
     *
     */
    
    public Cliente buscarCliente(String cedula) {
        return clientes.stream()
                .filter(cliente -> cliente.getCedula().equals(cedula))
                .findFirst()
                .orElse(null);
    }

    public ArrayList<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(ArrayList<Cliente> clientes) {
        this.clientes = clientes;
    }

}
