package org.example.angelsburgerpos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.angelsburgerpos.model.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class EmployeesController implements Initializable {

    private static final String USERS_FILE = "src/main/resources/org/example/angelsburgerpos/util/users.txt";
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, String> colUsername;
    @FXML
    private TableColumn<User, String> colFullname;
    @FXML
    private TableColumn<User, String> colRole;
    @FXML
    private TextField usernameInput;
    @FXML
    private TextField passwordInput;
    @FXML
    private TextField fullnameInput;
    @FXML
    private ComboBox<String> roleInput;
    private ObservableList<User> userList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colFullname.setCellValueFactory(new PropertyValueFactory<>("fullname"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        roleInput.setItems(FXCollections.observableArrayList("Manager", "Cashier"));

        loadUsers();
    }

    private void loadUsers() {
        userList.clear();
        File file = new File(USERS_FILE);
        if (file.exists()) {
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        userList.add(new User(parts[0], parts[1], parts[2], parts[3]));
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        usersTable.setItems(userList);
    }

    private void saveUsers() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (User u : userList) {
                pw.printf("%s,%s,%s,%s%n", u.getUsername(), u.getPassword(), u.getRole(), u.getFullname());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addUser() {
        String user = usernameInput.getText();
        String pass = passwordInput.getText();
        String name = fullnameInput.getText();
        String role = roleInput.getValue();

        if (user.isEmpty() || pass.isEmpty() || name.isEmpty() || role == null) {
            return;
        }

        userList.add(new User(user, pass, role, name));
        saveUsers();

        usernameInput.clear();
        passwordInput.clear();
        fullnameInput.clear();
        roleInput.getSelectionModel().clearSelection();
    }

    @FXML
    private void removeUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            userList.remove(selected);
            saveUsers();
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) usersTable.getScene().getWindow();
        stage.close();
    }
}