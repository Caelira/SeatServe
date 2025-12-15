package org.example.angelsburgerpos.DAO;

import org.example.angelsburgerpos.model.Product;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProductDAO {

    public Product findById(String id){
        for(Product p : this.getAllProducts()){
            if(p.getId().equals(id)){
                return p;
            }
        }
        return null;
    }
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        InputStream is = getClass().getResourceAsStream("/org/example/angelsburgerpos/util/product-database.txt");

        if (is == null) {
            System.out.println("ERROR: Could not find product-database.txt");
            return products;
        }

        Scanner scanner = new Scanner(is);

        while (scanner.hasNextLine()) {
            String full = scanner.nextLine();
            if (full.trim().isEmpty()) continue;


            String[] parts = full.split(",");

            if (parts.length >= 6) {
                try {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    double price = Double.parseDouble(parts[2].trim());

                    StringBuilder pathBuilder = new StringBuilder();
                    for (int i = 3; i < parts.length - 2; i++) {
                        pathBuilder.append(parts[i]);
                        if (i < parts.length - 3) pathBuilder.append(",");
                    }
                    String imagePath = pathBuilder.toString().trim();

                    String category = parts[parts.length - 2].trim();
                    int stock = Integer.parseInt(parts[parts.length - 1].trim());

                    products.add(new Product(id, name, price, imagePath, category, stock));

                } catch (Exception e) {
                    System.out.println("Skipping bad line: " + full);
                }
            }
        }
        scanner.close();
        return products;
    }
}