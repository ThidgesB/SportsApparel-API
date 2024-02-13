package io.catalyte.training.sportsproducts.domains.promocode;
import static io.catalyte.training.sportsproducts.constants.Paths.PROMOCODE_PATH;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Example;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.Optional;

@RunWith(SpringRunner.class)
@WebMvcTest(PromocodeController.class)
@Import(PromocodeServiceImpl.class)
public class PromocodeApiTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private PromocodeRepository promocodeRepository;

  @Test
  public void createPromocode() throws Exception {
    Promocode promoCode = new Promocode();
    promoCode.setTitle("TESTTITLE");
    promoCode.setDescription("Test Description");
    promoCode.setType("percent");
    promoCode.setRate(BigDecimal.valueOf(50));

    when(promocodeRepository.save(any(Promocode.class))).thenReturn(promoCode);

    mockMvc.perform(post(PROMOCODE_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(promoCode)))
        .andExpect(status().isCreated());

    ArgumentCaptor<Promocode> argumentCaptor = ArgumentCaptor.forClass(Promocode.class);
    verify(promocodeRepository).save(argumentCaptor.capture());
    Promocode savedPromoCode = argumentCaptor.getValue();
    assertEquals(promoCode.getTitle(), savedPromoCode.getTitle());
    assertEquals(promoCode.getDescription(), savedPromoCode.getDescription());
    assertEquals(promoCode.getType(), savedPromoCode.getType());
    assertEquals(promoCode.getRate(), savedPromoCode.getRate());
  }

  @Test
  public void getPromoCodes() throws Exception {
    Promocode promoCode1 = new Promocode();
    promoCode1.setTitle("TESTTITLE1");
    promoCode1.setDescription("Test Description 1");
    promoCode1.setType("percent");
    promoCode1.setRate(BigDecimal.valueOf(50));

    Promocode promoCode2 = new Promocode();
    promoCode2.setTitle("TESTTITLE2");
    promoCode2.setDescription("Test Description 2");
    promoCode2.setType("amount");
    promoCode2.setRate(BigDecimal.valueOf(10));

    when(promocodeRepository.findAll(any(Example.class)))
        .thenReturn(Arrays.asList(promoCode1, promoCode2));

    MvcResult result = mockMvc.perform(get(PROMOCODE_PATH))
        .andExpect(status().isOk())
        .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    List<Promocode> responsePromoCodes = objectMapper.readValue(responseContent, new TypeReference<List<Promocode>>() {});
    assertEquals(2, responsePromoCodes.size());
  }

  @Test
  public void getPromoCodeByTitle() throws Exception {
    Promocode promoCode = new Promocode();
    promoCode.setTitle("TESTTITLE");
    promoCode.setDescription("Test Description");
    promoCode.setType("percent");
    promoCode.setRate(BigDecimal.valueOf(50));

    when(promocodeRepository.findByTitle(promoCode.getTitle())).thenReturn(Optional.of(promoCode));

    MvcResult result = mockMvc.perform(get(PROMOCODE_PATH + "/" + promoCode.getTitle()))
        .andExpect(status().isOk())
        .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    Promocode responsePromoCode = objectMapper.readValue(responseContent, Promocode.class);
    assertEquals(promoCode.getTitle(), responsePromoCode.getTitle());
  }

}

