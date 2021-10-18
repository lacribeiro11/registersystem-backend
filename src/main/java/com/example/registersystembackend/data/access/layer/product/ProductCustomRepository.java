package com.example.registersystembackend.data.access.layer.product;

import java.util.List;

interface ProductCustomRepository {
    /**
     * Search for all the products by name. If the name is empty, it will return all
     * By default: It will search only products which aren't deleted, and it is sorted by name
     * @param name of the product
     * @return a sorted product list
     */
    List<Product> findProductsByName(String name);
}
