package org.example.angelsburgerpos.service;

import org.example.angelsburgerpos.DAO.ProductDAO;
import org.example.angelsburgerpos.model.Product;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ProductServiceImp implements ProductService{
    private ProductDAO dao = new ProductDAO();
    @Override
    public List<Product> getProducts()  {
        return dao.getAllProducts();
    }

    @Override
    public String deleteProduct(Product product) {
        return "";
    }

    @Override
    public String editProduct(Product product) {
        return "";
    }

    @Override
    public String updateProduct(Product product) {
        return "";
    }

    @Override
    public String storeProducts(ArrayList<Product> products) {
        return "";
    }
}
