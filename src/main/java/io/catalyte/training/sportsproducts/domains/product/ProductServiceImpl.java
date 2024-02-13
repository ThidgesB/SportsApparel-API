package io.catalyte.training.sportsproducts.domains.product;

import io.catalyte.training.sportsproducts.exceptions.ResourceNotFound;
import io.catalyte.training.sportsproducts.exceptions.ServerError;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * This class provides the implementation for the ProductService interface.
 */
@Service
public class ProductServiceImpl implements ProductService {

  private final Logger logger = LogManager.getLogger(ProductServiceImpl.class);

  ProductRepository productRepository;

  @Autowired
  public ProductServiceImpl(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  /**
   * Retrieves all products from the database, optionally making use of an example if it is passed.
   *
   * @param product - an example product to use for querying
   * @return - a list of products matching the example, or all products if no example was passed
   */
  public List<Product> getProducts(Product product) {
    try {
      return productRepository.findAll(Example.of(product));
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }
  }

  /**
   * Retrieves the product with the provided id from the database.
   *
   * @param id - the id of the product to retrieve
   * @return - the product
   */
  public Product getProductById(Long id) {
    Product product;

    try {
      product = productRepository.findById(id).orElse(null);
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }

    if (product != null) {
      return product;
    } else {
      logger.info("Get by id failed, it does not exist in the database: " + id);
      throw new ResourceNotFound("Get by id failed, it does not exist in the database: " + id);
    }
  }

  @Override
  public List<String> getUniqueCategories() {
    try {
      return productRepository.findDistinctCategories();
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }
  }

  @Override
  public List<String> getUniqueTypes() {
    try {
      return productRepository.findDistinctTypes();
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }
  }

  /**
   * Creates a new product and persists it across the database.
   *
   * @param product - the product to be persisted
   * @return - the product
   */
  public Product createProduct(Product product) {

    List<String> validationErrors = validateProduct(product);
    if (!validationErrors.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.join(", ", validationErrors));
    }

    return productRepository.save(product);
  }

  /**
   * Holds the valid types
   */
  private static final List<String> VALID_TYPES = Arrays.asList(
      "Pant", "Short", "Shoe", "Glove", "Jacket", "Tank Top", "Sock", "Sunglasses", "Hat",
      "Helmet", "Belt", "Visor", "Shin Guard", "Elbow Pad", "Headband", "Wristband",
      "Hoodie", "Flip Flop", "Pool Noodle"
  );

  /**
   * Holds the valid categories
   */
  private static final List<String> VALID_CATEGORIES = Arrays.asList(
      "Golf", "Soccer", "Basketball", "Hockey", "Football",
      "Running", "Baseball", "Skateboarding", "Boxing", "Weightlifting"
  );

  /**
   * Holds the valid demographics
   */
  private static final List<String> VALID_DEMOGRAPHICS = Arrays.asList("Men", "Women", "Kids");

  /**
   * Validates the product to ensure that all products persisted fit requirements.
   *
   * @param product - the product to be validated
   * @return - any errors in validation
   */
  public List<String> validateProduct(Product product) {
    List<String> validationErrors = new ArrayList<>();
    if (product.getName() == null || product.getName().length() < 3
        || product.getName().length() > 100) {
      validationErrors.add("Name should be between 3 and 100 characters.");
    }

    if (product.getDescription() == null) {
      validationErrors.add("Description is required.");
    } else if (product.getDescription().length() > 200) {
      validationErrors.add("Description should be at most 200 characters.");
    }

    if (!VALID_DEMOGRAPHICS.contains(product.getDemographic())) {
      validationErrors.add("Invalid demographic.");
    }
    if (!VALID_CATEGORIES.contains(product.getCategory())) {
      validationErrors.add("Invalid category.");
    }

    if (!VALID_TYPES.contains(product.getType())) {
      validationErrors.add("Invalid type.");
    }

    if (product.getReleaseDate() != null) {
      DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("MM/dd/yyyy");
      DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("MM-dd-yyyy");

      try {
        LocalDate releaseDate = LocalDate.parse(product.getReleaseDate(), formatter1);

        // Additional validation: Check if the date is after 1/1/1900
        LocalDate minDate = LocalDate.of(1900, 1, 1);
        if (releaseDate.isBefore(minDate)) {
          validationErrors.add("Release date must be after 01/01/1900.");
        } else {
          product.setReleaseDate(releaseDate.format(formatter1));
        }
      } catch (DateTimeParseException e1) {
        try {
          LocalDate releaseDate = LocalDate.parse(product.getReleaseDate(), formatter2);

          // Additional validation: Check if the date is after 1/1/1900
          LocalDate minDate = LocalDate.of(1900, 1, 1);
          if (releaseDate.isBefore(minDate)) {
            validationErrors.add("Release date must be after 1/1/1900.");
          } else {
            product.setReleaseDate(releaseDate.format(formatter2));
          }
        } catch (DateTimeParseException e2) {
          validationErrors.add(
              "Invalid release date format. Please use MM/dd/yyyy or MM-dd-yyyy format.");
        }
      }
    } else {
      validationErrors.add("Release date is required.");
    }

    if (product.getPrice() != null) {
      BigDecimal roundedPrice = product.getPrice().setScale(2, RoundingMode.DOWN);
      product.setPrice(roundedPrice);
    } else {
      validationErrors.add("Price is required.");
    }

    if (product.getImgSrc() == null) {
      validationErrors.add("imgSrc is required.");
    }

    if (product.getQuantity() == null) {
      validationErrors.add("Quantity is required.");
    }

    if (product.getBrand() == null) {
      validationErrors.add("Brand is required.");
    }

    if (product.getMaterial() == null) {
      validationErrors.add("Material is required.");
    }

    if (product.getPrimaryColorCode() == null) {
      validationErrors.add("Primary Color Code is required.");
    }
    if (product.getSecondaryColorCode() == null) {
      validationErrors.add("Secondary Color Code is required.");
    }

    if (product.getStyleNumber() == null) {
      validationErrors.add("Style Number is required.");
    }

    if (product.getGlobalProductCode() == null) {
      validationErrors.add("Global Product Code is required.");
    }

    if (product.getActive() == null) {
      validationErrors.add("Active field is required.");
    }

    return validationErrors;
  }
}