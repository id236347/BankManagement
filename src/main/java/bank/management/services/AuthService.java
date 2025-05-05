package bank.management.services;

import bank.management.dto.RegistrationUserDto;
import bank.management.dto.AuthorizationUserDto;
import bank.management.exceptions.unfound.InvalidLoginOrPasswordException;
import org.springframework.validation.BindingResult;

public interface AuthService {
    String register(RegistrationUserDto userDto, BindingResult bindingResult);

    String authenticate(AuthorizationUserDto userDto, BindingResult bindingResult) throws InvalidLoginOrPasswordException;
}
