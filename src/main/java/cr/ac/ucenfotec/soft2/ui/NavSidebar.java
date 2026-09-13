package cr.ac.ucenfotec.soft2.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Barra lateral de navegación oscura reutilizable para las pantallas de menú.
 */
public final class NavSidebar {

    private NavSidebar() {
    }

    /**
     * Construye la barra lateral.
     *
     * @param rol       "Gerente" o "Barista" (define las opciones visibles)
     * @param nombre    nombre del usuario logueado
     * @param onNavigate acción al pulsar una opción (recibe la clave de destino)
     * @param onLogout  acción de cerrar sesión
     */
    public static JPanel build(String rol, String nombre,
                               Consumer<String> onNavigate, Runnable onLogout) {
        JPanel side = new JPanel();
        side.setBackground(UITheme.SIDEBAR_BG);
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setPreferredSize(new Dimension(250, 0));
        side.setBorder(BorderFactory.createEmptyBorder(24, 0, 24, 0));

        // Marca
        JLabel brand = new JLabel("  \u2615  El Rincón");
        brand.setFont(UITheme.FONT_H1);
        brand.setForeground(java.awt.Color.WHITE);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.setBorder(BorderFactory.createEmptyBorder(0, 12, 4, 12));

        JLabel sub = new JLabel("  del Café");
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(UITheme.ACCENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setBorder(BorderFactory.createEmptyBorder(0, 12, 20, 12));

        side.add(brand);
        side.add(sub);
        side.add(sep());

        // Opciones según rol
        side.add(navItem("Inventario", "\uD83D\uDCE6", onNavigate, esGerente(rol)));
        side.add(navItem("Ventas", "\uD83D\uDED2", onNavigate, true));
        side.add(navItem("Facturacion", "\uD83E\uDDFE", onNavigate, true));
        side.add(navItem("Clientes", "\uD83D\uDC65", onNavigate, true));
        side.add(navItem("Usuarios", "\uD83D\uDC64", onNavigate, esGerente(rol)));
        side.add(navItem("Reportes", "\uD83D\uDCCA", onNavigate, esGerente(rol)));

        side.add(Box.createVerticalGlue());
        side.add(sep());

        // Usuario + logout
        JLabel user = new JLabel("  " + nombre);
        user.setFont(UITheme.FONT_BODY_BD);
        user.setForeground(UITheme.TEXT_ON_DARK);
        user.setAlignmentX(Component.LEFT_ALIGNMENT);
        user.setBorder(BorderFactory.createEmptyBorder(6, 12, 0, 12));

        JLabel rolLbl = new JLabel("  " + rol);
        rolLbl.setFont(UITheme.FONT_SMALL);
        rolLbl.setForeground(UITheme.ACCENT);
        rolLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        rolLbl.setBorder(BorderFactory.createEmptyBorder(0, 12, 10, 12));

        JButton logout = UITheme.sidebarButton("\u23FB  Cerrar Sesión");
        logout.setForeground(new java.awt.Color(0xF0B7AE));
        logout.addActionListener(e -> onLogout.run());

        side.add(user);
        side.add(rolLbl);
        side.add(logout);
        return side;
    }

    private static JButton navItem(String destino, String icon,
                                   Consumer<String> onNavigate, boolean enabled) {
        JButton b = UITheme.sidebarButton("  " + icon + "   " + label(destino));
        b.setEnabled(enabled);
        if (!enabled) {
            b.setForeground(new java.awt.Color(0x6E5D51));
            b.setCursor(java.awt.Cursor.getDefaultCursor());
        }
        b.addActionListener(e -> onNavigate.accept(destino));
        return b;
    }

    private static String label(String destino) {
        return switch (destino) {
            case "Facturacion" -> "Facturación";
            default -> destino;
        };
    }

    private static boolean esGerente(String rol) {
        return "Gerente".equalsIgnoreCase(rol);
    }

    private static Component sep() {
        JPanel s = new JPanel();
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        s.setBackground(new java.awt.Color(0x50413360 & 0xFFFFFF));
        s.setBackground(new java.awt.Color(0x50, 0x41, 0x33));
        return s;
    }
}
