package org.example.angelsburgerpos.service;

import org.example.angelsburgerpos.model.Product;
import java.util.List;

public interface ProductService {
    List<Product> getProducts();
    Product findById(String id);
}