package com.example.registersystembackend.presentation.layer.product;

import com.example.registersystembackend.business.logic.layer.product.ProductService;
import com.example.registersystembackend.data.access.layer.product.FoodType;
import com.example.registersystembackend.data.access.layer.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRestControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductRestController productRestController;
    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setName("name");
        product.setCode("code");
        product.setFoodType(FoodType.FOOD);
        product.setAmount(1);
        productDto = ProductMapper.INSTANCE.documentToDto(product);
    }

    @Test
    void getProduct() {
        when(productService.getProduct(product.getId())).thenReturn(product);

        ResponseEntity<ProductDto> responseEntity = productRestController.getProduct(product.getId());

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(productDto, responseEntity.getBody());
    }

    @Test
    void saveProduct() {

        ResponseEntity<Void> responseEntity = productRestController.saveProduct(productDto);

        verify(productService).saveProduct(product);
        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    void getAllProducts() {
        when(productService.getAllProducts(null, null)).thenReturn(new PageImpl<>(List.of(product)));

        ResponseEntity<Page<ProductDto>> responseEntity = productRestController.getAllProducts(null, null);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(List.of(productDto), Objects.requireNonNull(responseEntity.getBody()).getContent());
    }

    @Test
    void deleteProduct() {

        ResponseEntity<Void> responseEntity = productRestController.deleteProduct(productDto.getId());

        verify(productService).deleteProduct(product.getId());
        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    void getProductByCode() {
        when(productService.getProductByCode(product.getCode())).thenReturn(product);

        ResponseEntity<ProductDto> responseEntity = productRestController.getProductByCode(productDto.getCode());

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(productDto, responseEntity.getBody());
    }
}
