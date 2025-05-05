package bank.management.components.security;

import bank.management.components.tool.BadResponseCreator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Обработчик неудачных попыток обращения к защищённым ресурсам.
 */
@Component
public class BankAccessDeniedHandler implements AccessDeniedHandler {

    private final BadResponseCreator badResponseCreator;

    @Autowired
    public BankAccessDeniedHandler(BadResponseCreator badResponseCreator) {
        this.badResponseCreator = badResponseCreator;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {

        badResponseCreator.createBadResponse
                (
                        response,
                        HttpServletResponse.SC_FORBIDDEN,
                        "Попытка обратится к защищенному контенту или функционалу.",
                        "У вас нет административных прав на обращение к данному адресу!"
                );
    }
}
