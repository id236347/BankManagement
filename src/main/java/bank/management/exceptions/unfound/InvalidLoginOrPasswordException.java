package bank.management.exceptions.unfound;

import bank.management.exceptions.unfound.core.EntityNotFoundException;

public class InvalidLoginOrPasswordException extends EntityNotFoundException {

    public InvalidLoginOrPasswordException(String action, String message) {
        super(action, message);
    }
}
