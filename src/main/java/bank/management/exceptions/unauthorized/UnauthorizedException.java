package bank.management.exceptions.unauthorized;

import bank.management.exceptions.core.BankException;

public class UnauthorizedException extends BankException {
    public UnauthorizedException(String action, String message) {
        super(action, message);
    }
}
