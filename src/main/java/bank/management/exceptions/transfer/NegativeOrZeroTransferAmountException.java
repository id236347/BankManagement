package bank.management.exceptions.transfer;

import bank.management.exceptions.transfer.core.TransferException;

public class NegativeOrZeroTransferAmountException extends TransferException {

    public NegativeOrZeroTransferAmountException(String action, String message) {
        super(action, message);
    }
}
