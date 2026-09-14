package cr.ac.ucenfotec.soft2.usuarios;

import cr.ac.ucenfotec.soft2.db.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Gestor de usuarios con persistencia en SQLite.
 *
 * Conserva la API pública usada por la interfaz, pero ahora lee y escribe en la
 * base de datos. Los usuarios sobreviven al cerrar la aplicación.
 *
 * @author Carlos Carballo Villalobos
 */
public class GestorUsuarios {

    public GestorUsuarios() {
        Database.init();
    }

    public Usuario validarUsuario(String nombre, String cedula) {
        String sql = "SELECT nombre, apellido, cedula, rol FROM usuarios WHERE nombre = ? AND cedula = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al validar usuario: " + e.getMessage(), e);
        }
        return null;
    }

    public ArrayList<Usuario> getUsuarios() {
        ArrayList<Usuario> lista = new ArrayList<>();
        String sql = "SELECT nombre, apellido, cedula, rol FROM usuarios ORDER BY nombre";
        try (Connection con = Database.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar usuarios: " + e.getMessage(), e);
        }
        return lista;
    }

    public void agregarUsuario(String nombre, String apellido, String cedula, String rol) {
        String sql = "INSERT INTO usuarios (cedula, nombre, apellido, rol) VALUES (?,?,?,?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cedula);
            ps.setString(2, nombre);
            ps.setString(3, apellido);
            ps.setString(4, rol);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al agregar usuario: " + e.getMessage(), e);
        }
    }

    public boolean eliminarUsuario(String cedula) {
        String sql = "DELETE FROM usuarios WHERE cedula = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cedula);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar usuario: " + e.getMessage(), e);
        }
    }

    /** Actualiza nombre, apellido y rol de un usuario (la cédula no cambia). */
    public void actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre = ?, apellido = ?, rol = ? WHERE cedula = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getRol());
            ps.setString(4, usuario.getCedula());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar usuario: " + e.getMessage(), e);
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("cedula"),
                rs.getString("rol"));
    }
}
