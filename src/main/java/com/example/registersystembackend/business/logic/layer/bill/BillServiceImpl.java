package com.example.registersystembackend.business.logic.layer.bill;

import com.example.registersystembackend.data.access.layer.bill.Bill;
import com.example.registersystembackend.data.access.layer.bill.BillRepository;
import com.example.registersystembackend.data.access.layer.bill.Position;
import com.example.registersystembackend.data.access.layer.product.Product;
import com.example.registersystembackend.data.access.layer.product.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
class BillServiceImpl implements BillService {
    private static final Logger LOG = LoggerFactory.getLogger(BillServiceImpl.class);

    private final BillRepository billRepository;
    private final ProductRepository productRepository;
    private final ObjectMapper mapper;

    @Autowired
    BillServiceImpl(BillRepository billRepository, ProductRepository productRepository, ObjectMapper mapper) {
        this.billRepository = billRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Transactional
    @Override
    public void save(Bill bill) {
        final Set<Position> positions = bill.getPositions();
        compareProductAmountWithPositionAmount(positions);
        billRepository.save(bill);

        final Set<Product> products = positions.stream().map(pos -> {
                    final Product product = pos.getProduct();
                    product.subtractAmount(pos.getAmount());
                    return product;
                })
                .collect(Collectors.toSet());
        productRepository.saveAll(products);
    }

    private void compareProductAmountWithPositionAmount(Set<Position> positions) {

        final Set<String> positionsWithHigherAmount = positions.stream()
                .filter(p -> p.getAmount() > p.getProduct().getAmount())
                .map(p -> {
                    LOG.error("Position has a higher amount than in stock: {}", p);
                    return String.format("The amount of %s in this position exceeds what is in stock. Actual amount %d", p.getProduct().getName(), p.getProduct().getAmount());
                })
                .collect(Collectors.toSet());

        if (!positionsWithHigherAmount.isEmpty()) {
            try {
                throw new IllegalArgumentException(mapper.writeValueAsString(positionsWithHigherAmount));
            } catch (JsonProcessingException e) {
                // this should not happen
            }
        }
    }
}
