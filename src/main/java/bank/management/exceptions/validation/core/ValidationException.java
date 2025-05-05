package bank.management.exceptions.validation.core;

import bank.management.exceptions.core.BankException;

public class ValidationException extends BankException {
    public ValidationException(String action, String message) {
        super(action, message);
    }
}
