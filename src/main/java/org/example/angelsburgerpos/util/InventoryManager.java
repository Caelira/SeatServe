package org.example.angelsburgerpos.util;

import org.example.angelsburgerpos.model.CartItem;
import org.example.angelsburgerpos.model.Ingredient;
import javafx.application.Platform;

import java.io.*;
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
            if (parts.length >= 5) {
                boolean critical = Boolean.parseBoolean(parts[4].trim());
                ingredientMap.put(parts[0], new Ingredient(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3], critical));
            } else if (parts.length == 4) {
                ingredientMap.put(parts[0], new Ingredient(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3], true));
            }
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

    public static boolean canMakeProduct(String productId, int quantity) {
        if (ingredientMap.isEmpty()) initialize();

        if (!recipeMap.containsKey(productId)) return true;

        Map<String, Integer> requiredIngredients = recipeMap.get(productId);

        for (Map.Entry<String, Integer> entry : requiredIngredients.entrySet()) {
            String ingId = entry.getKey();
            int qtyPerUnit = entry.getValue();
            int totalRequired = qtyPerUnit * quantity;

            if (ingredientMap.containsKey(ingId)) {
                Ingredient ing = ingredientMap.get(ingId);
if (ing.isCritical() && ing.getStock() < totalRequired) {
                    return false;
                }
            } else {
                return false; // Ingredient missing from DB
            }
        }
        return true;
    }
    public static int calculateMaxStock(String productId, List<CartItem> currentCart) {
        if (ingredientMap.isEmpty()) initialize();
        if (!recipeMap.containsKey(productId)) return 100;

        Map<String, Integer> tempStock = new HashMap<>();
        for (Ingredient ing : ingredientMap.values()) {
            tempStock.put(ing.getId(), ing.getStock());
        }

        for (CartItem item : currentCart) {
            if (recipeMap.containsKey(item.getProduct().getId())) {
                Map<String, Integer> ingredientsNeeded = recipeMap.get(item.getProduct().getId());
                for (Map.Entry<String, Integer> entry : ingredientsNeeded.entrySet()) {
                    String ingId = entry.getKey();
                    int qtyPerUnit = entry.getValue();

                    if (item.getRemovedIngredients() != null && item.getRemovedIngredients().contains(ingId)) {
                        continue;
                    }

                    if (tempStock.containsKey(ingId)) {
                        tempStock.put(ingId, tempStock.get(ingId) - (qtyPerUnit * item.getQuantity()));
                    }
                }
            }
        }

        Map<String, Integer> neededForTarget = recipeMap.get(productId);
        int maxPossible = Integer.MAX_VALUE;

        for (Map.Entry<String, Integer> entry : neededForTarget.entrySet()) {
            String ingId = entry.getKey();
            int qtyPerUnit = entry.getValue();

            if (ingredientMap.containsKey(ingId)) {
                Ingredient ing = ingredientMap.get(ingId);

                if (ing.isCritical()) {
                    int available = tempStock.getOrDefault(ingId, 0);
                    if (available < 0) available = 0;

                    int canMake = available / qtyPerUnit;
                    if (canMake < maxPossible) {
                        maxPossible = canMake;
                    }
                }
            } else {
                return 0;
            }
        }
        return maxPossible;
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
                        if (item.getRemovedIngredients() != null && item.getRemovedIngredients().contains(ingId)) {
                            continue;
                        }

                        Ingredient ing = ingredientMap.get(ingId);
                        int newStock = ing.getStock() - totalDeduction;
                        ing.setStock(Math.max(0, newStock));
                    }
                }
            }
        }
        saveInventory();
        notifyRefresh();
    }

    private static void saveInventory() {
        try (PrintWriter writer = new PrintWriter(new File(AppConfig.INGREDIENTS_FILE))) {
            List<Ingredient> list = new ArrayList<>(ingredientMap.values());
            list.sort(Comparator.comparing(Ingredient::getId));
            for (Ingredient ing : list) {
                writer.printf("%s,%s,%d,%s,%b%n",
                        ing.getId(), ing.getName(), ing.getStock(), ing.getUnit(), ing.isCritical());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void notifyRefresh() {
        if (refreshListener != null) Platform.runLater(refreshListener);
    }

    public static List<Ingredient> getAllIngredients() {
        if (ingredientMap.isEmpty()) initialize();
        List<Ingredient> list = new ArrayList<>(ingredientMap.values());
        list.sort(Comparator.comparing(Ingredient::getId));
        return list;
    }

    public static Map<String, Integer> getRecipe(String productId) {
        return recipeMap.getOrDefault(productId, new HashMap<>());
    }

    public static String getIngredientName(String id) {
        return ingredientMap.containsKey(id) ? ingredientMap.get(id).getName() : id;
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
            String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            pw.printf("%s,%s,%s,%d,%s,%s%n", timestamp, ing.getId(), ing.getName(), ing.getStock(), ing.getUnit(), reason);
        } catch (IOException e) { e.printStackTrace(); }
    }
}