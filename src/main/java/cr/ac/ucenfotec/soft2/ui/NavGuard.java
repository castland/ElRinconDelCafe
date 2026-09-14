package cr.ac.ucenfotec.soft2.ui;

/**
 * Paneles que necesitan confirmar antes de que el usuario navegue fuera de
 * ellos (por ejemplo, una venta en progreso en el carrito).
 */
public interface NavGuard {
    /**
     * @return true si es seguro abandonar el panel; false para cancelar la
     *         navegación (normalmente tras preguntar al usuario).
     */
    boolean puedeSalir();
}
