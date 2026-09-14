package cr.ac.ucenfotec.soft2.ui;

import cr.ac.ucenfotec.soft2.facturacion.HistorialVentas;
import cr.ac.ucenfotec.soft2.inventario.Inventario;
import cr.ac.ucenfotec.soft2.inventario.Producto;
import cr.ac.ucenfotec.soft2.ventas.Factura;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Pantalla de inicio (dashboard): resumen del negocio con tarjetas de
 * indicadores, ventas recientes y productos con stock bajo.
 */
public class DashboardPanel extends javax.swing.JPanel implements Refrescable {

    private static final int UMBRAL_STOCK_BAJO = 25;

    private final NavigationHost host;
    private final Inventario inventario;
    private final HistorialVentas historial;
    private final String nombreUsuario;

    private JPanel tarjetas;
    private JPanel listaVentas;
    private JPanel listaStock;

    public DashboardPanel(NavigationHost host, Inventario inventario,
                          HistorialVentas historial, String nombreUsuario) {
        this.host = host;
        this.inventario = inventario;
        this.historial = historial;
        this.nombreUsuario = nombreUsuario;
        initUI();
        refrescar();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        ModuleScaffold sc = ModuleScaffold.build(
                "Hola, " + nombreUsuario,
                "Resumen general de El Rincón del Café");

        JPanel body = new JPanel(new BorderLayout(0, 18));
        body.setOpaque(false);

        // Tarjetas de indicadores (fila superior)
        tarjetas = new JPanel(new GridLayout(1, 4, 16, 0));
        tarjetas.setOpaque(false);
        body.add(tarjetas, BorderLayout.NORTH);

        // Dos columnas: ventas recientes | stock bajo
        JPanel columnas = new JPanel(new GridLayout(1, 2, 16, 0));
        columnas.setOpaque(false);

        JPanel colVentas = UITheme.card(new BorderLayout(0, 10));
        colVentas.add(seccion("Ventas recientes"), BorderLayout.NORTH);
        listaVentas = new JPanel();
        listaVentas.setOpaque(false);
        listaVentas.setLayout(new BoxLayout(listaVentas, BoxLayout.Y_AXIS));
        JScrollPane spVentas = new JScrollPane(listaVentas);
        spVentas.setBorder(null);
        spVentas.getViewport().setOpaque(false);
        spVentas.setOpaque(false);
        colVentas.add(spVentas, BorderLayout.CENTER);

        JPanel colStock = UITheme.card(new BorderLayout(0, 10));
        colStock.add(seccion("Productos con stock bajo (< " + UMBRAL_STOCK_BAJO + ")"), BorderLayout.NORTH);
        listaStock = new JPanel();
        listaStock.setOpaque(false);
        listaStock.setLayout(new BoxLayout(listaStock, BoxLayout.Y_AXIS));
        JScrollPane spStock = new JScrollPane(listaStock);
        spStock.setBorder(null);
        spStock.getViewport().setOpaque(false);
        spStock.setOpaque(false);
        colStock.add(spStock, BorderLayout.CENTER);

        columnas.add(colVentas);
        columnas.add(colStock);
        body.add(columnas, BorderLayout.CENTER);

        sc.content.setLayout(new BorderLayout());
        sc.content.add(body, BorderLayout.CENTER);
        add(sc.root, BorderLayout.CENTER);
    }

    @Override
    public void refrescar() {
        List<Producto> productos = inventario.obtenerTodosLosProductos();
        List<Factura> facturas = historial.getFacturas();

        int totalProductos = productos.size();
        int stockTotal = productos.stream().mapToInt(Producto::getCantidadStock).sum();
        int bajos = (int) productos.stream()
                .filter(p -> p.getCantidadStock() < UMBRAL_STOCK_BAJO).count();
        int totalFacturas = facturas.size();

        // --- Tarjetas ---
        tarjetas.removeAll();
        tarjetas.add(tarjeta(Icons.facturas(30, UITheme.BRAND), String.valueOf(totalFacturas), "Facturas emitidas"));
        tarjetas.add(tarjeta(Icons.boxes(30, UITheme.BRAND_LIGHT), String.valueOf(totalProductos), "Productos"));
        tarjetas.add(tarjeta(Icons.chart(30, UITheme.SUCCESS), String.valueOf(stockTotal), "Unidades en stock"));
        tarjetas.add(tarjeta(Icons.warning(30, UITheme.DANGER), String.valueOf(bajos), "Stock bajo"));

        // --- Ventas recientes (últimas 8) ---
        listaVentas.removeAll();
        if (facturas.isEmpty()) {
            listaVentas.add(vacio("Aún no hay ventas registradas."));
        } else {
            int desde = Math.max(0, facturas.size() - 8);
            for (int i = facturas.size() - 1; i >= desde; i--) {
                Factura f = facturas.get(i);
                listaVentas.add(fila(f.getNumeroFactura(), f.getFechaEmisionTexto()));
            }
        }

        // --- Stock bajo ---
        listaStock.removeAll();
        boolean hayBajos = false;
        for (Producto p : productos) {
            if (p.getCantidadStock() < UMBRAL_STOCK_BAJO) {
                hayBajos = true;
                listaStock.add(fila(p.getNombreProducto() + " (" + p.getCodigoProducto() + ")",
                        p.getCantidadStock() + " u."));
            }
        }
        if (!hayBajos) {
            listaStock.add(vacio("Todo el inventario tiene buen stock."));
        }

        revalidate();
        repaint();
    }

    private JPanel tarjeta(javax.swing.Icon icono, String valor, String etiqueta) {
        JPanel card = UITheme.card(new BorderLayout(10, 0));

        JLabel ic = new JLabel(icono);
        ic.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 8));

        JPanel texto = new JPanel();
        texto.setOpaque(false);
        texto.setLayout(new BoxLayout(texto, BoxLayout.Y_AXIS));
        JLabel val = new JLabel(valor);
        val.setFont(UITheme.FONT_DISPLAY);
        val.setForeground(UITheme.TEXT);
        val.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lab = new JLabel(etiqueta);
        lab.setFont(UITheme.FONT_SMALL);
        lab.setForeground(UITheme.TEXT_MUTED);
        lab.setAlignmentX(Component.LEFT_ALIGNMENT);
        texto.add(val);
        texto.add(lab);

        card.add(ic, BorderLayout.WEST);
        card.add(texto, BorderLayout.CENTER);
        return card;
    }

    private JLabel seccion(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(UITheme.FONT_H2);
        l.setForeground(UITheme.BRAND);
        return l;
    }

    private JPanel fila(String izquierda, String derecha) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(8, 2, 8, 2));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel izq = new JLabel(izquierda);
        izq.setFont(UITheme.FONT_BODY);
        izq.setForeground(UITheme.TEXT);
        JLabel der = new JLabel(derecha);
        der.setFont(UITheme.FONT_BODY_BD);
        der.setForeground(UITheme.TEXT_MUTED);
        p.add(izq, BorderLayout.WEST);
        p.add(der, BorderLayout.EAST);
        // Línea separadora inferior sutil.
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
                BorderFactory.createEmptyBorder(8, 2, 8, 2)));
        return p;
    }

    private JLabel vacio(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(UITheme.FONT_BODY);
        l.setForeground(UITheme.TEXT_MUTED);
        l.setBorder(BorderFactory.createEmptyBorder(10, 2, 0, 0));
        return l;
    }
}
