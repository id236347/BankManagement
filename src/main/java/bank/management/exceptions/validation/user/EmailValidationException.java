package bank.management.exceptions.validation.user;

import bank.management.exceptions.validation.user.core.UserValidationException;

public class EmailValidationException extends UserValidationException {
    public EmailValidationException(String action, String message) {
        super(action, message);
    }
}
