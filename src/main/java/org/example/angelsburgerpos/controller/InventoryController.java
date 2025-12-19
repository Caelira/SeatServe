package org.example.angelsburgerpos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.example.angelsburgerpos.model.Ingredient;
import org.example.angelsburgerpos.model.User;
import org.example.angelsburgerpos.util.InventoryManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class InventoryController implements Initializable {
    @FXML
    private Button addIngBtn, reduceBtn, deleteBtn, restockBtn;
    @FXML
    private TableView<Ingredient> inventoryTable;
    @FXML
    private TableColumn<Ingredient, String> colId, colName, colUnit;
    @FXML
    private TableColumn<Ingredient, Integer> colStock;
    @FXML
    private TextField restockInput;

    private ObservableList<Ingredient> ingredientList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));

        loadIngredients();
        InventoryManager.setRefreshListener(this::loadIngredients);
    }

    public void setUser(User user) {
        if (user != null && !user.getRole().equalsIgnoreCase("Admin")) {
            restockInput.setDisable(true);
            restockBtn.setDisable(true);
            addIngBtn.setDisable(true);
            deleteBtn.setDisable(true);
            reduceBtn.setDisable(true);
        }
    }

    private void loadIngredients() {
        ingredientList.clear();
        ingredientList.addAll(InventoryManager.getAllIngredients());

        inventoryTable.setItems(ingredientList);
        inventoryTable.refresh();
    }

    @FXML
    public void setDeleteBtn() {
        Ingredient selected = inventoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select an ingredient");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Archive Ingredient");
        dialog.setHeaderText("Archiving: " + selected.getName());
        dialog.setContentText("Reason for archive:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(reason -> {
            if (reason.trim().isEmpty()) reason = "No Reason Given";
            InventoryManager.archiveIngredient(selected.getId(), reason);
            loadIngredients();
        });
    }

    @FXML
    private void addStock() {
        modifyStock(1);
    }

    @FXML
    public void setReduceBtn() {
        modifyStock(-1);
    }

    private void modifyStock(int multiplier) {
        Ingredient selected = inventoryTable.getSelectionModel().getSelectedItem();
        String qtyText = restockInput.getText();

        if (selected == null || qtyText.isEmpty()) return;

        try {
            int qty = Integer.parseInt(qtyText);
            if (qty <= 0) {
                showAlert("Error", "Enter positive number");
                return;
            }
            selected.setStock(selected.getStock() + (qty * multiplier));
            InventoryManager.reduceStock(new ArrayList<>());
            restockInput.clear();
            inventoryTable.refresh();
        } catch (NumberFormatException e) {
            showAlert("Error", "Invalid Number");
        }
    }

    @FXML
    public void setAddingBtn() {
        Dialog<Ingredient> dialog = new Dialog<>();
        dialog.setTitle("Add New Ingredient");
        dialog.setHeaderText("Enter Details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField unitField = new TextField();
        unitField.setPromptText("Unit");
        TextField stockField = new TextField();
        stockField.setPromptText("Stock");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Unit:"), 0, 1);
        grid.add(unitField, 1, 1);
        grid.add(new Label("Stock:"), 0, 2);
        grid.add(stockField, 1, 2);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String name = nameField.getText();
                    String unit = unitField.getText();
                    int stock = Integer.parseInt(stockField.getText());
                    String id = (name.length() > 3 ? name.substring(0, 3) : name).toUpperCase();
                    return new Ingredient(id, name, stock, unit);
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        });

        Optional<Ingredient> result = dialog.showAndWait();
        result.ifPresent(newIngredient -> {
            InventoryManager.addIngredient(newIngredient);
            loadIngredients();
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void closeWindow() {
        ((Stage) inventoryTable.getScene().getWindow()).close();
    }
}