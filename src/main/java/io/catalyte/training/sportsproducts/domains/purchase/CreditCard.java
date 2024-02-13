package io.catalyte.training.sportsproducts.domains.purchase;

import javax.persistence.Embeddable;

/**
 * Describes Credit Card information for transactions
 */
@Embeddable
public class CreditCard {

  private String cardNumber;
  private String cvv;
  private String expiration;
  private String cardholder;

  public CreditCard() {
  }

  public CreditCard(String cardNumber, String cvv, String expiration, String cardholder) {
    this.cardNumber = cardNumber;
    this.cvv = cvv;
    this.expiration = expiration;
    this.cardholder = cardholder;
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public String getCvv() {
    return cvv;
  }

  public void setCvv(String cvv) {
    this.cvv = cvv;
  }

  public String getExpiration() {
    return expiration;
  }

  public void setExpiration(String expiration) {
    this.expiration = expiration;
  }

  public String getCardholder() {
    return cardholder;
  }

  public void setCardholder(String cardholder) {
    this.cardholder = cardholder;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CreditCard that = (CreditCard) o;

    if (getCardNumber() != null ? !getCardNumber().equals(that.getCardNumber())
        : that.getCardNumber() != null) {
      return false;
    }
    if (getCvv() != null ? !getCvv().equals(that.getCvv()) : that.getCvv() != null) {
      return false;
    }
    if (getExpiration() != null ? !getExpiration().equals(that.getExpiration())
        : that.getExpiration() != null) {
      return false;
    }
    return getCardholder() != null ? getCardholder().equals(that.getCardholder())
        : that.getCardholder() == null;
  }

  @Override
  public int hashCode() {
    int result = getCardNumber() != null ? getCardNumber().hashCode() : 0;
    result = 31 * result + (getCvv() != null ? getCvv().hashCode() : 0);
    result = 31 * result + (getExpiration() != null ? getExpiration().hashCode() : 0);
    result = 31 * result + (getCardholder() != null ? getCardholder().hashCode() : 0);
    return result;
  }
}
