package cr.ac.ucenfotec.soft2.ui;

import java.awt.Color;
import javax.swing.Icon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

/**
 * Fábrica central de iconos vectoriales (Ikonli + FontAwesome).
 *
 * Reemplaza los emojis (que no se renderizan en Swing) por iconos escalables
 * que se ven correctamente en cualquier sistema.
 */
public final class Icons {

    private Icons() {
    }

    public static Icon of(FontAwesomeSolid ikon, int size, Color color) {
        return FontIcon.of(ikon, size, color);
    }

    public static Icon of(FontAwesomeSolid ikon, int size) {
        return FontIcon.of(ikon, size, UITheme.TEXT);
    }

    // ---- Iconos de navegación (barra lateral) ----
    public static Icon home(int s, Color c)      { return of(FontAwesomeSolid.HOME, s, c); }
    public static Icon inventario(int s, Color c){ return of(FontAwesomeSolid.BOXES, s, c); }
    public static Icon ventas(int s, Color c)    { return of(FontAwesomeSolid.SHOPPING_CART, s, c); }
    public static Icon facturas(int s, Color c)  { return of(FontAwesomeSolid.FILE_INVOICE_DOLLAR, s, c); }
    public static Icon clientes(int s, Color c)  { return of(FontAwesomeSolid.USERS, s, c); }
    public static Icon usuarios(int s, Color c)  { return of(FontAwesomeSolid.USER_COG, s, c); }
    public static Icon reportes(int s, Color c)  { return of(FontAwesomeSolid.CHART_BAR, s, c); }
    public static Icon logout(int s, Color c)    { return of(FontAwesomeSolid.SIGN_OUT_ALT, s, c); }
    public static Icon menu(int s, Color c)      { return of(FontAwesomeSolid.BARS, s, c); }
    public static Icon coffee(int s, Color c)    { return of(FontAwesomeSolid.COFFEE, s, c); }

    // ---- Iconos de acciones (botones) ----
    public static Icon add(int s, Color c)       { return of(FontAwesomeSolid.PLUS, s, c); }
    public static Icon edit(int s, Color c)      { return of(FontAwesomeSolid.PEN, s, c); }
    public static Icon delete(int s, Color c)    { return of(FontAwesomeSolid.TRASH_ALT, s, c); }
    public static Icon refresh(int s, Color c)   { return of(FontAwesomeSolid.SYNC_ALT, s, c); }
    public static Icon search(int s, Color c)    { return of(FontAwesomeSolid.SEARCH, s, c); }
    public static Icon check(int s, Color c)     { return of(FontAwesomeSolid.CHECK, s, c); }
    public static Icon cart(int s, Color c)      { return of(FontAwesomeSolid.CART_PLUS, s, c); }
    public static Icon boxes(int s, Color c)     { return of(FontAwesomeSolid.BOXES, s, c); }
    public static Icon chart(int s, Color c)     { return of(FontAwesomeSolid.CHART_LINE, s, c); }
    public static Icon warning(int s, Color c)   { return of(FontAwesomeSolid.EXCLAMATION_TRIANGLE, s, c); }
}
