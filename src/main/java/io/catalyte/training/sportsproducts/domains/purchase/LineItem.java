package io.catalyte.training.sportsproducts.domains.purchase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.catalyte.training.sportsproducts.domains.product.Product;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Describes one line item of a purchase transaction
 */
@Entity
public class LineItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JsonIgnoreProperties("products")
  private Purchase purchase;

  @ManyToOne
  @JsonIgnoreProperties("products")
  private Product product;

  private int quantity;

  public LineItem() {
  }

  public LineItem(Long id, Purchase purchase, Product product, int quantity) {
    this.id = id;
    this.purchase = purchase;
    this.product = product;
    this.quantity = quantity;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public Purchase getPurchase() {
    return purchase;
  }

  public void setPurchase(Purchase purchase) {
    this.purchase = purchase;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    LineItem lineItem = (LineItem) o;

    if (quantity != lineItem.quantity) {
      return false;
    }
    if (!Objects.equals(purchase, lineItem.purchase)) {
      return false;
    }
    return Objects.equals(product, lineItem.product);
  }

  @Override
  public int hashCode() {
    int result = purchase != null ? purchase.hashCode() : 0;
    result = 31 * result + (product != null ? product.hashCode() : 0);
    result = 31 * result + quantity;
    return result;
  }

  @Override
  public String toString() {
    return "LineItem{" +
        "id=" + id +
        ", purchase=" + purchase +
        ", product=" + product +
        ", quantity=" + quantity +
        '}';
  }
}
