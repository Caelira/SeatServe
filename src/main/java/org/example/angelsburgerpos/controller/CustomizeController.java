package org.example.angelsburgerpos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.angelsburgerpos.model.CartItem;
import org.example.angelsburgerpos.model.Product;
import org.example.angelsburgerpos.util.InventoryManager;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class CustomizeController {

    @FXML private Label productNameLabel;
    @FXML private VBox ingredientsContainer;

    private Product product;
    private Consumer<CartItem> onConfirm;
    private Set<String> removedIds = new HashSet<>();

    public void setData(Product product, Consumer<CartItem> onConfirm) {
        this.product = product;
        this.onConfirm = onConfirm;
        productNameLabel.setText(product.getName());

        loadIngredients();
    }

    private void loadIngredients() {
        Map<String, Integer> recipe = InventoryManager.getRecipe(product.getId());

        for (String ingId : recipe.keySet()) {
            String ingName = InventoryManager.getIngredientName(ingId);

            CheckBox cb = new CheckBox(ingName);
            cb.setSelected(true); // Default is "Included"
            cb.setUserData(ingId);

            cb.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal) removedIds.add(ingId);
                else removedIds.remove(ingId);
            });

            ingredientsContainer.getChildren().add(cb);
        }
    }

    @FXML
    private void confirmCustomization() {
        CartItem item = new CartItem(product, 1);

        if (!removedIds.isEmpty()) {
            StringBuilder note = new StringBuilder("No: ");
            for (String id : removedIds) {
                note.append(InventoryManager.getIngredientName(id)).append(", ");
            }
            String noteStr = note.substring(0, note.length() - 2);
            item.setRemovedIngredients(new HashSet<>(removedIds), noteStr);
        }

        onConfirm.accept(item);
        closeWindow();
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) productNameLabel.getScene().getWindow()).close();
    }
}