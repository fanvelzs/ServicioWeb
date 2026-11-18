# Consulta de usuarios

Aplicación JavaFX sencilla, sin CSS adicional. Consulta los usuarios de JSONPlaceholder al abrir la ventana.

## Ejecutar

Desde esta carpeta, en PowerShell:

```powershell
.\ejecutar.ps1
```

El script utiliza el JDK 25 de `JAVA_HOME` o lo busca en la carpeta `.jdks` de tu usuario. Configura Java solo durante la ejecución y no cambia las variables permanentes del equipo.

También puedes ejecutar la clase `com.example.servicioweb.Launcher` desde IntelliJ IDEA después de importar el proyecto Maven con el SDK 25. Si ya tienes `JAVA_HOME` configurado con el JDK 25, puedes usar directamente `.\mvnw.cmd javafx:run`.

## Uso

- La tabla muestra ID, nombre, usuario, correo, teléfono, ciudad y empresa.
- Escribe en Buscar para filtrar por nombre, usuario o correo, sin distinguir mayúsculas.
- Pulsa un encabezado para ordenar la tabla.
- Pulsa Consultar usuarios para actualizar los datos o reintentar si falla la conexión.
- El contador indica cuántos resultados se muestran del total consultado.

La consulta se realiza en segundo plano. Si falla una actualización, se conservan los datos ya cargados.

## Pruebas

```powershell
.\ejecutar.ps1 -Pruebas
```

Las pruebas verifican la carga de la vista, la búsqueda, el ordenamiento, el estado vacío y la recuperación ante errores. Usan respuestas simuladas y no necesitan conexión a Internet; requieren un entorno gráfico para iniciar JavaFX.
