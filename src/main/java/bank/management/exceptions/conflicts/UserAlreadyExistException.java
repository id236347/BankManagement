package bank.management.exceptions.conflicts;


import bank.management.exceptions.conflicts.core.EntityAlreadyExistException;
import lombok.Getter;

@Getter
public class UserAlreadyExistException extends EntityAlreadyExistException {

    public UserAlreadyExistException(String action, String message) {
        super(action, message);
    }
}
