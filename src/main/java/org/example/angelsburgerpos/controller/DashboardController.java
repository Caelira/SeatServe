package org.example.angelsburgerpos.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.angelsburgerpos.model.CartItem;
import org.example.angelsburgerpos.model.MobileOrder;
import org.example.angelsburgerpos.model.Product;
import org.example.angelsburgerpos.model.User;
import org.example.angelsburgerpos.service.KioskServer;
import org.example.angelsburgerpos.service.MyListener;
import org.example.angelsburgerpos.service.ProductService;
import org.example.angelsburgerpos.service.ProductServiceImp;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import org.example.angelsburgerpos.util.OrderRequest;

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
    private Button reportsButton;
    @FXML
    private Button burgersButton;
    @FXML
    private Button sandwichesButton;
    @FXML
    private Button drinksButton;
    @FXML
    private Label roleLabel;
    @FXML
    private FlowPane menuContainer;
    @FXML
    private Button employeesButton;
    @FXML
    private Label totalLabel;
    @FXML
    private Button viewCartButton;
    @FXML
    private ImageView logo;

    private KioskServer kioskServer;
    private ProductService productService;
    private ObservableList<CartItem> cartList = FXCollections.observableArrayList();
    private MyListener myListener;
    private User user;
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
//        setLogo();
        loadProducts("Burger");
        initClock();
        startKioskServer();
    }
    private void startKioskServer(){
        try{
            kioskServer = new KioskServer(productService, this);
            kioskServer.startServer();
        }catch(Exception e){
            System.out.println("Failed to start server. Error: " + e.getMessage());
        }
    }

    private ObservableList<MobileOrder> pendingOrders = FXCollections.observableArrayList();


    public void handleMobileOrder(List<OrderRequest> mobileOrders){
        System.out.println("Received");
        for(OrderRequest req : mobileOrders){
            Product product = findyById(req.productId);

            if(product != null){
                for(int i = 0; i < req.quantity; i++){
                    addToCart(product);
                }
            }
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order received");
        alert.setHeaderText(null);
        alert.setContentText("Items added to cart");
        alert.show();
    }
    private Product findyById(String id){
        for(Product p : productService.getProducts()){
            if(p.getId().equals(id)){
                return p;
            }
        }
        return null;
    }
    public void stopServer(){
        if(kioskServer != null){
            kioskServer.stop();
        }
    }
    private void initClock() {
        javafx.animation.Timeline clock = new javafx.animation.Timeline(new javafx.animation.KeyFrame(javafx.util.Duration.ZERO, e -> {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss");
            dateLabel.setText(java.time.LocalDateTime.now().format(formatter));
        }), new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1)));

        clock.setCycleCount(javafx.animation.Animation.INDEFINITE);
        clock.play();
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

            if(user.getRole().equalsIgnoreCase("cashier")){

            }

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
    @FXML
    private void employeesClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/angelsburgerpos/employees-view.fxml"));
            javafx.scene.Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Employee Management");
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logoutClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/angelsburgerpos/login-view.fxml"));
            javafx.scene.Parent root = loader.load();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setLogo(User user) {
        try {
            String path = "/org/example/angelsburgerpos/images/logo.jpg";
            if(user.getFullname().equalsIgnoreCase("James Agmata".trim())){
                path= "/org/example/angelsburgerpos/images/james.png";
            }
            if(user.getFullname().equalsIgnoreCase("Rowena Acabado".trim())){
                path= "/org/example/angelsburgerpos/images/rowena.jpg";
            }
            if(user.getFullname().equalsIgnoreCase("Ayende".trim())){
                path= "/org/example/angelsburgerpos/images/gwyn.jpg";
            }
            if(user.getFullname().equalsIgnoreCase("James Agmata".trim())){
                path= "/org/example/angelsburgerpos/images/james.png";
            }
            if (getClass().getResource(path) != null) {
                String imagePath = getClass().getResource(path).toExternalForm();
                Image image = new Image(imagePath);
                logo.setImage(image);
                double radius = Math.min(logo.getFitWidth(), logo.getFitHeight()) / 2;

                Circle clip = new Circle(
                        logo.getFitWidth() / 2,
                        logo.getFitHeight() / 2,
                        radius
                );

                logo.setClip(clip);

            } else {
                System.out.println("Logo file not found at: " + path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setUserInfo(User user) {
        this.user = user;
        nameLabel.setText(user.getFullname());
        roleLabel.setText(user.getRole());
        setLogo(user);
        if(user.getRole().equalsIgnoreCase("Cashier")){
            employeesButton.setVisible(false);

        }
    }
}