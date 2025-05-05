package bank.management.exceptions.conflicts.core;

import bank.management.exceptions.core.BankException;

public class DeletingYourselfException  extends BankException {
    public DeletingYourselfException(String action, String message) {
        super(action, message);
    }
}
