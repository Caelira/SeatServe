package org.example.angelsburgerpos.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.angelsburgerpos.model.Ingredient;
import org.example.angelsburgerpos.util.InventoryManager;

import java.util.stream.Collectors;

public class LowStockController {

    @FXML private TableView<Ingredient> lowStockTable;
    @FXML private TableColumn<Ingredient, String> colName;
    @FXML private TableColumn<Ingredient, Integer> colStock;
    @FXML private TableColumn<Ingredient, String> colUnit;

    public void initialize() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));

        loadLowStockData();
    }

    private void loadLowStockData() {
        var lowItems = InventoryManager.getAllIngredients().stream()
                .filter(i -> i.getStock() <= 20)
                .collect(Collectors.toList());

        lowStockTable.setItems(FXCollections.observableArrayList(lowItems));
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) lowStockTable.getScene().getWindow();
        stage.close();
    }
}