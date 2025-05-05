package bank.management.exceptions.transfer;

import bank.management.exceptions.transfer.core.TransferException;

public class NotEnoughFundsException extends TransferException {

    public NotEnoughFundsException(String action, String message) {
        super(action, message);
    }
}
