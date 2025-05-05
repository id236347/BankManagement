package bank.management.exceptions.validation.card;

import bank.management.exceptions.validation.card.core.CardValidationException;

public class InvalidStatusCardException extends CardValidationException {
    public InvalidStatusCardException(String action, String message) {
        super(action, message);
    }
}
