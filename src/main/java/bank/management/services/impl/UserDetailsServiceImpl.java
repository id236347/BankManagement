package bank.management.services.impl;

import bank.management.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Выношу реализацию {@link UserDetailsService} в отдельный класс для избежания циклической зависимости с фильтром.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Реализация поведения UserDetailsService.
     *
     * @param username логин.
     * @return {@link UserDetails}.
     * @see UserDetails
     * @see bank.management.models.User
     */
    @Override

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}