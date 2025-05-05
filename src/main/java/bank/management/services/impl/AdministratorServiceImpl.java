package bank.management.services.impl;

import bank.management.components.Authenticator;
import bank.management.components.CardMapper;
import bank.management.components.UserMapper;
import bank.management.dto.CardDto;
import bank.management.dto.RegistrationUserDto;
import bank.management.dto.UserDto;
import bank.management.exceptions.conflicts.core.DeletingYourselfException;
import bank.management.exceptions.conflicts.core.EntityAlreadyExistException;
import bank.management.exceptions.unauthorized.UnauthorizedException;
import bank.management.exceptions.unfound.RoleNotFoundException;
import bank.management.exceptions.unfound.UserNotFoundException;
import bank.management.models.Card;
import bank.management.models.Role;
import bank.management.models.Status;
import bank.management.models.User;
import bank.management.repositories.CardsRepository;
import bank.management.repositories.RoleRepository;
import bank.management.repositories.UserRepository;
import bank.management.services.AdministratorService;
import bank.management.util.CardEncryptorUtil;
import bank.management.util.ReportingErrorUtil;
import bank.management.validation.CardDtoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
public class AdministratorServiceImpl implements AdministratorService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CardsRepository cardsRepository;
    private final CardMapper cardMapper;
    private final UserMapper userMapper;
    private final CardEncryptorUtil cardEncryptorUtil;
    private final CardDtoValidator cardDtoValidator;
    private final Authenticator authenticator;

    @Autowired
    public AdministratorServiceImpl(UserRepository userRepository, RoleRepository roleRepository, CardsRepository cardsRepository, CardMapper cardMapper, UserMapper userMapper, CardEncryptorUtil cardEncryptorUtil, CardDtoValidator cardDtoValidator, Authenticator authenticator) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.cardsRepository = cardsRepository;
        this.cardMapper = cardMapper;
        this.userMapper = userMapper;
        this.cardEncryptorUtil = cardEncryptorUtil;
        this.cardDtoValidator = cardDtoValidator;
        this.authenticator = authenticator;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE) // Нам важна консистентность данных!
    public void createCard(CardDto cardsDto, BindingResult bindingResult) {

        cardDtoValidator.validate(cardsDto, bindingResult);
        ReportingErrorUtil.validate(bindingResult, "Сохранение банковской карты", CardDtoValidator.DEFAULT_ERROR_MSG);

        // Проверяем существует ли подаваемый владелец.
        findUserById(cardsDto.getOwnerId());

        // Проверяем наличие карты по номеру.
        if (cardsRepository.findByNumber(cardsDto.getNumber()).isPresent())
            throw new EntityAlreadyExistException("Создание банковской карты", "Карта с данным номер уже зарегистрирована!");

        cardsRepository.save(cardEncryptorUtil.encryptCard(cardMapper.convertToCard(cardsDto)));

    }

    @Override
    public Card findCardById(int cardId) {
        Card card = cardsRepository.findById(cardId).orElseThrow(ReportingErrorUtil::createCardNotFoundException);
        card = cardEncryptorUtil.decryptCard(card);
        return card;
    }

    @Override
    public Set<Card> findAllCards() {
        return new HashSet<>(cardsRepository.findAll()).stream().map(cardEncryptorUtil::decryptCard).collect(Collectors.toSet());
    }

    @Override
    public Page<Card> findAllCards(Pageable pageable) {
        Page<Card> cardsPage = cardsRepository.findAll(pageable);
        Set<Card> decryptedCards = cardsPage.getContent().stream()
                .map(cardEncryptorUtil::decryptCard)
                .collect(Collectors.toSet());

        return new PageImpl<>(new ArrayList<>(decryptedCards), pageable, cardsPage.getTotalElements());
    }


    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void unblockCardById(int cardId) {
        Card supposedCard = cardsRepository.findById(cardId).orElseThrow(ReportingErrorUtil::createCardNotFoundException);
        supposedCard.setStatus(Status.ACTIVE);
        cardsRepository.save(supposedCard);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteCardById(int cardId) {
        cardsRepository.findById(cardId).orElseThrow(ReportingErrorUtil::createCardNotFoundException);
        cardsRepository.deleteById(cardId);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createUser(RegistrationUserDto user, BindingResult bindingResult, boolean isAdmin) {


        ReportingErrorUtil.validate(bindingResult, "Регистрация или создание пользователя", ReportingErrorUtil.DEFAULT_UPDATE_OR_SAVE_USER_MSG);

        User userToSave = userMapper.convertToUser(user);

        Optional<Role> roleForUser = roleRepository.findByName(isAdmin ? "ROLE_ADMIN" : "ROLE_USER");

        Role defaultRole = roleForUser.orElseThrow(() -> new RoleNotFoundException(
                        ReportingErrorUtil.REGISTRATION_ACTION,
                        "Роли НЕ инициализированы на сервере!"
                )
        );

        userToSave.setRoles(new HashSet<>(Set.of(defaultRole)));

        userRepository.save(userToSave);
    }

    @Override
    public UserDto findUserById(int userId) {
        User user = userRepository.findById(userId).orElseThrow(ReportingErrorUtil::createUserNotFoundException);
        return userMapper.convertToUserDto(user);
    }


    @Override
    public Set<UserDto> findAllUsers() {
        return new HashSet<>(userRepository.findAll()).stream().map(userMapper::convertToUserDto).collect(Collectors.toSet());
    }

    @Override
    public Page<UserDto> findAllUsers(Pageable pageable) {
        return new PageImpl<>(userRepository.findAll(pageable).stream().map(userMapper::convertToUserDto).toList());
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateUserById(UserDto user, int userId, BindingResult bindingResult) {

        ReportingErrorUtil.validate(bindingResult, "Обновление или редактирование пользователя", ReportingErrorUtil.DEFAULT_UPDATE_OR_SAVE_USER_MSG);

        userRepository.findById(userId).orElseThrow(ReportingErrorUtil::createUserNotFoundException);
        User userToUpdate = userMapper.convertToUser(user);
        userToUpdate.setId(userId);
        userRepository.save(userToUpdate);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteUserById(int userId) {

        User authenticatedUser = authenticator.getAuthenticatedUser().orElseThrow(() -> new UnauthorizedException(
                "Получения данных авторизованного администратора при удалении пользователя",
                ReportingErrorUtil.USER_NOT_AUTHENTICATED_MSG
        ));

        User userForRemove = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                ReportingErrorUtil.SEARCH_USER_BY_ID_ACTION,
                ReportingErrorUtil.USER_NOT_FOUND_MSG
        ));

        // Администратор не может удалить сам себя!
        if (authenticatedUser.getEmail().equals(userForRemove.getEmail()))
            throw new DeletingYourselfException("Удаление пользователя", "Администратор не может удалить сам себя!");

        userRepository.deleteById(userId);
    }


    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void blockCard(int cardId) {
        Card supposedCard = cardsRepository.findById(cardId).orElseThrow(ReportingErrorUtil::createCardNotFoundException);
        supposedCard.setStatus(Status.BLOCKED);
        cardsRepository.save(supposedCard);
    }

}
