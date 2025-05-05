package bank.management.exceptions.validation.card;

import bank.management.exceptions.validation.card.core.CardValidationException;

public class NumberOfCardValidationException extends CardValidationException {
    public NumberOfCardValidationException(String action, String message) {
        super(action, message);
    }
}
