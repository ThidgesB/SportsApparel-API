package io.catalyte.training.sportsproducts.data;

import io.catalyte.training.sportsproducts.domains.product.Product;
import io.catalyte.training.sportsproducts.domains.product.ProductRepository;
import io.catalyte.training.sportsproducts.domains.purchase.BillingAddress;
import io.catalyte.training.sportsproducts.domains.purchase.Purchase;
import io.catalyte.training.sportsproducts.domains.purchase.PurchaseRepository;
import io.catalyte.training.sportsproducts.domains.user.*;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Because this class implements CommandLineRunner, the run method is executed as soon as the server
 * successfully starts and before it begins accepting requests from the outside. Here, we use this
 * as a place to run some code that generates and saves a list of random products into the
 * database.
 */
@Component
public class DemoData implements CommandLineRunner {

  private final Logger logger = LogManager.getLogger(DemoData.class);

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private PurchaseRepository purchaseRepository;

  @Autowired
  private Environment env;

  ProductFactory productFactory = new ProductFactory();

  public static final int DEFAULT_NUMBER_OF_PRODUCTS = 1000;

  @Override
  public void run(String... strings) {
    boolean loadData;

    try {
      // Retrieve the value of custom property in application.yml
      loadData = Boolean.parseBoolean(env.getProperty("products.load"));
    } catch (NumberFormatException nfe) {
      logger.error("config variable loadData could not be parsed, falling back to default");
      loadData = true;
    }

    if (loadData) {
      seedDatabase();
    }
  }

  private void seedDatabase() {
    int numberOfProducts;

    try {
      // Retrieve the value of custom property in application.yml
      numberOfProducts = Integer.parseInt(env.getProperty("products.number"));
    } catch (NumberFormatException nfe) {
      logger.error("config variable numberOfProducts could not be parsed, falling back to default");
      // If it's not a string, set it to be a default value
      numberOfProducts = DEFAULT_NUMBER_OF_PRODUCTS;
    }

    // Generate products
    List<Product> productList = productFactory.generateRandomProducts(numberOfProducts);

    // Persist them to the database
    logger.info("Loading " + numberOfProducts + " products...");
    productRepository.saveAll(productList);
    logger.info("Data load completed. You can make requests now.");

    Purchase purchase1 = new Purchase();
    BillingAddress billingAddress = new BillingAddress();
    billingAddress.setEmail("bob@ross.com");
    purchase1.setBillingAddress(billingAddress);
    purchaseRepository.save(purchase1);

    Purchase purchase2 = new Purchase();
    purchase2.setBillingAddress(billingAddress);
    purchaseRepository.save(purchase2);

    Purchase purchase3 = new Purchase();
    purchase3.setBillingAddress(billingAddress);
    purchaseRepository.save(purchase3);

    Purchase purchase4 = new Purchase();
    billingAddress.setEmail("blah");

    User user = new User("amir@amir.com", "Customer", "Amir", "Sharapov");
    userRepository.save(user);

    purchase4.setBillingAddress(billingAddress);

    purchaseRepository.save(purchase4);

  }

}
