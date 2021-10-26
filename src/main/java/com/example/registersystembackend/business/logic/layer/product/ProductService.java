package com.example.registersystembackend.business.logic.layer.product;

import com.example.registersystembackend.data.access.layer.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.UUID;

public interface ProductService {
    void saveProduct(Product product);

    void deleteProduct(UUID id);

    Page<Product> getAllProducts(String name, Pageable pageable);

    Product getProduct(UUID id);

    Product getProductByCode(String code);

    Set<Product> getProductByIds(Set<UUID> ids);
}
