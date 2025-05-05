package bank.management.exceptions.validation.card;

import bank.management.exceptions.validation.card.core.CardValidationException;

public class YearMonthValidateException extends CardValidationException {
    public YearMonthValidateException(String action, String message) {
        super(action, message);
    }
}
