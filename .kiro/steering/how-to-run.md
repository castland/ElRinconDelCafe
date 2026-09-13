---
inclusion: always
---

# Cómo compilar y ejecutar El Rincón del Café

Proyecto **Java Swing** (Maven). Interfaz moderna con **FlatLaf**, declarado
como dependencia en `pom.xml`. Compatible con VS Code / Kiro, IntelliJ y Eclipse.

## Requisitos

- **JDK 24** (Eclipse Temurin u otro OpenJDK 24)
- **Maven 3.9+**

Si están instalados de forma portable (esta máquina):
- JDK: `F:\tools\jdk-24.0.2+12`
- Maven: `F:\tools\apache-maven-3.9.9`

## Estructura (layout estándar de Maven)

- `pom.xml` — configuración (Java 24, dependencias, clase main)
- `src/main/java/...` — código fuente
- Clase principal: `cr.ac.ucenfotec.soft2.app.Main`
- `target/` — salida de compilación (ignorada por git)

## Compilar, empaquetar y ejecutar

```bash
mvn clean package        # genera target/ElRinconDelCafe.jar (con FlatLaf incluido)
mvn exec:java            # ejecuta desde el código fuente
java -jar target/ElRinconDelCafe.jar   # ejecuta el JAR empaquetado
```

En esta máquina (JDK/Maven en F:), hay un wrapper local `mvnw.ps1` que fija
`JAVA_HOME` y llama al Maven de F: — p.ej. `.\mvnw.ps1 clean package`.
(No se versiona porque contiene rutas fijas a `F:\tools`.)

En **VS Code / Kiro**: abrir la carpeta; la extensión de Java detecta el
`pom.xml`. Usar Run/Debug sobre `Main.java`.

## Editar la interfaz (UI)

Las pantallas Swing están en `src/main/java/cr/ac/ucenfotec/soft2/ui/`.
El sistema de diseño (colores, tipografía, componentes) está en `UITheme.java`,
la barra lateral en `NavSidebar.java` y la estructura común de módulos en
`ModuleScaffold.java`. Es Swing escrito a mano (sin el diseñador visual de
NetBeans). Tras editar, recompilar con `mvn clean package` o usar Run/Debug.
