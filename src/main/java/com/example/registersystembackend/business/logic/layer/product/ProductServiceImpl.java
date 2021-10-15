package com.example.registersystembackend.business.logic.layer.product;

import com.example.registersystembackend.data.access.layer.product.Product;
import com.example.registersystembackend.data.access.layer.product.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
class ProductServiceImpl implements ProductService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductRepository productRepository;
    private final ObjectMapper mapper;


    @Autowired
    ProductServiceImpl(ProductRepository productRepository, ObjectMapper mapper) {
        this.productRepository = productRepository;
        this.mapper = mapper;
    }


    @Override
    public void saveProduct(Product product) {
        productRepository.findProductByIsDeletedIsFalseAndCode(product.getCode())
                .ifPresent(p -> {
                    if (!p.getId().equals(product.getId())) {
                        final String errorMsg = "The code " + p.getCode() + " already exist.";
                        LOG.error(errorMsg);
                        throw new IllegalArgumentException(errorMsg);
                    }
                });
        productRepository.save(product);
    }

    @Override
    public void deleteProduct(UUID id) {
        final Product product = getProduct(id);
        product.setDeleted(true);
        productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAllByIsDeletedIsFalseOrderByNameAsc();
    }

    @Override
    public Product getProduct(UUID id) {
        return productRepository.findById(id).orElseThrow(() -> {
            final String errorMsg = "There is no product with this id: " + id;
            LOG.error(errorMsg);
            return new NoSuchElementException(errorMsg);
        });
    }

    @Override
    public Product getProductByCode(String code) {
        return productRepository.findProductByIsDeletedIsFalseAndCode(code).orElseThrow(() -> {
            final String errorMsg = "There is no product with this code: " + code;
            LOG.error(errorMsg);
            return new NoSuchElementException(errorMsg);
        });
    }

    @Override
    public Set<Product> getProductByIds(Set<UUID> ids) {
        final Set<Product> products = productRepository.findAllByIsDeletedIsFalseAndIdIn(ids);
        final Set<UUID> uuidSet = products.stream().map(Product::getId).collect(Collectors.toSet());

        final Set<String> missingProducts = ids
                .stream()
                .filter(id -> !uuidSet.contains(id)).
                map(id -> {
                    final String msg = String.format("There is no product with this id: %s", id);
                    LOG.error(msg);
                    return msg;
                })
                .collect(Collectors.toSet());

        if (!missingProducts.isEmpty()) {
            try {
                throw new NoSuchElementException(mapper.writeValueAsString(missingProducts));
            } catch (JsonProcessingException e) {
                // this should not happen
            }
        }
        return products;
    }
}
