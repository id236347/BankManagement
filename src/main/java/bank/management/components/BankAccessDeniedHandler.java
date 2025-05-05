package bank.management.components;

import bank.management.models.BankError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class BankAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper;

    @Autowired
    public BankAccessDeniedHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8).toString());

        response.getWriter().write(mapper.writeValueAsString(
                        new BankError(
                                "Попытка обратится к защищенному контенту или функционалу.",
                                "У вас нет административных прав на обращение к данному адресу!"
                        )
                )
        );
    }
}
