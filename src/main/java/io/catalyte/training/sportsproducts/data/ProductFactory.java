package io.catalyte.training.sportsproducts.data;

import io.catalyte.training.sportsproducts.domains.product.Product;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * This class provides tools for random generation of products.
 */
public class ProductFactory {

  private static final String[] colors = {
      "#000000", // white
      "#ffffff", // black
      "#39add1", // light blue
      "#3079ab", // dark blue
      "#c25975", // mauve
      "#e15258", // red
      "#f9845b", // orange
      "#838cc7", // lavender
      "#7d669e", // purple
      "#53bbb4", // aqua
      "#51b46d", // green
      "#e0ab18", // mustard
      "#637a91", // dark gray
      "#f092b0", // pink
      "#b7c0c7"  // light gray
  };
  private static final String[] demographics = {
      "Men",
      "Women",
      "Kids"
  };
  private static final String[] categories = {
      "Golf",
      "Soccer",
      "Basketball",
      "Hockey",
      "Football",
      "Running",
      "Baseball",
      "Skateboarding",
      "Boxing",
      "Weightlifting"
  };
  private static final String[] adjectives = {
      "Lightweight",
      "Slim",
      "Shock Absorbing",
      "Exotic",
      "Elastic",
      "Fashionable",
      "Trendy",
      "Next Gen",
      "Colorful",
      "Comfortable",
      "Water Resistant",
      "Wicking",
      "Heavy Duty"
  };
  private static final String[] types = {
      "Pant",
      "Short",
      "Shoe",
      "Glove",
      "Jacket",
      "Tank Top",
      "Sock",
      "Sunglasses",
      "Hat",
      "Helmet",
      "Belt",
      "Visor",
      "Shin Guard",
      "Elbow Pad",
      "Headband",
      "Wristband",
      "Hoodie",
      "Flip Flop",
      "Pool Noodle"
  };

  /**
   * Made changes to this method in kn-25-missing-product branch. Returns a random string from a
   * list of strings.
   *
   * @return - a string
   */
  public static String getRandomField(String[] field) {
    Random randomGenerator = new Random();
    return field[randomGenerator.nextInt(field.length)];
  }

  /**
   * Generates a random product offering id.
   *
   * @return - a product offering id
   */
  public static String getRandomProductId() {
    return "po-" + RandomStringUtils.random(7, false, true);
  }

  /**
   * Generates a random style code.
   *
   * @return - a style code string
   */
  public static String getStyleCode() {
    return "sc" + RandomStringUtils.random(5, false, true);
  }

  /**
   * Finds a random date between two date bounds.
   *
   * @param startInclusive - the beginning bound
   * @param endExclusive   - the ending bound
   * @return - a random date as a LocalDate
   */
  private static LocalDate between(LocalDate startInclusive, LocalDate endExclusive) {
    long startEpochDay = startInclusive.toEpochDay();
    long endEpochDay = endExclusive.toEpochDay();
    long randomDay = ThreadLocalRandom
        .current()
        .nextLong(startEpochDay, endEpochDay);

    return LocalDate.ofEpochDay(randomDay);
  }

  private static boolean getRandomBoolean() {
    return Math.random() < 0.5;
  }

  private static BigDecimal getRandomPrice() {
    double min = 0.01;
    double max = 500.00;
    double randomValue = min + Math.random() * (max - min);
    return BigDecimal.valueOf(randomValue).setScale(2, RoundingMode.HALF_UP);
  }

  private static int getRandomQuantity() {
    return new Random().nextInt(2501);
  }

  private static String getRandomDescription(String category, String demographic) {
    String adjective = getRandomField(adjectives);
    return category + " " + demographic + " " + adjective;
  }

  private static String getRandomName(String adjective, String category, String type) {
    return adjective + " " + category + " " + type;
  }

  private static String getRandomMaterial() {
    String[] materials = {"Leather", "Suede", "Synthetic", "Cotton", "Polyester"};
    return getRandomField(materials);
  }


  /**
   * Generates a number of random products based on input.
   *
   * @param numberOfProducts - the number of random products to generate
   * @return - a list of random products
   */
  public List<Product> generateRandomProducts(Integer numberOfProducts) {

    List<Product> productList = new ArrayList<>();

    for (int i = 0; i < numberOfProducts; i++) {
      productList.add(createRandomProduct());
    }

    return productList;
  }

  /**
   * Uses random generators to build a product.
   *
   * @return - a randomly generated product
   */
  public Product createRandomProduct() {
    Product product = new Product();
    String demographic = ProductFactory.getRandomField(demographics);
    String category = ProductFactory.getRandomField(categories);
    String type = ProductFactory.getRandomField(types);

    product.setCategory(category);
    product.setType(type);
    product.setDemographic(demographic);
    product.setGlobalProductCode(ProductFactory.getRandomProductId());
    product.setStyleNumber(ProductFactory.getStyleCode());
    product.setDescription(getRandomDescription(category, demographic));
    product.setName(getRandomName(getRandomField(adjectives), category, type));
    product.setPrice(getRandomPrice());
    product.setPrimaryColorCode(getRandomField(colors));
    product.setSecondaryColorCode(getRandomField(colors));
    product.setActive(getRandomBoolean());
    product.setMaterial(getRandomMaterial());
    product.setBrand(getRandomField(new String[]{"Under Armour", "Nike", "Adidas"}));
    product.setDescription(getRandomDescription(category, demographic));
    product.setReleaseDate(String.valueOf(between(LocalDate.of(2019, 1, 1), LocalDate.now())));
    product.setQuantity((long) getRandomQuantity());

    // Set the default price for the product
    //product.setPrice(BigDecimal.valueOf(0.00));

    return product;
  }
}
