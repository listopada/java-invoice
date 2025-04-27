package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {
    private final Map<Product, Integer> products = new HashMap<>();
    private static final AtomicInteger invoiceCounter = new AtomicInteger(1);
    private final String invoiceNumber;

    public Invoice() {
        this.invoiceNumber = generateInvoiceNumber();
    }

    private String generateInvoiceNumber() {
        return "FV-" + invoiceCounter.getAndIncrement();
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void addProduct(Product product) {
        addProduct(product, 1);
    }

    public void addProduct(Product product, Integer quantity) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException();
        }
        products.merge(product, quantity, Integer::sum);
    }

    public BigDecimal getNetTotal() {
        BigDecimal totalNet = BigDecimal.ZERO;
        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            BigDecimal quantity = BigDecimal.valueOf(entry.getValue());
            BigDecimal productNet = entry.getKey().getPrice().multiply(quantity);
            totalNet = totalNet.add(productNet);
        }
        return totalNet.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    public BigDecimal getTaxTotal() {
        BigDecimal totalNet = getNetTotal();
        BigDecimal totalGross = getGrossTotal();
        return totalGross.subtract(totalNet).setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    public BigDecimal getGrossTotal() {
        BigDecimal totalGross = BigDecimal.ZERO;
        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            BigDecimal quantity = BigDecimal.valueOf(entry.getValue());
            BigDecimal productGross = entry.getKey().getPriceWithTax().multiply(quantity);
            totalGross = totalGross.add(productGross);
        }
        return totalGross.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    public static void resetInvoiceCounter() {
        invoiceCounter.set(1);
    }

    public String getInvoiceDetails() {
        StringBuilder invoiceDetails = new StringBuilder();
        invoiceDetails.append("Numer faktury: ").append(invoiceNumber).append("\n");

        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();
            invoiceDetails.append(product.getName())
                    .append(" | Liczba sztuk: ").append(quantity)
                    .append(" | Cena: ").append(product.getPrice())
                    .append("\n");
        }

        invoiceDetails.append("Liczba pozycji: ").append(products.size()).append("\n");

        return invoiceDetails.toString();
    }
}
