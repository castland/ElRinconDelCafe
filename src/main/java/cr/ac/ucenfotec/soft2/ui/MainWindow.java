package cr.ac.ucenfotec.soft2.ui;

import cr.ac.ucenfotec.soft2.facturacion.HistorialVentas;
import cr.ac.ucenfotec.soft2.inventario.Inventario;
import cr.ac.ucenfotec.soft2.usuarios.GestorUsuarios;
import cr.ac.ucenfotec.soft2.usuarios.Usuario;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import javax.swing.JPanel;

/**
 * Ventana principal única de la aplicación.
 *
 * Contiene una barra lateral persistente y un área de contenido que intercambia
 * paneles mediante CardLayout. La ventana no se recrea al navegar: sólo cambia
 * el panel visible, por lo que conserva su posición, tamaño y monitor.
 */
public class MainWindow extends javax.swing.JFrame implements NavigationHost {

    private final String nombreUsuario;
    private final Usuario usuario;

    // Servicios/datos compartidos por todos los módulos (una sola instancia).
    private final HistorialVentas historial;
    private final GestorUsuarios gestorUsuarios;
    private final Inventario inventario;

    private final CardLayout cards = new CardLayout();
    private final JPanel contenido = new JPanel(cards);
    private final java.util.Map<String, JPanel> paneles = new java.util.HashMap<>();
    private NavSidebar sidebar;
    private String destinoActual = null;

    public MainWindow(Usuario usuario) {
        this.usuario = usuario;
        this.nombreUsuario = usuario.getNombre() + " " + usuario.getApellido();
        this.historial = new HistorialVentas();
        this.gestorUsuarios = new GestorUsuarios();
        this.inventario = new Inventario();

        initUI();
        navegar("Dashboard");
        UITheme.openMaximized(this, 1000, 640);
    }

    private void initUI() {
        setTitle("El Rincón del Café");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        sidebar = new NavSidebar(this, usuario.getRol(), nombreUsuario);
        contenido.setBackground(UITheme.BG);

        add(sidebar, BorderLayout.WEST);
        add(contenido, BorderLayout.CENTER);

        // Al cerrar la ventana (X), advertir si hay una operación en progreso.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                if (confirmarSalidaPanelActual()) {
                    dispose();
                    System.exit(0);
                }
            }
        });
    }

    // ---------- NavigationHost ----------

    @Override
    public void navegar(String destino) {
        if (destino.equals(destinoActual)) {
            return; // ya estamos aquí
        }
        // Si el panel actual tiene una operación en progreso, pedir confirmación.
        if (!confirmarSalidaPanelActual()) {
            return;
        }
        JPanel panel = paneles.get(destino);
        if (panel == null) {
            panel = crearPanel(destino);
            paneles.put(destino, panel);
            contenido.add(panel, destino);
        } else if (panel instanceof Refrescable r) {
            // Refrescar datos al volver a un panel ya creado.
            r.refrescar();
        }
        cards.show(contenido, destino);
        sidebar.setActivo(destino);
        destinoActual = destino;
    }

    @Override
    public void cerrarSesion() {
        if (!confirmarSalidaPanelActual()) {
            return;
        }
        boolean ok = cr.ac.ucenfotec.soft2.app.Utilities.preguntarSioNo(
                "¿Está seguro que desea cerrar la sesión?",
                "Confirmar Cerrar Sesión", this);
        if (ok) {
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }

    /**
     * Consulta al panel actual si es seguro abandonarlo (p.ej. venta en curso).
     * @return true si se puede continuar; false si el usuario canceló.
     */
    private boolean confirmarSalidaPanelActual() {
        if (destinoActual != null) {
            JPanel actual = paneles.get(destinoActual);
            if (actual instanceof NavGuard g) {
                return g.puedeSalir();
            }
        }
        return true;
    }

    @Override
    public java.awt.Window getVentana() {
        return this;
    }

    // ---------- Construcción de paneles ----------

    private JPanel crearPanel(String destino) {
        return switch (destino) {
            case "Dashboard" -> new DashboardPanel(this, inventario, historial, nombreUsuario);
            case "Inventario" -> new InventarioFrame(this, inventario, usuario.getRol(), nombreUsuario);
            case "Ventas" -> new VentasFrame(this, historial, inventario, usuario);
            case "Facturacion" -> new FacturacionFrame(this, historial, usuario);
            case "Clientes" -> new AdministrarClientesFrame(this, usuario.getRol());
            case "Usuarios" -> new AdministrarUsuariosFrame(this, gestorUsuarios);
            case "Reportes" -> new ReportesFrame(this, historial, gestorUsuarios, inventario, usuario);
            default -> new DashboardPanel(this, inventario, historial, nombreUsuario);
        };
    }

}
