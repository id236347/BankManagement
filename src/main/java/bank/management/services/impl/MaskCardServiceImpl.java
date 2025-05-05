package bank.management.services.impl;

import bank.management.models.Card;
import bank.management.services.Converter;
import bank.management.services.MaskCardService;
import bank.management.util.MaskiratorCardsUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Обертка поведения {@link UserCardsServiceImpl}, {@link AdministratorServiceImpl}
 */
@Service
public class MaskCardServiceImpl implements MaskCardService {

    @Override
    public Card maskCardById(Integer id, Converter<Integer, Card> cardMethod, boolean isMask) {
        return !isMask ? cardMethod.convert(id) : MaskiratorCardsUtil.maskNumberAndCvcOfCard(cardMethod.convert(id));
    }

    @Override
    public Page<Card> maskCardsOfPageable(Pageable pageable, Converter<Pageable, Page<Card>> cardMethod, boolean isMask) {
        return !isMask ? cardMethod.convert(pageable) :
                cardMethod.convert(pageable).map(MaskiratorCardsUtil::maskNumberAndCvcOfCard);
    }

    @Override
    public Set<Card> maskCardsSet(Supplier<Set<Card>> cardsMethod, boolean isMask) {
        return !isMask ? cardsMethod.get() : cardsMethod.get()
                .stream()
                .map(MaskiratorCardsUtil::maskNumberAndCvcOfCard)
                .collect(Collectors.toSet());
    }


}
