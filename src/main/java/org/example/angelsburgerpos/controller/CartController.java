package org.example.angelsburgerpos.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.angelsburgerpos.model.CartItem;
import org.example.angelsburgerpos.model.User;

import java.io.IOException;

public class CartController {

    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> colName;
    @FXML private TableColumn<CartItem, Integer> colQty;
    @FXML private TableColumn<CartItem, Double> colPrice;
    @FXML private TableColumn<CartItem, Double> colTotal;

    public void setCartList(ObservableList<CartItem> cartList) {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        cartTable.setItems(cartList);
    }
    private User currentUser;

    public void setUser(User user) {
        this.currentUser = user;
    }
    @FXML
    private void onPayClicked() {
        ObservableList<CartItem> items = cartTable.getItems();
        if (items.isEmpty()) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/angelsburgerpos/checkout-view.fxml"));
            Parent root = loader.load();

            CheckoutController controller = loader.getController();

            double total = 0;
            for (CartItem item : items) {
                total += item.getTotal();
            }

            // PASS USER NAME TO CHECKOUT
            String cashierName = (currentUser != null) ? currentUser.getFullname() : "Unknown";

            controller.setOrderData(total, items, cashierName, () -> {
                items.clear();
                closeWindow();
            });

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Payment");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void addQty() {
        CartItem selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.increaseQuantity();
            cartTable.refresh();
        }
    }

    @FXML private void subtractQty() {
        CartItem selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.decreaseQuantity();
            if (selected.getQuantity() <= 0) {
                cartTable.getItems().remove(selected);
            }
            cartTable.refresh();
        }
    }

    @FXML private void removeSelected() {
        CartItem selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cartTable.getItems().remove(selected);
        }
    }

    @FXML private void closeWindow() {
        Stage stage = (Stage) cartTable.getScene().getWindow();
        stage.close();
    }
}