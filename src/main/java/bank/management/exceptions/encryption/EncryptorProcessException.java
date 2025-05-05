package bank.management.exceptions.encryption;

import bank.management.exceptions.core.BankException;

public class EncryptorProcessException extends BankException {
    public EncryptorProcessException(String action, String message) {
        super(action, message);
    }
}
