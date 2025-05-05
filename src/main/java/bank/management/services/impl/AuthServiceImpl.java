package bank.management.services.impl;

import bank.management.components.Authenticator;
import bank.management.components.JWTGenerator;
import bank.management.dto.RegistrationUserDto;
import bank.management.dto.AuthorizationUserDto;
import bank.management.exceptions.unfound.InvalidLoginOrPasswordException;
import bank.management.exceptions.unfound.RoleNotFoundException;
import bank.management.exceptions.conflicts.UserAlreadyExistException;
import bank.management.models.Role;
import bank.management.models.User;
import bank.management.repositories.RoleRepository;
import bank.management.repositories.UserRepository;
import bank.management.services.AuthService;
import bank.management.util.ReportingErrorUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final Authenticator authenticator;
    private final JWTGenerator jwtGenerator;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, RoleRepository roleRepository, Authenticator authenticator, JWTGenerator jwtGenerator) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.authenticator = authenticator;
        this.jwtGenerator = jwtGenerator;
    }


    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public String register(RegistrationUserDto userDto, BindingResult bindingResult) {

        ReportingErrorUtil.validate(bindingResult, "Регистрация пользователя", ReportingErrorUtil.DEFAULT_UPDATE_OR_SAVE_USER_MSG);

        User user = modelMapper.map(userDto, User.class);

        // Проверяем есть ли уже пользователь с поставляемым мылом.
        if (userRepository.findByEmail(userDto.getEmail()).isPresent())
            throw new UserAlreadyExistException(
                    ReportingErrorUtil.REGISTRATION_ACTION,
                    "Пользователь с таким адресом почты уже зарегистрирован!"
            );

        // Шифруем пароль
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Проверяем добавил ли роли Liquibase
        Optional<Role> expectedUserRole = roleRepository.findByName("ROLE_USER");
        Optional<Role> expectedAdminRole = roleRepository.findByName("ROLE_ADMIN");

        // Если не добавил, то добавляю сам ручками
        if (expectedUserRole.isEmpty() || expectedAdminRole.isEmpty()) {
            roleRepository.save(Role.builder().name("ROLE_USER").build());
            roleRepository.save(Role.builder().name("ROLE_ADMIN").build());
            expectedUserRole = roleRepository.findByName("ROLE_USER");
        }

        Role defaultRole = expectedUserRole.orElseThrow(() -> new RoleNotFoundException(
                        ReportingErrorUtil.REGISTRATION_ACTION,
                        "Роли НЕ инициализированы на сервере!"
                )
        );

        // Выдаем дефолтную роль.
        user.setRoles(new HashSet<>(Set.of(defaultRole)));

        // Сохраняемся
        userRepository.save(user);

        return jwtGenerator.generate(user.getEmail(), user.getPassword(), user.getRoles());
    }

    @Override
    public String authenticate(AuthorizationUserDto userDto, BindingResult bindingResult) throws InvalidLoginOrPasswordException {

        ReportingErrorUtil.validate(bindingResult, "Аутентификация пользователя", ReportingErrorUtil.DEFAULT_UPDATE_OR_SAVE_USER_MSG);

        authenticator.authenticate(userDto.getEmail(), userDto.getPassword());
        return jwtGenerator.generate(
                userDto.getEmail(),
                userDto.getPassword(),
                userRepository
                        .findByEmail(userDto.getEmail()).orElseThrow(() -> new RoleNotFoundException("Аутентификация", "Роли пользователя не найдены!"))
                        .getRoles()
        );
    }

}
