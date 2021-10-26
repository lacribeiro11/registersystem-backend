package com.example.registersystembackend.presentation.layer.bill;


import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

class BillDto {

    @Valid
    @NotEmpty
    private Set<PositionDto> positions = new HashSet<>();

    @Positive
    @Digits(integer = 9, fraction = 2)
    private double totalPrice;

    public Set<PositionDto> getPositions() {
        return positions;
    }

    public void setPositions(Set<PositionDto> positions) {
        this.positions = positions;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BillDto.class.getSimpleName() + "[", "]")
                .add("positions=" + positions)
                .add("totalPrice=" + totalPrice)
                .toString();
    }
}
