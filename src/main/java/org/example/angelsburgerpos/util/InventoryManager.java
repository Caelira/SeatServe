package org.example.angelsburgerpos.util;

import org.example.angelsburgerpos.model.CartItem;
import org.example.angelsburgerpos.model.Ingredient;

import java.io.*;
import java.util.*;

public class InventoryManager {

    private static final String INGREDIENTS_FILE = "src/main/resources/org/example/angelsburgerpos/util/ingredients.txt";
    private static final String RECIPES_FILE = "/org/example/angelsburgerpos/util/recipes.txt";

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
        try {
            File file = new File(INGREDIENTS_FILE);
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    if (parts.length == 4) {
                        ingredientMap.put(parts[0], new Ingredient(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3]));
                    }
                }
                scanner.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void addIngredient(Ingredient newIng) {
        ingredientMap.put(newIng.getId(), newIng);
        reduceStock(new ArrayList<>());
    }

    public static void deleteIngredient(String id) {
        ingredientMap.remove(id);
        reduceStock(new ArrayList<>());
    }

    private static void loadRecipes() {
        try {
            InputStream is = InventoryManager.class.getResourceAsStream(RECIPES_FILE);
            if (is != null) {
                Scanner scanner = new Scanner(is);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
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
                scanner.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reduceStock(List<CartItem> soldItems) {
        if (ingredientMap.isEmpty()) {
            initialize();
        }

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
                        int currentStock = ing.getStock();
                        ing.setStock(currentStock - totalDeduction);
                    }
                }
            }
        }

        saveInventory();
        if (refreshListener != null) {
            javafx.application.Platform.runLater(refreshListener);
        }
    }

    public static List<Ingredient> getAllIngredients() {
        if (ingredientMap.isEmpty()) {
            initialize();
        }
        List<Ingredient> list = new ArrayList<>(ingredientMap.values());
        list.sort(Comparator.comparing(Ingredient::getId));
        return list;
    }

    public static boolean canMakeProduct(String productId, int quantity){
        if(ingredientMap.isEmpty()){
            initialize();
        }

        if(recipeMap.containsKey(productId)){
            Map<String, Integer> requiredIngredients = recipeMap.get(productId);

            for(Map.Entry<String, Integer> entry : requiredIngredients.entrySet()){
                String ingId = entry.getKey();
                int qtyPerUnit = entry.getValue();
                int totalRequired = qtyPerUnit * quantity;

                if(ingredientMap.containsKey(ingId)){
                    Ingredient ing = ingredientMap.get(ingId);
                    if(ing.getStock() < totalRequired){
                        return false;
                    }
                } else {
                    System.out.println("Missing ingredients");
                    return false;
                }
            }
        }
        return true;
    }

    private static void saveInventory() {
        try {
            File file = new File(INGREDIENTS_FILE);
            PrintWriter writer = new PrintWriter(file);

            List<Ingredient> list = new ArrayList<>(ingredientMap.values());
            list.sort(Comparator.comparing(Ingredient::getId));

            for (Ingredient ing : list) {
                writer.printf("%s,%s,%d,%s%n", ing.getId(), ing.getName(), ing.getStock(), ing.getUnit());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}