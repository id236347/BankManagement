package bank.management.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@PropertySource("classpath:encryptor.properties")
public class KeyHelper {

    @Value("${cards.key}")
    private String secretKey;

    public static final String DEFAULT_ALGORITHM = "AES";

    @Bean
    public SecretKeySpec getSecretKey() {
        return new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), DEFAULT_ALGORITHM);
    }
}
