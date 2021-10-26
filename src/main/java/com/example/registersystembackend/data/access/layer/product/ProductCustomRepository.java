package com.example.registersystembackend.data.access.layer.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


interface ProductCustomRepository {
    /**
     * Search for all the products by name, if the name is not empty.
     * @param name of the product
     * @param pageable contains the page, size and sorting
     * @return a set of sorted products
     */
    Page<Product> findProductsByName(String name, Pageable pageable);
}
