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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
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
import org.example.angelsburgerpos.util.InventoryManager;
import org.example.angelsburgerpos.util.SalesLogger;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    // --- NEW LAYOUT VARIABLES ---
    @FXML
    private StackPane mainContentArea; // The parent holder in Center
    @FXML
    private AnchorPane menuSection;    // The actual "Burger Menu" UI (we hide this when showing reports)

    // --- SIDEBAR ---
    @FXML
    private Label nameLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private ImageView logo;
    @FXML
    private Label roleLabel;
    @FXML
    private Button mobileOrdersButton;
    @FXML
    private Button employeesButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button menuButton;
    @FXML
    private FlowPane menuContainer;
    @FXML
    private Label totalLabel;
    @FXML
    private Button viewCartButton;

    private KioskServer kioskServer;
    private ProductService productService;
    private ObservableList<CartItem> cartList = FXCollections.observableArrayList();
    private ObservableList<MobileOrder> pendingOrders = FXCollections.observableArrayList();
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

        loadProducts("Burger");
        initClock();
        startKioskServer();
        updateMobileOrderNotification();

        if (menuSection != null) menuSection.setVisible(true);
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            Object controller = loader.getController();

            if (controller instanceof MobileOrdersController) {
                ((MobileOrdersController) controller).setDashboard(this);
                ((MobileOrdersController) controller).setOrderList(pendingOrders);
            }

            if (menuSection != null) menuSection.setVisible(false);

            mainContentArea.getChildren().clear();
            mainContentArea.getChildren().add(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void menuButtonClicked() {
        mainContentArea.getChildren().clear();
        if (menuSection != null) {
            mainContentArea.getChildren().add(menuSection);
            menuSection.setVisible(true);
        }
    }

    @FXML
    private void inventoryClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/angelsburgerpos/inventory-view.fxml"));
            Parent view = loader.load();
            org.example.angelsburgerpos.controller.InventoryController controller = loader.getController();
            controller.setUser(this.user);

            mainContentArea.getChildren().clear();
            mainContentArea.getChildren().add(view);
            if (menuSection != null) menuSection.setVisible(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void reportsClicked() {
        loadView("/org/example/angelsburgerpos/sales-view.fxml");
    }

    @FXML
    private void employeesClicked() {
        loadView("/org/example/angelsburgerpos/employees-view.fxml");
    }

    @FXML
    private void viewMobileOrdersClicked() {
        loadView("/org/example/angelsburgerpos/mobile-orders-view.fxml");
    }

    private void startKioskServer() {
        try {
            kioskServer = new KioskServer(productService, this);
            kioskServer.startServer();
        } catch (Exception e) {
            System.out.println("Failed to start server. Error: " + e.getMessage());
        }
    }

    public List<MobileOrder> getPendingOrders() {
        return pendingOrders;
    }

    public void addPendingOrder(MobileOrder order) {
        pendingOrders.add(order);
        updateMobileOrderNotification();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order received");
        alert.setHeaderText(null);
        alert.setContentText("New order from " + order.getCustomerName());
        alert.show();
    }

    private void updateMobileOrderNotification() {
        if (mobileOrdersButton != null) {
            mobileOrdersButton.setText("Mobile Orders (" + pendingOrders.size() + ")");
            if (!pendingOrders.isEmpty()) {
                mobileOrdersButton.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white;");
            } else {
                mobileOrdersButton.setStyle("");
            }
        }
    }

    public void finalizeMobileOrder(MobileOrder order) {
        List<CartItem> itemsToDeduct = new ArrayList<>();
        List<Product> allProducts = productService.getProducts();
        double amount = 0.0;

        for (var mobileItem : order.getItems()) {
            for (Product p : allProducts) {
                if (p.getId().equals(mobileItem.productId)) {
                    CartItem item = new CartItem(p, mobileItem.quantity);

                    double lineTotal = p.getPrice() * mobileItem.quantity;
                    amount += lineTotal;

                    itemsToDeduct.add(item);
                    break;
                }
            }
        }

        if (!itemsToDeduct.isEmpty()) {
            InventoryManager.reduceStock(itemsToDeduct);
            System.out.println("Inventory deducted.");
        }

        SalesLogger.logSale(amount);

        org.example.angelsburgerpos.model.Receipt receipt = new org.example.angelsburgerpos.model.Receipt(itemsToDeduct, amount, amount, 0.0);
        receipt.saveToFile();
        System.out.println("Receipt saved.");
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
    private void logoutClicked() {
        try {
            stopServer();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/angelsburgerpos/login-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setLogo(User user) {
        try {
            String path = "/org/example/angelsburgerpos/images/logo.jpg";
            if (user.getFullname().equalsIgnoreCase("James Agmata".trim())) {
                path = "/org/example/angelsburgerpos/images/james.png";
            }
            if (user.getFullname().equalsIgnoreCase("Rowena Acabado".trim())) {
                path = "/org/example/angelsburgerpos/images/rowena.jpg";
            }
            if (user.getFullname().equalsIgnoreCase("Ayende".trim())) {
                path = "/org/example/angelsburgerpos/images/gwyn.jpg";
            }
            if (user.getFullname().equalsIgnoreCase("James Agmata".trim())) {
                path = "/org/example/angelsburgerpos/images/james.png";
            }
            if (getClass().getResource(path) != null) {
                String imagePath = getClass().getResource(path).toExternalForm();
                logo.setImage(new Image(imagePath));
                double radius = Math.min(logo.getFitWidth(), logo.getFitHeight()) / 2;
                Circle clip = new Circle(logo.getFitWidth() / 2, logo.getFitHeight() / 2, radius);
                logo.setClip(clip);
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
        if (user.getRole().equalsIgnoreCase("Cashier")) {
            employeesButton.setVisible(false);
        }
    }

    public void stopServer() {
        if (kioskServer != null) kioskServer.stop();
    }
}