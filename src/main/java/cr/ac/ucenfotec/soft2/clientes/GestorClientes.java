package cr.ac.ucenfotec.soft2.clientes;

import cr.ac.ucenfotec.soft2.db.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Gestor de clientes con persistencia en SQLite.
 *
 * Mantiene la misma API pública que antes (los formularios no cambian), pero
 * ahora cada operación lee o escribe en la base de datos. Los datos sobreviven
 * al cerrar la aplicación.
 *
 * @author Carlos Carballo Villalobos
 */
public class GestorClientes {

    public GestorClientes() {
        Database.init();
    }

    public void agregarCliente(String nombre, String apellido, String cedula, String correo) {
        String sql = "INSERT INTO clientes (cedula, nombre, apellido, correo) VALUES (?,?,?,?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cedula);
            ps.setString(2, nombre);
            ps.setString(3, apellido);
            ps.setString(4, correo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al agregar cliente: " + e.getMessage(), e);
        }
    }

    public boolean eliminarCliente(String cedula) {
        String sql = "DELETE FROM clientes WHERE cedula = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cedula);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar cliente: " + e.getMessage(), e);
        }
    }

    public Cliente buscarCliente(String cedula) {
        String sql = "SELECT nombre, apellido, cedula, correo FROM clientes WHERE cedula = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar cliente: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Actualiza los datos de un cliente existente (identificado por su cédula).
     * La cédula no se modifica. Útil para el modo edición del formulario.
     */
    public void actualizarCliente(Cliente cliente) {
        String sql = "UPDATE clientes SET nombre = ?, apellido = ?, correo = ? WHERE cedula = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getCorreo());
            ps.setString(4, cliente.getCedula());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar cliente: " + e.getMessage(), e);
        }
    }

    public ArrayList<Cliente> getClientes() {
        ArrayList<Cliente> lista = new ArrayList<>();
        String sql = "SELECT nombre, apellido, cedula, correo FROM clientes ORDER BY nombre";
        try (Connection con = Database.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar clientes: " + e.getMessage(), e);
        }
        return lista;
    }

    /**
     * Busca clientes cuya cédula, nombre o apellido contengan el texto dado
     * (coincidencia parcial, no exacta). Útil para filtrar mientras se escribe.
     * Un texto vacío devuelve todos los clientes.
     */
    public ArrayList<Cliente> buscarCoincidencias(String texto) {
        ArrayList<Cliente> lista = new ArrayList<>();
        String patron = "%" + (texto == null ? "" : texto.trim()) + "%";
        String sql = """
            SELECT nombre, apellido, cedula, correo
            FROM clientes
            WHERE cedula LIKE ? OR nombre LIKE ? OR apellido LIKE ?
            ORDER BY cedula
            LIMIT 50""";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, patron);
            ps.setString(2, patron);
            ps.setString(3, patron);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al filtrar clientes: " + e.getMessage(), e);
        }
        return lista;
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("cedula"),
                rs.getString("correo"));
    }
}
