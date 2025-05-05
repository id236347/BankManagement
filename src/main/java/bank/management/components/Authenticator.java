package bank.management.components;

import bank.management.exceptions.unfound.InvalidLoginOrPasswordException;
import bank.management.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.security.core.AuthenticationException;

import java.util.Optional;

/**
 * Конфиг аутентификации.
 */
@Slf4j
@Component
public class Authenticator {

    private final AuthenticationManager authenticationManager;

    @Autowired
    public Authenticator(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * Метод аутентификации.
     *
     * @param login    логин. (В рамках приложения - это адрес почтового ящика)
     * @param password пароль.
     */
    public void authenticate(String login, String password) throws InvalidLoginOrPasswordException {

        // В UPAT кладу логин и пароль.
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(login, password);

        try {
            authenticationManager.authenticate(token);
            // Аутентификация НЕ пройдет при неверных данных.
        } catch (AuthenticationException e) {
            log.error("Данные для аутентификации не найдены!");
            throw new InvalidLoginOrPasswordException("Аутентификация", "Неверен логин или пароль!");
        }

    }

    /**
     * Метод получения аутентифицированного пользователя.
     *
     * @return {@link User}
     * @see User
     * @see Authentication
     */
    public Optional<User> getAuthenticatedUser()  {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Проверяем аутентифицирован ли пользователь.
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                return Optional.of((User) principal);
            }
        }

        // Если пользователь не аутентифицирован или principal не User
        return Optional.empty();
    }

}
