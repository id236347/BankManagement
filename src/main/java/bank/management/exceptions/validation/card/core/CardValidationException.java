package bank.management.exceptions.validation.card.core;


import bank.management.exceptions.validation.core.ValidationException;

public class CardValidationException extends ValidationException {
    public CardValidationException(String action, String message) {
        super(action, message);
    }
}
