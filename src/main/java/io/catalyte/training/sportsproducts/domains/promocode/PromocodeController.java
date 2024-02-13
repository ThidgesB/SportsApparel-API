package io.catalyte.training.sportsproducts.domains.promocode;

import static io.catalyte.training.sportsproducts.constants.Paths.PROMOCODE_PATH;


import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * The PromoCodeController exposes endpoints for PromoCode related actions.
 */
@RestController
@RequestMapping(value = PROMOCODE_PATH)
public class PromocodeController {

    Logger logger = LogManager.getLogger(
        io.catalyte.training.sportsproducts.domains.product.ProductController.class);

    @Autowired
    private PromocodeService promoCodeService;

  @PostMapping
  public ResponseEntity<Promocode> create(@RequestBody Promocode promoCode) {
    Promocode savedPromoCode = promoCodeService.savePromoCode(promoCode);
    return new ResponseEntity<>(savedPromoCode, HttpStatus.CREATED);
  }

}
