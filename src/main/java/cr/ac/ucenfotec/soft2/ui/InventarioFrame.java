package cr.ac.ucenfotec.soft2.ui;

import cr.ac.ucenfotec.soft2.inventario.Inventario;
import cr.ac.ucenfotec.soft2.inventario.Producto;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Módulo de inventario rediseñado: tabla moderna a pantalla completa con barra
 * de acciones. Conserva la lógica original (Inventario, Producto).
 *
 * @author Carlos / Kenner Gamboa Suarez
 */
public class InventarioFrame extends javax.swing.JPanel implements Refrescable {

    private final NavigationHost host;
    public Inventario inventario;
    private final String rolUsuario;
    private final String nombreUsuario;

    private JTable jTable1;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtBuscar;
    private JComboBox<String> cboCategoria;

    public InventarioFrame(NavigationHost host, Inventario inventario, String rolUsuario, String nombreUsuario) {
        this.host = host;
        this.inventario = inventario;
        this.rolUsuario = rolUsuario;
        this.nombreUsuario = nombreUsuario;
        initUI();
        cargarProductosEnTabla();
    }

    @Override
    public void refrescar() {
        cargarProductosEnTabla();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        ModuleScaffold sc = ModuleScaffold.build(
                "Inventario", "Gestione los productos y el stock disponible");

        jTable1 = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Código", "Nombre", "Categoría", "Precio", "Cantidad Stock"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        UITheme.styleTable(jTable1);
        jTable1.setCellSelectionEnabled(true);
        UITheme.enableCopy(jTable1, 0, "Copiar código"); // columna 0 = Código

        sorter = new TableRowSorter<>((DefaultTableModel) jTable1.getModel());
        jTable1.setRowSorter(sorter);

        JButton refrescar = UITheme.secondaryButton("Actualizar");
        refrescar.setIcon(Icons.refresh(14, UITheme.BRAND));
        refrescar.addActionListener(e -> cargarProductosEnTabla());
        JButton agregar = UITheme.primaryButton("Agregar Producto");
        agregar.setIcon(Icons.add(14, java.awt.Color.WHITE));
        agregar.addActionListener(this::onAgregar);
        JButton modificar = UITheme.secondaryButton("Modificar");
        modificar.setIcon(Icons.edit(14, UITheme.BRAND));
        modificar.addActionListener(this::onModificar);

        sc.toolbar.add(agregar);
        sc.toolbar.add(modificar);
        sc.toolbar.add(refrescar);

        // Filtro por categoría + búsqueda en vivo (código / nombre / categoría).
        cboCategoria = new JComboBox<>(new String[]{"Todas", "Café", "Bebida Fría", "Pastelería"});
        cboCategoria.setPreferredSize(new Dimension(140, 36));
        cboCategoria.addActionListener(e -> aplicarFiltro());

        txtBuscar = UITheme.textField("Buscar código, nombre...", 16);
        txtBuscar.setPreferredSize(new Dimension(240, 36));
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltro(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltro(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltro(); }
        });
        sc.toolbarRight.add(cboCategoria);
        sc.toolbarRight.add(txtBuscar);

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

    /** Combina la búsqueda de texto (código/nombre/categoría) con el filtro de categoría. */
    private void aplicarFiltro() {
        String texto = txtBuscar.getText().trim();
        String categoria = (String) cboCategoria.getSelectedItem();

        List<RowFilter<Object, Object>> filtros = new ArrayList<>();

        if (!texto.isEmpty()) {
            // Buscar el texto en código (0), nombre (1) o categoría (2).
            filtros.add(RowFilter.regexFilter(
                    "(?i)" + java.util.regex.Pattern.quote(texto), 0, 1, 2));
        }
        if (categoria != null && !"Todas".equals(categoria)) {
            // Coincidencia exacta de la categoría (columna 2).
            filtros.add(RowFilter.regexFilter(
                    "^" + java.util.regex.Pattern.quote(categoria) + "$", 2));
        }

        if (filtros.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filtros));
        }
    }

    private void onAgregar(java.awt.event.ActionEvent evt) {
        ProductoDialog dlg = new ProductoDialog(host.getVentana(), null);
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
        ProductoDialog dlg = new ProductoDialog(host.getVentana(), p);
        dlg.setVisible(true);
        if (dlg.isConfirmado()) {
            dlg.aplicarA(p);
            inventario.actualizarProducto(p);
            cargarProductosEnTabla();
            JOptionPane.showMessageDialog(this, "Producto modificado correctamente.");
        }
    }
}
