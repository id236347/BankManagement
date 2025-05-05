package bank.management.exceptions.unfound;

import bank.management.exceptions.unfound.core.EntityNotFoundException;

public class RoleNotFoundException extends EntityNotFoundException {

    public RoleNotFoundException(String action, String message) {
        super(action, message);
    }
}
