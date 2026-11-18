package com.example.servicioweb;

import com.example.servicioweb.model.Usuario;
import com.example.servicioweb.service.UsuarioService;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioControllerTest {
    @BeforeAll
    static void iniciarJavaFx() throws Exception {
        CountDownLatch inicio = new CountDownLatch(1);
        Platform.startup(() -> {
            Platform.setImplicitExit(false);
            inicio.countDown();
        });
        assertTrue(inicio.await(10, TimeUnit.SECONDS));
    }

    @AfterAll
    static void cerrarJavaFx() {
        Platform.exit();
    }

    @Test
    void cargaFiltraYOrdenaLosUsuarios() throws Exception {
        ServicioSimulado servicio = new ServicioSimulado();
        servicio.respuesta = List.of(
                new Usuario(10, "Luis Pérez", "lucho", "luis@correo.com", "2222", "León", "Empresa B"),
                new Usuario(2, "Ana López", "anita", "ana@ejemplo.com", "1111", "Managua", "Empresa A"));
        FXMLLoader vista = cargarVista(servicio);

        enJavaFx(() -> {
            TableView<Usuario> tabla = tabla(vista);
            assertEquals(2, tabla.getItems().size());
            assertEquals("2 de 2 usuarios", etiqueta(vista, "lblTotalUsuarios").getText());
            assertEquals(List.of(10, "Luis Pérez", "lucho", "luis@correo.com", "2222", "León", "Empresa B"),
                    tabla.getColumns().stream().map(columna -> columna.getCellData(0)).toList());

            tabla.getSortOrder().add(tabla.getColumns().get(0));
            tabla.sort();
            assertEquals(2, tabla.getItems().getFirst().getId());

            busqueda(vista).setText(" ANA ");
            assertEquals("Ana López", tabla.getItems().getFirst().getNombre());
            assertEquals("1 de 2 usuarios", etiqueta(vista, "lblTotalUsuarios").getText());
            busqueda(vista).setText("LUCHO");
            assertEquals("Luis Pérez", tabla.getItems().getFirst().getNombre());
            busqueda(vista).setText("@EJEMPLO.COM");
            assertEquals("Ana López", tabla.getItems().getFirst().getNombre());
            busqueda(vista).setText("nadie");
            assertTrue(tabla.getItems().isEmpty());
            assertEquals("Sin coincidencias", etiqueta(vista, "lblSinUsuarios").getText());
            assertEquals("0 de 2 usuarios", etiqueta(vista, "lblTotalUsuarios").getText());
            busqueda(vista).clear();
            assertEquals(2, tabla.getItems().size());
            assertFalse(busqueda(vista).isDisabled());
            return null;
        });
    }

    @Test
    void permiteReintentarDespuesDeUnErrorYConservaDatosSiFallaLaRecarga() throws Exception {
        ServicioSimulado servicio = new ServicioSimulado();
        servicio.error = new IOException("Sin conexión");
        FXMLLoader vista = cargarVista(servicio);

        enJavaFx(() -> {
            assertTrue(tabla(vista).getItems().isEmpty());
            assertEquals("No se pudieron cargar los usuarios", etiqueta(vista, "lblSinUsuarios").getText());
            assertFalse(boton(vista).isDisabled());
            busqueda(vista).setText("Ana");
            assertEquals("No se pudieron cargar los usuarios", etiqueta(vista, "lblSinUsuarios").getText());
            return null;
        });

        servicio.error = null;
        servicio.respuesta = List.of(new Usuario(1, "Ana", "ana", "ana@correo.com", "1234", "Managua", "Empresa"));
        enJavaFx(() -> { boton(vista).fire(); return null; });
        enJavaFx(() -> {
            assertEquals(1, tabla(vista).getItems().size());
            assertEquals("Consulta completada.", etiqueta(vista, "lblEstado").getText());
            return null;
        });

        servicio.error = new IOException("Conexión interrumpida");
        enJavaFx(() -> { boton(vista).fire(); return null; });
        enJavaFx(() -> {
            assertEquals(1, tabla(vista).getItems().size());
            assertEquals("1 de 1 usuarios", etiqueta(vista, "lblTotalUsuarios").getText());
            assertTrue(etiqueta(vista, "lblEstado").getText().startsWith("No se pudo consultar"));
            assertFalse(boton(vista).isDisabled());
            return null;
        });
    }

    @Test
    void muestraElEstadoVacioCuandoElServicioNoDevuelveUsuarios() throws Exception {
        FXMLLoader vista = cargarVista(new ServicioSimulado());
        enJavaFx(() -> {
            assertTrue(tabla(vista).getItems().isEmpty());
            assertEquals("0 de 0 usuarios", etiqueta(vista, "lblTotalUsuarios").getText());
            assertEquals("El servicio no devolvió usuarios.", etiqueta(vista, "lblAyudaTabla").getText());
            assertEquals("Consulta completada.", etiqueta(vista, "lblEstado").getText());
            return null;
        });
    }

    private static FXMLLoader cargarVista(UsuarioService servicio) throws Exception {
        return enJavaFx(() -> {
            FXMLLoader vista = new FXMLLoader(HelloApplication.class.getResource("usuario-view.fxml"));
            vista.setControllerFactory(tipo -> new UsuarioController(servicio));
            vista.load();
            return vista;
        });
    }

    private static <T> T enJavaFx(Callable<T> accion) throws Exception {
        FutureTask<T> tarea = new FutureTask<>(accion);
        Platform.runLater(tarea);
        return tarea.get(10, TimeUnit.SECONDS);
    }

    private static Button boton(FXMLLoader vista) {
        return (Button) vista.getNamespace().get("btnConsultar");
    }

    private static TextField busqueda(FXMLLoader vista) {
        return (TextField) vista.getNamespace().get("txtBuscar");
    }

    private static Label etiqueta(FXMLLoader vista, String nombre) {
        return (Label) vista.getNamespace().get(nombre);
    }

    @SuppressWarnings("unchecked")
    private static TableView<Usuario> tabla(FXMLLoader vista) {
        return (TableView<Usuario>) vista.getNamespace().get("tablaUsuarios");
    }

    private static class ServicioSimulado extends UsuarioService {
        List<Usuario> respuesta = List.of();
        IOException error;

        @Override
        public List<Usuario> obtenerUsuarios() throws IOException, InterruptedException {
            if (error != null) throw error;
            return respuesta;
        }
    }
}
