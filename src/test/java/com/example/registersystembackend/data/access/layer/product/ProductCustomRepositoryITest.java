package com.example.registersystembackend.data.access.layer.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
class ProductCustomRepositoryITest {
    private static final PageRequest PAGE_REQUEST = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
    @Autowired
    private ProductRepository productRepository;

    private List<Product> expectedProducts;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        expectedProducts = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            expectedProducts.add(newProduct(i));
        }
        final Product deletedProduct = newProduct(101);
        deletedProduct.setName("deleted");
        deletedProduct.setDeleted(true);
        productRepository.saveAll(expectedProducts);
        productRepository.save(deletedProduct);
    }

    @Nested
    class FindProductsByName {

        @ParameterizedTest
        @NullAndEmptySource
        void nameIsNullOrEmpty(String name) {
            Page<Product> actualProducts = productRepository.findProductsByName(name, PAGE_REQUEST);

            assertEquals(expectedProducts.subList(0, 10), actualProducts.getContent());
        }

        @Test
        void findOne() {
            Page<Product> actualProducts = productRepository.findProductsByName("name001", PAGE_REQUEST);

            assertEquals(1, actualProducts.getContent().size());
            assertEquals(expectedProducts.get(0), actualProducts.getContent().get(0));
        }

        @Test
        void nameContains() {
            Page<Product> actualProducts = productRepository.findProductsByName("ame", PAGE_REQUEST);

            assertEquals(expectedProducts.subList(0, 10), actualProducts.getContent());
        }

        @Test
        void caseInsensitive() {
            Page<Product> actualProducts = productRepository.findProductsByName("ME", PAGE_REQUEST);

            assertEquals(expectedProducts.subList(0, 10), actualProducts.getContent());
        }

        @Test
        void foundNone() {
            Page<Product> actualProducts = productRepository.findProductsByName("deleted", PAGE_REQUEST);

            assertEquals(List.of(), actualProducts.getContent());
        }
    }

    @Nested
    class FindProductsByNamePageable {

        @Test
        void getSecondPage() {
            final PageRequest request = PageRequest.of(1, 10);
            Page<Product> actualProducts = productRepository.findProductsByName(null, request);

            assertEquals(expectedProducts.subList(10, 20), actualProducts.getContent());
        }

        @Test
        void pageSize5() {
            final PageRequest request = PageRequest.of(0, 5);
            Page<Product> actualProducts = productRepository.findProductsByName(null, request);

            assertEquals(expectedProducts.subList(0, 5), actualProducts.getContent());
        }

        @ParameterizedTest
        @ValueSource(strings = {"name", "code", "amount", "price"})
        void sortDescending(String field) {
            PageRequest request = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, field));

            Page<Product> actualProducts = productRepository.findProductsByName(null, request);

            assertEquals(reverseProducts(), actualProducts.getContent());
        }

        @Test
        void sortDescendingFoodType() {
            PageRequest request = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "foodType"));

            Page<Product> actualProducts = productRepository.findProductsByName(null, request);

            assertThat(actualProducts.getContent())
                    .hasSize(10)
                    .allMatch(p -> p.getFoodType() == FoodType.SOFT_DRINK);
        }

        private List<Product> reverseProducts() {
            List<Product> reverseProducts = new ArrayList<>(expectedProducts.subList(90, 100));
            Collections.reverse(reverseProducts);
            return reverseProducts;
        }
    }

    private static Product newProduct(int i) {
        final Product product = new Product();
        product.setName(String.format("name%03d", i));
        product.setCode(String.format("code%03d", i));
        product.setPrice(i);
        product.setFoodType(FoodType.values()[i % 3]);
        product.setAmount(i);
        return product;
    }
}
