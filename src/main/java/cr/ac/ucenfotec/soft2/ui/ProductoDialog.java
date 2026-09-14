package cr.ac.ucenfotec.soft2.ui;

import cr.ac.ucenfotec.soft2.inventario.Producto;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/**
 * Diálogo modal con formulario para agregar o modificar un producto.
 */
public class ProductoDialog extends JDialog {

    private final JTextField txtCodigo = UITheme.textField("Ej: CAF-001", 16);
    private final JTextField txtNombre = UITheme.textField("Ej: Espresso", 16);
    private final JComboBox<String> cboCategoria =
            new JComboBox<>(new String[]{"Café", "Bebida Fría", "Pastelería"});
    private final JSpinner spPrecio =
            new JSpinner(new SpinnerNumberModel(1000.0, 0.0, 1_000_000.0, 100.0));
    private final JSpinner spCantidad =
            new JSpinner(new SpinnerNumberModel(0, 0, 100000, 1));

    private boolean confirmado = false;

    public ProductoDialog(java.awt.Window owner, Producto existente) {
        super(owner, java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        boolean edicion = existente != null;
        setTitle(edicion ? "Modificar Producto" : "Agregar Producto");
        setLayout(new BorderLayout());

        JPanel card = UITheme.card(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 6, 0);

        int y = 0;
        y = addRow(card, c, y, "Código", txtCodigo);
        y = addRow(card, c, y, "Nombre", txtNombre);
        y = addRow(card, c, y, "Categoría", cboCategoria);
        y = addRow(card, c, y, "Precio (₡)", spPrecio);
        y = addRow(card, c, y, "Cantidad en stock", spCantidad);

        JButton aceptar = UITheme.primaryButton(edicion ? "Guardar" : "Agregar");
        aceptar.addActionListener(e -> onAceptar());
        JButton cancelar = UITheme.secondaryButton("Cancelar");
        cancelar.addActionListener(e -> dispose());

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        acciones.add(cancelar);
        acciones.add(aceptar);
        c.gridy = y; c.insets = new Insets(14, 0, 0, 0);
        card.add(acciones, c);

        if (edicion) {
            txtCodigo.setText(existente.getCodigoProducto());
            txtCodigo.setEnabled(false);
            txtNombre.setText(existente.getNombreProducto());
            cboCategoria.setSelectedItem(existente.getCategoriaProducto());
            spPrecio.setValue(existente.getPrecioProducto());
            spCantidad.setValue(existente.getCantidadStock());
        }

        JPanel wrap = UITheme.panel(new BorderLayout(), 18);
        wrap.add(card, BorderLayout.CENTER);
        add(wrap, BorderLayout.CENTER);

        getRootPane().setDefaultButton(aceptar);
        setResizable(false);
        pack();
        setMinimumSize(new Dimension(420, getHeight()));
        setLocationRelativeTo(owner);
    }

    private int addRow(JPanel card, GridBagConstraints c, int y, String label, java.awt.Component field) {
        c.gridy = y; c.insets = new Insets(y == 0 ? 0 : 12, 0, 6, 0);
        card.add(UITheme.fieldLabel(label), c);
        if (field instanceof javax.swing.JComponent jc) {
            jc.setPreferredSize(new Dimension(0, 38));
        }
        c.gridy = y + 1; c.insets = new Insets(0, 0, 0, 0);
        card.add(field, c);
        return y + 2;
    }

    private void onAceptar() {
        if (txtCodigo.getText().trim().isEmpty() || txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Código y nombre son obligatorios.",
                    "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        confirmado = true;
        dispose();
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public Producto construirProducto() {
        return new Producto(
                txtCodigo.getText().trim(),
                txtNombre.getText().trim(),
                (String) cboCategoria.getSelectedItem(),
                ((Number) spPrecio.getValue()).doubleValue(),
                ((Number) spCantidad.getValue()).intValue());
    }

    public void aplicarA(Producto p) {
        p.setNombreProducto(txtNombre.getText().trim());
        p.setCategoriaProducto((String) cboCategoria.getSelectedItem());
        p.setPrecioProducto(((Number) spPrecio.getValue()).doubleValue());
        p.setCantidadStock(((Number) spCantidad.getValue()).intValue());
    }
}
