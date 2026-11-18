# Consulta de usuarios

Aplicación JavaFX sencilla, sin CSS adicional. Consulta los usuarios de JSONPlaceholder al abrir la ventana.

## Ejecutar

Configura el JDK 25 como SDK del proyecto y como JDK de Maven. Desde esta carpeta, en PowerShell:

```powershell
.\mvnw.cmd clean javafx:run
```

También puedes ejecutar la clase `com.example.servicioweb.Launcher` desde IntelliJ IDEA después de importar el proyecto Maven.

## Uso

- La tabla muestra ID, nombre, usuario, correo, teléfono, ciudad y empresa.
- Escribe en Buscar para filtrar por nombre, usuario o correo, sin distinguir mayúsculas.
- Pulsa un encabezado para ordenar la tabla.
- Pulsa Consultar usuarios para actualizar los datos o reintentar si falla la conexión.
- El contador indica cuántos resultados se muestran del total consultado.

La consulta se realiza en segundo plano. Si falla una actualización, se conservan los datos ya cargados.

## Pruebas

```powershell
.\mvnw.cmd test
```

Las pruebas verifican la carga de la vista, la búsqueda, el ordenamiento, el estado vacío y la recuperación ante errores. Usan respuestas simuladas y no necesitan conexión a Internet; requieren un entorno gráfico para iniciar JavaFX.
