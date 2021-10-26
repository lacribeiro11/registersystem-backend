package com.example.registersystembackend.presentation.layer.bill;

import com.example.registersystembackend.business.logic.layer.bill.BillService;
import com.example.registersystembackend.business.logic.layer.product.ProductService;
import com.example.registersystembackend.data.access.layer.bill.Bill;
import com.example.registersystembackend.data.access.layer.bill.Position;
import com.example.registersystembackend.data.access.layer.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/bill")
class BillRestController {

    private final BillService billService;
    private final ProductService productService;
    private final BillMapper billMapper;

    @Autowired
    BillRestController(BillService billService, ProductService productService) {
        this.billService = billService;
        this.productService = productService;
        this.billMapper = BillMapper.INSTANCE;
    }

    @PostMapping
    ResponseEntity<Void> save(@RequestBody @Valid BillDto billDto) {
        final Bill bill = billMapper.dtoToDocument(billDto);

        setPositions(billDto, bill);
        billService.save(bill);
        return ResponseEntity.ok().build();
    }

    private void setPositions(BillDto billDto, Bill bill) {
        final Set<UUID> productIds = billDto.getPositions()
                .stream()
                .map(PositionDto::getProductId)
                .collect(Collectors.toSet());

        final Set<Product> products = productService.getProductByIds(productIds);
        products.forEach(p -> {
            final Position position = createPosition(billDto, p);
            bill.getPositions().add(position);
        });
    }

    private Position createPosition(BillDto billDto, Product p) {
        final PositionDto positionDto = billDto.getPositions().stream().filter(pos -> pos.getProductId().equals(p.getId())).findFirst().orElseThrow(NoSuchFieldError::new);
        final Position position = new Position();
        position.setProduct(p);
        position.setAmount(positionDto.getAmount());
        return position;
    }
}
