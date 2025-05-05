package bank.management.services.impl;

import bank.management.components.security.Authenticator;
import bank.management.exceptions.transfer.NegativeOrZeroTransferAmountException;
import bank.management.exceptions.transfer.NotEnoughFundsException;
import bank.management.exceptions.unauthorized.NoViewingRightsException;
import bank.management.models.Card;
import bank.management.models.Status;
import bank.management.models.User;
import bank.management.repositories.CardsRepository;
import bank.management.services.AdministratorService;
import bank.management.services.UserCardsService;
import bank.management.components.tool.CardEncryptor;
import bank.management.util.ReportingErrorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервисный слой, предоставляющий функционал для простого пользователя. </br>
 * На все операции чтения ставим REPEATABLE_READ, потому что есть {@link AdministratorService}. </br>
 * {@link AdministratorService} может менять состояние тех данных, которые мы будем считывать.
 */
@Slf4j
@Service
@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
public class UserCardsServiceImpl implements UserCardsService {

    private final Authenticator authenticator;
    private final CardsRepository cardsRepository;
    private final CardEncryptor cardEncryptor;

    @Autowired
    public UserCardsServiceImpl(Authenticator authenticator, CardsRepository cardsRepository, CardEncryptor cardEncryptor) {
        this.authenticator = authenticator;
        this.cardsRepository = cardsRepository;
        this.cardEncryptor = cardEncryptor;
    }

    @Override
    public Page<Card> getMyCards(Pageable pageable) {

        var cardsPage = cardsRepository.findByOwnerEmail(
                authenticator
                        .getAuthenticatedUser()
                        .orElseThrow(ReportingErrorUtil::createCardInfoAuthException).getEmail(),
                pageable
        );

        return cardEncryptor.decryptCards(cardsPage, pageable);
    }

    @Override
    public BigDecimal getTotalBalance() {
        return getCardBalances()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public List<BigDecimal> getCardBalances() {
        return cardsRepository.findByOwnerEmail(
                authenticator
                        .getAuthenticatedUser()
                        .orElseThrow(ReportingErrorUtil::createCardInfoAuthException).getEmail()
        ).stream().map(Card::getBalance).collect(Collectors.toList());
    }

    @Override
    public Card getCardById(int cardId) {

        User authenticatedUser = authenticator.getAuthenticatedUser().orElseThrow(ReportingErrorUtil::createCardInfoAuthException);
        Card supposedCard = cardEncryptor.decryptCard(
                cardsRepository
                        .findById(cardId)
                        .orElseThrow(ReportingErrorUtil::createCardNotFoundException)
        );

        User supposedOwner = supposedCard.getOwner();

        // Я могу получить банковскую карту по айдишнику если авторизированный пользователь владелец карты.
        if (supposedOwner.equals(authenticatedUser))
            return supposedCard;

        throw new NoViewingRightsException(
                ReportingErrorUtil.SEARCH_CARD_BY_ID_ACTION,
                "Авторизированный пользователь не является владельцем разыскиваемой карты, у вас нет прав!"
        );
    }

    @Override
    public BigDecimal getCardBalanceById(int cardId) {
        return getCardById(cardId).getBalance();
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE) // Делаю акцент на точную и надёжную работу с деньгами.
    public void transferMoney(int fromId, int toId, BigDecimal amount) {

        // Проверка на суммы перевода отрицательность и равенство нулю.
        if (amount.equals(BigDecimal.ZERO) || amount.compareTo(BigDecimal.ZERO) < 0)
            throw new NegativeOrZeroTransferAmountException(ReportingErrorUtil.TRANSFER_MONEY_ACTION, ReportingErrorUtil.INVALID_AMOUNT_MSG);

        Card from = getCardById(fromId);
        Card to = getCardById(toId);

        // Хватает денег для перевода? Окей переводим.
        if (from.getBalance().compareTo(amount) >= 0) {
            from.setBalance(from.getBalance().subtract(amount));
            to.setBalance(to.getBalance().add(amount));
        } else {
            throw new NotEnoughFundsException(ReportingErrorUtil.TRANSFER_MONEY_ACTION, ReportingErrorUtil.NO_MONEY_MSG);
        }

    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void blockCard(int cardId) {
        getCardById(cardId).setStatus(Status.BLOCKED);
    }


}
