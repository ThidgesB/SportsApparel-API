package io.catalyte.training.sportsproducts.domains.purchase;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PurchaseApiTest {
  @Autowired
  private WebApplicationContext wac;
  private MockMvc mockMvc;
  @Before
  public void setUp() { mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build(); }
  @Test
  public void findPurchasesByEmail_WithValidEmail_Returns200Ok() throws Exception{
    mockMvc.perform(get("/purchases/{email}", "example@example.com"))
        .andExpect(status().isOk());
  }
  @Test
  public void findPurchasesByEmail_EmailNotSpecified_Returns404NotFound() throws Exception {
    mockMvc.perform(get("/purchases/"))
        .andExpect(status().isNotFound());
  }

}
