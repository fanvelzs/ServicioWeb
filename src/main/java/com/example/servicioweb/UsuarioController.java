package com.example.servicioweb;

import com.example.servicioweb.model.Usuario;
import com.example.servicioweb.service.UsuarioService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UsuarioController {
    @FXML private TextField txtBuscar;
    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId;
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
    private List<Usuario> usuarios = new ArrayList<>();

    public UsuarioController() {
        this(new UsuarioService());
    }

    UsuarioController(UsuarioService servicio) {
        this.servicio = servicio;
    }

    @FXML
    private void initialize() {
        // Cada columna usa el getter correspondiente de Usuario.
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudad"));
        colEmpresa.setCellValueFactory(new PropertyValueFactory<>("empresa"));

        txtBuscar.textProperty().addListener((observable, anterior, nuevo) -> {
            if (!txtBuscar.isDisabled()) {
                buscarUsuarios();
            }
        });

        consultarUsuarios();
    }

    @FXML
    private void consultarUsuarios() {
        try {
            usuarios = servicio.obtenerUsuarios();
            txtBuscar.setDisable(false);
            buscarUsuarios();
            lblEstado.setText("Consulta completada.");
        } catch (IOException e) {
            mostrarErrorConsulta();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            mostrarErrorConsulta();
        }
    }

    private void buscarUsuarios() {
        String texto = txtBuscar.getText().trim().toLowerCase(Locale.ROOT);
        ObservableList<Usuario> resultados = FXCollections.observableArrayList();

        // Agregamos a la tabla solamente los usuarios que coinciden.
        for (Usuario usuario : usuarios) {
            if (usuario.getNombre().toLowerCase(Locale.ROOT).contains(texto)
                    || usuario.getUsername().toLowerCase(Locale.ROOT).contains(texto)
                    || usuario.getEmail().toLowerCase(Locale.ROOT).contains(texto)) {
                resultados.add(usuario);
            }
        }

        tablaUsuarios.setItems(resultados);
        tablaUsuarios.sort();
        lblTotalUsuarios.setText(resultados.size() + " de " + usuarios.size() + " usuarios");
        if (usuarios.isEmpty()) {
            lblSinUsuarios.setText("Sin usuarios para mostrar");
            lblAyudaTabla.setText("El servicio no devolvió usuarios.");
        } else {
            lblSinUsuarios.setText("Sin coincidencias");
            lblAyudaTabla.setText("Prueba con otro nombre, usuario o correo.");
        }
    }

    private void mostrarErrorConsulta() {
        lblEstado.setText("No se pudo consultar el servicio. Inténtalo de nuevo.");
        if (usuarios.isEmpty()) {
            txtBuscar.setDisable(true);
            lblSinUsuarios.setText("No se pudieron cargar los usuarios");
            lblAyudaTabla.setText("Pulsa Consultar usuarios para volver a intentar.");
        }
    }
}
