package cr.ac.ucenfotec.soft2.ui;

import cr.ac.ucenfotec.soft2.inventario.Inventario;
import cr.ac.ucenfotec.soft2.inventario.Producto;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Módulo de inventario rediseñado: tabla moderna a pantalla completa con barra
 * de acciones. Conserva la lógica original (Inventario, Producto).
 *
 * @author Carlos / Kenner Gamboa Suarez
 */
public class InventarioFrame extends javax.swing.JFrame {

    private final javax.swing.JFrame menuPadre;
    public Inventario inventario;
    private final String rolUsuario;
    private final String nombreUsuario;

    private JTable jTable1;

    public InventarioFrame(javax.swing.JFrame menuPadre, Inventario inventario, String rolUsuario, String nombreUsuario) {
        this.menuPadre = menuPadre;
        this.inventario = inventario;
        this.rolUsuario = rolUsuario;
        this.nombreUsuario = nombreUsuario;
        initUI();
        cargarProductosEnTabla();
        UITheme.openMaximized(this, 900, 600);
    }

    private void initUI() {
        setTitle("El Rincón del Café — Inventario");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        ModuleScaffold sc = ModuleScaffold.build(
                "Inventario", "Gestione los productos y el stock disponible",
                this::volver);

        jTable1 = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Código", "Nombre", "Categoría", "Precio", "Cantidad Stock"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        UITheme.styleTable(jTable1);

        JButton refrescar = UITheme.secondaryButton("\u21BB  Actualizar");
        refrescar.addActionListener(e -> cargarProductosEnTabla());
        JButton agregar = UITheme.primaryButton("+  Agregar Producto");
        agregar.addActionListener(this::onAgregar);
        JButton modificar = UITheme.secondaryButton("\u270E  Modificar");
        modificar.addActionListener(this::onModificar);

        sc.toolbar.add(agregar);
        sc.toolbar.add(modificar);
        sc.toolbar.add(refrescar);

        sc.content.add(UITheme.tableScroll(jTable1), BorderLayout.CENTER);
        add(sc.root, BorderLayout.CENTER);
    }

    public void cargarProductosEnTabla() {
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        modelo.setRowCount(0);
        for (Producto p : inventario.obtenerTodosLosProductos()) {
            modelo.addRow(new Object[]{
                p.getCodigoProducto(),
                p.getNombreProducto(),
                p.getCategoriaProducto(),
                p.getPrecioProducto(),
                p.getCantidadStock()
            });
        }
    }

    private void onAgregar(java.awt.event.ActionEvent evt) {
        ProductoDialog dlg = new ProductoDialog(this, null);
        dlg.setVisible(true);
        if (dlg.isConfirmado()) {
            inventario.agregarProducto(dlg.construirProducto());
            cargarProductosEnTabla();
            JOptionPane.showMessageDialog(this, "Producto agregado correctamente.");
        }
    }

    private void onModificar(java.awt.event.ActionEvent evt) {
        int fila = jTable1.getSelectedRow();
        Producto p = null;
        if (fila != -1) {
            String codigo = jTable1.getValueAt(fila, 0).toString();
            p = inventario.buscarProducto(codigo);
        }
        if (p == null) {
            String codigo = JOptionPane.showInputDialog(this, "Código del producto a modificar:");
            if (codigo == null) return;
            p = inventario.buscarProducto(codigo);
        }
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Producto no encontrado.");
            return;
        }
        ProductoDialog dlg = new ProductoDialog(this, p);
        dlg.setVisible(true);
        if (dlg.isConfirmado()) {
            dlg.aplicarA(p);
            cargarProductosEnTabla();
            JOptionPane.showMessageDialog(this, "Producto modificado correctamente.");
        }
    }

    private void volver() {
        this.dispose();
        menuPadre.setVisible(true);
    }
}
