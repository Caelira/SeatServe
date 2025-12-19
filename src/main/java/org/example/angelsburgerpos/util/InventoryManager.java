package org.example.angelsburgerpos.util;

import org.example.angelsburgerpos.model.CartItem;
import org.example.angelsburgerpos.model.Ingredient;
import javafx.application.Platform;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class InventoryManager {

    private static Map<String, Ingredient> ingredientMap = new HashMap<>();
    private static Map<String, Map<String, Integer>> recipeMap = new HashMap<>();
    private static Runnable refreshListener;

    public static void initialize() {
        loadIngredients();
        loadRecipes();
    }

    public static void setRefreshListener(Runnable listener) {
        refreshListener = listener;
    }

    private static void loadIngredients() {
        List<String> lines = FileUtil.readLines(AppConfig.INGREDIENTS_FILE);
        ingredientMap.clear();
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 4) {
                ingredientMap.put(parts[0], new Ingredient(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3]));
            }
        }
    }

    public static void addIngredient(Ingredient newIng) {
        ingredientMap.put(newIng.getId(), newIng);
        saveInventory();
        notifyRefresh();
    }

    public static void archiveIngredient(String id, String reason) {
        if (ingredientMap.containsKey(id)) {
            Ingredient ing = ingredientMap.get(id);
            logArchive(ing, reason);

            ingredientMap.remove(id);
            saveInventory();
            notifyRefresh();
        }
    }

    private static void logArchive(Ingredient ing, String reason) {
        try (FileWriter fw = new FileWriter(AppConfig.ARCHIVES_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            pw.printf("%s,%s,%s,%d,%s,%s%n",
                    timestamp, ing.getId(), ing.getName(), ing.getStock(), ing.getUnit(), reason);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadRecipes() {
        List<String> lines = FileUtil.readResource(AppConfig.RECIPES_RESOURCE);
        for (String line : lines) {
            String[] parts = line.split(",");
            String productId = parts[0];
            Map<String, Integer> ingredients = new HashMap<>();
            for (int i = 1; i < parts.length; i++) {
                String[] ingQty = parts[i].split(":");
                if (ingQty.length == 2) {
                    ingredients.put(ingQty[0], Integer.parseInt(ingQty[1]));
                }
            }
            recipeMap.put(productId, ingredients);
        }
    }

    public static void reduceStock(List<CartItem> soldItems) {
        if (ingredientMap.isEmpty()) initialize();

        for (CartItem item : soldItems) {
            String prodId = item.getProduct().getId();
            int qtySold = item.getQuantity();
            if (recipeMap.containsKey(prodId)) {
                Map<String, Integer> requiredIngredients = recipeMap.get(prodId);
                for (Map.Entry<String, Integer> entry : requiredIngredients.entrySet()) {
                    String ingId = entry.getKey();
                    int qtyPerUnit = entry.getValue();
                    int totalDeduction = qtyPerUnit * qtySold;
                    if (ingredientMap.containsKey(ingId)) {
                        Ingredient ing = ingredientMap.get(ingId);
                        ing.setStock(ing.getStock() - totalDeduction);
                    }
                }
            }
        }
        saveInventory();
        notifyRefresh();
    }

    public static int calculateMaxStock(String productId, List<CartItem> currentCart) {
        if (ingredientMap.isEmpty()) initialize();
        if (!recipeMap.containsKey(productId)) return 0;

        Map<String, Integer> tempStock = new HashMap<>();
        for (Ingredient ing : ingredientMap.values()) {
            tempStock.put(ing.getId(), ing.getStock());
        }
        for (CartItem item : currentCart) {
            String cartProdId = item.getProduct().getId();
            int cartQty = item.getQuantity();

            if (recipeMap.containsKey(cartProdId)) {
                Map<String, Integer> ingredientsNeeded = recipeMap.get(cartProdId);
                for (Map.Entry<String, Integer> entry : ingredientsNeeded.entrySet()) {
                    String ingId = entry.getKey();
                    int qtyPerUnit = entry.getValue();
                    int totalToDeduct = qtyPerUnit * cartQty;

                    if (tempStock.containsKey(ingId)) {
                        int current = tempStock.get(ingId);
                        tempStock.put(ingId, current - totalToDeduct);
                    }
                }
            }
        }
        Map<String, Integer> neededForTarget = recipeMap.get(productId);
        int maxPossible = Integer.MAX_VALUE;

        for (Map.Entry<String, Integer> entry : neededForTarget.entrySet()) {
            String ingId = entry.getKey();
            int qtyPerUnit = entry.getValue();

            if (tempStock.containsKey(ingId)) {
                int available = tempStock.get(ingId);
                if (available < 0) available = 0;

                int canMake = available / qtyPerUnit;
                if (canMake < maxPossible) {
                    maxPossible = canMake;
                }
            } else {
                return 0;
            }
        }

        return maxPossible;
    }

    public static boolean canMakeProduct(String productId, int quantity) {
        if (ingredientMap.isEmpty()) initialize();
        if (recipeMap.containsKey(productId)) {
            Map<String, Integer> requiredIngredients = recipeMap.get(productId);
            for (Map.Entry<String, Integer> entry : requiredIngredients.entrySet()) {
                String ingId = entry.getKey();
                int qtyPerUnit = entry.getValue();
                int totalRequired = qtyPerUnit * quantity;
                if (ingredientMap.containsKey(ingId)) {
                    if (ingredientMap.get(ingId).getStock() < totalRequired) return false;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public static List<Ingredient> getAllIngredients() {
        if (ingredientMap.isEmpty()) initialize();
        List<Ingredient> list = new ArrayList<>(ingredientMap.values());
        list.sort(Comparator.comparing(Ingredient::getId));
        return list;
    }

    private static void saveInventory() {
        try (PrintWriter writer = new PrintWriter(new File(AppConfig.INGREDIENTS_FILE))) {
            List<Ingredient> list = new ArrayList<>(ingredientMap.values());
            list.sort(Comparator.comparing(Ingredient::getId));

            for (Ingredient ing : list) {
                writer.printf("%s,%s,%d,%s%n", ing.getId(), ing.getName(), ing.getStock(), ing.getUnit());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void notifyRefresh() {
        if (refreshListener != null) {
            Platform.runLater(refreshListener);
        }
    }
}