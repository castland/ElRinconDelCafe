package cr.ac.ucenfotec.soft2.ui;

import cr.ac.ucenfotec.soft2.clientes.Cliente;
import cr.ac.ucenfotec.soft2.clientes.GestorClientes;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Formulario para agregar o modificar un cliente, con diseño moderno en tarjeta.
 *
 * @author Carlos Carballo Villalobos
 */
public class AgregarNuevoClienteFrame extends javax.swing.JDialog {

    private final GestorClientes gestor;
    private final AdministrarClientesFrame framePadre;
    private final Cliente clienteAEditar;
    private final boolean modoEdicion;

    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtCedula;
    private JTextField txtCorreo;

    public AgregarNuevoClienteFrame(GestorClientes gestor, AdministrarClientesFrame framePadre) {
        this(gestor, framePadre, null);
    }

    public AgregarNuevoClienteFrame(GestorClientes gestor, AdministrarClientesFrame framePadre, Cliente cliente) {
        super(framePadre != null ? javax.swing.SwingUtilities.getWindowAncestor(framePadre) : null,
                java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        this.gestor = gestor;
        this.framePadre = framePadre;
        this.clienteAEditar = cliente;
        this.modoEdicion = cliente != null;
        initUI();
        if (modoEdicion) {
            txtNombre.setText(cliente.getNombre());
            txtApellido.setText(cliente.getApellido());
            txtCedula.setText(cliente.getCedula());
            txtCorreo.setText(cliente.getCorreo());
            txtCedula.setEnabled(false);
        }
        setLocationRelativeTo(framePadre);
    }

    private void initUI() {
        setTitle(modoEdicion ? "Modificar Cliente" : "Agregar Nuevo Cliente");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);

        txtNombre = UITheme.textField("Nombre", 18);
        txtApellido = UITheme.textField("Apellido", 18);
        txtCedula = UITheme.textField("Cédula", 18);
        txtCorreo = UITheme.textField("correo@ejemplo.com", 18);

        JPanel card = UITheme.card(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        head.add(UITheme.title(modoEdicion ? "Modificar Cliente" : "Nuevo Cliente"), BorderLayout.CENTER);
        c.gridy = 0; c.insets = new Insets(0, 0, 18, 0);
        card.add(head, c);

        int y = 1;
        y = addRow(card, c, y, "Nombre", txtNombre);
        y = addRow(card, c, y, "Apellido", txtApellido);
        y = addRow(card, c, y, "Cédula", txtCedula);
        y = addRow(card, c, y, "Correo", txtCorreo);

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

    private int addRow(JPanel card, GridBagConstraints c, int y, String label, JTextField field) {
        c.gridy = y; c.insets = new Insets(0, 0, 6, 0);
        card.add(UITheme.fieldLabel(label), c);
        field.setPreferredSize(new Dimension(0, 38));
        c.gridy = y + 1; c.insets = new Insets(0, 0, 14, 0);
        card.add(field, c);
        return y + 2;
    }

    private void onAceptar(java.awt.event.ActionEvent evt) {
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String cedula = txtCedula.getText().trim();
        String correo = txtCorreo.getText().trim();

        if (nombre.isEmpty() || apellido.isEmpty() || cedula.isEmpty() || correo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos deben estar llenos.");
            return;
        }
        if (!correo.contains("@") || !correo.contains(".")) {
            JOptionPane.showMessageDialog(this, "Formato de correo inválido.");
            return;
        }

        if (modoEdicion) {
            clienteAEditar.setNombre(nombre);
            clienteAEditar.setApellido(apellido);
            clienteAEditar.setCorreo(correo);
            gestor.actualizarCliente(clienteAEditar);
            JOptionPane.showMessageDialog(this, "Cliente modificado correctamente.");
        } else {
            if (gestor.buscarCliente(cedula) != null) {
                JOptionPane.showMessageDialog(this, "Ya existe un cliente con esta cédula.");
                return;
            }
            gestor.agregarCliente(nombre, apellido, cedula, correo);
            JOptionPane.showMessageDialog(this, "Cliente agregado correctamente.");
        }

        if (framePadre != null) {
            framePadre.cargarClientesEnTabla();
        }
        this.dispose();
    }
}
