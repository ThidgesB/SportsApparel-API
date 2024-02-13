package io.catalyte.training.sportsproducts.domains.promocode;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PromocodeServiceImplTest {

  @Mock
  private PromocodeRepository promoCodeRepository;

  @InjectMocks
  private PromocodeServiceImpl promocodeService;

  @Test
  public void testSavePromoCode() {
    Promocode promoCode = new Promocode();
    promoCode.setTitle("TEST_TITLE");
    promoCode.setType("flat");
    promoCode.setRate(new BigDecimal("10.00"));

    when(promoCodeRepository.findByTitle("TEST_TITLE")).thenReturn(Optional.empty());
    when(promoCodeRepository.save(promoCode)).thenReturn(promoCode);

    Promocode result = promocodeService.savePromoCode(promoCode);
    assertEquals("TEST_TITLE", result.getTitle());
  }

  @Test(expected = ResponseStatusException.class)
  public void testSavePromoCodeInvalidTitle() {
    Promocode promoCode = new Promocode();
    promoCode.setTitle("test_title");
    promoCode.setType("flat");
    promoCode.setRate(new BigDecimal("10.00"));


    when(promoCodeRepository.findByTitle("test_title")).thenReturn(Optional.empty());

    promocodeService.savePromoCode(promoCode);
  }

  @Test(expected = ResponseStatusException.class)
  public void testSavePromoCodeDuplicateTitle() {
    Promocode promoCode = new Promocode();
    promoCode.setTitle("TEST_TITLE");

    when(promoCodeRepository.findByTitle("TEST_TITLE")).thenReturn(Optional.of(promoCode));

    promocodeService.savePromoCode(promoCode);
  }

  @Test(expected = ResponseStatusException.class)
  public void testSavePromoCodeInvalidDescription() {
    Promocode promoCode = new Promocode();
    promoCode.setTitle("TEST_TITLE");
    promoCode.setDescription("This description is longer than 100 characters. This description is longer than 100 characters. This description is longer than 100 characters.");

    promocodeService.savePromoCode(promoCode);
  }

  @Test(expected = ResponseStatusException.class)
  public void testSavePromoCodeInvalidType() {
    Promocode promoCode = new Promocode();
    promoCode.setTitle("TEST_TITLE");
    promoCode.setType("invalid_type");

    promocodeService.savePromoCode(promoCode);
  }

  @Test(expected = ResponseStatusException.class)
  public void testSavePromoCodeInvalidRate() {
    Promocode promoCode = new Promocode();
    promoCode.setTitle("TEST_TITLE");
    promoCode.setType("flat");
    promoCode.setRate(new BigDecimal("10.000"));

    promocodeService.savePromoCode(promoCode);
  }

  @Test(expected = ResponseStatusException.class)
  public void testSavePromoCodeNullRate() {
    Promocode promoCode = new Promocode();
    promoCode.setTitle("TEST_TITLE");
    promoCode.setType("flat");

    promocodeService.savePromoCode(promoCode);
  }

  @Test(expected = ResponseStatusException.class)
  public void testSavePromoCodeInvalidPercentRate() {
    Promocode promoCode = new Promocode();
    promoCode.setTitle("TEST_TITLE");
    promoCode.setType("percent");
    promoCode.setRate(new BigDecimal("10.5"));

    promocodeService.savePromoCode(promoCode);
  }

  @Test
  public void testPromocodeConstructor() {
    String title = "TESTTITLE";
    String description = "Test Description";
    String type = "percent";
    BigDecimal rate = BigDecimal.valueOf(50);

    Promocode promoCode = new Promocode(title, description, type, rate);

    assertEquals(title, promoCode.getTitle());
    assertEquals(description, promoCode.getDescription());
    assertEquals(type, promoCode.getType());
    assertEquals(rate, promoCode.getRate());
  }
}
