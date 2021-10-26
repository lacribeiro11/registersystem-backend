package com.example.registersystembackend.presentation.layer.product;

import com.example.registersystembackend.data.access.layer.product.FoodType;
import com.example.registersystembackend.data.access.layer.product.Product;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProductMapperTest {

    private final ProductMapper productMapper = ProductMapper.INSTANCE;

    @Test
    void mapDocumentToDto() {
        final Product product = new Product();
        product.setName("name");
        product.setCode("code");
        product.setFoodType(FoodType.FOOD);
        product.setAmount(1);

        ProductDto productDto = productMapper.documentToDto(product);

        assertEquals(product.getId(), productDto.getId());
        assertEquals(product.getName(), productDto.getName());
        assertEquals(product.getCode(), productDto.getCode());
        assertEquals(product.getFoodType(), productDto.getFoodType());
        assertEquals(product.getAmount(), productDto.getAmount());
    }

    @Test
    void mapDtoToDocumentDtoIdIsNull() {
        final ProductDto productDto = new ProductDto();
        productDto.setName("name");
        productDto.setCode("code");
        productDto.setFoodType(FoodType.FOOD);
        productDto.setAmount(1);

        Product product = productMapper.dtoToDocument(productDto);

        assertNull(productDto.getId());
        assertNotNull(product.getId());
        assertFalse(product.isDeleted());
        assertEquals(productDto.getName(), product.getName());
        assertEquals(productDto.getCode(), product.getCode());
        assertEquals(productDto.getFoodType(), product.getFoodType());
        assertEquals(productDto.getAmount(), product.getAmount());
    }

    @Test
    void mapDtoToDocumentDtoIdIsNotNull() {
        final ProductDto productDto = new ProductDto();
        productDto.setId(UUID.randomUUID());
        productDto.setName("name");
        productDto.setCode("code");
        productDto.setFoodType(FoodType.FOOD);
        productDto.setAmount(1);

        Product product = productMapper.dtoToDocument(productDto);

        assertFalse(product.isDeleted());
        assertEquals(productDto.getId(), product.getId());
        assertEquals(productDto.getName(), product.getName());
        assertEquals(productDto.getCode(), product.getCode());
        assertEquals(productDto.getFoodType(), product.getFoodType());
        assertEquals(productDto.getAmount(), product.getAmount());
    }

}
