package org.example.angelsburgerpos.service;

import com.google.gson.Gson;
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
            config.staticFiles.add("/webapp", Location.CLASSPATH);
        }).start(8080);

        app.get("/api/menu", ctx -> {
            ctx.json(productService.getProducts());
        });
        app.post("/api/order", ctx -> {
            String body = ctx.body();
            List<OrderRequest> items = gson.fromJson(body, new TypeToken<List<OrderRequest>>(){}.getType());

            MobileOrder newOrder = new MobileOrder("Mobile Customer", items);

            Platform.runLater(() -> {
             //   dashboard.addPendingOrder(newOrder);
            });

            ctx.status(201).result("Order Sent to Cashier");
        });
    }
        public void stop () {
            if (app != null) {
                app.stop();
            }
        }

    }
