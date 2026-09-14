package cr.ac.ucenfotec.soft2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Barra lateral de navegación persistente y colapsable.
 *
 * Permanece visible en todas las pantallas. Se puede contraer a una franja
 * estrecha de iconos (por ejemplo durante una venta) y expandir de nuevo.
 * Resalta el módulo activo.
 */
public class NavSidebar extends JPanel {

    private static final int ANCHO_EXPANDIDO = 240;
    private static final int ANCHO_COLAPSADO = 64;

    private final NavigationHost host;
    private final String rol;
    private final String nombre;

    private boolean colapsado = false;
    private final List<ItemNav> items = new ArrayList<>();
    private JLabel brandLabel;
    private JLabel brandSub;
    private JLabel userLabel;
    private JLabel rolLabel;
    private JButton btnToggle;
    private ItemNav logoutItem;
    private String destinoActivo = "Dashboard";

    public NavSidebar(NavigationHost host, String rol, String nombre) {
        this.host = host;
        this.rol = rol;
        this.nombre = nombre;
        construir();
    }

    private void construir() {
        setBackground(UITheme.SIDEBAR_BG);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));
        aplicarAncho();

        // Botón para colapsar/expandir
        btnToggle = crearBotonPlano("");
        btnToggle.setIcon(Icons.menu(18, UITheme.TEXT_ON_DARK));
        btnToggle.addActionListener(e -> alternar());
        JPanel toggleWrap = new JPanel(new BorderLayout());
        toggleWrap.setOpaque(false);
        toggleWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        toggleWrap.add(btnToggle, BorderLayout.WEST);
        add(toggleWrap);

        // Marca
        brandLabel = new JLabel("  El Rincón");
        brandLabel.setIcon(Icons.coffee(20, UITheme.ACCENT));
        brandLabel.setFont(UITheme.FONT_H1);
        brandLabel.setForeground(Color.WHITE);
        brandLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandLabel.setBorder(BorderFactory.createEmptyBorder(6, 12, 2, 12));

        brandSub = new JLabel("  del Café");
        brandSub.setFont(UITheme.FONT_SMALL);
        brandSub.setForeground(UITheme.ACCENT);
        brandSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandSub.setBorder(BorderFactory.createEmptyBorder(0, 12, 16, 12));

        add(brandLabel);
        add(brandSub);
        add(sep());

        // Opciones (icono, etiqueta, destino, habilitado según rol)
        boolean g = esGerente(rol);
        java.awt.Color ic = UITheme.TEXT_ON_DARK;
        int sz = 18;
        agregarItem(Icons.home(sz, ic), "Inicio", "Dashboard", true);
        agregarItem(Icons.inventario(sz, ic), "Inventario", "Inventario", g);
        agregarItem(Icons.ventas(sz, ic), "Ventas", "Ventas", true);
        agregarItem(Icons.facturas(sz, ic), "Facturación", "Facturacion", true);
        agregarItem(Icons.clientes(sz, ic), "Clientes", "Clientes", true);
        agregarItem(Icons.usuarios(sz, ic), "Usuarios", "Usuarios", g);
        agregarItem(Icons.reportes(sz, ic), "Reportes", "Reportes", g);

        add(Box.createVerticalGlue());
        add(sep());

        // Usuario
        userLabel = new JLabel("  " + nombre);
        userLabel.setFont(UITheme.FONT_BODY_BD);
        userLabel.setForeground(UITheme.TEXT_ON_DARK);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userLabel.setBorder(BorderFactory.createEmptyBorder(6, 14, 0, 12));

        rolLabel = new JLabel("  " + rol);
        rolLabel.setFont(UITheme.FONT_SMALL);
        rolLabel.setForeground(UITheme.ACCENT);
        rolLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rolLabel.setBorder(BorderFactory.createEmptyBorder(0, 14, 10, 12));
        add(userLabel);
        add(rolLabel);

        // Logout
        logoutItem = new ItemNav(Icons.logout(18, new Color(0xF0B7AE)), "Cerrar Sesión", null, true);
        logoutItem.boton.setForeground(new Color(0xF0B7AE));
        logoutItem.boton.addActionListener(e -> host.cerrarSesion());
        add(logoutItem.boton);

        setActivo(destinoActivo);
    }

    private void agregarItem(javax.swing.Icon icono, String etiqueta, String destino, boolean habilitado) {
        ItemNav item = new ItemNav(icono, etiqueta, destino, habilitado);
        item.boton.addActionListener(e -> {
            if (habilitado) {
                host.navegar(destino);
            }
        });
        items.add(item);
        add(item.boton);
    }

    /** Marca visualmente el módulo activo. */
    public void setActivo(String destino) {
        this.destinoActivo = destino;
        for (ItemNav it : items) {
            it.setActivo(destino.equals(it.destino));
        }
    }

    private void alternar() {
        colapsado = !colapsado;
        aplicarAncho();
        // Mostrar/ocultar textos
        brandLabel.setVisible(!colapsado);
        brandSub.setVisible(!colapsado);
        userLabel.setVisible(!colapsado);
        rolLabel.setVisible(!colapsado);
        for (ItemNav it : items) {
            it.setColapsado(colapsado);
        }
        logoutItem.setColapsado(colapsado);
        revalidate();
        repaint();
    }

    private void aplicarAncho() {
        int w = colapsado ? ANCHO_COLAPSADO : ANCHO_EXPANDIDO;
        setPreferredSize(new Dimension(w, 0));
        setMaximumSize(new Dimension(w, Integer.MAX_VALUE));
        setMinimumSize(new Dimension(w, 0));
    }

    private JButton crearBotonPlano(String texto) {
        JButton b = new JButton(texto);
        b.setFont(new java.awt.Font(UITheme.FONT_FAMILY, java.awt.Font.PLAIN, 18));
        b.setForeground(UITheme.TEXT_ON_DARK);
        b.setBackground(UITheme.SIDEBAR_BG);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return b;
    }

    private static boolean esGerente(String rol) {
        return "Gerente".equalsIgnoreCase(rol);
    }

    private Component sep() {
        JPanel s = new JPanel();
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        s.setBackground(new Color(0x50, 0x41, 0x33));
        return s;
    }

    /** Un elemento de navegación con icono + etiqueta y estado activo/colapsado. */
    private static class ItemNav {
        final javax.swing.Icon icono;
        final String etiqueta;
        final String destino;
        final boolean habilitado;
        final JButton boton;
        boolean activo = false;
        boolean colapsado = false;

        ItemNav(javax.swing.Icon icono, String etiqueta, String destino, boolean habilitado) {
            this.icono = icono;
            this.etiqueta = etiqueta;
            this.destino = destino;
            this.habilitado = habilitado;
            this.boton = new JButton();
            boton.setIcon(icono);
            boton.setIconTextGap(14);
            boton.setFont(UITheme.FONT_BODY_BD);
            boton.setHorizontalAlignment(JButton.LEFT);
            boton.setFocusPainted(false);
            boton.setBorderPainted(false);
            boton.setContentAreaFilled(true);
            boton.setBackground(UITheme.SIDEBAR_BG);
            boton.setForeground(habilitado ? UITheme.TEXT_ON_DARK : new Color(0x6E5D51));
            boton.setCursor(Cursor.getPredefinedCursor(
                    habilitado ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            boton.setAlignmentX(Component.LEFT_ALIGNMENT);
            boton.setEnabled(habilitado);
            actualizarTexto();
            boton.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (habilitado && !activo) boton.setBackground(UITheme.SIDEBAR_HOVER);
                }
                @Override public void mouseExited(java.awt.event.MouseEvent e) {
                    if (!activo) boton.setBackground(UITheme.SIDEBAR_BG);
                }
            });
        }

        void setActivo(boolean activo) {
            this.activo = activo;
            boton.setBackground(activo ? UITheme.BRAND_LIGHT : UITheme.SIDEBAR_BG);
        }

        void setColapsado(boolean colapsado) {
            this.colapsado = colapsado;
            actualizarTexto();
        }

        private void actualizarTexto() {
            if (colapsado) {
                boton.setText(null);
                boton.setToolTipText(etiqueta);
                boton.setHorizontalAlignment(JButton.CENTER);
                boton.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
            } else {
                boton.setText(etiqueta);
                boton.setToolTipText(null);
                boton.setHorizontalAlignment(JButton.LEFT);
                boton.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 18));
            }
        }
    }
}
