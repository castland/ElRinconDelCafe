package cr.ac.ucenfotec.soft2.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Estructura común para las pantallas de módulo (Inventario, Clientes, etc.):
 * un encabezado con título/subtítulo y botón de retorno, una barra de acciones
 * y un área de contenido en tarjeta.
 */
public final class ModuleScaffold {

    public final JPanel root;
    public final JPanel toolbar;   // barra de acciones (izquierda)
    public final JPanel toolbarRight; // barra de acciones (derecha, p.ej. búsqueda)
    public final JPanel content;   // área principal (tarjeta)

    private ModuleScaffold(JPanel root, JPanel toolbar, JPanel toolbarRight, JPanel content) {
        this.root = root;
        this.toolbar = toolbar;
        this.toolbarRight = toolbarRight;
        this.content = content;
    }

    /** Módulo con encabezado sin botón de retorno (la barra lateral navega). */
    public static ModuleScaffold build(String titulo, String subtitulo) {
        return build(titulo, subtitulo, null);
    }

    public static ModuleScaffold build(String titulo, String subtitulo, Runnable onBack) {
        JPanel root = UITheme.panel(new BorderLayout(0, 16), 26);

        // --- Encabezado ---
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        JLabel t = UITheme.title(titulo);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel s = UITheme.subtitle(subtitulo);
        s.setAlignmentX(Component.LEFT_ALIGNMENT);
        titles.add(t);
        titles.add(javax.swing.Box.createVerticalStrut(3));
        titles.add(s);

        header.add(titles, BorderLayout.WEST);

        // Botón de retorno opcional (sólo si se proporciona onBack).
        if (onBack != null) {
            JButton back = UITheme.secondaryButton("\u2190  Menú Principal");
            back.addActionListener(e -> onBack.run());
            JPanel backWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            backWrap.setOpaque(false);
            backWrap.add(back);
            header.add(backWrap, BorderLayout.EAST);
        }

        // --- Barra de acciones ---
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);
        JPanel toolbarRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbarRight.setOpaque(false);
        JPanel toolbarWrap = new JPanel(new BorderLayout());
        toolbarWrap.setOpaque(false);
        toolbarWrap.add(toolbar, BorderLayout.WEST);
        toolbarWrap.add(toolbarRight, BorderLayout.EAST);

        // --- Contenido ---
        JPanel content = UITheme.card(new BorderLayout());

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);
        center.add(toolbarWrap, BorderLayout.NORTH);
        center.add(content, BorderLayout.CENTER);

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);

        return new ModuleScaffold(root, toolbar, toolbarRight, content);
    }
}
