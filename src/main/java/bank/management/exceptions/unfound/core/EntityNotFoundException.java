package bank.management.exceptions.unfound.core;

import bank.management.exceptions.core.BankException;

public class EntityNotFoundException extends BankException {

    public EntityNotFoundException(String action, String message) {
        super(action, message);
    }
}
