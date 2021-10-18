package com.example.registersystembackend.presentation.layer.product;

import com.example.registersystembackend.business.logic.layer.product.ProductService;
import com.example.registersystembackend.data.access.layer.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

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
    ResponseEntity<Page<ProductDto>> getAllProducts(@RequestParam(required = false) String name,
                                                    @PageableDefault
                                                    @SortDefault(sort = "name")
                                                            Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(name, pageable).map(productMapper::documentToDto));
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
