package bank.management.services;

import bank.management.models.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.function.Supplier;

public interface MaskCardService {
    Card maskCardById(Integer id, Converter<Integer, Card> cardMethod, boolean isMask);

    Page<Card> maskCardsOfPageable(Pageable pageable, Converter<Pageable, Page<Card>> cardMethod, boolean isMask);

    Set<Card> maskCardsSet(Supplier<Set<Card>> cardsMethod, boolean isMask);

}
