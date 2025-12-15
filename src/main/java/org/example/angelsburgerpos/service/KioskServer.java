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
import org.example.angelsburgerpos.util.OrderRequest;

import java.util.List;

public class KioskServer {

    private final ProductService productService;
    private final DashboardController dashboard;
    private final Gson gson = new Gson();
    private Javalin app;

    public KioskServer(ProductService productService, DashboardController dashboard) {
        this.productService = productService;
        this.dashboard = dashboard;
    }

    public void startServer() {
        app = Javalin.create(config -> {
            config.staticFiles.add("webapp", Location.CLASSPATH);
        }).start(5050); //5050 makapasa HAHAHAHAAHAH

        app.get("/api/menu", ctx -> {
            List<Product> products = productService.getProducts();
            String jsonResponse = gson.toJson(products); // Use Gson manually
            ctx.result(jsonResponse).contentType("application/json");
        });
        app.post("/api/order", ctx -> {
            String body = ctx.body();
            try {
                List<OrderRequest> items = gson.fromJson(body, new TypeToken<List<OrderRequest>>(){}.getType());

                if(items != null && !items.isEmpty()){
                    MobileOrder order = new MobileOrder("Customer", items);
                    Platform.runLater(() -> {
                        dashboard.addPendingOrder(order);
                    });

                    String jsonResponse = gson.toJson(order.getOrderId());
                    ctx.status(201).result(jsonResponse).contentType("application/json");
                }else{
                    ctx.status(400).result("Empty order");
                }
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
