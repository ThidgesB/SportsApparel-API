package io.catalyte.training.sportsproducts.domains.purchase;

import io.catalyte.training.sportsproducts.domains.product.Product;
import io.catalyte.training.sportsproducts.domains.product.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PurchaseServiceImplTest {

  @InjectMocks
  private PurchaseServiceImpl purchaseService;

  @Mock
  private PurchaseRepository purchaseRepository;

  @Mock
  private ProductService productService;

  @Mock
  private LineItemRepository lineItemRepository;

  @Mock
  private CreditCard validCreditCard;

  @BeforeEach
  void setup() {
    MockitoAnnotations.initMocks(this);
    validCreditCard = new CreditCard();
    validCreditCard.setCardNumber("1234567890123456");
    validCreditCard.setCvv("123");
    validCreditCard.setExpiration("12/23");
    validCreditCard.setCardholder("John Doe");
  }

  @Test
  void testValidateCreditCard_missingCreditCard() {
    List<String> errors = purchaseService.validateCreditCard(null);
    assertEquals(1, errors.size());
    assertEquals("Credit card information is missing.", errors.get(0));
  }

  @Test
  void testValidateCreditCard_invalidNumber() {
    validCreditCard.setCardNumber("123456789012345");
    List<String> errors = purchaseService.validateCreditCard(validCreditCard);
    assertEquals(1, errors.size());
    assertEquals("Credit card number must have 16 digits.", errors.get(0));
  }

  @Test
  void testValidateCreditCard_invalidCvv() {
    validCreditCard.setCvv("12");
    List<String> errors = purchaseService.validateCreditCard(validCreditCard);
    assertEquals(1, errors.size());
    assertEquals("CVV must have 3 digits.", errors.get(0));
  }

  @Test
  void testValidateCreditCard_missingExpiration() {
    validCreditCard.setExpiration(null);
    List<String> errors = purchaseService.validateCreditCard(validCreditCard);
    assertEquals(1, errors.size());
    assertEquals("Expiration date is missing.", errors.get(0));
  }

  @Test
  void testValidateCreditCard_expired() {
    validCreditCard.setExpiration("01/20");
    List<String> errors = purchaseService.validateCreditCard(validCreditCard);
    assertEquals(1, errors.size());
    assertEquals("Credit card is expired.", errors.get(0));
  }

  @Test
  void testValidateCreditCard_missingCardholder() {
    validCreditCard.setCardholder(null);
    List<String> errors = purchaseService.validateCreditCard(validCreditCard);
    assertEquals(1, errors.size());
    assertEquals("Cardholder name is missing.", errors.get(0));
  }

  @Test
  void testValidateCreditCard_valid() {
    List<String> errors = purchaseService.validateCreditCard(validCreditCard);
    assertTrue(errors.isEmpty());
  }

  @Test
  void testFindPurchasesByEmail() {
    String email = "test@example.com";
    List<Purchase> expectedPurchases = new ArrayList<>();
    when(purchaseRepository.findByBillingAddressEmail(email)).thenReturn(expectedPurchases);

    List<Purchase> actualPurchases = purchaseService.findPurchasesByEmail(email);

    assertEquals(expectedPurchases, actualPurchases);
    verify(purchaseRepository).findByBillingAddressEmail(email);
  }

  @Test
  void testFindPurchasesByEmail_WithInvalidEmail_ShouldThrowException() {
    String email = null;
    when(purchaseRepository.findByBillingAddressEmail(email)).thenThrow(new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Email not specified."));

    assertThrows(ResponseStatusException.class, () -> purchaseService.findPurchasesByEmail(email),
        "Email not specified.");
  }

  @Test
  void testSavePurchase_WithInactiveProducts_ShouldThrowException() {
    Purchase purchase = new Purchase();
    Set<LineItem> lineItems = new HashSet<>();
    LineItem lineItem1 = new LineItem();
    Product inactiveProduct = new Product();
    inactiveProduct.setId(1L);
    inactiveProduct.setName("Inactive Product");
    inactiveProduct.setActive(false);
    lineItem1.setProduct(inactiveProduct);
    lineItems.add(lineItem1);
    purchase.setProducts(lineItems);
    when(productService.getProductById(1L)).thenReturn(inactiveProduct);

    assertThrows(ResponseStatusException.class, () -> purchaseService.savePurchase(purchase));
    verify(purchaseRepository, never()).save(any(Purchase.class));
    verify(lineItemRepository, never()).save(any(LineItem.class));
  }

  @Test
  void testSavePurchase_WithValidPurchase_ShouldSavePurchaseAndLineItems() {
    Purchase purchase = new Purchase();
    Set<LineItem> lineItems = new HashSet<>();
    LineItem lineItem1 = new LineItem();
    Product product1 = new Product();
    product1.setId(1L);
    product1.setName("Product 1");
    product1.setActive(true);
    lineItem1.setProduct(product1);
    lineItems.add(lineItem1);
    purchase.setProducts(lineItems);
    purchase.setCreditCard(validCreditCard); // Set the valid credit card

    when(productService.getProductById(1L)).thenReturn(product1);

    Purchase savedPurchase = purchaseService.savePurchase(purchase);

    verify(purchaseRepository, times(1)).save(purchase);
    verify(lineItemRepository, times(1)).save(lineItem1);
    assertEquals(purchase, savedPurchase);
  }
}
