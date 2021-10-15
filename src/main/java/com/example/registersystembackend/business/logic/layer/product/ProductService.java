package com.example.registersystembackend.business.logic.layer.product;

import com.example.registersystembackend.data.access.layer.product.Product;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ProductService {
    void saveProduct(Product product);

    void deleteProduct(UUID id);

    List<Product> getAllProducts();

    Product getProduct(UUID id);

    Product getProductByCode(String code);

    Set<Product> getProductByIds(Set<UUID> ids);
}
