package com.example.registersystembackend.presentation.layer.bill;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

class PositionDto {

    @NotNull
    private UUID productId;

    @Positive
    private int amount;

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PositionDto that = (PositionDto) o;
        return productId.equals(that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PositionDto.class.getSimpleName() + "[", "]")
                .add("productId=" + productId)
                .add("amount=" + amount)
                .toString();
    }
}
