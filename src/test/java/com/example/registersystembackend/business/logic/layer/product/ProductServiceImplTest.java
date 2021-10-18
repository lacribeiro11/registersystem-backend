package com.example.registersystembackend.business.logic.layer.product;

import com.example.registersystembackend.data.access.layer.product.Product;
import com.example.registersystembackend.data.access.layer.product.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void createProduct() {
        final Product product = Mockito.mock(Product.class);

        productService.saveProduct(product);

        verify(productRepository).save(product);
    }

    @Test
    void createProductButCodeAlreadyExists() {
        final String code = "code";

        final Product existingProduct = new Product();
        existingProduct.setCode(code);
        final Product newProduct = new Product();
        newProduct.setCode(code);

        when(productRepository.findProductByIsDeletedIsFalseAndCode(code)).thenReturn(Optional.of(existingProduct));

        assertThrows(IllegalArgumentException.class, () -> productService.saveProduct(newProduct));

        verify(productRepository, times(0)).save(newProduct);
    }

    @Test
    void updateProduct() {
        final String code = "code";

        final Product existingProduct = new Product();
        existingProduct.setCode(code);
        final Product updatedProduct = new Product();
        updatedProduct.setId(existingProduct.getId());
        updatedProduct.setCode(code);

        when(productRepository.findProductByIsDeletedIsFalseAndCode(code)).thenReturn(Optional.of(existingProduct));

        productService.saveProduct(updatedProduct);

        verify(productRepository).save(updatedProduct);
    }

    @Test
    void deleteProduct() {
        final Product product = Mockito.mock(Product.class);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        productService.deleteProduct(product.getId());

        verify(product).setDeleted(true);
        verify(productRepository).save(product);
    }

    @Test
    void deleteProductButDoesNotExist() {
        final Product product = Mockito.mock(Product.class);
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> productService.deleteProduct(product.getId()));

        verify(product, times(0)).setDeleted(true);
        verify(productRepository, times(0)).save(product);
    }

    @Test
    void getAllProducts() {
        final Product product = Mockito.mock(Product.class);
        final List<Product> expectedProductList = List.of(product);
        when(productRepository.findProductsByName(null)).thenReturn(expectedProductList);

        final List<Product> actualProductList = productService.getAllProducts(null);

        assertEquals(expectedProductList, actualProductList);
    }

    @Test
    void getProduct() {
        final Product expectedProduct = Mockito.mock(Product.class);
        when(productRepository.findById(expectedProduct.getId())).thenReturn(Optional.of(expectedProduct));

        Product actualProduct = productService.getProduct(expectedProduct.getId());

        assertEquals(expectedProduct, actualProduct);
    }

    @Test
    void getProductButDoesNotExist() {
        final Product product = Mockito.mock(Product.class);
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> productService.deleteProduct(product.getId()));
    }

    @Test
    void getProductByCode() {
        final Product product = Mockito.mock(Product.class);
        when(productRepository.findProductByIsDeletedIsFalseAndCode(product.getCode())).thenReturn(Optional.of(product));

        Product actualProduct = productService.getProductByCode(product.getCode());

        assertEquals(product, actualProduct);
    }

    @Test
    void getProductByCodeDoesNotExist() {
        final Product product = Mockito.mock(Product.class);
        when(productRepository.findProductByIsDeletedIsFalseAndCode(product.getCode())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> productService.getProductByCode(product.getCode()));
    }

    @Test
    void getProductsByIds() {
        final Product product = new Product();
        product.setCode("code");
        final Set<Product> expectedProducts = Set.of(product);
        final Set<UUID> expectedId = Set.of(product.getId());
        when(productRepository.findAllByIsDeletedIsFalseAndIdIn(expectedId)).thenReturn(expectedProducts);

        Set<Product> actualProducts = productService.getProductByIds(expectedId);

        assertEquals(expectedProducts, actualProducts);
    }

    @Test
    void getProductsByIdsButOneIdDoesNotExist() {
        final Product product = new Product();
        product.setCode("code");
        final Set<Product> expectedProducts = Set.of(product);
        final Set<UUID> expectedId = Set.of(product.getId(), UUID.randomUUID());
        when(productRepository.findAllByIsDeletedIsFalseAndIdIn(expectedId)).thenReturn(expectedProducts);

        assertThrows(NoSuchElementException.class, () -> productService.getProductByIds(expectedId));
    }
}
