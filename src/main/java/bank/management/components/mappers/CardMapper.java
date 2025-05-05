package bank.management.components.mappers;

import bank.management.dto.CardDto;
import bank.management.exceptions.validation.card.core.CardValidationException;
import bank.management.exceptions.validation.core.ValidationException;
import bank.management.models.Card;
import bank.management.models.Status;
import bank.management.repositories.UserRepository;
import bank.management.util.ReportingErrorUtil;
import bank.management.components.tool.YearMonthAttributeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CardMapper {

    private final UserRepository userRepository;
    private final YearMonthAttributeConverter yearMonthAttributeConverter;

    @Autowired
    public CardMapper(UserRepository userRepository, YearMonthAttributeConverter yearMonthAttributeConverter) {
        this.userRepository = userRepository;
        this.yearMonthAttributeConverter = yearMonthAttributeConverter;
    }

    public Card convertToCard(CardDto cardsDto) throws ValidationException {

        Status status = switch (cardsDto.getStatus()) {
            case -1 -> Status.BLOCKED;
            case 0 -> Status.OVERDUE;
            case 1 -> Status.ACTIVE;
            default -> throw new CardValidationException("Конвертация карты при сохранении или регистрации", "Переданный код статуса состояния карты неверен!");
        };

        return Card
                .builder()
                .number(cardsDto.getNumber())
                .cvc(cardsDto.getCvc())
                .owner(userRepository.findById(cardsDto.getOwnerId()).orElseThrow(ReportingErrorUtil::createUserNotFoundException))
                .expiryDate(yearMonthAttributeConverter.convertToEntityAttribute(cardsDto.getExpiryDate()))
                .status(status)
                .balance(BigDecimal.valueOf(cardsDto.getBalance()))
                .build();
    }

    public CardDto convertToCardDto(Card card) {

        short status = switch (card.getStatus()) {
            case BLOCKED -> -1;
            case OVERDUE -> 0;
            case ACTIVE -> 1;
        };

        return CardDto
                .builder()
                .number(card.getNumber())
                .cvc(card.getCvc())
                .ownerId(card.getOwner().getId())
                .expiryDate(yearMonthAttributeConverter.convertToDatabaseColumn(card.getExpiryDate()))
                .status(status)
                .balance(card.getBalance().doubleValue())
                .build();
    }

    public Set<CardDto> convertToCardDtoSet(Set<Card> cardsSet) throws ValidationException {
        return cardsSet.stream().map(this::convertToCardDto).collect(Collectors.toSet());
    }
}
