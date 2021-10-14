package com.example.registersystembackend.presentation.layer.product;

import com.example.registersystembackend.data.access.layer.product.FoodType;
import com.example.registersystembackend.data.access.layer.product.Product;
import com.example.registersystembackend.data.access.layer.product.ProductRepository;
import com.example.registersystembackend.presentation.layer.JsonMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductRestControllerITest {
    private final static String PRODUCT_URL = "/product";
    private final TypeReference<ProductDto> PRODUCT_DTO_TYPE_REFERENCE = new TypeReference<>() {
    };

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Nested
    class GetProduct {
        private final TypeReference<List<ProductDto>> PRODUCT_DTO_LIST_TYPE_REFERENCE = new TypeReference<>() {
        };

        @Test
        void happyPath() throws Exception {
            final ProductDto product = persistProduct("code");

            final ResultActions resultActions = ProductRestControllerITest.this.getProduct(product.getId().toString());

            resultActions.andExpect(status().isOk());

            final ProductDto productDto = JsonMapper.resultToObject(resultActions, PRODUCT_DTO_TYPE_REFERENCE);
            assertEquals(product.getCode(), productDto.getCode());
        }

        @Test
        void notFound() throws Exception {
            UUID id = UUID.randomUUID();

            ProductRestControllerITest.this.getProduct(id.toString())
                    .andExpect(status().isNotFound())
                    .andExpect(content().json(getErrorMessage(String.format("There is no product with this id: %s", id))));
        }

        @Test
        void badId() throws Exception {
            ProductRestControllerITest.this.getProduct("badId").andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @NullAndEmptySource
        void emptyOrNull(String id) throws Exception {

            ProductRestControllerITest.this.getProduct(id)
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        void getAllProductsSorted() throws Exception {
            final ProductDto productDto1 = persistProduct("code1");
            final ProductDto productDto2 = persistProduct("code2");
            final ProductDto productDto3 = persistProduct("code3");
            final List<ProductDto> expectedProductDtos = List.of(productDto1, productDto2, productDto3);

            final ResultActions resultActions = ProductRestControllerITest.this.getAllProducts();

            resultActions.andExpect(status().isOk());
            final List<ProductDto> actualProductDtos = JsonMapper.resultToObject(resultActions, PRODUCT_DTO_LIST_TYPE_REFERENCE);
            assertEquals(expectedProductDtos, actualProductDtos);
        }

        @Test
        void getAllProductsDatabaseIsEmpty() throws Exception {
            final ResultActions resultActions = ProductRestControllerITest.this.getAllProducts();

            resultActions.andExpect(status().isOk());
            final List<ProductDto> actualProductDtos = JsonMapper.resultToObject(resultActions, PRODUCT_DTO_LIST_TYPE_REFERENCE);
            assertEquals(List.of(), actualProductDtos);
        }

        @Test
        void getAllNonDeletedProducts() throws Exception {
            final ProductDto productDto1 = persistProduct("code1");
            final ProductDto deleteDto2 = persistProduct("delete2");
            final ProductDto deleteDto3 = persistProduct("delete3");

            productRepository.findById(deleteDto2.getId()).ifPresent(p -> {
                p.setDeleted(true);
                productRepository.save(p);
            });
            productRepository.findById(deleteDto3.getId()).ifPresent(p -> {
                p.setDeleted(true);
                productRepository.save(p);
            });
            final List<ProductDto> expectedProductDtos = List.of(productDto1);

            final ResultActions resultActions = ProductRestControllerITest.this.getAllProducts();

            resultActions.andExpect(status().isOk());
            final List<ProductDto> actualProductDtos = JsonMapper.resultToObject(resultActions, PRODUCT_DTO_LIST_TYPE_REFERENCE);
            assertEquals(expectedProductDtos, actualProductDtos);
        }
    }

    @Nested
    class SaveProduct {

        @Test
        void happyPath() throws Exception {
            final ProductDto productDto = newProductDto("code");

            final ResultActions resultActions = saveProduct(JsonMapper.objectToString(productDto));

            resultActions.andExpect(status().isOk());
        }

        @Test
        void updateCode() throws Exception {
            final ProductDto productDto = persistProduct("code");
            productDto.setCode("code1");

            final ResultActions resultActions = saveProduct(JsonMapper.objectToString(productDto));

            resultActions.andExpect(status().isOk());
        }

        @Test
        void duplicateCode() throws Exception {
            persistProduct("code");
            final ProductDto productDto = newProductDto("code");

            final ResultActions resultActions = saveProduct(JsonMapper.objectToString(productDto));

            resultActions.andExpect(status().isBadRequest());
            resultActions.andExpect(content().json(getErrorMessage(String.format("The code %s already exist.", productDto.getCode()))));
        }

        @ParameterizedTest
        @ValueSource(strings = " ")
        @NullAndEmptySource
        void badName(String name) throws Exception {
            final ProductDto productDto = newProductDto("code");
            productDto.setName(name);

            final ResultActions resultActions = saveProduct(JsonMapper.objectToString(productDto));

            resultActions.andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = " ")
        @NullAndEmptySource
        void badCode(String code) throws Exception {
            final ProductDto productDto = newProductDto(code);

            final ResultActions resultActions = saveProduct(JsonMapper.objectToString(productDto));

            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void typeIsNull() throws Exception {
            final ProductDto productDto = newProductDto("code");
            productDto.setFoodType(null);
            final ResultActions resultActions = saveProduct(JsonMapper.objectToString(productDto));

            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void amountIsNegative() throws Exception {
            final ProductDto productDto = newProductDto("code");
            productDto.setAmount(-1);
            final ResultActions resultActions = saveProduct(JsonMapper.objectToString(productDto));

            resultActions.andExpect(status().isBadRequest());
        }

        @Test
        void priceIsNegative() throws Exception {
            final ProductDto productDto = newProductDto("code");
            productDto.setPrice(-1);
            final ResultActions resultActions = saveProduct(JsonMapper.objectToString(productDto));

            resultActions.andExpect(status().isBadRequest());
        }
    }

    @Nested
    class DeleteProduct {

        @Test
        void happyPath() throws Exception {
            final UUID id = persistProduct("code").getId();

            deleteProduct(id.toString()).andExpect(status().isOk());
        }

        @Test
        void notFound() throws Exception {
            final String id = UUID.randomUUID().toString();
            deleteProduct(id)
                    .andExpect(status().isNotFound())
                    .andExpect(content().json(getErrorMessage(String.format("There is no product with this id: %s", id))));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void emptyOrNullId(String id) throws Exception {
            deleteProduct(id).andExpect(status().isMethodNotAllowed());
        }

        @Test
        void badId() throws Exception {
            deleteProduct("badId").andExpect(status().isBadRequest());
        }
    }

    @Nested
    class GetProductByCode {

        @Test
        void happyPath() throws Exception {
            final String code = persistProduct("code").getCode();

            final ResultActions resultActions = getProductByCode(code).andExpect(status().isOk());

            resultActions.andExpect(status().isOk());

            final ProductDto productDto = JsonMapper.resultToObject(resultActions, PRODUCT_DTO_TYPE_REFERENCE);
            assertEquals(code, productDto.getCode());
        }

        @Test
        void notFound() throws Exception {
            final String code = UUID.randomUUID().toString();
            getProductByCode(code)
                    .andExpect(status().isNotFound())
                    .andExpect(content().json(getErrorMessage(String.format("There is no product with this code: %s", code))));
        }

        @ParameterizedTest
        @NullAndEmptySource
        void emptyOrNullId(String code) throws Exception {
            getProductByCode(code).andExpect(status().isBadRequest());
        }
    }

    private ProductDto persistProduct(String code) {
        final Product product = ProductMapper.INSTANCE.dtoToDocument(newProductDto(code));
        return ProductMapper.INSTANCE.documentToDto(productRepository.save(product));
    }

    private ProductDto newProductDto(String code) {
        final Product product = new Product();
        product.setName(code);
        product.setCode(code);
        product.setFoodType(FoodType.FOOD);
        product.setAmount(1);
        return ProductMapper.INSTANCE.documentToDto(product);
    }

    private ResultActions getProduct(String id) throws Exception {
        return mockMvc.perform(get(PRODUCT_URL + "/{id}", id));
    }

    private ResultActions saveProduct(String content) throws Exception {
        return mockMvc.perform(post(PRODUCT_URL)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions getAllProducts() throws Exception {
        return mockMvc.perform(get(PRODUCT_URL + "/all"));
    }

    private ResultActions deleteProduct(String id) throws Exception {
        return mockMvc.perform(delete(PRODUCT_URL + "/{id}", id));
    }

    private ResultActions getProductByCode(String code) throws Exception {
        return mockMvc.perform(get(PRODUCT_URL + "/code/{code}", code));
    }

    private static String getErrorMessage(String message) {
        return String.format("{'message': '%s'}", message);
    }
}
