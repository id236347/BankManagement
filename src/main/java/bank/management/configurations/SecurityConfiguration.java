package bank.management.configurations;

import bank.management.components.security.BankAccessDeniedHandler;
import bank.management.components.security.JWTFilter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Setter
@Configuration
@EnableWebSecurity
@PropertySource("classpath:jwt.properties")
public class SecurityConfiguration {

    /**
     * Ендпоинты разрешенные без токена.
     */
    @Value("${jwt.allowed}")
    private String[] allowedEndpoints;

    @Value("${jwt.allowed.for.admin}")
    private String allowedForAdmin;

    /**
     * Фильтр валидирующий токены.
     */
    private final JWTFilter filter;

    private final BankAccessDeniedHandler accessDeniedHandler;

    @Autowired
    public SecurityConfiguration(JWTFilter filter, BankAccessDeniedHandler accessDeniedHandler) {
        this.filter = filter;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth -> {

                            auth
                                    .requestMatchers(allowedEndpoints).permitAll()
                                    .requestMatchers(allowedForAdmin).hasRole("ADMIN")
                                    .anyRequest().authenticated();
                        }
                )
                .exceptionHandling(exception ->
                                exception.accessDeniedHandler(accessDeniedHandler)
                )
                .sessionManagement(
                        sm -> sm
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
