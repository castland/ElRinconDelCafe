package cr.ac.ucenfotec.soft2.ui;

import static cr.ac.ucenfotec.soft2.app.Utilities.preguntarSioNo;
import cr.ac.ucenfotec.soft2.facturacion.HistorialVentas;
import cr.ac.ucenfotec.soft2.inventario.Inventario;
import cr.ac.ucenfotec.soft2.usuarios.Usuario;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Panel principal del Barista con navegación lateral y accesos rápidos.
 *
 * @author Carlos Carballo Villalobos
 */
public class MenuBaristaFrame extends javax.swing.JFrame {

    private final String nombreUsuario;
    private final HistorialVentas historial;
    private final Usuario usuario;

    public MenuBaristaFrame(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
        this.historial = new HistorialVentas();
        this.usuario = new Usuario(nombreUsuario, "", "", "Barista");
        initUI();
        UITheme.openMaximized(this, 1000, 640);
    }

    private void initUI() {
        setTitle("El Rincón del Café — Panel de Barista");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(NavSidebar.build("Barista", nombreUsuario, this::abrir, this::logout), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildContent() {
        JPanel content = UITheme.panel(new BorderLayout(0, 18), 30);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel h = UITheme.title("Hola, " + nombreUsuario);
        h.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel s = UITheme.subtitle("Panel de operación · Rol Barista");
        s.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(h);
        header.add(javax.swing.Box.createVerticalStrut(4));
        header.add(s);

        JPanel grid = new JPanel(new GridLayout(1, 3, 18, 18));
        grid.setOpaque(false);
        grid.add(quickCard("Ventas", "Registre y cobre ventas", "Ventas"));
        grid.add(quickCard("Clientes", "Administre la cartera de clientes", "Clientes"));
        grid.add(quickCard("Facturación", "Consulte y busque facturas", "Facturacion"));

        JPanel gridWrap = new JPanel(new BorderLayout());
        gridWrap.setOpaque(false);
        gridWrap.add(grid, BorderLayout.NORTH);

        content.add(header, BorderLayout.NORTH);
        content.add(gridWrap, BorderLayout.CENTER);
        return content;
    }

    private JPanel quickCard(String titulo, String desc, String destino) {
        JPanel card = UITheme.card(new BorderLayout(0, 8));
        card.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        card.setPreferredSize(new java.awt.Dimension(0, 170));

        JLabel t = new JLabel(titulo);
        t.setFont(UITheme.FONT_H2);
        t.setForeground(UITheme.BRAND);

        JLabel d = new JLabel("<html>" + desc + "</html>");
        d.setFont(UITheme.FONT_SMALL);
        d.setForeground(UITheme.TEXT_MUTED);

        JButton open = UITheme.primaryButton("Abrir");
        open.addActionListener(e -> abrir(destino));

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(open, BorderLayout.WEST);

        card.add(t, BorderLayout.NORTH);
        card.add(d, BorderLayout.CENTER);
        card.add(south, BorderLayout.SOUTH);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { abrir(destino); }
        });
        return card;
    }

    private void abrir(String destino) {
        this.setVisible(false);
        switch (destino) {
            case "Ventas" -> {
                Inventario inventario = new Inventario();
                new VentasFrame(this, historial, inventario, usuario).setVisible(true);
            }
            case "Clientes" ->
                new AdministrarClientesFrame(this, "Barista").setVisible(true);
            case "Facturacion" ->
                new FacturacionFrame(this, historial, usuario).setVisible(true);
            default -> this.setVisible(true);
        }
    }

    private void logout() {
        if (preguntarSioNo("¿Está seguro que desea cerrar la sesión?",
                "Confirmar Cerrar Sesión", this)) {
            this.setVisible(false);
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
