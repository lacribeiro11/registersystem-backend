package com.example.registersystembackend.data.access.layer.bill;

import com.example.registersystembackend.data.access.layer.product.Product;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.StringJoiner;

public class Position {

    @Positive
    private int amount;

    @NotNull
    @DBRef
    private Product product;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Position position = (Position) o;
        return product.equals(position.product);
    }

    @Override
    public int hashCode() {
        return product.hashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Position.class.getSimpleName() + "[", "]")
                .add("amount=" + amount)
                .add("product=" + product)
                .toString();
    }
}
