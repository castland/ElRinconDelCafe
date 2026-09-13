/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.ucenfotec.soft2.usuarios;
import java.util.ArrayList;

/**
 *
 * @author Carlos Carballo Villalobos
 */
public class GestorUsuarios {
    public ArrayList<Usuario> usuarios;

    public GestorUsuarios() {
        usuarios = new ArrayList<>();
        usuarios.add(new Usuario("Carlos", "Villalobos", "402350652", "Gerente"));
        usuarios.add(new Usuario("Mauricio", "Zamora", "987654321", "Gerente"));
        usuarios.add(new Usuario("Kenner", "Gamboa", "901320842", "Barista"));
        usuarios.add(new Usuario("John", "Doe", "123456789", "Barista"));
    }

    public Usuario validarUsuario(String nombre, String cedula) {
        for (Usuario u : usuarios) {
            if (u.getNombre().equals(nombre) && u.getCedula().equals(cedula)) {
                return u;
            }
        }
        return null;
    }
    
    public ArrayList<Usuario> getUsuarios() {
        return usuarios;
    }
    
    public void agregarUsuario(String nombre, String apellido, String cedula, String rol) {
        usuarios.add(new Usuario(nombre, apellido, cedula, rol));
    }
}
