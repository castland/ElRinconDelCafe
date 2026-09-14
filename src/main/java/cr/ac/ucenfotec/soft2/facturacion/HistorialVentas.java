package cr.ac.ucenfotec.soft2.facturacion;

import cr.ac.ucenfotec.soft2.db.Database;
import cr.ac.ucenfotec.soft2.ventas.DetalleVenta;
import cr.ac.ucenfotec.soft2.ventas.Factura;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Historial de facturas con persistencia en SQLite.
 *
 * Al registrar una factura se guarda su cabecera, sus líneas de detalle y el
 * texto completo generado. Al consultar el historial, las facturas se
 * reconstruyen desde la base de datos, por lo que sobreviven a reinicios.
 *
 * @author kenner
 */
public class HistorialVentas {

    public HistorialVentas() {
        Database.init();
    }

    /** Registra una factura (cabecera + detalle + texto) en la base de datos. */
    public void agregarFactura(Factura f) {
        String sqlCab = """
            INSERT INTO facturas
                (numero_factura, numero_venta, fecha_emision, cliente_cedula,
                 cliente_nombre, vendedor, subtotal, iva, total, estado)
            VALUES (?,?,?,?,?,?,?,?,?,?)""";
        String sqlDet = """
            INSERT INTO factura_detalle
                (numero_factura, codigo_producto, nombre_producto, cantidad,
                 precio_unitario, subtotal)
            VALUES (?,?,?,?,?,?)""";
        // Guardamos también el texto renderizado para poder mostrarlo tal cual.
        String texto = f.generarFacturaTexto();

        try (Connection con = Database.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sqlCab)) {
                var v = f.getVenta();
                ps.setString(1, f.getNumeroFactura());
                ps.setString(2, v != null ? v.getNumeroVenta() : null);
                ps.setString(3, f.getFechaEmisionTexto());
                ps.setString(4, v != null ? v.getCliente().getCedula() : null);
                ps.setString(5, v != null ? v.getCliente().getNombre() + " " + v.getCliente().getApellido() : null);
                ps.setString(6, v != null ? v.getUsuarioVendedor() : null);
                ps.setDouble(7, v != null ? v.getSubtotal() : 0);
                ps.setDouble(8, v != null ? v.getIva() : 0);
                ps.setDouble(9, v != null ? v.getTotal() : 0);
                ps.setString(10, f.getEstado());
                ps.executeUpdate();
            }
            if (f.getVenta() != null) {
                try (PreparedStatement ps = con.prepareStatement(sqlDet)) {
                    for (DetalleVenta d : f.getVenta().getDetalles()) {
                        ps.setString(1, f.getNumeroFactura());
                        ps.setString(2, d.getProducto().getCodigoProducto());
                        ps.setString(3, d.getProducto().getNombreProducto());
                        ps.setInt(4, d.getCantidad());
                        ps.setDouble(5, d.getPrecioUnitario());
                        ps.setDouble(6, d.getSubtotal());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
            // Persistimos el texto en una columna adicional (tabla aparte simple).
            guardarTexto(con, f.getNumeroFactura(), texto);
            con.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar factura: " + e.getMessage(), e);
        }
    }

    private void guardarTexto(Connection con, String numero, String texto) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS factura_texto (
                    numero_factura TEXT PRIMARY KEY,
                    texto TEXT
                )""");
        }
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT OR REPLACE INTO factura_texto (numero_factura, texto) VALUES (?,?)")) {
            ps.setString(1, numero);
            ps.setString(2, texto);
            ps.executeUpdate();
        }
    }

    public Factura buscarFactura(String numero) {
        String sql = """
            SELECT f.numero_factura, f.numero_venta, f.fecha_emision, f.estado,
                   t.texto
            FROM facturas f
            LEFT JOIN factura_texto t ON t.numero_factura = f.numero_factura
            WHERE f.numero_factura = ?""";
        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar factura: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Factura> getFacturas() {
        List<Factura> lista = new ArrayList<>();
        String sql = """
            SELECT f.numero_factura, f.numero_venta, f.fecha_emision, f.estado,
                   t.texto
            FROM facturas f
            LEFT JOIN factura_texto t ON t.numero_factura = f.numero_factura
            ORDER BY f.fecha_emision""";
        try (Connection con = Database.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            // Si aún no existe la tabla de texto (primer arranque), devolver vacío.
            return lista;
        }
        return lista;
    }

    public void mostrarHistorial() {
        System.out.println("=== Historial de Ventas ===");
        for (Factura f : getFacturas()) {
            System.out.println(f.generarFacturaTexto());
            System.out.println("--------------------------");
        }
    }

    private Factura mapear(ResultSet rs) throws SQLException {
        return new Factura(
                rs.getString("numero_factura"),
                rs.getString("numero_venta"),
                rs.getString("fecha_emision"),
                rs.getString("estado"),
                rs.getString("texto"));
    }
}
