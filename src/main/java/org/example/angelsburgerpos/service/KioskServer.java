package org.example.angelsburgerpos.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import javafx.application.Platform;
import org.example.angelsburgerpos.controller.DashboardController;
import org.example.angelsburgerpos.model.MobileOrder;
import org.example.angelsburgerpos.model.Product;
import org.example.angelsburgerpos.util.InventoryManager;
import org.example.angelsburgerpos.util.OrderRequest;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KioskServer {

    private static int orderCounter = 1;
    private final ProductService productService;
    private final DashboardController dashboard;
    private final Gson gson = new Gson();
    private Javalin app;

    public KioskServer(ProductService productService, DashboardController dashboard) {
        this.productService = productService;
        this.dashboard = dashboard;
    }

    private boolean checkAvailability(Product p) {
        return InventoryManager.canMakeProduct(p.getId(), 1);
    }

    public void startServer() {
        app = Javalin.create(config -> {
            config.staticFiles.add("webapp", Location.CLASSPATH);
        }).start(5050);

        app.get("/api/menu", ctx -> {
            List<Product> allProducts = productService.getProducts();
            List<Map<String, Object>> menuResponse = new ArrayList<>();

            System.out.println("--- CHECKING IMAGES ---");

            for (Product p : allProducts) {
                boolean available = checkAvailability(p);

                String imageName = "placeholder.png";
                String fullPath = p.getImagePath();
                if (fullPath != null && !fullPath.isEmpty()) {
                    java.io.File f = new java.io.File(fullPath);
                    imageName = f.getName();
                }

                 URL resourceUrl = getClass().getResource("/webapp/images/" + imageName);
                if (resourceUrl == null) {
                    System.out.println("⚠️ MISSING IMAGE: " + imageName + " (Product: " + p.getName() + ")");
                    imageName = "placeholder.png";
                }

                menuResponse.add(Map.of(
                        "id", p.getId(),
                        "name", p.getName(),
                        "price", p.getPrice(),
                        "category", p.getCategory(),
                        "available", available,
                        "image", imageName
                ));
            }
            String jsonResponse = gson.toJson(menuResponse);
            ctx.result(jsonResponse).contentType("application/json");
        });

        app.post("/api/order", ctx -> {
            String body = ctx.body();
            try {
                List<OrderRequest> items = gson.fromJson(body, new TypeToken<List<OrderRequest>>() {}.getType());
                String shortId = String.format("%03d", orderCounter++);
                MobileOrder order = new MobileOrder("Customer #" + shortId, items);
                order.setOrderId(shortId);
                order.setStatus("PENDING_PAYMENT");
                Platform.runLater(() -> dashboard.addPendingOrder(order));
                ctx.status(201).result(gson.toJson(order.getOrderId())).contentType("application/json");
            } catch (JsonSyntaxException e) {
                ctx.status(400).result("Error");
            }
        });

        app.get("/api/order/{id}/status", ctx -> {
            String id = ctx.pathParam("id");
            String status = "PENDING";
            if (dashboard.getPendingOrders() != null) {
                for (MobileOrder o : dashboard.getPendingOrders()) {
                    if (o.getOrderId().equals(id)) {
                        status = o.getStatus();
                        break;
                    }
                }
            }
            ctx.result(status);
        });
    }

    public void stop() {
        if (app != null) app.stop();
    }
}