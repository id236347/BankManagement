package bank.management.services;

import bank.management.dto.CardDto;
import bank.management.dto.RegistrationUserDto;
import bank.management.dto.UserDto;
import bank.management.models.Card;
import bank.management.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

import java.util.Set;

public interface AdministratorService extends BlockingCardService {


    // ---- Поведение взаимодействия с банковскими картами ----

    void createCard(CardDto cardsDto, BindingResult bindingResult);

    Card findCardById(int cardId);

    Set<Card> findAllCards();

    Page<Card> findAllCards(Pageable pageable);

    void unblockCardById(int cardId);

    void deleteCardById(int cardId);


    // ---- Поведение управления пользователями ----

    void createUser(RegistrationUserDto user, BindingResult bindingResult, boolean isAdmin);

    UserDto findUserById(int userId);

    Set<UserDto> findAllUsers();

    Page<UserDto> findAllUsers(Pageable pageable);

    void updateUserById(UserDto user, int userId, BindingResult bindingResult);

    void deleteUserById(int userId);

}
