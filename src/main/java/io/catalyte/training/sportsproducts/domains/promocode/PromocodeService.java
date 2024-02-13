package io.catalyte.training.sportsproducts.domains.promocode;


import java.util.List;

/**
 * This interface provides an abstraction layer for the PromoCode Service
 */
public interface PromocodeService {

  Promocode savePromoCode(Promocode promoCode);
}