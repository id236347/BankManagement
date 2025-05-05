package bank.management.exceptions.transfer.core;

import bank.management.exceptions.core.BankException;

public class TransferException extends BankException {

    public TransferException(String action, String message) {
        super(action, message);
    }
}
