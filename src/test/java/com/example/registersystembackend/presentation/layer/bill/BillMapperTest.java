package com.example.registersystembackend.presentation.layer.bill;


import com.example.registersystembackend.data.access.layer.bill.Bill;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class BillMapperTest {
    private final BillMapper billMapper = BillMapper.INSTANCE;

    @Test
    void mapDtoToDocument() {
        final PositionDto positionDto = new PositionDto();
        positionDto.setProductId(UUID.randomUUID());
        final BillDto billDto = new BillDto();
        billDto.setPositions(Set.of(positionDto));

        final Bill bill = billMapper.dtoToDocument(billDto);

        assertNotNull(bill.getId());
        assertNotNull(bill.getTimestamp());
        assertTrue(bill.getPositions().isEmpty(), "The positions aren't copied");
    }
}
