package com.example.servicioweb;

import com.example.servicioweb.model.Usuario;
import com.example.servicioweb.service.UsuarioService;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.Locale;

public class UsuarioController {
    @FXML private TextField txtBuscar;
    @FXML private Button btnConsultar;
    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, Number> colId;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colUsername;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, String> colTelefono;
    @FXML private TableColumn<Usuario, String> colCiudad;
    @FXML private TableColumn<Usuario, String> colEmpresa;
    @FXML private Label lblEstado;
    @FXML private Label lblTotalUsuarios;
    @FXML private Label lblSinUsuarios;
    @FXML private Label lblAyudaTabla;

    private final UsuarioService servicio;
    private final ObservableList<Usuario> usuarios = FXCollections.observableArrayList();
    private final FilteredList<Usuario> usuariosFiltrados = new FilteredList<>(usuarios);

    public UsuarioController() {
        this(new UsuarioService());
    }

    UsuarioController(UsuarioService servicio) {
        this.servicio = servicio;
    }

    @FXML
    private void initialize() {
        colId.setCellValueFactory(dato -> new ReadOnlyIntegerWrapper(dato.getValue().getId()));
        colNombre.setCellValueFactory(dato -> new ReadOnlyStringWrapper(dato.getValue().getNombre()));
        colUsername.setCellValueFactory(dato -> new ReadOnlyStringWrapper(dato.getValue().getUsername()));
        colEmail.setCellValueFactory(dato -> new ReadOnlyStringWrapper(dato.getValue().getEmail()));
        colTelefono.setCellValueFactory(dato -> new ReadOnlyStringWrapper(dato.getValue().getTelefono()));
        colCiudad.setCellValueFactory(dato -> new ReadOnlyStringWrapper(dato.getValue().getCiudad()));
        colEmpresa.setCellValueFactory(dato -> new ReadOnlyStringWrapper(dato.getValue().getEmpresa()));

        SortedList<Usuario> usuariosOrdenados = new SortedList<>(usuariosFiltrados);
        usuariosOrdenados.comparatorProperty().bind(tablaUsuarios.comparatorProperty());
        tablaUsuarios.setItems(usuariosOrdenados);

        lblTotalUsuarios.textProperty().bind(Bindings.createStringBinding(
                () -> usuariosFiltrados.size() + " de " + usuarios.size() + " usuarios",
                usuarios, usuariosFiltrados));

        txtBuscar.textProperty().addListener((observable, anterior, nuevo) -> {
            String busqueda = nuevo.strip().toLowerCase(Locale.ROOT);
            usuariosFiltrados.setPredicate(usuario ->
                    usuario.getNombre().toLowerCase(Locale.ROOT).contains(busqueda)
                    || usuario.getUsername().toLowerCase(Locale.ROOT).contains(busqueda)
                    || usuario.getEmail().toLowerCase(Locale.ROOT).contains(busqueda));
            if (!usuarios.isEmpty()) {
                actualizarMensajeTabla();
            }
        });

        consultarUsuarios();
    }

    @FXML
    private void consultarUsuarios() {
        if (btnConsultar.isDisabled()) {
            return;
        }

        btnConsultar.setDisable(true);
        txtBuscar.setDisable(true);
        lblEstado.setText("Consultando usuarios...");
        if (usuarios.isEmpty()) {
            lblSinUsuarios.setText("Cargando usuarios...");
            lblAyudaTabla.setText("Espera un momento mientras se consulta el servicio.");
        }

        Task<List<Usuario>> consulta = new Task<>() {
            @Override
            protected List<Usuario> call() throws Exception {
                return servicio.obtenerUsuarios();
            }
        };

        consulta.setOnSucceeded(event -> {
            usuarios.setAll(consulta.getValue());
            actualizarMensajeTabla();
            lblEstado.setText("Consulta completada.");
            finalizarConsulta();
        });

        consulta.setOnFailed(event -> {
            lblEstado.setText("No se pudo consultar el servicio. Inténtalo de nuevo.");
            if (usuarios.isEmpty()) {
                lblSinUsuarios.setText("No se pudieron cargar los usuarios");
                lblAyudaTabla.setText("Pulsa Consultar usuarios para volver a intentar.");
            }
            finalizarConsulta();
        });

        Thread hilo = new Thread(consulta, "consulta-usuarios");
        hilo.setDaemon(true);
        hilo.start();
    }

    private void finalizarConsulta() {
        btnConsultar.setDisable(false);
        txtBuscar.setDisable(false);
    }

    private void actualizarMensajeTabla() {
        if (usuarios.isEmpty()) {
            lblSinUsuarios.setText("Sin usuarios para mostrar");
            lblAyudaTabla.setText("El servicio no devolvió usuarios.");
        } else {
            lblSinUsuarios.setText("Sin coincidencias");
            lblAyudaTabla.setText("Prueba con otro nombre, usuario o correo.");
        }
    }
}
