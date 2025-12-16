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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class KioskServer {

    private final ProductService productService;
    private final DashboardController dashboard;
    private final Gson gson = new Gson();
    private Javalin app;
    private static int orderCounter = 1;
    public KioskServer(ProductService productService, DashboardController dashboard) {
        this.productService = productService;
        this.dashboard = dashboard;
    }

    private boolean checkAvailability(Product p){
        return InventoryManager.canMakeProduct(p.getId(), 1);
    }
    public void startServer() {
        app = Javalin.create(config -> {
            config.staticFiles.add("webapp", Location.CLASSPATH);
        }).start(5050); //5050 makapasa HAHAHAHAAHAH

        app.get("/api/menu", ctx -> {
            List<Product> allProducts = productService.getProducts();

            List<Map<String, Object>> menuResponse = new ArrayList<>();

            for(Product p : allProducts){
                boolean available = checkAvailability(p);

                menuResponse.add(Map.of(
                        "id", p.getId(),
                        "name", p.getName(),
                        "price", p.getPrice(),
                        "available", available
                ));

            }
            String jsonResponse = gson.toJson(menuResponse);
            ctx.result(jsonResponse).contentType("application/json");
        });
        app.post("/api/order", ctx -> {
            String body = ctx.body();
            try {
                List<OrderRequest> items = gson.fromJson(body, new TypeToken<List<OrderRequest>>(){}.getType());

                String shortId = String.format("%03d", orderCounter++);

                MobileOrder order = new MobileOrder("Customer #" + shortId, items);
                order.setOrderId(shortId);
                order.setStatus("PENDING_PAYMENT");

                Platform.runLater(() -> dashboard.addPendingOrder(order));
                String jsonResponse = gson.toJson(order.getOrderId());
                ctx.status(201).result(jsonResponse).contentType("application/json");

            } catch (JsonSyntaxException e) {
                System.out.println("Error: " +  e.getMessage());
                ctx.status(400).result("Error processing order. Please try again.");
            }


        });

        app.get("/api/order/{id}/status", ctx -> {
            String id = ctx.pathParam("id");
            String status = "PENDING";

            if(dashboard.getPendingOrders() != null){
                for(MobileOrder o : dashboard.getPendingOrders()){
                    if(o.getOrderId().equals(id)){
                        status = o.getStatus();
                        break;
                    }
                }
            }
            ctx.result(status);
        });
    }
        public void stop () {
            if (app != null) {
                app.stop();
            }
        }

    }
