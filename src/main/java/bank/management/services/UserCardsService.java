package bank.management.services;


import bank.management.models.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface UserCardsService extends BlockingCardService {
    Page<Card> getMyCards(Pageable pageable);

    BigDecimal getTotalBalance();

    List<BigDecimal> getCardBalances();

    Card getCardById(int cardId);

    BigDecimal getCardBalanceById(int cardId);

    void transferMoney(int fromId, int toId, BigDecimal amount);
}
