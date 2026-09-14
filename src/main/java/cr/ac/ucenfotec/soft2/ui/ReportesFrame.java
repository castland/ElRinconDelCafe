package cr.ac.ucenfotec.soft2.ui;

import cr.ac.ucenfotec.soft2.clientes.Cliente;
import cr.ac.ucenfotec.soft2.clientes.GestorClientes;
import cr.ac.ucenfotec.soft2.facturacion.HistorialVentas;
import cr.ac.ucenfotec.soft2.inventario.Inventario;
import cr.ac.ucenfotec.soft2.inventario.Producto;
import cr.ac.ucenfotec.soft2.usuarios.GestorUsuarios;
import cr.ac.ucenfotec.soft2.usuarios.Usuario;
import cr.ac.ucenfotec.soft2.ventas.Factura;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Módulo de reportes rediseñado: panel de parámetros a la izquierda, resultado
 * a la derecha.
 *
 * @author Carlos / Kenner
 */
public class ReportesFrame extends javax.swing.JPanel implements Refrescable {

    private final NavigationHost host;
    private final HistorialVentas historial;
    private final GestorUsuarios gestorUsuarios;
    private final Inventario inventario;
    private final Usuario usuario;
    private final GestorClientes gestorClientes;

    private JRadioButton ventasRadioBoton;
    private JRadioButton productosRadioBoton;
    private JRadioButton clientesRadioBoton;
    private JComboBox<String> fechasReportesComboBox;
    private JTextArea detallesDelReporteTextArea;

    public ReportesFrame(NavigationHost host, HistorialVentas historial,
                         GestorUsuarios gestorUsuarios, Inventario inventario, Usuario usuario) {
        this.host = host;
        this.historial = historial;
        this.gestorUsuarios = gestorUsuarios;
        this.inventario = inventario;
        this.usuario = usuario;
        this.gestorClientes = new GestorClientes();
        initUI();
    }

    @Override
    public void refrescar() {
    }

    private void initUI() {
        setLayout(new BorderLayout());

        ModuleScaffold sc = ModuleScaffold.build(
                "Reportes", "Genere reportes de ventas, productos y clientes");

        JPanel split = new JPanel(new BorderLayout(16, 0));
        split.setOpaque(false);

        // Izquierda: parámetros
        JPanel params = new JPanel();
        params.setOpaque(false);
        params.setLayout(new BoxLayout(params, BoxLayout.Y_AXIS));
        params.setPreferredSize(new Dimension(280, 0));

        JLabel tipoLbl = UITheme.fieldLabel("Tipo de reporte");
        tipoLbl.setFont(UITheme.FONT_H2);
        tipoLbl.setForeground(UITheme.BRAND);
        tipoLbl.setAlignmentX(LEFT_ALIGNMENT);

        ventasRadioBoton = radio("Ventas");
        productosRadioBoton = radio("Productos");
        clientesRadioBoton = radio("Clientes");
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(ventasRadioBoton);
        grupo.add(productosRadioBoton);
        grupo.add(clientesRadioBoton);

        JLabel fechaLbl = UITheme.fieldLabel("Período");
        fechaLbl.setAlignmentX(LEFT_ALIGNMENT);
        fechaLbl.setBorder(BorderFactory.createEmptyBorder(16, 0, 6, 0));
        fechasReportesComboBox = new JComboBox<>(new String[]{"Última Semana", "Último Mes", "Último Año"});
        fechasReportesComboBox.setAlignmentX(LEFT_ALIGNMENT);
        fechasReportesComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JButton generar = UITheme.primaryButton("Generar Reporte");
        generar.setAlignmentX(LEFT_ALIGNMENT);
        generar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        generar.addActionListener(this::onGenerar);

        params.add(tipoLbl);
        params.add(javax.swing.Box.createVerticalStrut(10));
        params.add(ventasRadioBoton);
        params.add(productosRadioBoton);
        params.add(clientesRadioBoton);
        params.add(fechaLbl);
        params.add(fechasReportesComboBox);
        params.add(javax.swing.Box.createVerticalStrut(20));
        params.add(generar);
        params.add(javax.swing.Box.createVerticalGlue());

        // Derecha: resultado
        detallesDelReporteTextArea = new JTextArea();
        detallesDelReporteTextArea.setEditable(false);
        detallesDelReporteTextArea.setFont(new java.awt.Font("Consolas", java.awt.Font.PLAIN, 13));
        detallesDelReporteTextArea.setMargin(new java.awt.Insets(10, 12, 10, 12));
        JScrollPane sp = new JScrollPane(detallesDelReporteTextArea);
        sp.setBorder(new UITheme.RoundedLineBorder(UITheme.BORDER, 14, 1));

        split.add(params, BorderLayout.WEST);
        split.add(sp, BorderLayout.CENTER);

        sc.content.setLayout(new BorderLayout());
        sc.content.add(split, BorderLayout.CENTER);
        add(sc.root, BorderLayout.CENTER);
    }

    private JRadioButton radio(String text) {
        JRadioButton r = new JRadioButton(text);
        r.setFont(UITheme.FONT_BODY);
        r.setOpaque(false);
        r.setAlignmentX(LEFT_ALIGNMENT);
        return r;
    }

    private void onGenerar(java.awt.event.ActionEvent evt) {
        String fechaSeleccionada = (String) fechasReportesComboBox.getSelectedItem();
        StringBuilder reporte = new StringBuilder();

        if (ventasRadioBoton.isSelected()) {
            reporte.append("Reporte de Ventas - ").append(fechaSeleccionada).append("\n\n");
            if (historial.getFacturas().isEmpty()) {
                reporte.append("(Sin facturas registradas)\n");
            }
            for (Factura f : historial.getFacturas()) {
                reporte.append(f.generarFacturaTexto()).append("\n\n");
            }
        } else if (productosRadioBoton.isSelected()) {
            reporte.append("Reporte de Productos - ").append(fechaSeleccionada).append("\n\n");
            for (Producto p : inventario.getListaProductos()) {
                reporte.append(p.getCodigoProducto()).append(" - ")
                        .append(p.getNombreProducto()).append(" (")
                        .append(p.getCategoriaProducto()).append(")\n")
                        .append("Precio: \u20A1").append(p.getPrecioProducto())
                        .append(" | Stock: ").append(p.getCantidadStock()).append("\n\n");
            }
        } else if (clientesRadioBoton.isSelected()) {
            reporte.append("Reporte de Clientes - ").append(fechaSeleccionada).append("\n\n");
            for (Cliente c : gestorClientes.getClientes()) {
                reporte.append(c.getNombre()).append(" ").append(c.getApellido())
                        .append(" | Cédula: ").append(c.getCedula())
                        .append(" | Correo: ").append(c.getCorreo()).append("\n");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un tipo de reporte.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        detallesDelReporteTextArea.setText(reporte.toString());
        detallesDelReporteTextArea.setCaretPosition(0);
    }
}
