package com.example.registersystembackend.presentation.layer.bill;

import com.example.registersystembackend.data.access.layer.bill.BillRepository;
import com.example.registersystembackend.data.access.layer.product.FoodType;
import com.example.registersystembackend.data.access.layer.product.Product;
import com.example.registersystembackend.data.access.layer.product.ProductRepository;
import com.example.registersystembackend.presentation.layer.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class BillRestControllerITest {
    private final static String BILL_URL = "/bill";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BillRepository billRepository;
    private BillDto billDto;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        billRepository.deleteAll();

        final Product product1 = newProduct("code1");
        final Product product2 = newProduct("code2");
        productRepository.saveAll(Set.of(product1, product2));

        final PositionDto positionDto1 = newPositionDto(product1);
        final PositionDto positionDto2 = newPositionDto(product2);
        billDto = new BillDto();
        billDto.setTotalPrice(2);
        billDto.setPositions(Set.of(positionDto1, positionDto2));
    }

    @Nested
    class SaveBill {
        @Test
        void happyPath() throws Exception {

            saveBill(JsonMapper.objectToString(billDto))
                    .andExpect(status().isOk());

            productRepository.findAll().forEach(p -> assertEquals(0, p.getAmount()));
        }

        @Test
        void amountIsTooHigh() throws Exception {
            billDto.getPositions().forEach(p -> p.setAmount(2));
            saveBill(JsonMapper.objectToString(billDto))
                    .andExpect(status().isBadRequest());

            productRepository.findAll().forEach(p -> assertEquals(1, p.getAmount()));
            assertEquals(0, billRepository.findAll().size());
        }

        @Test
        void productIdsDontExist() throws Exception {
            billDto.getPositions().forEach(p -> p.setProductId(UUID.randomUUID()));
            saveBill(JsonMapper.objectToString(billDto))
                    .andExpect(status().isNotFound());

            productRepository.findAll().forEach(p -> assertEquals(1, p.getAmount()));
            assertEquals(0, billRepository.findAll().size());
        }
    }

    @Nested
    class BadPositionDto {

        @Test
        void productIdIsNull() throws Exception {
            billDto.getPositions().forEach(p -> p.setProductId(null));
            saveBill(JsonMapper.objectToString(billDto))
                    .andExpect(status().isBadRequest());

        }

        @Test
        void amountIsZero() throws Exception {
            billDto.getPositions().forEach(p -> p.setAmount(0));
            saveBill(JsonMapper.objectToString(billDto))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class BadBillDto {

        @ParameterizedTest
        @NullAndEmptySource
        void positionsIsEmptyOrNull(Set<PositionDto> positions) throws Exception {
            billDto.setPositions(positions);
            saveBill(JsonMapper.objectToString(billDto))
                    .andExpect(status().isBadRequest());

        }

        @Test
        void amountIsZero() throws Exception {
            billDto.setTotalPrice(0);
            saveBill(JsonMapper.objectToString(billDto))
                    .andExpect(status().isBadRequest());
        }
    }

    private static PositionDto newPositionDto(Product product1) {
        final PositionDto positionDto = new PositionDto();
        positionDto.setProductId(product1.getId());
        positionDto.setAmount(1);
        return positionDto;
    }

    private static Product newProduct(String code) {
        final Product product = new Product();
        product.setName(code);
        product.setCode(code);
        product.setPrice(1);
        product.setFoodType(FoodType.FOOD);
        product.setAmount(1);
        return product;
    }

    private ResultActions saveBill(String content) throws Exception {
        return mockMvc.perform(post(BILL_URL)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON));
    }
}
