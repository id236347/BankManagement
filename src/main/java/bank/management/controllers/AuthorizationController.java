package bank.management.controllers;

import bank.management.dto.RegistrationUserDto;
import bank.management.dto.AuthorizationUserDto;
import bank.management.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authorization endpoints.")
public class AuthorizationController {

    private final AuthService authService;

    @Autowired
    public AuthorizationController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрирует пользователя на основе полученных данных из тела запроса, выдавая токен аутентификации.")
    public ResponseEntity<String> register(
            @Valid
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Данные будущего пользователя.")
            RegistrationUserDto userDto,
            BindingResult bindingResult
    ) {
        return new ResponseEntity<>(authService.register(userDto, bindingResult), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Выдает новый токен на основе данных пользователя.")
    public ResponseEntity<String> login(
            @Valid
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Данные пользователя для входа." )
            AuthorizationUserDto userDto,
            BindingResult bindingResult
    ) {
        return new ResponseEntity<>(authService.authenticate(userDto, bindingResult), HttpStatus.OK);
    }

}
