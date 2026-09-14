package cr.ac.ucenfotec.soft2.ui;

/**
 * Contrato de navegación dentro de la ventana principal.
 *
 * Los paneles de módulo (Inventario, Ventas, etc.) reciben una referencia a
 * este host para poder pedir navegación o cerrar sesión, sin conocer los
 * detalles de la ventana que los contiene.
 */
public interface NavigationHost {

    /** Muestra el módulo indicado (clave: "Dashboard", "Inventario", ...). */
    void navegar(String destino);

    /** Cierra la sesión y vuelve a la pantalla de inicio de sesión. */
    void cerrarSesion();

    /** Ventana de nivel superior (para posicionar diálogos modales). */
    java.awt.Window getVentana();
}
