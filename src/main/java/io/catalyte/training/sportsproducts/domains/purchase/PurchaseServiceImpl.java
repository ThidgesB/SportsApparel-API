package io.catalyte.training.sportsproducts.domains.purchase;

import io.catalyte.training.sportsproducts.domains.product.Product;
import io.catalyte.training.sportsproducts.domains.product.ProductService;
import io.catalyte.training.sportsproducts.exceptions.ServerError;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.time.LocalDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PurchaseServiceImpl implements PurchaseService {

  private final Logger logger = LogManager.getLogger(PurchaseServiceImpl.class);

  PurchaseRepository purchaseRepository;
  ProductService productService;
  LineItemRepository lineItemRepository;

  @Autowired
  public PurchaseServiceImpl(PurchaseRepository purchaseRepository, ProductService productService,
      LineItemRepository lineItemRepository) {
    this.purchaseRepository = purchaseRepository;
    this.productService = productService;
    this.lineItemRepository = lineItemRepository;
  }

  /**
   * Retrieves all purchases from the database
   *
   * @return
   */
  public List<Purchase> findPurchasesByEmail(String email) {
    List<Purchase> purchases = purchaseRepository.findByBillingAddressEmail(email);
    return purchases;
  }

  /**
   * Persists a purchase to the database
   *
   * @param newPurchase - the purchase to persist
   * @return the persisted purchase with ids
   */
  public Purchase savePurchase(Purchase newPurchase) {

    List<String> errors = new ArrayList<>(validateCreditCard(newPurchase.getCreditCard()));

    Set<LineItem> lineItems = newPurchase.getProducts();
    List<Map<String, Object>> inactiveProducts = new ArrayList<>();
    for (LineItem lineItem : lineItems) {
      Product product = lineItem.getProduct();
      if (product != null && !product.getActive()) {
        Map<String, Object> inactiveProduct = new HashMap<>();
        inactiveProduct.put("id", product.getId());
        inactiveProduct.put("name", product.getName());
        inactiveProducts.add(inactiveProduct);
      }
    }
    if (!inactiveProducts.isEmpty()) {
      // Throw an exception or return an error response indicating inactive products
      // For simplicity, let's assume you return an error response directly
      Map<String, Object> response = new HashMap<>();
      response.put("message", "Some products are inactive and cannot be purchased.");
      response.put("inactiveProducts", inactiveProducts);
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, response.toString());
    }
    try {
      purchaseRepository.save(newPurchase);
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }

    if (!errors.isEmpty()) {
      String errorMessage = String.join(" ", errors);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
    }

    // after the purchase is persisted and has an id, we need to handle its lineitems and persist them as well
    handleLineItems(newPurchase);

    return newPurchase;
  }

  /**
   * Validates the information in a CreditCard object being saved to the database.
   *
   * @param creditCard - the creditCard to validate
   * @return errors - the errors found while validating.
   */
  public List<String> validateCreditCard(CreditCard creditCard) {
    List<String> errors = new ArrayList<>();

    if (creditCard == null) {
      errors.add("Credit card information is missing.");
    } else {
      if (creditCard.getCardNumber() == null || creditCard.getCardNumber().length() != 16) {
        errors.add("Credit card number must have 16 digits.");
      }
      if (creditCard.getCvv() == null || creditCard.getCvv().length() != 3) {
        errors.add("CVV must have 3 digits.");
      }
      if (creditCard.getExpiration() == null) {
        errors.add("Expiration date is missing.");
      } else {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth yearMonth = YearMonth.parse(creditCard.getExpiration(), inputFormatter);
        int lastDayOfMonth = yearMonth.lengthOfMonth();
        String dateString = creditCard.getExpiration() + "/" + lastDayOfMonth;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy/dd");
        LocalDate expirationDate = LocalDate.parse(dateString, formatter);
        LocalDate currentDate = LocalDate.now();
        if (expirationDate.isBefore(currentDate)) {
          errors.add("Credit card is expired.");
        }
      }
      if (creditCard.getCardholder() == null || creditCard.getCardholder().isEmpty()) {
        errors.add("Cardholder name is missing.");
      }
    }

    return errors;
  }


  /**
   * This helper method retrieves product information for each line item and persists it
   *
   * @param purchase - the purchase object to handle lineitems for
   */
  private void handleLineItems(Purchase purchase) {
    Set<LineItem> itemsList = purchase.getProducts();

    if (itemsList != null) {
      itemsList.forEach(lineItem -> {

        // retrieve full product information from the database
        Product product = productService.getProductById(lineItem.getProduct().getId());

        // set the product info into the lineitem
        if (product != null) {
          lineItem.setProduct(product);
        }

        // set the purchase on the line item
        lineItem.setPurchase(purchase);

        // persist the populated lineitem
        try {
          lineItemRepository.save(lineItem);
        } catch (DataAccessException e) {
          logger.error(e.getMessage());
          throw new ServerError(e.getMessage());
        }
      });
    }
  }
}

