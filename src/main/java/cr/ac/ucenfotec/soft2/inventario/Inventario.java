package cr.ac.ucenfotec.soft2.inventario;

import cr.ac.ucenfotec.soft2.db.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Inventario con persistencia en SQLite.
 *
 * Mantiene la API pública usada por la interfaz (obtenerTodosLosProductos,
 * buscarProducto, agregarProducto, getListaProductos). Ahora cada operación
 * lee o escribe en la base de datos, y el stock se descuenta de forma
 * persistente al finalizar una venta.
 *
 * @author Kenner Gamboa Suarez
 */
public class Inventario {

    public Inventario() {
        Database.init();
    }

    public void agregarProducto(Producto nuevo) {
        String sql = "INSERT INTO productos (codigo, nombre, categoria, precio, stock) VALUES (?,?,?,?,?)";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevo.getCodigoProducto());
            ps.setString(2, nuevo.getNombreProducto());
            ps.setString(3, nuevo.getCategoriaProducto());
            ps.setDouble(4, nuevo.getPrecioProducto());
            ps.setInt(5, nuevo.getCantidadStock());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al agregar producto: " + e.getMessage(), e);
        }
    }

    public Producto buscarProducto(String codigo) {
        String sql = "SELECT codigo, nombre, categoria, precio, stock FROM productos WHERE codigo = ? COLLATE NOCASE";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar producto: " + e.getMessage(), e);
        }
        return null;
    }

    /** Actualiza nombre, categoría, precio y stock de un producto existente. */
    public void actualizarProducto(Producto p) {
        String sql = "UPDATE productos SET nombre = ?, categoria = ?, precio = ?, stock = ? WHERE codigo = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getNombreProducto());
            ps.setString(2, p.getCategoriaProducto());
            ps.setDouble(3, p.getPrecioProducto());
            ps.setInt(4, p.getCantidadStock());
            ps.setString(5, p.getCodigoProducto());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar producto: " + e.getMessage(), e);
        }
    }

    /** Descuenta la cantidad indicada del stock de un producto (venta). */
    public void descontarStock(String codigo, int cantidad) {
        String sql = "UPDATE productos SET stock = stock - ? WHERE codigo = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setString(2, codigo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al descontar stock: " + e.getMessage(), e);
        }
    }

    public ArrayList<Producto> obtenerTodosLosProductos() {
        ArrayList<Producto> lista = new ArrayList<>();
        String sql = "SELECT codigo, nombre, categoria, precio, stock FROM productos ORDER BY codigo";
        try (Connection con = Database.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar productos: " + e.getMessage(), e);
        }
        return lista;
    }

    public ArrayList<Producto> getListaProductos() {
        return obtenerTodosLosProductos();
    }

    private Producto mapear(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getString("categoria"),
                rs.getDouble("precio"),
                rs.getInt("stock"));
    }
}
