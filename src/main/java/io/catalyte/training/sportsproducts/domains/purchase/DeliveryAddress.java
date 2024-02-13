package io.catalyte.training.sportsproducts.domains.purchase;

import javax.persistence.Embeddable;

/**
 * Describes the object for the delivery address of the purchase
 */
@Embeddable
public class DeliveryAddress {

  private String firstName;
  private String lastName;
  private String deliveryStreet;
  private String deliveryStreet2;
  private String deliveryCity;
  private String deliveryState;
  private int deliveryZip;

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getDeliveryStreet() {
    return deliveryStreet;
  }

  public void setDeliveryStreet(String deliveryStreet) {
    this.deliveryStreet = deliveryStreet;
  }

  public String getDeliveryStreet2() {
    return deliveryStreet2;
  }

  public void setDeliveryStreet2(String deliveryStreet2) {
    this.deliveryStreet2 = deliveryStreet2;
  }

  public String getDeliveryCity() {
    return deliveryCity;
  }

  public void setDeliveryCity(String deliveryCity) {
    this.deliveryCity = deliveryCity;
  }

  public String getDeliveryState() {
    return deliveryState;
  }

  public void setDeliveryState(String deliveryState) {
    this.deliveryState = deliveryState;
  }

  public int getDeliveryZip() {
    return deliveryZip;
  }

  public void setDeliveryZip(int deliveryZip) {
    this.deliveryZip = deliveryZip;
  }
}
