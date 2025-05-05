package bank.management.util;

import bank.management.exceptions.unfound.CardNotFoundException;
import bank.management.exceptions.transfer.NegativeOrZeroTransferAmountException;
import bank.management.exceptions.transfer.NotEnoughFundsException;
import bank.management.exceptions.unfound.UserNotFoundException;
import bank.management.exceptions.validation.core.ValidationException;
import org.springframework.validation.BindingResult;

/**
 * Утилитарный класс с готовыми пояснениями ошибок и методами порождения их часто встречающихся комбинаций.
 */
public final class ReportingErrorUtil {

    public static final String CARD_INFO_ACTION = "Выдача информации по карте или выдача самой карты";
    public static final String SEARCH_CARD_BY_ID_ACTION = "Поиск карты по id";
    public static final String TRANSFER_MONEY_ACTION = "Перевод денег";
    public static final String SEARCH_USER_BY_ID_ACTION = "Поиск пользователя по id";
    public static final String REGISTRATION_ACTION = "Регистрация";

    public static final String CARD_NOT_FOUND_MSG = "Карта с данным id не найдена!";
    public static final String USER_NOT_FOUND_MSG = "Пользователь с данным id не найден!";
    public static final String USER_NOT_AUTHENTICATED_MSG = "Пользователь не аутентифицирован!";
    public static final String INVALID_AMOUNT_MSG = "Сумма перевода меньше либо равна 0!";
    public static final String NO_MONEY_MSG = "Не хватает баланса для перевода!";
    public static final String DEFAULT_UPDATE_OR_SAVE_USER_MSG = "Невалидные данные пользователя!";

    public static CardNotFoundException createCardNotFoundException() {
        return new CardNotFoundException(CARD_INFO_ACTION, CARD_NOT_FOUND_MSG);
    }

    public static UserNotFoundException createCardInfoAuthException() {
        return new UserNotFoundException(CARD_INFO_ACTION, USER_NOT_AUTHENTICATED_MSG);
    }

    public static UserNotFoundException createUserNotFoundException() {
        return new UserNotFoundException(SEARCH_USER_BY_ID_ACTION, USER_NOT_FOUND_MSG);
    }

    public static NegativeOrZeroTransferAmountException createNegativeOrZeroTransferAmountException() {
        return new NegativeOrZeroTransferAmountException(TRANSFER_MONEY_ACTION, INVALID_AMOUNT_MSG);
    }

    public static NotEnoughFundsException createNotEnoughFundsException() {
        return new NotEnoughFundsException(TRANSFER_MONEY_ACTION, NO_MONEY_MSG);
    }

    public static void validate(
            BindingResult bindingResult,
            String action,
            String defaultMessage
    ) {
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            throw new ValidationException(action, msg == null ? defaultMessage : msg);
        }
    }

}
