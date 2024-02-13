package io.catalyte.training.sportsproducts.domains.product;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import io.catalyte.training.sportsproducts.data.ProductFactory;
import io.catalyte.training.sportsproducts.exceptions.ResourceNotFound;
import java.math.BigDecimal;
import io.catalyte.training.sportsproducts.exceptions.ServerError;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.DataAccessException;

@RunWith(MockitoJUnitRunner.class)
@WebMvcTest(ProductServiceImpl.class)
public class ProductServiceImplTest {

  @InjectMocks
  private ProductServiceImpl productServiceImpl;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Mock
  private ProductRepository productRepository;

  Product testProduct;

  ProductFactory productFactory;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    productFactory = new ProductFactory();
    testProduct = productFactory.createRandomProduct();
    when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));
  }

  @Test
  public void getProductByIdReturnsProduct() {
    Product actual = productServiceImpl.getProductById(123L);
    assertEquals(testProduct, actual);
  }

  @Test
  public void getProductByIdThrowsErrorWhenNotFound() {
    when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFound.class, () -> productServiceImpl.getProductById(123L));
  }

  @Test
  public void testCreateProductWithValidProduct() {
    // Create a valid product
    Product validProduct = new Product();
    validProduct.setReleaseDate("07-25-1994"); // Set a valid release date
    validProduct.setName("Test Product");
    validProduct.setDescription("This is a test product");
    validProduct.setDemographic("Men");
    validProduct.setCategory("Soccer");
    validProduct.setType("Shoe");
    validProduct.setPrice(BigDecimal.valueOf(99.99));
    validProduct.setImgSrc("test_img.jpg");
    validProduct.setQuantity(10L);
    validProduct.setBrand("Nike");
    validProduct.setMaterial("Leather");
    validProduct.setPrimaryColorCode("#FFFFFF");
    validProduct.setSecondaryColorCode("#000000");
    validProduct.setStyleNumber("ABC123");
    validProduct.setGlobalProductCode("XYZ789");
    validProduct.setActive(true);

    // Mock the save method of productRepository to return the same product
    when(productRepository.save(any())).thenReturn(validProduct);

    // Call the createProduct method
    Product createdProduct = productServiceImpl.createProduct(validProduct);

    // Assert that the returned product is the same as the input product
    assertEquals(validProduct, createdProduct);
  }

  @Test
  public void testCreateProductWithInvalidProduct() {
    // Create an invalid product with missing fields
    Product invalidProduct = new Product();

    // Call the createProduct method and expect a ResponseStatusException
    assertThrows(ResponseStatusException.class, () -> productServiceImpl.createProduct(invalidProduct));
  }

  // Add more test cases to cover different validation errors

  @Test
  public void testCreateProductWithInvalidName() {
    // Create an invalid product with a short name
    Product invalidProduct = productFactory.createRandomProduct();
    invalidProduct.setName("A");

    // Call the createProduct method and expect a ResponseStatusException
    assertThrows(ResponseStatusException.class, () -> productServiceImpl.createProduct(invalidProduct));
  }

  @Test
  public void testCreateProductWithInvalidDescription() {
    // Create an invalid product with a long description
    Product invalidProduct = productFactory.createRandomProduct();
    invalidProduct.setDescription(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque sodales.");

    // Call the createProduct method and expect a ResponseStatusException
    assertThrows(ResponseStatusException.class,
        () -> productServiceImpl.createProduct(invalidProduct));
  }

  @Test
  public void testCreateProductWithInvalidReleaseDate() {
    // Create an invalid product with an invalid release date format
    Product invalidProduct = new Product();
    invalidProduct.setName("Test Product");
    invalidProduct.setDescription("This is a test product");
    invalidProduct.setDemographic("Men");
    invalidProduct.setCategory("Soccer");
    invalidProduct.setType("Shoe");
    invalidProduct.setPrice(BigDecimal.valueOf(99.99));
    invalidProduct.setImgSrc("test_img.jpg");
    invalidProduct.setQuantity(10L);
    invalidProduct.setBrand("Nike");
    invalidProduct.setMaterial("Leather");
    invalidProduct.setPrimaryColorCode("#FFFFFF");
    invalidProduct.setSecondaryColorCode("#000000");
    invalidProduct.setStyleNumber("ABC123");
    invalidProduct.setGlobalProductCode("XYZ789");
    invalidProduct.setActive(true);
    // ... (set other required fields)
    invalidProduct.setReleaseDate("2023/07/25"); // Invalid format

    // Call the createProduct method and expect a ResponseStatusException
    assertThrows(ResponseStatusException.class, () -> productServiceImpl.createProduct(invalidProduct));
  }

  @Test
  public void testCreateProductWithInvalidPrice() {
    // Create an invalid product with a price having more than two decimal places
    Product invalidProduct = productFactory.createRandomProduct();
    invalidProduct.setPrice(BigDecimal.valueOf(19.999));

    // Call the createProduct method and expect a ResponseStatusException
    assertThrows(ResponseStatusException.class, () -> productServiceImpl.createProduct(invalidProduct));
  }

  @Test
  public void testCreateProductWithInvalidQuantityFormat() {
    // Create an invalid product with a decimal quantity
    Product invalidProduct = productFactory.createRandomProduct();
    invalidProduct.setQuantity((long) 10.5);

    // Call the createProduct method and expect a ResponseStatusException
    assertThrows(ResponseStatusException.class, () -> productServiceImpl.createProduct(invalidProduct));
  }

  @Test
  public void testValidateProductWithAllFieldsNull() {
    // Create a product with all fields set to null
    Product invalidProduct = new Product();

    // Call the validateProduct method
    List<String> validationErrors = productServiceImpl.validateProduct(invalidProduct);

    // Assert that each field error is present in the validationErrors list
    assertTrue(validationErrors.contains("Name should be between 3 and 100 characters."));
    assertTrue(validationErrors.contains("Description is required."));
    assertTrue(validationErrors.contains("Invalid demographic."));
    assertTrue(validationErrors.contains("Invalid category."));
    assertTrue(validationErrors.contains("Invalid type."));
    assertTrue(validationErrors.contains("Price is required."));
    assertTrue(validationErrors.contains("imgSrc is required."));
    assertTrue(validationErrors.contains("Quantity is required."));
    assertTrue(validationErrors.contains("Brand is required."));
    assertTrue(validationErrors.contains("Material is required."));
    assertTrue(validationErrors.contains("Primary Color Code is required."));
    assertTrue(validationErrors.contains("Secondary Color Code is required."));
    assertTrue(validationErrors.contains("Style Number is required."));
    assertTrue(validationErrors.contains("Global Product Code is required."));
    assertTrue(validationErrors.contains("Active field is required."));
    assertTrue(validationErrors.contains("Release date is required."));
  }
  @Test
  public void testValidateProductWithInvalidDescriptionLength() {
    // Create a product with a description that exceeds 200 characters
    Product invalidProduct = new Product();
    invalidProduct.setDescription(
        "This is a very long description that exceeds the maximum allowed characters. This is a very long description that exceeds the maximum allowed characters. This is a very long description that exceeds the maximum allowed characters. This is a very long description that exceeds the maximum allowed characters. This is a very long description that exceeds the maximum allowed characters.");

    // Call the validateProduct method
    List<String> validationErrors = productServiceImpl.validateProduct(invalidProduct);

    // Assert that the error message for invalid description length is present in the validationErrors list
    assertTrue(validationErrors.contains("Description should be at most 200 characters."));
  }

  @Test
  public void getUniqueCategoriesReturnsListOfCategories() {
    // Arrange
    List<String> categories = new ArrayList<>();
    categories.add("Golf");
    categories.add("Soccer");
    categories.add("Basketball");
    categories.add("Hockey");
    when(productRepository.findDistinctCategories()).thenReturn(categories);

    // Act
    List<String> result = productServiceImpl.getUniqueCategories();

    // Assert
    assertEquals(categories, result);
  }

  @Test
  public void getUniqueCategories_ThrowsServerError_OnDataAccessException() {
    // Arrange
    when(productRepository.findDistinctCategories()).thenThrow(new DataAccessException("Database Error") {});

    // Act and Assert
    assertThrows(ServerError.class, () -> productServiceImpl.getUniqueCategories());
  }

  @Test
  public void getUniqueTypesReturnsListOfTypes() {
    // Arrange
    List<String> types = new ArrayList<>();
    types.add("Pant");
    types.add("Short");
    types.add("Shoe");
    types.add("Glove");
    when(productRepository.findDistinctTypes()).thenReturn(types);

    // Act
    List<String> result = productServiceImpl.getUniqueTypes();

    // Assert
    assertEquals(types, result);
  }

  @Test
  public void getUniqueTypes_ThrowsServerError_OnDataAccessException() {
    // Arrange
    when(productRepository.findDistinctTypes()).thenThrow(new DataAccessException("Database Error") {});

    // Act and Assert
    assertThrows(ServerError.class, () -> productServiceImpl.getUniqueTypes());
  }
}
