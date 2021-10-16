package com.example.registersystembackend.business.logic.layer.bill;

import com.example.registersystembackend.data.access.layer.bill.Bill;
import com.example.registersystembackend.data.access.layer.bill.BillRepository;
import com.example.registersystembackend.data.access.layer.bill.Position;
import com.example.registersystembackend.data.access.layer.product.Product;
import com.example.registersystembackend.data.access.layer.product.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BillServiceImplTest {

    @Mock
    private BillRepository billRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ObjectMapper mapper;
    @InjectMocks
    private BillServiceImpl billService;

    private Bill expectedBill;

    @BeforeEach
    void setUp() {
        Product product1 = new Product();
        product1.setCode("code1");
        Position position1 = new Position();
        position1.setProduct(product1);

        Product product2 = new Product();
        product2.setCode("code2");
        Position position2 = new Position();
        position2.setProduct(product2);
        Set<Position> expectedPositions = Set.of(position1, position2);

        expectedBill = new Bill();
        expectedBill.setPositions(expectedPositions);
        expectedBill.setTotalPrice(1);
    }

    @Nested
    class SaveBill {

        @Test
        void save() {

            billService.save(expectedBill);

            final Set<Product> products = expectedBill.getPositions().stream().map(Position::getProduct).collect(Collectors.toSet());

            verify(billRepository).save(expectedBill);
            verify(productRepository).saveAll(products);
        }

        @Test
        void positionsAmountIsToHigh() {
            expectedBill.getPositions().forEach(p -> p.setAmount(10));

            assertThrows(IllegalArgumentException.class, () -> billService.save(expectedBill));

            verify(billRepository, times(0)).save(expectedBill);
        }
    }
}
