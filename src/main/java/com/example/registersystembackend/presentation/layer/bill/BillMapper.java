package com.example.registersystembackend.presentation.layer.bill;

import com.example.registersystembackend.data.access.layer.bill.Bill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.UUID;


@Mapper(imports = UUID.class)
interface BillMapper {
    BillMapper INSTANCE = Mappers.getMapper(BillMapper.class);

    @Mapping(target = "positions", ignore = true)
    Bill dtoToDocument(BillDto billDto);
}
