package bank.management.exceptions.validation.user.core;

import bank.management.exceptions.validation.core.ValidationException;

public class UserValidationException extends ValidationException {
    public UserValidationException(String action, String message) {
        super(action, message);
    }
}
