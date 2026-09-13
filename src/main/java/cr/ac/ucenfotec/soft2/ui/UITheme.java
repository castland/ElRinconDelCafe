package cr.ac.ucenfotec.soft2.ui;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;

/**
 * Sistema de diseño central de la aplicación "El Rincón del Café".
 *
 * Define la paleta de color, la tipografía y fábricas de componentes con estilo
 * moderno (botones, tarjetas, encabezados, tablas, inputs) para lograr una
 * interfaz consistente y profesional en todas las pantallas.
 */
public final class UITheme {

    // ---- Paleta (tema café, claro y profesional) ----
    public static final Color BRAND        = new Color(0x6F4A2E); // café oscuro (marca)
    public static final Color BRAND_LIGHT  = new Color(0x8B5E3C); // café medio
    public static final Color ACCENT       = new Color(0xC8873E); // ámbar / acento
    public static final Color ACCENT_HOVER = new Color(0xB0772F);

    public static final Color BG           = new Color(0xF4EFEA); // fondo general
    public static final Color SURFACE      = Color.WHITE;         // tarjetas
    public static final Color SIDEBAR_BG   = new Color(0x3A2A20); // barra lateral oscura
    public static final Color SIDEBAR_HOVER= new Color(0x4E3A2C);

    public static final Color TEXT         = new Color(0x2B2320);
    public static final Color TEXT_MUTED   = new Color(0x8A7E76);
    public static final Color TEXT_ON_DARK = new Color(0xF2E9E2);

    public static final Color DANGER       = new Color(0xC0392B);
    public static final Color DANGER_HOVER = new Color(0xA5322447 & 0xFFFFFF);
    public static final Color SUCCESS      = new Color(0x2E7D4F);
    public static final Color BORDER       = new Color(0xE1D8D0);

    // ---- Tipografía ----
    public static final String FONT_FAMILY = "Segoe UI";
    public static final Font FONT_DISPLAY = new Font(FONT_FAMILY, Font.BOLD, 26);
    public static final Font FONT_H1      = new Font(FONT_FAMILY, Font.BOLD, 20);
    public static final Font FONT_H2      = new Font(FONT_FAMILY, Font.BOLD, 16);
    public static final Font FONT_BODY    = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font FONT_BODY_BD = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font FONT_SMALL   = new Font(FONT_FAMILY, Font.PLAIN, 12);

    private UITheme() {
    }

    /** Instala FlatLaf y define los valores globales del look-and-feel. */
    public static void setup() {
        System.setProperty("flatlaf.useWindowDecorations", "true");
        try {
            FlatLightLaf.setup();

            UIManager.put("Component.focusWidth", 1);
            UIManager.put("Component.arc", 14);
            UIManager.put("Button.arc", 14);
            UIManager.put("ProgressBar.arc", 14);
            UIManager.put("TextComponent.arc", 12);
            UIManager.put("ScrollBar.thumbArc", 12);
            UIManager.put("ScrollBar.width", 12);

            UIManager.put("Component.accentColor", ACCENT);
            UIManager.put("Component.focusColor", ACCENT);

            UIManager.put("Panel.background", BG);
            UIManager.put("defaultFont", FONT_BODY);

            UIManager.put("Table.rowHeight", 32);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.gridColor", BORDER);
            UIManager.put("Table.intercellSpacing", new Dimension(0, 1));
            UIManager.put("TableHeader.height", 34);

            UIManager.put("TextField.placeholderForeground", TEXT_MUTED);
        } catch (Exception ex) {
            System.err.println("No se pudo aplicar FlatLaf: " + ex.getMessage());
        }
    }

    // ---------- UX de ventana ----------

    /** Abre la ventana maximizada (pantalla completa) con un tamaño mínimo. */
    public static void openMaximized(Window window, int minWidth, int minHeight) {
        window.setMinimumSize(new Dimension(minWidth, minHeight));
        window.setPreferredSize(new Dimension(
                Math.max(minWidth, 1100), Math.max(minHeight, 720)));
        window.setSize(window.getPreferredSize());
        window.setLocationRelativeTo(null);
        if (window instanceof javax.swing.JFrame f) {
            f.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        }
    }

    /** Centra la ventana con un tamaño fijo (para diálogos/login). */
    public static void centerFixed(Window window, int width, int height) {
        window.setSize(width, height);
        window.setMinimumSize(new Dimension(width, height));
        window.setLocationRelativeTo(null);
    }

    // ---------- Fábricas de componentes ----------

    /** Panel con fondo de la app y un padding uniforme. */
    public static JPanel panel(java.awt.LayoutManager layout, int pad) {
        JPanel p = new JPanel(layout);
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
        return p;
    }

    /** Tarjeta blanca con borde suave y esquinas redondeadas simuladas. */
    public static JPanel card(java.awt.LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(SURFACE);
        p.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(BORDER, 16, 1),
                BorderFactory.createEmptyBorder(20, 22, 20, 22)));
        return p;
    }

    public static JLabel title(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_H1);
        l.setForeground(TEXT);
        return l;
    }

    public static JLabel subtitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY);
        l.setForeground(TEXT_MUTED);
        return l;
    }

    public static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY_BD);
        l.setForeground(TEXT);
        return l;
    }

    /** Botón primario (relleno con el color de acento). */
    public static JButton primaryButton(String text) {
        JButton b = baseButton(text);
        b.setBackground(ACCENT);
        b.setForeground(Color.WHITE);
        b.putClientProperty("JButton.buttonType", "roundRect");
        hoverEffect(b, ACCENT, ACCENT_HOVER, Color.WHITE);
        return b;
    }

    /** Botón secundario (contorno suave sobre superficie clara). */
    public static JButton secondaryButton(String text) {
        JButton b = baseButton(text);
        b.setBackground(SURFACE);
        b.setForeground(BRAND);
        b.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(BRAND_LIGHT, 14, 1),
                BorderFactory.createEmptyBorder(9, 16, 9, 16)));
        hoverEffect(b, SURFACE, new Color(0xF0E6DE), BRAND);
        return b;
    }

    /** Botón de acción destructiva (rojo). */
    public static JButton dangerButton(String text) {
        JButton b = baseButton(text);
        b.setBackground(DANGER);
        b.setForeground(Color.WHITE);
        hoverEffect(b, DANGER, new Color(0xA53225), Color.WHITE);
        return b;
    }

    private static JButton baseButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BODY_BD);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        return b;
    }

    private static void hoverEffect(JButton b, Color base, Color hover, Color fg) {
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(hover); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { b.setBackground(base); }
        });
    }

    /** Botón de navegación para la barra lateral oscura. */
    public static JButton sidebarButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BODY_BD);
        b.setForeground(TEXT_ON_DARK);
        b.setBackground(SIDEBAR_BG);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setHorizontalAlignment(JButton.LEFT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(SIDEBAR_HOVER); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { b.setBackground(SIDEBAR_BG); }
        });
        return b;
    }

    /** Campo de texto con placeholder y altura cómoda. */
    public static JTextField textField(String placeholder, int columns) {
        JTextField f = new JTextField(columns);
        f.putClientProperty("JTextField.placeholderText", placeholder);
        f.setFont(FONT_BODY);
        return f;
    }

    /** Aplica estilo moderno a una tabla existente (sin recrearla). */
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(0xF3E4D3));
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);
        table.setGridColor(BORDER);
        JTableHeader h = table.getTableHeader();
        h.setFont(FONT_BODY_BD);
        h.setBackground(new Color(0xEFE7E0));
        h.setForeground(BRAND);
        h.setPreferredSize(new Dimension(h.getPreferredSize().width, 36));
        h.setReorderingAllowed(false);
    }

    /** Envuelve una tabla en un scroll pane con borde de tarjeta. */
    public static JScrollPane tableScroll(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(new RoundedLineBorder(BORDER, 14, 1));
        sp.getViewport().setBackground(SURFACE);
        return sp;
    }

    // ---- utilidades heredadas (compatibilidad) ----
    public static void applyWindowUX(Window window, int minWidth, int minHeight) {
        centerFixed(window, Math.max(minWidth, window.getWidth()), Math.max(minHeight, window.getHeight()));
    }

    public static void markPrimary(JComponent button) {
        button.putClientProperty("JButton.buttonType", "default");
    }

    /** Borde de línea con esquinas redondeadas. */
    public static class RoundedLineBorder implements Border {
        private final Color color;
        private final int radius;
        private final int thickness;

        public RoundedLineBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, java.awt.Graphics g, int x, int y, int w, int h) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                    java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new java.awt.BasicStroke(thickness));
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public java.awt.Insets getBorderInsets(Component c) {
            int m = radius / 2 + thickness;
            return new java.awt.Insets(m, m, m, m);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}
