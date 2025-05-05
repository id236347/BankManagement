package bank.management.exceptions.unauthorized;

import bank.management.exceptions.core.BankException;

public class NoViewingRightsException extends BankException {

    public NoViewingRightsException(String action, String message) {
        super(action, message);
    }
}
