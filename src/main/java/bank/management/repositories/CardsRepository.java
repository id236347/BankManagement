package bank.management.repositories;

import bank.management.models.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CardsRepository extends JpaRepository<Card, Integer> {
    Page<Card> findByOwnerEmail(String email, Pageable pageable);

    Set<Card> findByOwnerEmail(String email);

    Optional<Card> findById(int id);

    Optional<Card> findByNumber(String number);
}
