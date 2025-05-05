package bank.management.exceptions.core;

import lombok.Getter;

@Getter
public class BankException extends RuntimeException {

    private final String action;

    public BankException(String action, String message) {
        super(message);
        this.action = action;
    }

}
