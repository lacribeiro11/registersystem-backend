package com.example.registersystembackend.presentation.layer.product;

import com.example.registersystembackend.business.logic.layer.product.ProductService;
import com.example.registersystembackend.data.access.layer.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/product")
class ProductRestController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @Autowired
    ProductRestController(ProductService productService) {
        this.productService = productService;
        productMapper = ProductMapper.INSTANCE;
    }

    @GetMapping("/{id}")
    ResponseEntity<ProductDto> getProduct(@PathVariable UUID id) {
        final Product product = productService.getProduct(id);
        return ResponseEntity.ok(productMapper.documentToDto(product));
    }

    @PostMapping
    ResponseEntity<Void> saveProduct(@RequestBody @Valid ProductDto productDto) {
        productService.saveProduct(productMapper.dtoToDocument(productDto));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    ResponseEntity<List<ProductDto>> getAllProducts() {
        final List<ProductDto> productDtoList = productService.getAllProducts()
                .stream()
                .map(productMapper::documentToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDtoList);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/code/{code}")
    ResponseEntity<ProductDto> getProductByCode(@PathVariable String code) {
        return ResponseEntity.ok(productMapper.documentToDto(productService.getProductByCode(code)));
    }
}
