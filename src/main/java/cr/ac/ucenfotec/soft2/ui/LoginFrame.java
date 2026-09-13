package cr.ac.ucenfotec.soft2.ui;

import cr.ac.ucenfotec.soft2.usuarios.GestorUsuarios;
import cr.ac.ucenfotec.soft2.usuarios.Usuario;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Pantalla de inicio de sesión con diseño moderno de dos paneles:
 * un lado de marca (café) y un lado con el formulario en tarjeta.
 *
 * @author Carlos Carballo Villalobos
 */
public class LoginFrame extends javax.swing.JFrame {

    private JTextField inputNombreUsuario;
    private JTextField inputCedulaUsuario;
    private JButton botonLoginAceptar;
    private JButton botonLoginCancelar;

    public LoginFrame() {
        initUI();
        UITheme.centerFixed(this, 900, 560);
        getRootPane().setDefaultButton(botonLoginAceptar);
        java.awt.EventQueue.invokeLater(inputNombreUsuario::requestFocusInWindow);
    }

    private void initUI() {
        setTitle("El Rincón del Café — Inicio de Sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildBrandPanel(), BorderLayout.WEST);
        add(buildFormPanel(), BorderLayout.CENTER);
    }

    /** Panel izquierdo con degradado café y la marca. */
    private JPanel buildBrandPanel() {
        JPanel brand = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new java.awt.GradientPaint(
                        0, 0, UITheme.BRAND,
                        0, getHeight(), UITheme.BRAND_LIGHT));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        brand.setPreferredSize(new Dimension(380, 0));

        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new javax.swing.BoxLayout(col, javax.swing.BoxLayout.Y_AXIS));

        JLabel icon = new JLabel("\u2615"); // taza de café
        icon.setFont(new java.awt.Font(UITheme.FONT_FAMILY, java.awt.Font.PLAIN, 72));
        icon.setForeground(UITheme.TEXT_ON_DARK);
        icon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel name = new JLabel("El Rincón del Café");
        name.setFont(UITheme.FONT_DISPLAY);
        name.setForeground(Color.WHITE);
        name.setAlignmentX(CENTER_ALIGNMENT);

        JLabel tag = new JLabel("Sistema de Gestión Integral");
        tag.setFont(UITheme.FONT_BODY);
        tag.setForeground(UITheme.TEXT_ON_DARK);
        tag.setAlignmentX(CENTER_ALIGNMENT);

        col.add(icon);
        col.add(javax.swing.Box.createVerticalStrut(16));
        col.add(name);
        col.add(javax.swing.Box.createVerticalStrut(6));
        col.add(tag);
        brand.add(col);
        return brand;
    }

    /** Panel derecho con el formulario dentro de una tarjeta. */
    private JPanel buildFormPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UITheme.BG);

        JPanel card = UITheme.card(new GridBagLayout());
        card.setPreferredSize(new Dimension(360, 380));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 6, 0);

        JLabel welcome = new JLabel("Bienvenido");
        welcome.setFont(UITheme.FONT_H1);
        welcome.setForeground(UITheme.TEXT);

        JLabel hint = new JLabel("Ingrese sus credenciales para continuar");
        hint.setFont(UITheme.FONT_SMALL);
        hint.setForeground(UITheme.TEXT_MUTED);

        inputNombreUsuario = UITheme.textField("Ej: Carlos", 18);
        inputCedulaUsuario = UITheme.textField("Ej: 402350652", 18);

        botonLoginAceptar = UITheme.primaryButton("Iniciar Sesión");
        botonLoginAceptar.addActionListener(this::onAceptar);

        botonLoginCancelar = UITheme.secondaryButton("Limpiar");
        botonLoginCancelar.addActionListener(e -> {
            inputNombreUsuario.setText("");
            inputCedulaUsuario.setText("");
            inputNombreUsuario.requestFocusInWindow();
        });

        c.gridy = 0; card.add(welcome, c);
        c.insets = new Insets(0, 0, 22, 0);
        c.gridy = 1; card.add(hint, c);

        c.insets = new Insets(0, 0, 6, 0);
        c.gridy = 2; card.add(UITheme.fieldLabel("Nombre"), c);
        c.insets = new Insets(0, 0, 16, 0);
        c.gridy = 3; card.add(sized(inputNombreUsuario), c);

        c.insets = new Insets(0, 0, 6, 0);
        c.gridy = 4; card.add(UITheme.fieldLabel("Cédula"), c);
        c.insets = new Insets(0, 0, 24, 0);
        c.gridy = 5; card.add(sized(inputCedulaUsuario), c);

        c.insets = new Insets(0, 0, 10, 0);
        c.gridy = 6; card.add(sized(botonLoginAceptar), c);
        c.insets = new Insets(0, 0, 0, 0);
        c.gridy = 7; card.add(sized(botonLoginCancelar), c);

        wrapper.add(card);
        return wrapper;
    }

    private static JTextField sized(JTextField f) {
        f.setPreferredSize(new Dimension(0, 40));
        return f;
    }

    private static JButton sized(JButton b) {
        b.setPreferredSize(new Dimension(0, 42));
        return b;
    }

    private void onAceptar(java.awt.event.ActionEvent evt) {
        String nombre = inputNombreUsuario.getText().trim();
        String cedula = inputCedulaUsuario.getText().trim();

        if (nombre.isEmpty() || cedula.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor complete nombre y cédula.",
                    "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        GestorUsuarios gestor = new GestorUsuarios();
        Usuario usuario = gestor.validarUsuario(nombre, cedula);

        if (usuario != null) {
            JOptionPane.showMessageDialog(this,
                    "Bienvenido " + usuario.getNombre() + " (" + usuario.getRol() + ")!");
            this.setVisible(false);
            this.dispose();
            String nombreCompleto = usuario.getNombre() + " " + usuario.getApellido();
            if (usuario.getRol().equals("Gerente")) {
                new MenuGerenteFrame(nombreCompleto).setVisible(true);
            } else {
                new MenuBaristaFrame(nombreCompleto).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Usuario no encontrado. Verifique el nombre y la cédula.",
                    "Acceso denegado", JOptionPane.ERROR_MESSAGE);
        }
    }
}
