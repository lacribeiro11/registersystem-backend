package com.example.registersystembackend.data.access.layer.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
class ProductCustomRepositoryITest {

    @Autowired
    private ProductRepository productRepository;

    private List<Product> expectedProducts;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        final Product product1 = newProduct("code1");
        final Product product2 = newProduct("code2");
        final Product deletedProduct = newProduct("deleted");
        deletedProduct.setDeleted(true);
        expectedProducts = List.of(product1, product2);
        productRepository.saveAll(Set.of(product1, product2, deletedProduct));
    }

    @Nested
    class FindProductsByName {

        @ParameterizedTest
        @NullAndEmptySource
        void nameIsNullOrEmpty(String name) {
            List<Product> actualProducts = productRepository.findProductsByName(name);

            assertEquals(expectedProducts, actualProducts);
        }

        @Test
        void findOne() {
            List<Product> actualProducts = productRepository.findProductsByName("code1");

            assertEquals(1, actualProducts.size());
            assertEquals(expectedProducts.get(0), actualProducts.get(0));
        }

        @Test
        void nameContains() {
            List<Product> actualProducts = productRepository.findProductsByName("ode");

            assertEquals(expectedProducts, actualProducts);
        }

        @Test
        void caseInsensitive() {
            List<Product> actualProducts = productRepository.findProductsByName("DE");

            assertEquals(expectedProducts, actualProducts);
        }

        @Test
        void foundNone() {
            List<Product> actualProducts = productRepository.findProductsByName("deleted");

            assertEquals(List.of(), actualProducts);
        }
    }

    private static Product newProduct(String code) {
        final Product product = new Product();
        product.setName(code);
        product.setCode(code);
        product.setPrice(1);
        product.setFoodType(FoodType.FOOD);
        product.setAmount(1);
        return product;
    }
}
