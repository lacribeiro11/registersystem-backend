package com.example.registersystembackend.presentation.layer.product;

import com.example.registersystembackend.data.access.layer.product.FoodType;
import com.example.registersystembackend.data.access.layer.product.Product;
import com.example.registersystembackend.data.access.layer.product.ProductRepository;
import com.example.registersystembackend.presentation.layer.JsonMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        @Test
        void happyPath() throws Exception {
            final ProductDto product = persistProductDto("code");

            final ResultActions resultActions = getProduct(product.getId().toString());

            resultActions.andExpect(status().isOk());

            final ProductDto productDto = JsonMapper.resultToObject(resultActions, PRODUCT_DTO_TYPE_REFERENCE);
            assertEquals(product.getCode(), productDto.getCode());
        }

        @Test
        void notFound() throws Exception {
            UUID id = UUID.randomUUID();

            getProduct(id.toString())
                    .andExpect(status().isNotFound())
                    .andExpect(content().json(getErrorMessage(String.format("There is no product with this id: %s", id))));
        }

        @Test
        void badId() throws Exception {
            getProduct("badId").andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @NullAndEmptySource
        void emptyOrNull(String id) throws Exception {

            getProduct(id)
                    .andExpect(status().isMethodNotAllowed());
        }
    }

    @Nested
    class GetAllProducts {
        private final TypeReference<Map> PRODUCT_DTO_PAGE_REFERENCE = new TypeReference<>() {
        };

        @Test
        void happyPath() throws Exception {
            final List<ProductDto> expectedProductDtos = persistDefaultProductDtos();

            final ResultActions resultActions = getAllProducts(null, null, null, null);

            resultActions.andExpect(status().isOk());
            assertEquals(expectedProductDtos.subList(0, 10), getProductDtos(resultActions));
        }

        @Test
        void databaseIsEmpty() throws Exception {
            final ResultActions resultActions = getAllProducts(null, null, null, null);

            resultActions.andExpect(status().isOk());
            assertEquals(List.of(), getProductDtos(resultActions));
        }

        @Test
        void allNonDeletedProducts() throws Exception {
            final ProductDto productDto1 = persistProductDto("code1");
            final ProductDto deleteDto2 = persistProductDto("delete2");
            final ProductDto deleteDto3 = persistProductDto("delete3");

            productRepository.findById(deleteDto2.getId()).ifPresent(p -> {
                p.setDeleted(true);
                productRepository.save(p);
            });
            productRepository.findById(deleteDto3.getId()).ifPresent(p -> {
                p.setDeleted(true);
                productRepository.save(p);
            });
            final List<ProductDto> expectedProductDtos = List.of(productDto1);

            final ResultActions resultActions = getAllProducts(null, null, null, null);

            resultActions.andExpect(status().isOk());
            assertEquals(expectedProductDtos, getProductDtos(resultActions));
        }

        @Test
        void nameIsEmpty() throws Exception {
            final List<ProductDto> expectedProductDtos = persistDefaultProductDtos();

            final ResultActions resultActions = getAllProducts("", null, null, null);

            resultActions.andExpect(status().isOk());
            assertEquals(expectedProductDtos.subList(0, 10), getProductDtos(resultActions));
        }

        @Test
        void productWithThisNameDoesNotExist() throws Exception {
            persistDefaultProductDtos();

            final ResultActions resultActions = getAllProducts("does not exist", null, null, null);

            resultActions.andExpect(status().isOk());
            assertEquals(List.of(), getProductDtos(resultActions));
        }

        @Test
        void findOne() throws Exception {
            final List<ProductDto> expectedProductDtos = persistDefaultProductDtos();

            final ResultActions resultActions = getAllProducts("name01", null, null, null);

            resultActions.andExpect(status().isOk());
            final List<ProductDto> actualProductDtos = getProductDtos(resultActions);
            assertEquals(1, actualProductDtos.size());
            assertEquals(expectedProductDtos.get(0), actualProductDtos.get(0));
        }

        @Test
        void secondPage() throws Exception {
            final List<ProductDto> expectedProductDtos = persistDefaultProductDtos();

            final ResultActions resultActions = getAllProducts(null, 1, null, null);

            resultActions.andExpect(status().isOk());
            assertEquals(expectedProductDtos.subList(10, 20), getProductDtos(resultActions));
        }

        @Test
        void pageSize5() throws Exception {
            final List<ProductDto> expectedProductDtos = persistDefaultProductDtos();

            final ResultActions resultActions = getAllProducts(null, null, 5, null);

            resultActions.andExpect(status().isOk());
            assertEquals(expectedProductDtos.subList(0, 5), getProductDtos(resultActions));
        }

        @Test
        void sorting() throws Exception {
            final List<ProductDto> expectedProductDtos = persistDefaultProductDtos().subList(10, 20);
            Collections.reverse(expectedProductDtos);

            final ResultActions resultActions = getAllProducts(null, null, null, List.of("name,desc"));

            resultActions.andExpect(status().isOk());
            assertEquals(expectedProductDtos, getProductDtos(resultActions));
        }

        private List<ProductDto> getProductDtos(ResultActions resultActions) throws JsonProcessingException, UnsupportedEncodingException {
            final Map map = JsonMapper.resultToObject(resultActions, PRODUCT_DTO_PAGE_REFERENCE);
            final List content = (List) map.get("content");
            List<ProductDto> products = new ArrayList<>();
            for (Object row : content) {
                LinkedHashMap<String, Object> item = (LinkedHashMap<String, Object>) row;
                final ProductDto productDto = newProductDto(item.get("name").toString(),
                        item.get("code").toString(),
                        FoodType.valueOf(item.get("foodType").toString()),
                        Integer.parseInt(item.get("amount").toString()),
                        Double.parseDouble(item.get("price").toString()));
                productDto.setId(UUID.fromString(item.get("id").toString()));
                products.add(productDto);
            }
            return products;
        }

        private List<ProductDto> persistDefaultProductDtos() {
            Set<Product> newProducts = new HashSet<>();
            List<ProductDto> productDtos = new ArrayList<>();
            final ProductMapper instance = ProductMapper.INSTANCE;
            for (int i = 1; i <= 20; i++) {
                String name = String.format("name%02d", i);
                String code = String.format("code%02d", i);
                FoodType foodType = FoodType.values()[i % 3];
                final ProductDto productDto = newProductDto(name, code, foodType, i, i);
                final Product product = instance.dtoToDocument(productDto);
                productDto.setId(product.getId());
                productDtos.add(productDto);
                newProducts.add(product);
            }
            productRepository.saveAll(newProducts);
            return productDtos;
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
            final ProductDto productDto = persistProductDto("code");
            productDto.setCode("code1");

            final ResultActions resultActions = saveProduct(JsonMapper.objectToString(productDto));

            resultActions.andExpect(status().isOk());
        }

        @Test
        void duplicateCode() throws Exception {
            persistProductDto("code");
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
            final UUID id = persistProductDto("code").getId();

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
            final String code = persistProductDto("code").getCode();

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

    private ProductDto persistProductDto(String code) {
        final Product product = ProductMapper.INSTANCE.dtoToDocument(newProductDto(code));
        return ProductMapper.INSTANCE.documentToDto(productRepository.save(product));
    }

    private ProductDto newProductDto(String code) {
        return newProductDto(code, code, FoodType.FOOD, 1, 1);
    }

    private ProductDto newProductDto(String name, String code, FoodType foodType, int amount, double price) {
        final ProductDto product = new ProductDto();
        product.setName(name);
        product.setCode(code);
        product.setFoodType(foodType);
        product.setAmount(amount);
        product.setPrice(price);
        return product;
    }

    private ResultActions getProduct(String id) throws Exception {
        return mockMvc.perform(get(PRODUCT_URL + "/{id}", id));
    }

    private ResultActions saveProduct(String content) throws Exception {
        return mockMvc.perform(post(PRODUCT_URL)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions getAllProducts(String name, Integer page, Integer size, List<String> sort) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get(PRODUCT_URL + "/all");
        if (name != null) {
            requestBuilder.param("name", name);
        }
        if (page != null) {
            requestBuilder.param("page", page.toString());
        }
        if (size != null) {
            requestBuilder.param("size", size.toString());
        }
        if (!CollectionUtils.isEmpty(sort)) {
            sort.forEach(s -> requestBuilder.param("sort", s));
        }
        return mockMvc.perform(requestBuilder);
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
