package io.catalyte.training.sportsproducts.domains.promocode;

import io.catalyte.training.sportsproducts.exceptions.ResourceNotFound;
import io.catalyte.training.sportsproducts.exceptions.ServerError;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class PromocodeServiceImpl implements PromocodeService {

  private final Logger logger = LogManager.getLogger(
      io.catalyte.training.sportsproducts.domains.promocode.PromocodeServiceImpl.class);

  PromocodeRepository promoCodeRepository;

  @Autowired
  public PromocodeServiceImpl(PromocodeRepository promoCodeRepository) {
    this.promoCodeRepository = promoCodeRepository;
  }

  /**
   * Helper function to find unique titles for promocode verification.
   *
   * @param title - the title to check for uniqueness
   * @return the titles that conflict with the inserted promocode
   */
  private Promocode getPromoCodeByTitleHelper(String title) {
    Promocode promoCode = promoCodeRepository.findByTitle(title).orElse(null);
    return promoCode;
  }

  /**
   * Persists a promocode to the database
   *
   * @param promoCode - the purchase to persist
   * @return the persisted purchase with ids
   */
  @Override
  public Promocode savePromoCode(Promocode promoCode) {
    // Unique Title Check.
    if  (getPromoCodeByTitleHelper(promoCode.getTitle()) != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Invalid title: title must be unique.");
    }

    List<String> errors = validatePromocode(promoCode);
    if (!errors.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.join(", ", errors));
    }

    return promoCodeRepository.save(promoCode);
  }

  /**
   * Promocode validation logic
   *
   * @param promoCode - the promocode to be verified
   */
  public List<String> validatePromocode(Promocode promoCode) {
    List<String> errors = new ArrayList<>();

// Title validation
    if (promoCode.getTitle() == null) {
      errors.add("Invalid title: Title must exist.");
    } else {
      if (!promoCode.getTitle().equals(promoCode.getTitle().toUpperCase())) {
        errors.add("Invalid title: Promo code title must be uppercase only.");
      }
      if (promoCode.getTitle().contains(" ")) {
        errors.add("Invalid title: Promo code title must not contain spaces.");
      }
    }

// Description validation
    if (promoCode.getDescription() == null) {
      errors.add("Invalid description: Description must exist.");
    } else if (promoCode.getDescription().length() > 100) {
      errors.add("Invalid description: Description must be 100 characters or less.");
    }

    // Type validation
    if (promoCode.getType() == null || (!promoCode.getType().equals("flat") && !promoCode.getType().equals("percent"))) {
      errors.add("Invalid type: Type must be either 'flat' or 'percent'.");
    }

  // Rate validation
    if (promoCode.getRate() == null) {
      errors.add("Invalid rate: Rate must exist.");
    }
    if (promoCode.getRate() != null && promoCode.getType() != null) {
      if (promoCode.getType().equals("flat")) {
        BigDecimal formattedRate = promoCode.getRate().setScale(2, RoundingMode.HALF_UP);
        promoCode.setRate(formattedRate);
      }
      if (promoCode.getType().equals("percent") && (promoCode.getRate().scale() > 0 || promoCode.getRate().intValue() < 0 || promoCode.getRate().intValue() > 100)) {
        errors.add("Invalid rate: When the rate is a percent, the rate must be an integer between 0 and 100.");
      }
    }

    return errors;

    }
  }
