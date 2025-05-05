package bank.management.components;

import bank.management.models.Role;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Компонент взаимодействия с токенами:
 * - генерация
 * - валидация
 */
@Slf4j
@Setter
@Component
@PropertySource("classpath:jwt.properties")
public class JWTGenerator {

    /**
     * Время жизни токена.
     */
    @Value("${jwt.lifeTime}")
    private long tokenLifeTime;

    /**
     * Краткая информация о назначении токена.
     */
    @Value("${jwt.subject}")
    private String subject;

    /**
     * Кто выдал токен.
     */
    @Value("${jwt.issuer}")
    private String issuer;

    /**
     * Секрет токена.
     */
    @Value("${jwt.secret}")
    private String secret;

    public static final String LOGIN_CLAIM = "login";

    public static final String PASSWORD_CLAIM = "password";

    public static final String ROLES_CLAIM = "roles";

    /**
     * Метод генерации токена.
     * @param login    логин пользователя.
     * @param password пароль пользователя.
     * @return сгенерированный токен в строковом формате.
     */
    public String generate(String login, String password, Set<Role> roles) {
        Date tokenExpiryDate = generateDateOfExpiration(tokenLifeTime);
        log.info("Время когда токен протухнет: {}", tokenExpiryDate);
        return JWT
                .create()
                .withSubject(subject)
                .withClaim(JWTGenerator.LOGIN_CLAIM, login)
                .withClaim(JWTGenerator.ROLES_CLAIM, roles.stream().map(Role::getName).toList())
                .withClaim(JWTGenerator.PASSWORD_CLAIM, password)
                .withIssuedAt(new Date())
                .withIssuer(issuer)
                .withExpiresAt(tokenExpiryDate)
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * Метод валидации токена, возвращающий карту основных данных "входа".
     * @param token принимаемый токен.
     * @return карта основных данных "входа".
     */
    public Map<String, String> validate(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject(subject)
                .withIssuer(issuer)
                .build();


        DecodedJWT jwt = verifier.verify(token);

        return Map.of(
                JWTGenerator.LOGIN_CLAIM,
                jwt.getClaim(JWTGenerator.LOGIN_CLAIM).asString(),
                JWTGenerator.PASSWORD_CLAIM,
                jwt.getClaim(JWTGenerator.PASSWORD_CLAIM).asString()
        );
    }

    /**
     * Метод генерации время жизни токена.
     * @param time время жизни в формате long.
     * @return время жизни в Data.
     */
    private Date generateDateOfExpiration(long time) {
        return Date.from(ZonedDateTime.now().plusMinutes(time).toInstant());
    }

    /**
     * Метод возвращающий номер телефона пользователя по JWT токену.
     * @param header хедер из запроса.
     * @return номер телефона.
     */
    public String getPhoneNumberByHeader(String header) {
        return this.validate(header.substring(7)).get(JWTGenerator.LOGIN_CLAIM);
    }


}