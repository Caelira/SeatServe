package org.example.angelsburgerpos.DAO;

import org.example.angelsburgerpos.model.Product;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProductDAO {

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
            String[] parts = full.split(",");

            if (parts.length >= 6) {
                products.add(new Product(
                        parts[0],
                        parts[1],
                        Double.parseDouble(parts[2]),
                        parts[3].trim(),
                        parts[4].trim(),
                        Integer.parseInt(parts[5].trim())
                ));
            }
        }
        scanner.close();
        return products;
    }
}