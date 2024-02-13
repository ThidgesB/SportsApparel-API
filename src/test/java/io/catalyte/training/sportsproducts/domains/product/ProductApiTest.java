package io.catalyte.training.sportsproducts.domains.product;

import static io.catalyte.training.sportsproducts.constants.Paths.PRODUCTS_PATH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductApiTest {

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Before
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }

  @Test
  public void getProductsReturns200() throws Exception {
    mockMvc.perform(get(PRODUCTS_PATH))
        .andExpect(status().isOk());
  }

  @Test
  public void getProductByIdReturnsProductWith200() throws Exception {
    mockMvc.perform(get(PRODUCTS_PATH + "/1"))
        .andExpect(status().isOk());
  }

  @Test
  public void createProductReturns201() throws Exception {
    // Create a Product object to be sent in the request body
    Product newProduct = new Product();
    newProduct.setReleaseDate("07-25-1994"); // Set a valid release date
    newProduct.setName("Test Product");
    newProduct.setDescription("This is a test product");
    newProduct.setDemographic("Men");
    newProduct.setCategory("Soccer");
    newProduct.setType("Shoe");
    newProduct.setPrice(BigDecimal.valueOf(99.99));
    newProduct.setImgSrc("test_img.jpg");
    newProduct.setQuantity(10L);
    newProduct.setBrand("Nike");
    newProduct.setMaterial("Leather");
    newProduct.setPrimaryColorCode("#FFFFFF");
    newProduct.setSecondaryColorCode("#000000");
    newProduct.setStyleNumber("ABC123");
    newProduct.setGlobalProductCode("XYZ789");
    newProduct.setActive(true);

    // Convert the Product object to JSON format
    String productJson = objectMapper.writeValueAsString(newProduct);

    // Perform the POST request to create a new product
    mockMvc.perform(MockMvcRequestBuilders.post(PRODUCTS_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(productJson))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  public void getUniqueCategoriesReturnsWith200() throws Exception {
    mockMvc.perform(get(PRODUCTS_PATH + "/categories")).andExpect(status().isOk());
  }

  @Test
  public void getUniqueTypesReturnsWith200() throws Exception {
    mockMvc.perform(get(PRODUCTS_PATH + "/types")).andExpect(status().isOk());
  }
}
