package org.example.angelsburgerpos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import org.example.angelsburgerpos.DAO.ProductDAO;
import org.example.angelsburgerpos.model.Product;
import org.example.angelsburgerpos.service.ProductService;
import org.example.angelsburgerpos.service.ProductServiceImp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML
    private Label welcomeText;
    @FXML
    private Button burgersButton;

    @FXML
    private Label dateLabel;

    @FXML
    private Button drinksButton;

    @FXML
    private Button inventoryButton;

    @FXML
    private Label logoLabel;

    @FXML
    private Button logoutButton;

    @FXML
    private FlowPane menuContainer;

    @FXML
    private Label nameLabel;

    @FXML
    private Button sandwichesButton;
    private ProductService productService;

    public DashboardController() {
        productService = new ProductServiceImp();
    }


    @FXML
    private void inventoryClicked() {
    }

    private void loadProducts(String category) {
        System.out.println("Loading category: " + category); // Debug 1

        List<Product> products = productService.getProducts();
        System.out.println("Found " + products.size() + " products in database."); // Debug 2

        menuContainer.getChildren().clear();

        for (Product product : products) {
            if (product.getCategory().equalsIgnoreCase(category)) {
                System.out.println("Adding product: " + product.getName()); // Debug 3

                FXMLLoader loader = new FXMLLoader();
                // CORRECTED PATH
                loader.setLocation(getClass().getResource("/org/example/angelsburgerpos/product-card-view.fxml"));

                try {
                    AnchorPane anchorPane = loader.load();
                    ProductCardController cardController = loader.getController();
                    cardController.setData(product);
                    menuContainer.getChildren().add(anchorPane);
                } catch (IOException e) {
                    e.printStackTrace(); // This will print the specific error if FXML fails
                }
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        loadProducts("Burger");
    }
}