package io.catalyte.training.sportsproducts.domains.promocode;


import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromocodeRepository extends JpaRepository<Promocode, Long> {

  Optional<Promocode> findByTitle(String title);
}