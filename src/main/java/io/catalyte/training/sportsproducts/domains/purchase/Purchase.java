package io.catalyte.training.sportsproducts.domains.purchase;

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Describes a purchase object that holds the information for a transaction
 */
@Entity
public class Purchase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToMany(mappedBy = "purchase")
  private Set<LineItem> products;

  private DeliveryAddress deliveryAddress;

  private BillingAddress billingAddress;

  private CreditCard creditCard;

  public Purchase() {
    billingAddress = new BillingAddress();
    deliveryAddress = new DeliveryAddress();
    creditCard = new CreditCard();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Set<LineItem> getProducts() {
    return products;
  }

  public void setProducts(Set<LineItem> products) {
    this.products = products;
  }

  public DeliveryAddress getDeliveryAddress() {
    return deliveryAddress;
  }

  public void setDeliveryAddress(DeliveryAddress deliveryAddress) {
    this.deliveryAddress = deliveryAddress;
  }

  public BillingAddress getBillingAddress() {
    return billingAddress;
  }

  public void setBillingAddress(BillingAddress billingAddress) {
    this.billingAddress = billingAddress;
  }

  public CreditCard getCreditCard() {
    return creditCard;
  }

  public void setCreditCard(CreditCard creditCard) {
    this.creditCard = creditCard;
  }

  @Override
  public String toString() {
    return "Purchase{" +
        "id=" + id +
        ", deliveryAddress=" + deliveryAddress +
        ", billingAddress=" + billingAddress +
        ", creditCard=" + creditCard +
        '}';
  }
}