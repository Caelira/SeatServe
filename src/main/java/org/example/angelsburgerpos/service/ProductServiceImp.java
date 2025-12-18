package org.example.angelsburgerpos.service;

import org.example.angelsburgerpos.DAO.ProductDAO;
import org.example.angelsburgerpos.model.Product;
import java.util.List;

public class ProductServiceImp implements ProductService{
    private ProductDAO dao = new ProductDAO();

    @Override
    public List<Product> getProducts()  {
        return dao.getAllProducts();
    }

    @Override
    public Product findById(String id) {
        return dao.findById(id);
    }
}