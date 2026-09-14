package cr.ac.ucenfotec.soft2.ui;

import cr.ac.ucenfotec.soft2.usuarios.GestorUsuarios;
import cr.ac.ucenfotec.soft2.usuarios.Usuario;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Formulario para agregar o modificar un usuario, con diseño moderno.
 *
 * @author Carlos Carballo Villalobos
 */
public class AgregarNuevoUsuarioFrame extends javax.swing.JDialog {

    public GestorUsuarios gestor;
    private final AdministrarUsuariosFrame framePadre;
    private final Usuario usuarioAEditar;
    private final boolean modoEdicion;

    private JTextField textNuevoUsuarioNombre;
    private JTextField textNuevoUsuarioApellido;
    private JTextField textNuevoUsuarioCedula;
    private JComboBox<String> dropdownNuevoUsuarioRol;

    public AgregarNuevoUsuarioFrame(GestorUsuarios gestor, AdministrarUsuariosFrame framePadre) {
        this(gestor, framePadre, null);
    }

    public AgregarNuevoUsuarioFrame(GestorUsuarios gestor, AdministrarUsuariosFrame framePadre, Usuario usuario) {
        super(framePadre != null ? javax.swing.SwingUtilities.getWindowAncestor(framePadre) : null,
                java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        this.gestor = gestor;
        this.framePadre = framePadre;
        this.usuarioAEditar = usuario;
        this.modoEdicion = usuario != null;
        initUI();
        if (modoEdicion) {
            textNuevoUsuarioNombre.setText(usuario.getNombre());
            textNuevoUsuarioApellido.setText(usuario.getApellido());
            textNuevoUsuarioCedula.setText(usuario.getCedula());
            dropdownNuevoUsuarioRol.setSelectedItem(usuario.getRol());
            textNuevoUsuarioCedula.setEnabled(false);
        }
        setLocationRelativeTo(framePadre);
    }

    private void initUI() {
        setTitle(modoEdicion ? "Modificar Usuario" : "Agregar Nuevo Usuario");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        textNuevoUsuarioNombre = UITheme.textField("Nombre", 18);
        textNuevoUsuarioApellido = UITheme.textField("Apellido", 18);
        textNuevoUsuarioCedula = UITheme.textField("Cédula", 18);
        dropdownNuevoUsuarioRol = new JComboBox<>(new String[]{"Gerente", "Barista"});

        JPanel card = UITheme.card(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.add(UITheme.title(modoEdicion ? "Modificar Usuario" : "Nuevo Usuario"), BorderLayout.CENTER);
        c.gridy = 0; c.insets = new Insets(0, 0, 18, 0);
        card.add(head, c);

        int y = 1;
        y = addRow(card, c, y, "Nombre", textNuevoUsuarioNombre);
        y = addRow(card, c, y, "Apellido", textNuevoUsuarioApellido);
        y = addRow(card, c, y, "Cédula", textNuevoUsuarioCedula);
        y = addRow(card, c, y, "Rol", dropdownNuevoUsuarioRol);

        JButton aceptar = UITheme.primaryButton(modoEdicion ? "Guardar" : "Agregar");
        aceptar.addActionListener(this::onAceptar);
        JButton cancelar = UITheme.secondaryButton("Cancelar");
        cancelar.addActionListener(e -> dispose());

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        acciones.add(cancelar);
        acciones.add(aceptar);
        c.gridy = y; c.insets = new Insets(16, 0, 0, 0);
        card.add(acciones, c);

        JPanel wrap = UITheme.panel(new BorderLayout(), 18);
        wrap.add(card, BorderLayout.CENTER);
        add(wrap, BorderLayout.CENTER);

        getRootPane().setDefaultButton(aceptar);
        pack();
        setMinimumSize(new Dimension(440, getHeight()));
    }

    private int addRow(JPanel card, GridBagConstraints c, int y, String label, java.awt.Component field) {
        c.gridy = y; c.insets = new Insets(0, 0, 6, 0);
        card.add(UITheme.fieldLabel(label), c);
        if (field instanceof javax.swing.JComponent jc) {
            jc.setPreferredSize(new Dimension(0, 38));
        }
        c.gridy = y + 1; c.insets = new Insets(0, 0, 14, 0);
        card.add(field, c);
        return y + 2;
    }

    private void onAceptar(java.awt.event.ActionEvent evt) {
        String nombre = textNuevoUsuarioNombre.getText().trim();
        String apellido = textNuevoUsuarioApellido.getText().trim();
        String cedula = textNuevoUsuarioCedula.getText().trim();
        String rol = dropdownNuevoUsuarioRol.getSelectedItem().toString();

        if (nombre.isEmpty() || apellido.isEmpty() || cedula.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos deben estar llenos.");
            return;
        }

        if (modoEdicion) {
            usuarioAEditar.setNombre(nombre);
            usuarioAEditar.setApellido(apellido);
            usuarioAEditar.setRol(rol);
            gestor.actualizarUsuario(usuarioAEditar);
            JOptionPane.showMessageDialog(this, "Usuario modificado correctamente.");
        } else {
            boolean duplicada = false;
            for (Usuario u : gestor.getUsuarios()) {
                if (u.getCedula().equals(cedula)) { duplicada = true; break; }
            }
            if (duplicada) {
                JOptionPane.showMessageDialog(this, "Ya existe un usuario con esta cédula.");
                return;
            }
            gestor.agregarUsuario(nombre, apellido, cedula, rol);
            JOptionPane.showMessageDialog(this, "Usuario agregado correctamente.");
        }

        if (framePadre != null) {
            framePadre.cargarUsuariosEnTabla(gestor);
        }
        this.dispose();
    }
}
