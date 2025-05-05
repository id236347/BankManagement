package bank.management.exceptions.conflicts.core;

import bank.management.exceptions.core.BankException;

public class EntityAlreadyExistException extends BankException {

    public EntityAlreadyExistException(String action, String message) {
        super(action, message);
    }
}
