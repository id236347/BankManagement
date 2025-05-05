package bank.management.exceptions;

import bank.management.exceptions.conflicts.core.DeletingYourselfException;
import bank.management.exceptions.conflicts.core.EntityAlreadyExistException;
import bank.management.exceptions.conflicts.UserAlreadyExistException;
import bank.management.exceptions.core.BankException;
import bank.management.exceptions.encryption.EncryptorProcessException;
import bank.management.exceptions.transfer.core.TransferException;
import bank.management.exceptions.unauthorized.NoViewingRightsException;
import bank.management.exceptions.unauthorized.UnauthorizedException;
import bank.management.exceptions.unfound.core.EntityNotFoundException;
import bank.management.exceptions.unfound.RoleNotFoundException;
import bank.management.exceptions.validation.core.ValidationException;
import bank.management.models.BankError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.auth0.jwt.exceptions.JWTVerificationException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityAlreadyExistException.class)
    public ResponseEntity<BankError> handleUserAlreadyExistException(EntityAlreadyExistException e) {
        return new ResponseEntity<>(generateBankError(e), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<BankError> handleEntityNotFoundException(EntityNotFoundException e) {
        return new ResponseEntity<>(generateBankError(e), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoViewingRightsException.class)
    public ResponseEntity<BankError> handleNoViewingRightsException(NoViewingRightsException e) {
        return new ResponseEntity<>(generateBankError(e), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(TransferException.class)
    public ResponseEntity<BankError> handleTransferException(TransferException e) {
        return new ResponseEntity<>(generateBankError(e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<BankError> handleRoleNotFoundException(RoleNotFoundException e) {
        return new ResponseEntity<>(generateBankError(e), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<BankError> handleUserAlreadyExistException(UserAlreadyExistException e) {
        log.error("UserAlreadyExistException is activated!");
        return new ResponseEntity<>(generateBankError(e), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EncryptorProcessException.class)
    public ResponseEntity<BankError> handleEncryptorProcessException(EncryptorProcessException e) {
        return new ResponseEntity<>(generateBankError(e), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<BankError> handleValidationException(ValidationException e) {
        return new ResponseEntity<>(generateBankError(e), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<BankError> handleUnauthorizedException(UnauthorizedException e) {
        return new ResponseEntity<>(generateBankError(e), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DeletingYourselfException.class)
    public ResponseEntity<BankError> handleDeletingYourselfException(DeletingYourselfException e) {
        return new ResponseEntity<>(generateBankError(e), HttpStatus.CONFLICT);
    }

    private BankError generateBankError(BankException b) {
        return new BankError(b.getAction(), b.getMessage());
    }

}
