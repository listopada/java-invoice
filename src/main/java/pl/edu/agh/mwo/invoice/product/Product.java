package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

public abstract class Product {
    private final String name;
    private final BigDecimal price;
    private final BigDecimal taxPercent;

    protected Product(String name, BigDecimal price, BigDecimal taxPercent) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }

        if (taxPercent == null) {
            throw new IllegalArgumentException("Tax percent cannot be null");
        }

        this.name = name;
        this.price = price;
        this.taxPercent = taxPercent;
    }

    public String getName() {
        return this.name;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public BigDecimal getTaxPercent() {
        return this.taxPercent;
    }

    public BigDecimal getPriceWithTax() {
        return this.price.multiply(this.taxPercent).add(this.price);
    }

    public BigDecimal getTaxRate() {
    }
}
