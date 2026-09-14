package cr.ac.ucenfotec.soft2.ui;

import cr.ac.ucenfotec.soft2.clientes.Cliente;
import cr.ac.ucenfotec.soft2.clientes.GestorClientes;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Módulo de administración de clientes rediseñado.
 *
 * @author Carlos Carballo Villalobos
 */
public class AdministrarClientesFrame extends javax.swing.JPanel implements Refrescable {

    private final NavigationHost host;
    public GestorClientes gestor = new GestorClientes();
    private final String rolUsuario;

    private JTable jTableClientes;
    private JTextField txtBuscarCedula;
    private TableRowSorter<DefaultTableModel> sorter;

    public AdministrarClientesFrame(NavigationHost host, String rolUsuario) {
        this.host = host;
        this.rolUsuario = rolUsuario;
        initUI();
        cargarClientesEnTabla();
    }

    @Override
    public void refrescar() {
        cargarClientesEnTabla();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        ModuleScaffold sc = ModuleScaffold.build(
                "Clientes", "Administre la cartera de clientes");

        jTableClientes = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Nombre", "Apellido", "Cédula", "Correo"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        UITheme.styleTable(jTableClientes);
        jTableClientes.setCellSelectionEnabled(true);
        UITheme.enableCopy(jTableClientes, 2, "Copiar cédula"); // columna 2 = Cédula

        // Filtrado en vivo sobre la vista de la tabla (no toca los datos).
        sorter = new TableRowSorter<>((DefaultTableModel) jTableClientes.getModel());
        jTableClientes.setRowSorter(sorter);

        JButton agregar = UITheme.primaryButton("Agregar Cliente");
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

        txtBuscarCedula = UITheme.textField("Buscar por cédula, nombre o correo...", 18);
        txtBuscarCedula.setPreferredSize(new Dimension(280, 36));
        txtBuscarCedula.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltro(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltro(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltro(); }
        });
        sc.toolbarRight.add(txtBuscarCedula);

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
        // Diálogo modal: bloquea hasta cerrarse; luego refrescamos.
        new AgregarNuevoClienteFrame(gestor, this).setVisible(true);
        cargarClientesEnTabla();
    }

    private void onEliminar(java.awt.event.ActionEvent evt) {
        int fila = jTableClientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente para eliminar.");
            return;
        }
        // getValueAt en el JTable ya tiene en cuenta el filtro/orden (índice de vista).
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

    /** Filtra la tabla en vivo por cualquier columna (insensible a mayúsculas). */
    private void aplicarFiltro() {
        String texto = txtBuscarCedula.getText().trim();
        if (texto.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            // (?i) = insensible a mayúsculas; Pattern.quote evita errores con
            // caracteres especiales que el usuario pudiera escribir.
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(texto)));
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
            new AgregarNuevoClienteFrame(gestor, this, cliente).setVisible(true);
            cargarClientesEnTabla();
        }
    }
}
