package com.flashdeal.app.domain.inventory;

public record Stock(
        Quantity total,
        Quantity reserved,
        Quantity available,
        Quantity sold) {

    public Stock {
        if (total.value() != reserved.value() + available.value() + sold.value()) {
            throw new IllegalArgumentException(
                    String.format(
                            "Invariant violated: total(%d) must equal reserved(%d) + available(%d) + sold(%d) = %d",
                            total.value(), reserved.value(), available.value(), sold.value(),
                            reserved.value() + available.value() + sold.value()));
        }
    }

    public static Stock initial(Quantity total) {
        return new Stock(total, new Quantity(0), total, new Quantity(0));
    }

    public Stock reserve(Quantity quantity) {
        if (quantity.value() > available.value()) {
            throw new IllegalArgumentException(
                    String.format("Cannot decrease by %d: only %d available", quantity.value(), available.value()));
        }

        return new Stock(
                total,
                new Quantity(reserved.value() + quantity.value()),
                new Quantity(available.value() - quantity.value()),
                sold);
    }

    public Stock confirm(Quantity quantity) {
        if (quantity.value() > reserved.value()) {
            throw new IllegalArgumentException(
                    String.format("Cannot confirm %d: only %d reserved", quantity.value(), reserved.value()));
        }

        return new Stock(
                total,
                new Quantity(reserved.value() - quantity.value()),
                available,
                new Quantity(sold.value() + quantity.value()));
    }

    public Stock release(Quantity quantity) {
        if (quantity.value() > reserved.value()) {
            throw new IllegalArgumentException(
                    String.format("Cannot release %d: only %d reserved", quantity.value(), reserved.value()));
        }

        return new Stock(
                total,
                new Quantity(reserved.value() - quantity.value()),
                new Quantity(available.value() + quantity.value()),
                sold);
    }

    public boolean outOfStock() {
        return available.value() == 0;
    }
}
