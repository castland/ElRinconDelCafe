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
public class AdministrarUsuariosFrame extends javax.swing.JPanel implements Refrescable {

    private final NavigationHost host;
    public GestorUsuarios gestor;

    private JTable jTableUsuarios;

    public AdministrarUsuariosFrame(NavigationHost host, GestorUsuarios gestor) {
        this.host = host;
        this.gestor = gestor;
        initUI();
        cargarUsuariosEnTabla(gestor);
    }

    @Override
    public void refrescar() {
        cargarUsuariosEnTabla(gestor);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        ModuleScaffold sc = ModuleScaffold.build(
                "Usuarios", "Gestione el personal del sistema");

        jTableUsuarios = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Nombre", "Apellido", "Cédula", "Rol"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        UITheme.styleTable(jTableUsuarios);

        JButton agregar = UITheme.primaryButton("Agregar Usuario");
        agregar.setIcon(Icons.add(14, java.awt.Color.WHITE));
        agregar.addActionListener(this::onAgregar);
        JButton editar = UITheme.secondaryButton("Editar");
        editar.setIcon(Icons.edit(14, UITheme.BRAND));
        editar.addActionListener(this::onModificar);
        JButton eliminar = UITheme.dangerButton("Eliminar");
        eliminar.setIcon(Icons.delete(14, java.awt.Color.WHITE));
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
        new AgregarNuevoUsuarioFrame(gestor, this).setVisible(true);
        cargarUsuariosEnTabla(gestor);
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
            gestor.eliminarUsuario(cedula);
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
            new AgregarNuevoUsuarioFrame(gestor, this, usuario).setVisible(true);
            cargarUsuariosEnTabla(gestor);
        }
    }
}
