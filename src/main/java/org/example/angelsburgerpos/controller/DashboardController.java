package org.example.angelsburgerpos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.angelsburgerpos.model.CartItem;
import org.example.angelsburgerpos.model.Product;
import org.example.angelsburgerpos.service.MyListener;
import org.example.angelsburgerpos.service.ProductService;
import org.example.angelsburgerpos.service.ProductServiceImp;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Label nameLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label logoLabel;

    @FXML
    private Button logoutButton;
    @FXML
    private Button inventoryButton;

    @FXML
    private Button burgersButton;
    @FXML
    private Button sandwichesButton;
    @FXML
    private Button drinksButton;

    @FXML
    private FlowPane menuContainer;

    @FXML
    private Label totalLabel;
    @FXML
    private Button viewCartButton;

    private ProductService productService;
    private ObservableList<CartItem> cartList = FXCollections.observableArrayList();
    private MyListener myListener;

    public DashboardController() {
        productService = new ProductServiceImp();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        myListener = new MyListener() {
            @Override
            public void onClickListener(Product product) {
                addToCart(product);
            }
        };

        loadProducts("Burger");
    }

    private void addToCart(Product product) {
        boolean found = false;
        for (CartItem item : cartList) {
            if (item.getName().equals(product.getName())) {
                item.increaseQuantity();
                found = true;
                break;
            }
        }

        if (!found) {
            cartList.add(new CartItem(product, 1));
        }

        updateFooter();
    }

    private void updateFooter() {
        double total = 0;
        int count = 0;
        for (CartItem item : cartList) {
            total += item.getTotal();
            count += item.getQuantity();
        }
        totalLabel.setText("P " + String.format("%.2f", total));
        viewCartButton.setText("VIEW CART (" + count + ")");
    }

    @FXML
    private void reportsClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/angelsburgerpos/sales-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Sales Report");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void viewCartClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/angelsburgerpos/cart-popup-view.fxml"));
            Parent root = loader.load();

            CartController cartController = loader.getController();
            cartController.setCartList(this.cartList);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Current Order");
            stage.setScene(new Scene(root));
            stage.setOnHidden(e -> updateFooter());

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void filterBurgers() {
        loadProducts("Burger");
    }

    @FXML
    private void filterSandwiches() {
        loadProducts("Sandwich");
    }

    @FXML
    private void filterDrinks() {
        loadProducts("Drink");
    }

    @FXML
    private void inventoryClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/angelsburgerpos/inventory-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Inventory Management");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadProducts(String category) {
        menuContainer.getChildren().clear();
        List<Product> products = productService.getProducts();

        for (Product product : products) {
            if (product.getCategory().equalsIgnoreCase(category)) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/angelsburgerpos/product-card-view.fxml"));
                    AnchorPane cardBox = loader.load();

                    ProductCardController cardController = loader.getController();

                    cardController.setData(product, myListener);

                    menuContainer.getChildren().add(cardBox);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}