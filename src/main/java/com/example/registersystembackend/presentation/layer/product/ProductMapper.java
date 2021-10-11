package com.example.registersystembackend.presentation.layer.product;

import com.example.registersystembackend.data.access.layer.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(imports = UUID.class)
interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductDto documentToDto(Product product);

    @Mapping(source = "id", target = "id", defaultExpression = "java(UUID.randomUUID())")
    Product dtoToDocument(ProductDto productDto);
}
