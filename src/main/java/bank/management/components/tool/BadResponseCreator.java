package bank.management.components.tool;

import bank.management.models.BankError;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class BadResponseCreator {

    private final ObjectMapper objectMapper;

    @Autowired
    public BadResponseCreator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void createBadResponse(HttpServletResponse response, int responseStatus, String action, String msg) throws IOException {
        response.setStatus(responseStatus);
        response.setContentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8).toString());
        response.getWriter().write(objectMapper.writeValueAsString(new BankError(action, msg))
        );
    }
}
