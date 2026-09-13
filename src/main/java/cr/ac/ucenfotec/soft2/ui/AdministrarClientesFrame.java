package cr.ac.ucenfotec.soft2.ui;

import cr.ac.ucenfotec.soft2.clientes.Cliente;
import cr.ac.ucenfotec.soft2.clientes.GestorClientes;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * Módulo de administración de clientes rediseñado.
 *
 * @author Carlos Carballo Villalobos
 */
public class AdministrarClientesFrame extends javax.swing.JFrame {

    private final javax.swing.JFrame menuPadre;
    public GestorClientes gestor = new GestorClientes();
    private final String rolUsuario;

    private JTable jTableClientes;
    private JTextField txtBuscarCedula;

    public AdministrarClientesFrame(javax.swing.JFrame menuPadre, String rolUsuario) {
        this.menuPadre = menuPadre;
        this.rolUsuario = rolUsuario;
        initUI();
        cargarClientesEnTabla();
        UITheme.openMaximized(this, 900, 600);
    }

    private void initUI() {
        setTitle("El Rincón del Café — Clientes");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        ModuleScaffold sc = ModuleScaffold.build(
                "Clientes", "Administre la cartera de clientes", this::volver);

        jTableClientes = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Nombre", "Apellido", "Cédula", "Correo"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        UITheme.styleTable(jTableClientes);

        JButton agregar = UITheme.primaryButton("+  Agregar Cliente");
        agregar.addActionListener(this::onAgregar);
        JButton editar = UITheme.secondaryButton("\u270E  Editar");
        editar.addActionListener(this::onModificar);
        JButton eliminar = UITheme.dangerButton("\uD83D\uDDD1  Eliminar");
        eliminar.addActionListener(this::onEliminar);

        sc.toolbar.add(agregar);
        sc.toolbar.add(editar);
        sc.toolbar.add(eliminar);

        txtBuscarCedula = UITheme.textField("Buscar por cédula...", 14);
        txtBuscarCedula.setPreferredSize(new Dimension(220, 36));
        txtBuscarCedula.addActionListener(this::onBuscar);
        JButton buscar = UITheme.secondaryButton("Buscar");
        buscar.addActionListener(this::onBuscar);
        sc.toolbarRight.add(txtBuscarCedula);
        sc.toolbarRight.add(buscar);

        sc.content.add(UITheme.tableScroll(jTableClientes), BorderLayout.CENTER);
        add(sc.root, BorderLayout.CENTER);
    }

    public void cargarClientesEnTabla() {
        DefaultTableModel modelo = (DefaultTableModel) jTableClientes.getModel();
        modelo.setRowCount(0);
        for (Cliente c : gestor.getClientes()) {
            modelo.addRow(new Object[]{c.getNombre(), c.getApellido(), c.getCedula(), c.getCorreo()});
        }
    }

    private void onAgregar(java.awt.event.ActionEvent evt) {
        this.setEnabled(false);
        AgregarNuevoClienteFrame nuevoFrame = new AgregarNuevoClienteFrame(gestor, this);
        nuevoFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                AdministrarClientesFrame.this.setEnabled(true);
                AdministrarClientesFrame.this.toFront();
                AdministrarClientesFrame.this.requestFocus();
            }
        });
        nuevoFrame.setVisible(true);
    }

    private void onEliminar(java.awt.event.ActionEvent evt) {
        int fila = jTableClientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente para eliminar.");
            return;
        }
        String cedula = jTableClientes.getValueAt(fila, 2).toString();
        String nombre = jTableClientes.getValueAt(fila, 0).toString();
        String apellido = jTableClientes.getValueAt(fila, 1).toString();
        int conf = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar al cliente " + nombre + " " + apellido + "?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION && gestor.eliminarCliente(cedula)) {
            cargarClientesEnTabla();
            JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente.");
        }
    }

    private void onBuscar(java.awt.event.ActionEvent evt) {
        String cedula = txtBuscarCedula.getText().trim();
        if (cedula.isEmpty()) {
            cargarClientesEnTabla();
            return;
        }
        Cliente cliente = gestor.buscarCliente(cedula);
        DefaultTableModel modelo = (DefaultTableModel) jTableClientes.getModel();
        modelo.setRowCount(0);
        if (cliente != null) {
            modelo.addRow(new Object[]{cliente.getNombre(), cliente.getApellido(),
                cliente.getCedula(), cliente.getCorreo()});
        } else {
            JOptionPane.showMessageDialog(this, "Cliente no encontrado.");
        }
    }

    private void onModificar(java.awt.event.ActionEvent evt) {
        int fila = jTableClientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente para modificar.");
            return;
        }
        String cedula = jTableClientes.getValueAt(fila, 2).toString();
        Cliente cliente = gestor.buscarCliente(cedula);
        if (cliente != null) {
            this.setEnabled(false);
            AgregarNuevoClienteFrame frame = new AgregarNuevoClienteFrame(gestor, this, cliente);
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosed(java.awt.event.WindowEvent e) {
                    AdministrarClientesFrame.this.setEnabled(true);
                    AdministrarClientesFrame.this.toFront();
                    AdministrarClientesFrame.this.requestFocus();
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
