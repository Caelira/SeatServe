package org.example.angelsburgerpos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.angelsburgerpos.model.User;
import org.example.angelsburgerpos.util.AppConfig;
import org.example.angelsburgerpos.util.FileUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private List<User> users = new ArrayList<>();

    @FXML
    public void initialize() {
        loadUsers();
    }

    private void loadUsers() {
        List<String> lines = FileUtil.readLines(AppConfig.USERS_FILE);

        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length == 4) {
                users.add(new User(parts[0], parts[1], parts[2], parts[3]));
            }
        }
    }

    @FXML
    private void loginClicked() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        for (User u : users) {
            if (u.getUsername().equals(user) && u.getPassword().equals(pass)) {
                openDashboard(u);
                return;
            }
        }
        errorLabel.setText("Invalid username or password.");
    }

    private void openDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/angelsburgerpos/dashboard-view.fxml"));
            Parent root = loader.load();

            DashboardController dashboard = loader.getController();
            dashboard.setUserInfo(user);

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("Angels Burger POS - Dashboard");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}