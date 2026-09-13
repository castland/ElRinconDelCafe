package cr.ac.ucenfotec.soft2.ui;

import cr.ac.ucenfotec.soft2.usuarios.GestorUsuarios;
import cr.ac.ucenfotec.soft2.usuarios.Usuario;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Módulo de administración de usuarios rediseñado.
 *
 * @author Carlos Carballo Villalobos
 */
public class AdministrarUsuariosFrame extends javax.swing.JFrame {

    private final javax.swing.JFrame menuPadre;
    public GestorUsuarios gestor;

    private JTable jTableUsuarios;

    public AdministrarUsuariosFrame(javax.swing.JFrame menuPadre, GestorUsuarios gestor) {
        this.menuPadre = menuPadre;
        this.gestor = gestor;
        initUI();
        cargarUsuariosEnTabla(gestor);
        UITheme.openMaximized(this, 900, 600);
    }

    private void initUI() {
        setTitle("El Rincón del Café — Usuarios");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        ModuleScaffold sc = ModuleScaffold.build(
                "Usuarios", "Gestione el personal del sistema", this::volver);

        jTableUsuarios = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Nombre", "Apellido", "Cédula", "Rol"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        UITheme.styleTable(jTableUsuarios);

        JButton agregar = UITheme.primaryButton("+  Agregar Usuario");
        agregar.addActionListener(this::onAgregar);
        JButton editar = UITheme.secondaryButton("\u270E  Editar");
        editar.addActionListener(this::onModificar);
        JButton eliminar = UITheme.dangerButton("\uD83D\uDDD1  Eliminar");
        eliminar.addActionListener(this::onEliminar);

        sc.toolbar.add(agregar);
        sc.toolbar.add(editar);
        sc.toolbar.add(eliminar);

        sc.content.add(UITheme.tableScroll(jTableUsuarios), BorderLayout.CENTER);
        add(sc.root, BorderLayout.CENTER);
    }

    public void cargarUsuariosEnTabla(GestorUsuarios gestor) {
        DefaultTableModel modelo = (DefaultTableModel) jTableUsuarios.getModel();
        modelo.setRowCount(0);
        for (Usuario u : gestor.getUsuarios()) {
            modelo.addRow(new Object[]{u.getNombre(), u.getApellido(), u.getCedula(), u.getRol()});
        }
    }

    private void onAgregar(java.awt.event.ActionEvent evt) {
        this.setEnabled(false);
        AgregarNuevoUsuarioFrame nuevoFrame = new AgregarNuevoUsuarioFrame(gestor, this);
        nuevoFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                AdministrarUsuariosFrame.this.setEnabled(true);
                AdministrarUsuariosFrame.this.toFront();
                AdministrarUsuariosFrame.this.requestFocus();
            }
        });
        nuevoFrame.setVisible(true);
    }

    private void onEliminar(java.awt.event.ActionEvent evt) {
        int fila = jTableUsuarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un usuario para eliminar.");
            return;
        }
        String cedula = jTableUsuarios.getValueAt(fila, 2).toString();
        String nombre = jTableUsuarios.getValueAt(fila, 0).toString();
        int conf = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar al usuario " + nombre + "?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            gestor.getUsuarios().removeIf(u -> u.getCedula().equals(cedula));
            cargarUsuariosEnTabla(gestor);
            JOptionPane.showMessageDialog(this, "Usuario eliminado correctamente.");
        }
    }

    private void onModificar(java.awt.event.ActionEvent evt) {
        int fila = jTableUsuarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un usuario para modificar.");
            return;
        }
        String cedula = jTableUsuarios.getValueAt(fila, 2).toString();
        Usuario usuario = null;
        for (Usuario u : gestor.getUsuarios()) {
            if (u.getCedula().equals(cedula)) { usuario = u; break; }
        }
        if (usuario != null) {
            this.setEnabled(false);
            AgregarNuevoUsuarioFrame frame = new AgregarNuevoUsuarioFrame(gestor, this, usuario);
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosed(java.awt.event.WindowEvent e) {
                    AdministrarUsuariosFrame.this.setEnabled(true);
                    AdministrarUsuariosFrame.this.toFront();
                    AdministrarUsuariosFrame.this.requestFocus();
                }
            });
            frame.setVisible(true);
        }
    }

    private void volver() {
        this.dispose();
        menuPadre.setVisible(true);
    }
}
