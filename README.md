# El Rincón del Café

Sistema de gestión integral para una cafetería, construido como aplicación de
escritorio en **Java Swing**. Interfaz moderna con **FlatLaf** y build con
**Maven**.

## Características

- Inicio de sesión por rol (Gerente / Barista)
- Gestión de **inventario** (productos, stock, categorías)
- **Ventas** con carrito, validación de stock y cálculo de IVA (13 %)
- **Facturación** con historial y búsqueda
- Administración de **clientes** y **usuarios**
- **Reportes** de ventas, productos y clientes

## Requisitos

- **JDK 24** (Eclipse Temurin u otro OpenJDK 24)
- **Maven 3.9+**

## Compilar y ejecutar

```bash
# Compilar y empaquetar (genera target/ElRinconDelCafe.jar con dependencias)
mvn clean package

# Ejecutar desde el código fuente
mvn exec:java

# O ejecutar el JAR empaquetado
java -jar target/ElRinconDelCafe.jar
```

En **VS Code** (con la extensión de Java) o **IntelliJ/Eclipse**: abrir la
carpeta y usar el botón Run/Debug sobre `Main.java`.

## Estructura

```
src/main/java/cr/ac/ucenfotec/soft2/
├── app/          Punto de entrada (Main) y utilidades
├── ui/           Pantallas Swing + sistema de diseño (UITheme, NavSidebar, ...)
├── clientes/     Modelo y gestor de clientes
├── usuarios/     Modelo y gestor de usuarios
├── inventario/   Productos e inventario
├── ventas/       Venta, carrito, factura, gestor de ventas
├── facturacion/  Historial de ventas
└── reportes/     Reportes
```

Clase principal: `cr.ac.ucenfotec.soft2.app.Main`

## Usuarios de prueba

| Nombre  | Cédula     | Rol     |
|---------|------------|---------|
| Carlos  | 111111111  | Gerente |
| Kenner  | 333333333  | Barista |
