package bank.management.components.mappers;

import bank.management.dto.RegistrationUserDto;
import bank.management.dto.UserDto;
import bank.management.exceptions.unfound.CardNotFoundException;
import bank.management.exceptions.unfound.RoleNotFoundException;
import bank.management.models.Card;
import bank.management.models.Role;
import bank.management.models.User;
import bank.management.repositories.CardsRepository;
import bank.management.repositories.RoleRepository;
import bank.management.util.ReportingErrorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    private final CardsRepository cardsRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserMapper(CardsRepository cardsRepository, RoleRepository roleRepository) {
        this.cardsRepository = cardsRepository;
        this.roleRepository = roleRepository;
    }

    public User convertToUser(RegistrationUserDto registrationUserDto) {
        return User
                .builder()
                .fullName(registrationUserDto.getFullName())
                .email(registrationUserDto.getEmail())
                .password(registrationUserDto.getPassword())

                .build();
    }

    public User convertToUser(UserDto userDto) {
        return User
                .builder()
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .fullName(userDto.getFullName())
                .cards(
                        userDto
                                .getCardIds()
                                .stream()
                                .map(e -> cardsRepository
                                        .findById(e)
                                        .orElseThrow(() -> new CardNotFoundException(
                                                        ReportingErrorUtil.SEARCH_CARD_BY_ID_ACTION,
                                                        ReportingErrorUtil.CARD_NOT_FOUND_MSG
                                                )
                                        )
                                )
                                .collect(Collectors.toSet())
                )
                .roles(
                        userDto
                                .getRolesIds()
                                .stream()
                                .map(e -> roleRepository
                                        .findById(e)
                                        .orElseThrow(() -> new RoleNotFoundException(
                                                          "Поиск роли по id",
                                                          "Роль с id, казанная у пользователя не найдена!"
                                                )
                                        )
                                )
                                .collect(Collectors.toSet())
                )
                .build();
    }

    public UserDto convertToUserDto(User user) {
        return UserDto
                .builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .fullName(user.getFullName())
                .cardIds(user.getCards().stream().map(Card::getId).collect(Collectors.toSet()))
                .rolesIds(user.getRoles().stream().map(Role::getId).collect(Collectors.toSet()))
                .build();
    }
}
