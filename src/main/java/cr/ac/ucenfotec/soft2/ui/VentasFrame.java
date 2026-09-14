package cr.ac.ucenfotec.soft2.ui;

import cr.ac.ucenfotec.soft2.clientes.Cliente;
import cr.ac.ucenfotec.soft2.clientes.GestorClientes;
import cr.ac.ucenfotec.soft2.facturacion.HistorialVentas;
import cr.ac.ucenfotec.soft2.inventario.Inventario;
import cr.ac.ucenfotec.soft2.inventario.Producto;
import cr.ac.ucenfotec.soft2.usuarios.Usuario;
import cr.ac.ucenfotec.soft2.ventas.Factura;
import cr.ac.ucenfotec.soft2.ventas.GestorVentas;
import cr.ac.ucenfotec.soft2.ventas.ItemCarrito;
import cr.ac.ucenfotec.soft2.ventas.Venta;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

/**
 * Módulo de ventas rediseñado: cliente arriba, productos y carrito lado a lado,
 * y una barra de totales con acciones. Conserva toda la lógica original.
 *
 * @author Carlos Carballo Villalobos
 */
public class VentasFrame extends javax.swing.JPanel implements Refrescable, NavGuard {

    private final NavigationHost host;
    private final GestorClientes gestorClientes;
    private final Inventario inventario;
    private final GestorVentas gestorVentas;
    private final String nombreUsuarioActual;
    private final String rolUsuario;
    private final ArrayList<ItemCarrito> carrito;
    private Cliente clienteActual;
    private final HistorialVentas historial;
    private final Usuario usuario;

    // Componentes
    private JTextField txtCedulaCliente;
    private JLabel lblNombreCliente;
    private JButton btnBuscarCliente;
    private JButton btnRegistrarNuevoCliente;
    // Autocompletado de clientes
    private javax.swing.JPopupMenu popupClientes;
    private javax.swing.JList<cr.ac.ucenfotec.soft2.clientes.Cliente> listaSugerencias;
    private javax.swing.DefaultListModel<cr.ac.ucenfotec.soft2.clientes.Cliente> modeloSugerencias;
    private boolean actualizandoTexto = false;
    private JTable tablaProductosDisponibles;
    private JTextField txtBuscarProductos;
    private JSpinner spinnerCantidad;
    private JButton btnAgregarAlCarrito;
    private JButton btnLimpiarBusqueda;
    private JTable tablaCarrito;
    private JButton btnEliminarDelCarrito;
    private JButton btnModificarCantidad;
    private JLabel lblSubtotal;
    private JLabel lblIVA;
    private JLabel lblTotal;
    private JButton btnCancelarVenta;
    private JButton btnFinalizarVenta;

    public VentasFrame(NavigationHost host, HistorialVentas historial, Inventario inventario, Usuario usuario) {
        this.host = host;
        this.historial = historial;
        this.inventario = inventario;
        this.usuario = usuario;
        this.rolUsuario = usuario.getRol();
        this.nombreUsuarioActual = usuario.getNombre();
        this.gestorClientes = new GestorClientes();
        this.gestorVentas = new GestorVentas();
        this.carrito = new ArrayList<>();
        this.clienteActual = null;

        initUI();
        configurarComponentes();
        cargarProductosEnTabla();
    }

    @Override
    public void refrescar() {
        // Recargar productos (stock puede haber cambiado en otro módulo).
        cargarProductosEnTabla();
    }

    /**
     * Guarda contra abandonar una venta en progreso. Si hay artículos en el
     * carrito, pide confirmación antes de permitir salir.
     */
    @Override
    public boolean puedeSalir() {
        if (carrito.isEmpty()) {
            return true;
        }
        int r = JOptionPane.showConfirmDialog(this,
                "Hay una venta en proceso con " + carrito.size()
                        + " artículo(s). ¿Desea salir y descartarla?",
                "Venta en proceso", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            limpiarVenta(); // descartar la venta al salir
            return true;
        }
        return false;
    }

    private void initUI() {
        setLayout(new BorderLayout());

        ModuleScaffold sc = ModuleScaffold.build(
                "Ventas", "Registre productos y finalice la venta");

        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setOpaque(false);

        body.add(buildClientePanel(), BorderLayout.NORTH);
        body.add(buildCenter(), BorderLayout.CENTER);
        body.add(buildTotalesPanel(), BorderLayout.SOUTH);

        sc.content.setLayout(new BorderLayout());
        sc.content.add(body, BorderLayout.CENTER);
        add(sc.root, BorderLayout.CENTER);
    }

    private JPanel buildClientePanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        p.setBackground(new java.awt.Color(0xF3E9E0));
        p.setBorder(new UITheme.RoundedLineBorder(UITheme.BORDER, 14, 1));

        JLabel lbl = UITheme.fieldLabel("Cliente (cédula o nombre):");
        txtCedulaCliente = UITheme.textField("Escriba para filtrar...", 16);
        txtCedulaCliente.setPreferredSize(new Dimension(240, 36));
        txtCedulaCliente.addActionListener(e -> onBuscarCliente());

        btnBuscarCliente = UITheme.primaryButton("Buscar");
        btnBuscarCliente.addActionListener(e -> onBuscarCliente());

        btnRegistrarNuevoCliente = UITheme.secondaryButton("Nuevo Cliente");
        btnRegistrarNuevoCliente.addActionListener(e -> onRegistrarNuevoCliente());

        lblNombreCliente = new JLabel("");
        lblNombreCliente.setFont(UITheme.FONT_BODY_BD);
        lblNombreCliente.setForeground(UITheme.BRAND);
        lblNombreCliente.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));

        configurarAutocompletado();

        p.add(lbl);
        p.add(txtCedulaCliente);
        p.add(btnBuscarCliente);
        p.add(btnRegistrarNuevoCliente);
        p.add(lblNombreCliente);
        return p;
    }

    /** Configura el filtrado en vivo de clientes con un popup de sugerencias. */
    private void configurarAutocompletado() {
        modeloSugerencias = new javax.swing.DefaultListModel<>();
        listaSugerencias = new javax.swing.JList<>(modeloSugerencias);
        listaSugerencias.setFont(UITheme.FONT_BODY);
        listaSugerencias.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listaSugerencias.setCellRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<?> list, Object value, int index,
                    boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, index, sel, focus);
                if (value instanceof cr.ac.ucenfotec.soft2.clientes.Cliente c) {
                    setText(c.getCedula() + "   —   " + c.getNombre() + " " + c.getApellido());
                }
                return this;
            }
        });

        javax.swing.JScrollPane sp = new javax.swing.JScrollPane(listaSugerencias);
        sp.setBorder(null);
        sp.setPreferredSize(new Dimension(320, 160));

        popupClientes = new javax.swing.JPopupMenu();
        popupClientes.setFocusable(false); // el foco permanece en el campo de texto
        popupClientes.add(sp);

        // Al hacer clic en una sugerencia, seleccionar ese cliente.
        listaSugerencias.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                seleccionarSugerencia();
            }
        });

        // Filtrar a medida que se escribe.
        txtCedulaCliente.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });

        // Flechas y Enter para navegar/elegir dentro del popup desde el campo.
        txtCedulaCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent e) {
                if (!popupClientes.isVisible()) return;
                int idx = listaSugerencias.getSelectedIndex();
                switch (e.getKeyCode()) {
                    case java.awt.event.KeyEvent.VK_DOWN -> {
                        if (idx < modeloSugerencias.getSize() - 1) {
                            listaSugerencias.setSelectedIndex(idx + 1);
                            listaSugerencias.ensureIndexIsVisible(idx + 1);
                        }
                        e.consume();
                    }
                    case java.awt.event.KeyEvent.VK_UP -> {
                        if (idx > 0) {
                            listaSugerencias.setSelectedIndex(idx - 1);
                            listaSugerencias.ensureIndexIsVisible(idx - 1);
                        }
                        e.consume();
                    }
                    case java.awt.event.KeyEvent.VK_ENTER -> {
                        if (idx != -1) { seleccionarSugerencia(); e.consume(); }
                    }
                    case java.awt.event.KeyEvent.VK_ESCAPE -> popupClientes.setVisible(false);
                    default -> { }
                }
            }
        });
    }

    private void filtrar() {
        if (actualizandoTexto) return;
        String texto = txtCedulaCliente.getText().trim();
        if (texto.isEmpty()) {
            popupClientes.setVisible(false);
            return;
        }
        java.util.List<cr.ac.ucenfotec.soft2.clientes.Cliente> resultados =
                gestorClientes.buscarCoincidencias(texto);
        modeloSugerencias.clear();
        for (var c : resultados) {
            modeloSugerencias.addElement(c);
        }
        if (modeloSugerencias.isEmpty()) {
            popupClientes.setVisible(false);
        } else {
            listaSugerencias.setSelectedIndex(0);
            popupClientes.show(txtCedulaCliente, 0, txtCedulaCliente.getHeight());
            txtCedulaCliente.requestFocusInWindow();
        }
    }

    private void seleccionarSugerencia() {
        var c = listaSugerencias.getSelectedValue();
        if (c == null) return;
        popupClientes.setVisible(false);
        actualizandoTexto = true;
        txtCedulaCliente.setText(c.getCedula());
        actualizandoTexto = false;
        seleccionarCliente(c);
    }

    /** Marca un cliente como el cliente actual de la venta y habilita controles. */
    private void seleccionarCliente(cr.ac.ucenfotec.soft2.clientes.Cliente c) {
        clienteActual = c;
        lblNombreCliente.setText("\u2713  " + c.getNombre() + " " + c.getApellido());
        habilitarControlesVenta();
    }

    private JPanel buildCenter() {
        JPanel split = new JPanel(new GridLayout(1, 2, 14, 0));
        split.setOpaque(false);
        split.add(buildProductosPanel());
        split.add(buildCarritoPanel());
        return split;
    }

    private JPanel buildProductosPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.add(section("Productos Disponibles"), BorderLayout.NORTH);

        tablaProductosDisponibles = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Código", "Nombre", "Categoría", "Precio", "Stock Disponible"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        UITheme.styleTable(tablaProductosDisponibles);
        panel.add(UITheme.tableScroll(tablaProductosDisponibles), BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        controls.setOpaque(false);
        txtBuscarProductos = UITheme.textField("Buscar código, nombre o categoría...", 14);
        txtBuscarProductos.setPreferredSize(new Dimension(240, 34));
        txtBuscarProductos.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { onBuscarProductos(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { onBuscarProductos(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { onBuscarProductos(); }
        });
        btnLimpiarBusqueda = UITheme.secondaryButton("Reset");
        btnLimpiarBusqueda.addActionListener(e -> onLimpiarBusqueda());
        spinnerCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        spinnerCantidad.setPreferredSize(new Dimension(70, 34));
        btnAgregarAlCarrito = UITheme.primaryButton("Agregar");
        btnAgregarAlCarrito.setIcon(Icons.cart(14, java.awt.Color.WHITE));
        btnAgregarAlCarrito.addActionListener(e -> onAgregarAlCarrito());

        controls.add(txtBuscarProductos);
        controls.add(btnLimpiarBusqueda);
        controls.add(new JLabel("Cant.:"));
        controls.add(spinnerCantidad);
        controls.add(btnAgregarAlCarrito);
        panel.add(controls, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildCarritoPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.add(section("Carrito de Compras"), BorderLayout.NORTH);

        tablaCarrito = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"Código", "Nombre", "Cantidad", "Precio Unidad", "Subtotal"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        UITheme.styleTable(tablaCarrito);
        panel.add(UITheme.tableScroll(tablaCarrito), BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        controls.setOpaque(false);
        btnModificarCantidad = UITheme.secondaryButton("Editar Cantidad");
        btnModificarCantidad.setIcon(Icons.edit(14, UITheme.BRAND));
        btnModificarCantidad.addActionListener(e -> onModificarCantidad());
        btnEliminarDelCarrito = UITheme.dangerButton("Eliminar");
        btnEliminarDelCarrito.setIcon(Icons.delete(14, java.awt.Color.WHITE));
        btnEliminarDelCarrito.addActionListener(e -> onEliminarDelCarrito());
        controls.add(btnModificarCantidad);
        controls.add(btnEliminarDelCarrito);
        panel.add(controls, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildTotalesPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UITheme.SIDEBAR_BG);
        p.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JPanel totales = new JPanel(new FlowLayout(FlowLayout.LEFT, 28, 0));
        totales.setOpaque(false);
        lblSubtotal = totalLabel("\u20A10.00");
        lblIVA = totalLabel("\u20A10.00");
        lblTotal = totalLabel("\u20A10.00");
        lblTotal.setFont(UITheme.FONT_H1);
        lblTotal.setForeground(UITheme.ACCENT);
        totales.add(pair("Subtotal", lblSubtotal));
        totales.add(pair("IVA (13%)", lblIVA));
        totales.add(pair("TOTAL", lblTotal));

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acciones.setOpaque(false);
        btnCancelarVenta = UITheme.secondaryButton("Cancelar Venta");
        btnCancelarVenta.addActionListener(e -> onCancelarVenta());
        btnFinalizarVenta = UITheme.primaryButton("Finalizar Venta");
        btnFinalizarVenta.setIcon(Icons.check(14, java.awt.Color.WHITE));
        btnFinalizarVenta.setPreferredSize(new Dimension(190, UITheme.BUTTON_HEIGHT));
        btnFinalizarVenta.addActionListener(e -> onFinalizarVenta());
        acciones.add(btnCancelarVenta);
        acciones.add(btnFinalizarVenta);

        p.add(totales, BorderLayout.WEST);
        p.add(acciones, BorderLayout.EAST);
        return p;
    }

    private JPanel pair(String caption, JLabel value) {
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new javax.swing.BoxLayout(col, javax.swing.BoxLayout.Y_AXIS));
        JLabel cap = new JLabel(caption);
        cap.setFont(UITheme.FONT_SMALL);
        cap.setForeground(UITheme.TEXT_ON_DARK);
        cap.setAlignmentX(LEFT_ALIGNMENT);
        value.setAlignmentX(LEFT_ALIGNMENT);
        col.add(cap);
        col.add(value);
        return col;
    }

    private JLabel totalLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_H2);
        l.setForeground(java.awt.Color.WHITE);
        return l;
    }

    private JPanel section(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_H2);
        l.setForeground(UITheme.BRAND);
        p.add(l, BorderLayout.WEST);
        return p;
    }

    // ---------------- Lógica (conservada del original) ----------------

    private void configurarComponentes() {
        deshabilitarControlesVenta();
        spinnerCantidad.setValue(1);
        lblNombreCliente.setText("");
    }

    private void deshabilitarControlesVenta() {
        tablaProductosDisponibles.setEnabled(false);
        txtBuscarProductos.setEnabled(false);
        spinnerCantidad.setEnabled(false);
        btnAgregarAlCarrito.setEnabled(false);
        btnEliminarDelCarrito.setEnabled(false);
        btnModificarCantidad.setEnabled(false);
        btnFinalizarVenta.setEnabled(false);
    }

    private void habilitarControlesVenta() {
        tablaProductosDisponibles.setEnabled(true);
        txtBuscarProductos.setEnabled(true);
        spinnerCantidad.setEnabled(true);
        btnAgregarAlCarrito.setEnabled(true);
        btnEliminarDelCarrito.setEnabled(true);
        btnModificarCantidad.setEnabled(true);
        btnFinalizarVenta.setEnabled(true);
    }

    /**
     * Renderiza la tabla de productos aplicando el texto de búsqueda y mostrando
     * el stock DISPONIBLE = stock físico − lo que ya está en el carrito.
     * El stock físico (base de datos) no cambia hasta finalizar la venta.
     */
    private void cargarProductosEnTabla() {
        String busqueda = txtBuscarProductos.getText().trim().toLowerCase();
        DefaultTableModel modelo = (DefaultTableModel) tablaProductosDisponibles.getModel();
        modelo.setRowCount(0);
        for (Producto p : inventario.obtenerTodosLosProductos()) {
            if (!busqueda.isEmpty()
                    && !p.getNombreProducto().toLowerCase().contains(busqueda)
                    && !p.getCodigoProducto().toLowerCase().contains(busqueda)
                    && !p.getCategoriaProducto().toLowerCase().contains(busqueda)) {
                continue;
            }
            int disponible = p.getCantidadStock() - obtenerCantidadEnCarrito(p.getCodigoProducto());
            modelo.addRow(new Object[]{
                p.getCodigoProducto(), p.getNombreProducto(), p.getCategoriaProducto(),
                String.format("\u20A1%.2f", p.getPrecioProducto()), disponible});
        }
    }

    private void actualizarTablaCarrito() {
        DefaultTableModel modelo = (DefaultTableModel) tablaCarrito.getModel();
        modelo.setRowCount(0);
        for (ItemCarrito item : carrito) {
            modelo.addRow(new Object[]{
                item.getProducto().getCodigoProducto(),
                item.getProducto().getNombreProducto(),
                item.getCantidad(),
                String.format("\u20A1%.2f", item.getProducto().getPrecioProducto()),
                String.format("\u20A1%.2f", item.getSubtotal())});
        }
        actualizarTotales();
        // Refrescar la tabla de productos para reflejar el disponible en vivo.
        cargarProductosEnTabla();
    }

    private void actualizarTotales() {
        double subtotal = 0;
        for (ItemCarrito item : carrito) {
            subtotal += item.getSubtotal();
        }
        double iva = subtotal * 0.13;
        double total = subtotal + iva;
        lblSubtotal.setText(String.format("\u20A1%.2f", subtotal));
        lblIVA.setText(String.format("\u20A1%.2f", iva));
        lblTotal.setText(String.format("\u20A1%.2f", total));
    }

    private void limpiarVenta() {
        carrito.clear();
        clienteActual = null;
        txtCedulaCliente.setText("");
        lblNombreCliente.setText("");
        actualizarTablaCarrito();
        deshabilitarControlesVenta();
        spinnerCantidad.setValue(1);
        txtBuscarProductos.setText("");
    }

    private int obtenerCantidadEnCarrito(String codigoProducto) {
        for (ItemCarrito item : carrito) {
            if (item.getProducto().getCodigoProducto().equals(codigoProducto)) {
                return item.getCantidad();
            }
        }
        return 0;
    }

    private void onBuscarCliente() {
        String texto = txtCedulaCliente.getText().trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar una cédula o nombre.");
            return;
        }

        // 1) Coincidencia exacta por cédula.
        cr.ac.ucenfotec.soft2.clientes.Cliente exacto = gestorClientes.buscarCliente(texto);
        if (exacto != null) {
            popupClientes.setVisible(false);
            seleccionarCliente(exacto);
            return;
        }

        // 2) Coincidencias parciales.
        java.util.List<cr.ac.ucenfotec.soft2.clientes.Cliente> coincidencias =
                gestorClientes.buscarCoincidencias(texto);
        if (coincidencias.size() == 1) {
            popupClientes.setVisible(false);
            actualizandoTexto = true;
            txtCedulaCliente.setText(coincidencias.get(0).getCedula());
            actualizandoTexto = false;
            seleccionarCliente(coincidencias.get(0));
            return;
        }
        if (coincidencias.size() > 1) {
            // Mostrar el popup con las opciones para que el usuario elija.
            filtrar();
            return;
        }

        // 3) Sin coincidencias: ofrecer registro.
        lblNombreCliente.setText("");
        clienteActual = null;
        deshabilitarControlesVenta();
        int respuesta = JOptionPane.showConfirmDialog(this,
                "Cliente no encontrado. ¿Desea registrarlo?",
                "Cliente no encontrado", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            onRegistrarNuevoCliente();
        }
    }

    private void onRegistrarNuevoCliente() {
        // Diálogo modal: al cerrarse, intentamos seleccionar el cliente recién creado.
        AgregarNuevoClienteFrame dlg = new AgregarNuevoClienteFrame(gestorClientes, null);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        String cedula = txtCedulaCliente.getText().trim();
        if (!cedula.isEmpty()) {
            clienteActual = gestorClientes.buscarCliente(cedula);
            if (clienteActual != null) {
                lblNombreCliente.setText("\u2713  " + clienteActual.getNombre() + " " + clienteActual.getApellido());
                habilitarControlesVenta();
            }
        }
    }

    /** Búsqueda en tiempo real: re-renderiza aplicando el texto actual. */
    private void onBuscarProductos() {
        cargarProductosEnTabla();
    }

    private void onAgregarAlCarrito() {
        int fila = tablaProductosDisponibles.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un producto.");
            return;
        }
        String codigoProducto = tablaProductosDisponibles.getValueAt(fila, 0).toString();
        Producto producto = inventario.buscarProducto(codigoProducto);
        if (producto == null) {
            JOptionPane.showMessageDialog(this, "Producto no encontrado.");
            return;
        }
        int cantidadSolicitada = (int) spinnerCantidad.getValue();
        int cantidadEnCarrito = obtenerCantidadEnCarrito(codigoProducto);
        int stockDisponible = producto.getCantidadStock() - cantidadEnCarrito;
        if (cantidadSolicitada > stockDisponible) {
            JOptionPane.showMessageDialog(this,
                    "Stock insuficiente. Disponible: " + stockDisponible,
                    "Stock Insuficiente", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean encontrado = false;
        for (ItemCarrito item : carrito) {
            if (item.getProducto().getCodigoProducto().equals(codigoProducto)) {
                item.setCantidad(item.getCantidad() + cantidadSolicitada);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            carrito.add(new ItemCarrito(producto, cantidadSolicitada));
        }
        actualizarTablaCarrito();
        spinnerCantidad.setValue(1);
    }

    private void onEliminarDelCarrito() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un producto del carrito.");
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar este producto del carrito?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            carrito.remove(fila);
            actualizarTablaCarrito();
        }
    }

    private void onModificarCantidad() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un producto del carrito.");
            return;
        }
        ItemCarrito item = carrito.get(fila);
        String input = JOptionPane.showInputDialog(this,
                "Nueva cantidad (Stock disponible: " + item.getProducto().getCantidadStock() + "):",
                item.getCantidad());
        if (input != null && !input.trim().isEmpty()) {
            try {
                int nuevaCantidad = Integer.parseInt(input.trim());
                if (nuevaCantidad <= 0) {
                    JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0.");
                    return;
                }
                if (nuevaCantidad > item.getProducto().getCantidadStock()) {
                    JOptionPane.showMessageDialog(this,
                            "Stock insuficiente. Disponible: " + item.getProducto().getCantidadStock(),
                            "Stock Insuficiente", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                item.setCantidad(nuevaCantidad);
                actualizarTablaCarrito();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Cantidad inválida.");
            }
        }
    }

    private void onFinalizarVenta() {
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.");
            return;
        }
        if (clienteActual == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente.");
            return;
        }
        String numeroVenta = gestorVentas.generarNumeroVenta();
        Venta venta = new Venta(numeroVenta, clienteActual, nombreUsuarioActual);
        for (ItemCarrito item : carrito) {
            venta.agregarDetalle(item.toDetalleVenta());
        }
        int confirmacion = JOptionPane.showConfirmDialog(this,
                venta.obtenerResumen(), "Confirmar Venta", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            Factura factura = venta.finalizarVenta();
            if (factura != null) {
                // Persistir: descontar stock en la BD y guardar la factura.
                for (ItemCarrito item : carrito) {
                    inventario.descontarStock(
                            item.getProducto().getCodigoProducto(), item.getCantidad());
                }
                gestorVentas.registrarVenta(venta);
                gestorVentas.registrarFactura(factura);
                historial.agregarFactura(factura);
                JOptionPane.showMessageDialog(this,
                        factura.generarFacturaTexto(),
                        "Factura Generada", JOptionPane.INFORMATION_MESSAGE);
                limpiarVenta();
                cargarProductosEnTabla();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error: Stock insuficiente para completar la venta.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onCancelarVenta() {
        if (!carrito.isEmpty()) {
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro que desea cancelar la venta?",
                    "Confirmar Cancelación", JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                limpiarVenta();
            }
        } else {
            limpiarVenta();
        }
    }

    private void onLimpiarBusqueda() {
        txtBuscarProductos.setText("");
        cargarProductosEnTabla();
    }
}
