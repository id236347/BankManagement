package bank.management.components;

import bank.management.models.Role;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;


/**
 * Компонент фильтра валидации токенов.
 */
@Slf4j
@Setter
@Component
@PropertySource("classpath:jwt.properties")
public class JWTFilter extends OncePerRequestFilter {

    /**
     * Разрешенные без регистрации/входа ендпоинты.
     */
    @Value("${jwt.allowed}")
    private List<String> allowedEndpoints;

    /**
     * Инструмент генерации и валидации токенов.
     */
    private final JWTGenerator generator;

    /**
     * Сервис для попытки достать пользователя.
     */
    private final UserDetailsService userDetailsService;

    /**
     * Часто используемое сообщение об ошибке выношу в константу.
     */
    public static final String INVALID_TOKEN = "Невалидный токен!";

    @Autowired
    public JWTFilter(JWTGenerator generator, UserDetailsService userDetailsService) {
        this.generator = generator;
        this.userDetailsService = userDetailsService;
    }


    /**
     * Метод фильтрации запроса.
     *
     * @param request     входящий HTTP-запрос.
     * @param response    исходящий HTTP-ответ.
     * @param filterChain цепочка фильтров, позволяющая передать запрос/ответ дальше.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String endPoint = request.getRequestURI();
        // Проверяю можно ли пропустить фильтрацию.
        if (isAllowed(endPoint, allowedEndpoints)) {
            filterChain.doFilter(request, response);
            log.info("Ендпоинт {} доступен по-умолчанию.", endPoint);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        // Проверяю корректность хедера.
        if (authHeader != null && authHeader.startsWith("Bearer ") && !authHeader.isBlank()) {

            String token = authHeader.substring(7);

            // Если после барьера ничего нет, то кидаю 400.
            if (token.isBlank()) {
                log.error("Токен пустой!");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, INVALID_TOKEN);
                return;
            } else {
                try {

                    // Валидирую токен.
                    String login = generator.validate(token).get(JWTGenerator.LOGIN_CLAIM);
                    // Пытаюсь достать пользователя по логину из токена.
                    UserDetails userDetails = userDetailsService.loadUserByUsername(login);
                    log.info("Authorities: {}", ((Set<Role>)userDetails.getAuthorities()).stream().map(Role::getName).toList());

                    if (userDetails == null || userDetails.getPassword() == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, INVALID_TOKEN);
                        return;
                    }

                    // Создаю UPAT на основе получившегося пользователя.
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    userDetails.getPassword(),
                                    userDetails.getAuthorities()
                            );

                    if (SecurityContextHolder.getContext().getAuthentication() == null)
                        SecurityContextHolder.getContext().setAuthentication(authToken);

                    // Если что-то не так при аутентификации, то кидаю 400.
                } catch (JWTVerificationException jwt) {
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                    return;
                }
            }
        }

        // Пропускаю дальше через фильтр запрос.
        filterChain.doFilter(request, response);
    }

    /**
     * Метод проверки того, разрешен ли ендпоинт любому без отправки токена?
     *
     * @param currentEndpoint  ендпоинт на который приходит запрос.
     * @param allowedEndpoints список разрешенных ендпоинтов.
     * @return разрешен/неразрешен.
     */
    private boolean isAllowed(String currentEndpoint, List<String> allowedEndpoints) {
        for (String endpoint : allowedEndpoints) {
            log.info("current endpoint : {}", currentEndpoint);
            log.info("allowed endpoint : {}", endpoint);
            log.info("formed allowed endpoint : {}", endpoint.replaceAll("\\*", ""));
            log.info("contains ? -> {}", currentEndpoint.contains(endpoint.replaceAll("\\*", "")));
            log.info("equals? -> {}", currentEndpoint.equals(endpoint));
            if (currentEndpoint.contains(endpoint.replaceAll("\\*", "")))
                return true;
        }
        return false;
    }
}
