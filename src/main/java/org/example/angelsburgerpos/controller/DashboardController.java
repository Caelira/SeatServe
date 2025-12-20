package org.example.angelsburgerpos.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.angelsburgerpos.model.CartItem;
import org.example.angelsburgerpos.model.MobileOrder;
import org.example.angelsburgerpos.model.Product;
import org.example.angelsburgerpos.model.User;
import org.example.angelsburgerpos.service.KioskServer;
import org.example.angelsburgerpos.service.ProductService;
import org.example.angelsburgerpos.service.ProductServiceImp;
import org.example.angelsburgerpos.service.TransactionService;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML
    private StackPane mainContentArea;
    @FXML
    private AnchorPane menuSection;
    @FXML
    private Label nameLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private ImageView logo;
    @FXML
    private Label roleLabel;
    @FXML
    private Button menuButton;
    @FXML
    private Button mobileOrdersButton;
    @FXML
    private Button employeesButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Label totalLabel;
    @FXML
    private Button viewCartButton;
    @FXML
    private Button warningButtonForAdmin;
    @FXML
    private TableView<Product> menuTable;
    @FXML
    private TableColumn<Product, String> colName;
    @FXML
    private TableColumn<Product, String> colCategory;
    @FXML
    private TableColumn<Product, Double> colPrice;
    @FXML
    private TableColumn<Product, Integer> colStock;
    @FXML
    private TableColumn<Product, Void> colAction;
    @FXML
    private Button warningButton;
    private KioskServer kioskServer;
    private ProductService productService;
    private ObservableList<CartItem> cartList = FXCollections.observableArrayList();
    private ObservableList<MobileOrder> pendingOrders = FXCollections.observableArrayList();
    private ObservableList<Product> productList = FXCollections.observableArrayList();
    private User user;

    public DashboardController() {
        productService = new ProductServiceImp();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTable();
        loadProducts("ALL");
        initClock();
        startKioskServer();
        updateMobileOrderNotification();
        checkLowStock();
        if (menuSection != null) menuSection.setVisible(true);
    }

    private void initTable() {
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colCategory.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory()));
        colPrice.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrice()).asObject());
        colStock.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStock()).asObject());

        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button addBtn = new Button("Add");
            private final Button custBtn = new Button("Cust.");
            private final HBox pane = new HBox(5, addBtn, custBtn);

            {
                addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                custBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

                addBtn.setOnAction(event -> {
                    Product p = getTableView().getItems().get(getIndex());
                    addToCart(p);
                });

                custBtn.setOnAction(event -> {
                    Product p = getTableView().getItems().get(getIndex());
                    openCustomizeWindow(p);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Product p = getTableView().getItems().get(getIndex());

                    boolean available = p.getStock() > 0;
                    addBtn.setDisable(!available);
                    custBtn.setDisable(!available);

                    setGraphic(pane);
                }
            }
        });

        menuTable.setItems(productList);
    }

    private void openCustomizeWindow(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/angelsburgerpos/customize-view.fxml"));
            Parent root = loader.load();

            CustomizeController controller = loader.getController();

            controller.setData(product, (customItem) -> {
                cartList.add(customItem);
                updateFooter();
                refreshStockCounts(); // Recalculate stock immediately
            });

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Customize " + product.getName());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadProducts(String category) {
        productList.clear();
        List<Product> products = productService.getProducts();

        for (Product product : products) {
            if (category.equals("ALL") || product.getCategory().equalsIgnoreCase(category)) {
                int realStock = org.example.angelsburgerpos.util.InventoryManager.calculateMaxStock(
                        product.getId(),
                        new ArrayList<>(cartList)
                );

                product.setStock(realStock);
                productList.add(product);
            }
        }
        menuTable.refresh();
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
    private void filterAll() {
        loadProducts("ALL");
    }

    private void addToCart(Product product) {
        int remainingStock = org.example.angelsburgerpos.util.InventoryManager.calculateMaxStock(product.getId(), new ArrayList<>(cartList));

        if (remainingStock > 0) {
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
            refreshStockCounts();

        } else {
            showAlert("Out of Stock", "Not enough ingredients.");
        }
    }

    private void refreshStockCounts() {
        for (Product p : productList) {
            int realStock = org.example.angelsburgerpos.util.InventoryManager.calculateMaxStock(
                    p.getId(),
                    new ArrayList<>(cartList)
            );
            p.setStock(realStock);
        }
        menuTable.refresh();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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
    private void menuButtonClicked() {
        mainContentArea.getChildren().clear();
        if (menuSection != null) {
            mainContentArea.getChildren().add(menuSection);
            menuSection.setVisible(true);
        }
    }

    @FXML
    private void inventoryClicked() {
        loadView("/org/example/angelsburgerpos/inventory-view.fxml");
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

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            Object controller = loader.getController();

            if (controller instanceof MobileOrdersController) {
                ((MobileOrdersController) controller).setDashboard(this);
                ((MobileOrdersController) controller).setOrderList(pendingOrders);
            }
            if (controller instanceof InventoryController) {
                ((InventoryController) controller).setUser(this.user);
            }
            if (menuSection != null) menuSection.setVisible(false);
            mainContentArea.getChildren().clear();
            mainContentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startKioskServer() {
        try {
            kioskServer = new KioskServer(productService, this);
            kioskServer.startServer();
        } catch (Exception e) {
            System.out.println("Failed to start server: " + e.getMessage());
        }
    }

    public List<MobileOrder> getPendingOrders() {
        return pendingOrders;
    }

    public void addPendingOrder(MobileOrder order) {
        pendingOrders.add(order);
        updateMobileOrderNotification();
    }

    public void finalizeMobileOrder(MobileOrder order) {
        checkLowStock();
    }

    private void updateMobileOrderNotification() {
        if (mobileOrdersButton != null) {
            mobileOrdersButton.setText("Mobile Orders (" + pendingOrders.size() + ")");
            if (!pendingOrders.isEmpty())
                mobileOrdersButton.setStyle("-fx-background-color: #ff9800; -fx-text-fill: white;");
            else mobileOrdersButton.setStyle("");
        }
    }

    private void initClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss");
            dateLabel.setText(java.time.LocalDateTime.now().format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
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

    public void stopServer() {
        if (kioskServer != null) kioskServer.stop();
    }

    public void setUserInfo(User user) {
        this.user = user;
        nameLabel.setText(user.getFullname());
        roleLabel.setText(user.getRole());
        setLogo(user);
        if (user.getRole().equalsIgnoreCase("Cashier/cook")) {
            employeesButton.setVisible(false);
            warningButtonForAdmin.setVisible(false);
        }else if(user.getRole().equalsIgnoreCase("admin")){
            mobileOrdersButton.setVisible(false);
            menuButton.setVisible(false);
        }
    }

    private void setLogo(User user) {
        try {
            String path = "/org/example/angelsburgerpos/images/logo.jpg";
            if (user.getFullname().equalsIgnoreCase("James Agmata"))
                path = "/org/example/angelsburgerpos/images/james.png";
            else if (user.getFullname().equalsIgnoreCase("Rowena Acabado"))
                path = "/org/example/angelsburgerpos/images/rowena.jpg";
            else if (user.getFullname().equalsIgnoreCase("Ayende"))
                path = "/org/example/angelsburgerpos/images/gwyn.jpg";
            if (getClass().getResource(path) != null) {
                logo.setImage(new Image(getClass().getResource(path).toExternalForm()));
                double radius = Math.min(logo.getFitWidth(), logo.getFitHeight()) / 2;
                Circle clip = new Circle(logo.getFitWidth() / 2, logo.getFitHeight() / 2, radius);
                logo.setClip(clip);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkLowStock() {
        boolean hasLowStock = false;
        int threshold = 20;

        for (org.example.angelsburgerpos.model.Ingredient ing : org.example.angelsburgerpos.util.InventoryManager.getAllIngredients()) {
            if (ing.getStock() <= threshold) {
                hasLowStock = true;
                break;
            }
        }

        //hide or show if there is low stock
        if (warningButton != null) {
            warningButton.setVisible(hasLowStock);
            warningButton.setManaged(hasLowStock);
        }
    }

    @FXML
    private void showLowStockView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/angelsburgerpos/low-stock-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Low Stock Warning");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getUser() {
        return user;
    }

    @FXML
    private void viewCartClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/angelsburgerpos/cart-popup-view.fxml"));
            Parent root = loader.load();

            CartController cartController = loader.getController();
            cartController.setCartList(this.cartList);

            cartController.setUser(this.user);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Current Order");
            stage.setScene(new Scene(root));
            stage.setOnHidden(e -> updateFooter());
            stage.showAndWait();

            checkLowStock();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}