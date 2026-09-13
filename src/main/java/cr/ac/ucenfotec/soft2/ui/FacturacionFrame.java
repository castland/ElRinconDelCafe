package cr.ac.ucenfotec.soft2.ui;

import cr.ac.ucenfotec.soft2.facturacion.HistorialVentas;
import cr.ac.ucenfotec.soft2.usuarios.Usuario;
import cr.ac.ucenfotec.soft2.ventas.Factura;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Módulo de facturación rediseñado: historial a la izquierda, detalle a la
 * derecha, con búsqueda por número de factura.
 *
 * @author Carlos / kenner
 */
public class FacturacionFrame extends javax.swing.JFrame {

    private final javax.swing.JFrame menuPadre;
    private final HistorialVentas historial;
    private final Usuario usuario;

    private JList<String> listaHistorialFacturas;
    private JTextArea detalleFacturaTextArea;
    private JTextField buscarFacturaTextField;

    public FacturacionFrame(javax.swing.JFrame menuPadre, HistorialVentas historial, Usuario usuario) {
        this.menuPadre = menuPadre;
        this.historial = historial;
        this.usuario = usuario;
        initUI();
        cargarListaFacturas();
        UITheme.openMaximized(this, 1000, 620);
    }

    private void initUI() {
        setTitle("El Rincón del Café — Facturación");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        ModuleScaffold sc = ModuleScaffold.build(
                "Facturación", "Consulte el historial y el detalle de facturas", this::volver);

        buscarFacturaTextField = UITheme.textField("Número de factura...", 16);
        buscarFacturaTextField.setPreferredSize(new Dimension(240, 36));
        buscarFacturaTextField.addActionListener(this::onBuscar);
        JButton buscar = UITheme.primaryButton("Buscar");
        buscar.addActionListener(this::onBuscar);
        sc.toolbar.add(buscarFacturaTextField);
        sc.toolbar.add(buscar);

        // Contenido: split horizontal
        JPanel split = new JPanel(new GridLayout(1, 2, 16, 0));
        split.setOpaque(false);

        // Izquierda: lista de facturas
        JPanel left = new JPanel(new BorderLayout(0, 10));
        left.setOpaque(false);
        left.add(sectionLabel("Historial de Facturas"), BorderLayout.NORTH);
        listaHistorialFacturas = new JList<>();
        listaHistorialFacturas.setFont(UITheme.FONT_BODY);
        listaHistorialFacturas.setFixedCellHeight(30);
        JScrollPane spList = new JScrollPane(listaHistorialFacturas);
        spList.setBorder(new UITheme.RoundedLineBorder(UITheme.BORDER, 14, 1));
        left.add(spList, BorderLayout.CENTER);
        JButton verDetalles = UITheme.secondaryButton("Ver Detalles");
        verDetalles.addActionListener(this::onVerDetalles);
        JPanel leftSouth = new JPanel(new BorderLayout());
        leftSouth.setOpaque(false);
        leftSouth.add(verDetalles, BorderLayout.WEST);
        left.add(leftSouth, BorderLayout.SOUTH);

        // Derecha: detalle
        JPanel right = new JPanel(new BorderLayout(0, 10));
        right.setOpaque(false);
        right.add(sectionLabel("Detalle de la Factura"), BorderLayout.NORTH);
        detalleFacturaTextArea = new JTextArea();
        detalleFacturaTextArea.setEditable(false);
        detalleFacturaTextArea.setFont(new java.awt.Font("Consolas", java.awt.Font.PLAIN, 13));
        detalleFacturaTextArea.setMargin(new java.awt.Insets(10, 12, 10, 12));
        JScrollPane spDet = new JScrollPane(detalleFacturaTextArea);
        spDet.setBorder(new UITheme.RoundedLineBorder(UITheme.BORDER, 14, 1));
        right.add(spDet, BorderLayout.CENTER);

        split.add(left);
        split.add(right);

        sc.content.setLayout(new BorderLayout());
        sc.content.add(split, BorderLayout.CENTER);
        add(sc.root, BorderLayout.CENTER);
    }

    private JPanel sectionLabel(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        javax.swing.JLabel l = UITheme.fieldLabel(text);
        l.setFont(UITheme.FONT_H2);
        l.setForeground(UITheme.BRAND);
        p.add(l, BorderLayout.WEST);
        p.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
        return p;
    }

    private void cargarListaFacturas() {
        DefaultListModel<String> modelo = new DefaultListModel<>();
        for (Factura f : historial.getFacturas()) {
            modelo.addElement(f.getNumeroFactura() + "  |  " + f.getFechaEmision());
        }
        if (modelo.isEmpty()) {
            modelo.addElement("(No hay facturas registradas)");
            listaHistorialFacturas.setEnabled(false);
        } else {
            listaHistorialFacturas.setEnabled(true);
        }
        listaHistorialFacturas.setModel(modelo);
    }

    private void onBuscar(java.awt.event.ActionEvent evt) {
        String numeroFactura = buscarFacturaTextField.getText().trim();
        if (numeroFactura.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un número de factura.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Factura factura = historial.buscarFactura(numeroFactura);
        if (factura == null) {
            JOptionPane.showMessageDialog(this,
                    "No se encontró la factura con número: " + numeroFactura,
                    "Factura no encontrada", JOptionPane.ERROR_MESSAGE);
            return;
        }
        detalleFacturaTextArea.setText(factura.generarFacturaTexto());
        detalleFacturaTextArea.setCaretPosition(0);
    }

    private void onVerDetalles(java.awt.event.ActionEvent evt) {
        int seleccion = listaHistorialFacturas.getSelectedIndex();
        if (seleccion == -1 || historial.getFacturas().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura de la lista.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Factura f = historial.getFacturas().get(seleccion);
        detalleFacturaTextArea.setText(f.generarFacturaTexto());
        detalleFacturaTextArea.setCaretPosition(0);
    }

    private void volver() {
        this.dispose();
        menuPadre.setVisible(true);
    }
}
