package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.mwo.invoice.product.*;

public class InvoiceTest {
    private Invoice invoice;

    @Before
    public void createEmptyInvoiceForTheTest() {
        invoice = new Invoice();
    }

    @Test
    public void testEmptyInvoiceHasEmptySubtotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTaxAmount() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceSubtotalWithTwoDifferentProducts() {
        Product onions = new TaxFreeProduct("Warzywa", new BigDecimal("10"));
        Product apples = new TaxFreeProduct("Owoce", new BigDecimal("10"));
        invoice.addProduct(onions);
        invoice.addProduct(apples);
        Assert.assertThat(new BigDecimal("20"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceSubtotalWithManySameProducts() {
        Product onions = new TaxFreeProduct("Warzywa", BigDecimal.valueOf(10));
        invoice.addProduct(onions, 100);
        Assert.assertThat(new BigDecimal("1000"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasTheSameSubtotalAndTotalIfTaxIsZero() {
        Product taxFreeProduct = new TaxFreeProduct("Warzywa", new BigDecimal("199.99"));
        invoice.addProduct(taxFreeProduct);
        Assert.assertThat(invoice.getNetTotal(), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasProperSubtotalForManyProducts() {
        invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")));
        invoice.addProduct(new DairyProduct("Maslanka", new BigDecimal("100")));
        invoice.addProduct(new OtherProduct("Wino", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("310"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasProperTaxValueForManyProduct() {
        // tax: 0
        invoice.addProduct(new TaxFreeProduct("Pampersy", new BigDecimal("200")));
        // tax: 8
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("100")));
        // tax: 2.30
        invoice.addProduct(new OtherProduct("Piwko", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("10.30"), Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testInvoiceHasProperTotalValueForManyProduct() {
        // price with tax: 200
        invoice.addProduct(new TaxFreeProduct("Maskotki", new BigDecimal("200")));
        // price with tax: 108
        invoice.addProduct(new DairyProduct("Maslo", new BigDecimal("100")));
        // price with tax: 12.30
        invoice.addProduct(new OtherProduct("Chipsy", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("320.30"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasPropoerSubtotalWithQuantityMoreThanOne() {
        // 2x kubek - price: 10
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        // 3x kozi serek - price: 30
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        // 1000x pinezka - price: 10
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("50"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasPropoerTotalWithQuantityMoreThanOne() {
        // 2x chleb - price with tax: 10
        invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
        // 3x chedar - price with tax: 32.40
        invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
        // 1000x pinezka - price with tax: 12.30
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("54.70"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithZeroQuantity() {
        invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithNegativeQuantity() {
        invoice.addProduct(new DairyProduct("Zsiadle mleko", new BigDecimal("5.55")), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddingNullProduct() {
        invoice.addProduct(null);
    }

    @Test
    public void testInvoiceNumberIsGeneratedCorrectly() {
        Invoice.resetInvoiceCounter();
        Invoice invoice1 = new Invoice();
        Invoice invoice2 = new Invoice();
        Assert.assertEquals("FV-1", invoice1.getInvoiceNumber());
        Assert.assertEquals("FV-2", invoice2.getInvoiceNumber());
    }

    @Test
    public void testInvoiceCounterResetsProperly() {
        Invoice.resetInvoiceCounter();
        Invoice invoice1 = new Invoice();
        Assert.assertEquals("FV-1", invoice1.getInvoiceNumber());

        Invoice.resetInvoiceCounter();
        Invoice invoice2 = new Invoice();
        Assert.assertEquals("FV-1", invoice2.getInvoiceNumber());
    }

    @Test
    public void testInvoiceDetailsContent() {
        Product apple = new TaxFreeProduct("Jablko", new BigDecimal("2.50"));
        Product milk = new DairyProduct("Mleko", new BigDecimal("3.00"));

        invoice.addProduct(apple, 2); // 2 x 2.50
        invoice.addProduct(milk, 1);  // 1 x 3.00

        String details = invoice.getInvoiceDetails();

        Assert.assertTrue(details.contains("Jablko"));
        Assert.assertTrue(details.contains("Mleko"));
        Assert.assertTrue(details.contains("Liczba sztuk: 2"));
        Assert.assertTrue(details.contains("Liczba sztuk: 1"));
        Assert.assertTrue(details.contains("Liczba pozycji: 2"));
        Assert.assertTrue(details.contains(invoice.getInvoiceNumber()));
    }

    @Test
    public void testAddingSameProductTwiceSumsTheQuantities() {
        Product cheese = new DairyProduct("Ser", new BigDecimal("10"));

        invoice.addProduct(cheese, 2);
        invoice.addProduct(cheese, 3);

        Assert.assertThat(new BigDecimal("50.00"), Matchers.comparesEqualTo(invoice.getNetTotal()));
        Assert.assertTrue(invoice.getInvoiceDetails().contains("Liczba sztuk: 5"));
        Assert.assertTrue(invoice.getInvoiceDetails().contains("Ser"));
        Assert.assertTrue(invoice.getInvoiceDetails().contains("Liczba pozycji: 1"));
    }

    @Test
    public void testExciseProductsIncludeExtraTax() {
        Product wine = new BottleOfWine("Czerwone wino", new BigDecimal("100"));
        Product fuel = new FuelCanister("Kanister paliwa", new BigDecimal("50"));

        invoice.addProduct(wine);
        invoice.addProduct(fuel);

        Assert.assertThat(new BigDecimal("150"), Matchers.comparesEqualTo(invoice.getNetTotal()));
        Assert.assertThat(new BigDecimal("45.62"), Matchers.comparesEqualTo(invoice.getTaxTotal()));
        Assert.assertThat(new BigDecimal("195.62"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

}
