package org.example.angelsburgerpos.service;

import org.example.angelsburgerpos.model.Product;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public interface ProductService {

    public List<Product> getProducts() ;
    public String deleteProduct(Product product);
    public String editProduct(Product product);
    public String updateProduct(Product product);
    public String storeProducts(ArrayList<Product> products);
}
