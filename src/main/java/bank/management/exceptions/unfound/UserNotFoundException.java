package bank.management.exceptions.unfound;

import bank.management.exceptions.unfound.core.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException(String action, String message) {
        super(action, message);
    }
}
