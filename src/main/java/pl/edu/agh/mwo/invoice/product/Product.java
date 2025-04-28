package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

public abstract class Product {
    private final String name;
    private final BigDecimal price;
    private final BigDecimal taxPercent;

    protected Product(String name, BigDecimal price, BigDecimal taxPercent) {
        if (name == null
                || name.isEmpty()
                || price == null
                || taxPercent == null
                || taxPercent.compareTo(BigDecimal.ZERO) < 0
                || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.price = price;
        this.taxPercent = taxPercent;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    public BigDecimal getPriceWithTax() {
        return price.multiply(BigDecimal.ONE.add(taxPercent));
    }
}
