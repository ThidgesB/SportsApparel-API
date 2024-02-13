package io.catalyte.training.sportsproducts.domains.purchase;

import static io.catalyte.training.sportsproducts.constants.Paths.PURCHASES_PATH;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exposes endpoints for the purchase domain
 */
@RestController
@RequestMapping(value = PURCHASES_PATH)
public class PurchaseController {

  Logger logger = LogManager.getLogger(PurchaseController.class);

  private PurchaseService purchaseService;

  @Autowired
  public PurchaseController(PurchaseService purchaseService) {
    this.purchaseService = purchaseService;
  }

  /**
   * Updated this method to return the response object and a 201 status code.
   * @param purchase
   * @return Created purchase and 201 status
   */
  @PostMapping
  public ResponseEntity savePurchase(@RequestBody Purchase purchase) {

    return new ResponseEntity<>(purchaseService.savePurchase(purchase), HttpStatus.CREATED);
  }
//@PostMapping
//public ResponseEntity<Object> savePurchase(@RequestBody Purchase purchase) {
//  try {
//    purchaseService.savePurchase(purchase);
//    return ResponseEntity.noContent().build();
//  } catch (ResponseStatusException ex) {
//    return ResponseEntity
//        .status(ex.getStatus())
//        .contentType(MediaType.APPLICATION_JSON)
//        .body(ex.getReason());
//  }
//}


  @GetMapping("/{email}")
  public ResponseEntity<List<Purchase>> findPurchasesByEmail(@PathVariable("email") String email) {
    if (email == null || email.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email not specified.");
    }
    List<Purchase> purchases = purchaseService.findPurchasesByEmail(email);
    return new ResponseEntity<>(purchases, HttpStatus.OK);
  }

  @GetMapping("/")
  public ResponseEntity<List<Purchase>> invalidFindPurchase () {
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email not specified.");
  }
}