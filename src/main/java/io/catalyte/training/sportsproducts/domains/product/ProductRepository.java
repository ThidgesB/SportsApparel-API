package io.catalyte.training.sportsproducts.domains.product;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
  @Query("SELECT DISTINCT p.category FROM Product p")
  List<String> findDistinctCategories();
  @Query("SELECT DISTINCT p.type FROM Product p")
  List<String> findDistinctTypes();
}
