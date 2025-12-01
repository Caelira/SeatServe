package org.example.angelsburgerpos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.angelsburgerpos.model.Ingredient;
import org.example.angelsburgerpos.util.InventoryManager;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class InventoryController implements Initializable {

    @FXML private TableView<Ingredient> inventoryTable;
    @FXML private TableColumn<Ingredient, String> colId;
    @FXML private TableColumn<Ingredient, String> colName;
    @FXML private TableColumn<Ingredient, Integer> colStock;
    @FXML private TableColumn<Ingredient, String> colUnit;
    @FXML private TextField restockInput;

    private ObservableList<Ingredient> ingredientList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Link Table Columns to Ingredient.java fields
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));

        loadIngredients();
    }

    private void loadIngredients() {
        // Reads from the INGREDIENTS file, not the product file
        InputStream is = getClass().getResourceAsStream("/org/example/angelsburgerpos/util/ingredients.txt");
        if (is != null) {
            Scanner scanner = new Scanner(is);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    ingredientList.add(new Ingredient(
                            parts[0],
                            parts[1],
                            Integer.parseInt(parts[2]),
                            parts[3]
                    ));
                }
            }
            scanner.close();
        }
        inventoryTable.setItems(ingredientList);
    }

    @FXML
    private void addStock() {
        Ingredient selected = inventoryTable.getSelectionModel().getSelectedItem();
        String qtyText = restockInput.getText();

        if (selected != null && !qtyText.isEmpty()) {
            try {
                int qtyToAdd = Integer.parseInt(qtyText);
                selected.setStock(selected.getStock() + qtyToAdd);
                inventoryTable.refresh();
                restockInput.clear();

              } catch (NumberFormatException e) {
                System.out.println("Invalid number");
            }
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) inventoryTable.getScene().getWindow();
        stage.close();
    }
}