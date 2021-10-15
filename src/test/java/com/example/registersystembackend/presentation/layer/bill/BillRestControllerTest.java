package com.example.registersystembackend.presentation.layer.bill;

import com.example.registersystembackend.business.logic.layer.bill.BillService;
import com.example.registersystembackend.business.logic.layer.product.ProductService;
import com.example.registersystembackend.data.access.layer.bill.Bill;
import com.example.registersystembackend.data.access.layer.bill.Position;
import com.example.registersystembackend.data.access.layer.product.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillRestControllerTest {
    private final BillMapper billMapper = BillMapper.INSTANCE;

    @Mock
    private BillService billService;
    @Mock
    private ProductService productService;
    @InjectMocks
    private BillRestController billRestController;

    @Test
    void save() {
        final Product product = new Product();
        product.setCode("code");
        final PositionDto positionDto = new PositionDto();
        positionDto.setProductId(product.getId());
        final BillDto billDto = new BillDto();
        billDto.setPositions(Set.of(positionDto));
        final Bill bill = billMapper.dtoToDocument(billDto);
        final Position position = new Position();
        position.setProduct(product);
        bill.setPositions(Set.of(position));
        when(productService.getProductByIds(Set.of(product.getId()))).thenReturn(Set.of(product));

        final ResponseEntity<Void> responseEntity = billRestController.save(billDto);

        verify(billService).save(argThat(b -> b.getPositions().size() == 1 && b.getPositions().iterator().next().getProduct().equals(product)));
        assertEquals(200, responseEntity.getStatusCodeValue());
    }
}
