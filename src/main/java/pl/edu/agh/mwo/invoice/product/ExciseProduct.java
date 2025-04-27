package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

public abstract class ExciseProduct extends Product {
    private static final BigDecimal EXCISE_TAX = new BigDecimal("5.56");

    public ExciseProduct(String name, BigDecimal price) {
        super(name, price, BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getPriceWithTax() {
        BigDecimal priceWithVat = getPrice().multiply(BigDecimal.ONE.add(new BigDecimal("0.23")));
        return priceWithVat.add(EXCISE_TAX);
    }
}
