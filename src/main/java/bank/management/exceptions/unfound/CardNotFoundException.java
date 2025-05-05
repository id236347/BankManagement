package bank.management.exceptions.unfound;

import bank.management.exceptions.unfound.core.EntityNotFoundException;

public class CardNotFoundException extends EntityNotFoundException {
    public CardNotFoundException(String action, String message) {
        super(action, message);
    }
}
