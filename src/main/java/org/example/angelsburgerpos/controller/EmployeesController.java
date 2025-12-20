package org.example.angelsburgerpos.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.angelsburgerpos.model.User;
import org.example.angelsburgerpos.util.UserManager;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class EmployeesController implements Initializable {

    @FXML private TableView<User> employeeTable;@FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colFullname;
    @FXML private TableColumn<User, String> colRole;

    @FXML private TextField usernameInput;
    @FXML private TextField passwordInput;
    @FXML private TextField fullnameInput;
    @FXML private ComboBox<String> roleInput;
    @FXML private TableView<User> usersTable; // FXML uses "usersTable", so we link it here

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setup Table
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colFullname.setCellValueFactory(new PropertyValueFactory<>("fullname"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        roleInput.setItems(FXCollections.observableArrayList("Admin", "Cashier/Cook"));

        loadData();
    }

    private void loadData() {

        usersTable.setItems(FXCollections.observableArrayList(UserManager.getAllUsers()));
    }

    @FXML
    private void addUser() {
        String user = usernameInput.getText().trim();
        String pass = passwordInput.getText().trim();
        String name = fullnameInput.getText().trim();
        String role = roleInput.getValue();

        if (user.isEmpty() || pass.isEmpty() || name.isEmpty() || role == null) {
            showAlert("Missing Info", "Please fill in all fields.");
            return;
        }

        for (User u : UserManager.getAllUsers()) {
            if (u.getUsername().equalsIgnoreCase(user)) {
                showAlert("Error", "Username already taken.");
                return;
            }
        }

        User newUser = new User(user, pass, role, name);
        UserManager.addUser(newUser);

        usernameInput.clear();
        passwordInput.clear();
        fullnameInput.clear();
        roleInput.getSelectionModel().clearSelection();
        loadData();
    }

    @FXML
    private void removeUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a user to remove.");
            return;
        }

        if (selected.getRole().equalsIgnoreCase("Admin")) {
            showAlert("Error", "Cannot delete an Admin account!");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Archive User");
        dialog.setHeaderText("Archiving User: " + selected.getFullname());
        dialog.setContentText("Reason for removal:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(reason -> {
            if (reason.trim().isEmpty()) reason = "No reason provided";
            UserManager.archiveUser(selected, reason);
            loadData();
        });
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) usersTable.getScene().getWindow();
         System.out.println("Close button clicked - Navigate via Dashboard Menu instead.");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}