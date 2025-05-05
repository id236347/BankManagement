package bank.management.controllers;

import bank.management.components.mappers.CardMapper;
import bank.management.dto.CardDto;
import bank.management.dto.RegistrationUserDto;
import bank.management.dto.UserDto;;
import bank.management.services.AdministratorService;
import bank.management.services.MaskCardService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdministratorService administratorService;
    private final MaskCardService maskCardService;
    private final CardMapper cardMapper;

    @Autowired
    public AdminController(AdministratorService administratorService, MaskCardService maskCardService, CardMapper cardMapper) {
        this.administratorService = administratorService;
        this.maskCardService = maskCardService;
        this.cardMapper = cardMapper;

    }

    // OK
    @PostMapping("/cards")
    public ResponseEntity<HttpStatus> addCard(
            @RequestBody @Valid CardDto cardDto,
            BindingResult bindingResult
    ) {
        administratorService.createCard(cardDto, bindingResult);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // OK
    @GetMapping("/cards/{id}")
    public ResponseEntity<CardDto> findCardById(
            @PathVariable int id,
            @RequestParam(defaultValue = "true") boolean isMask
    ) {
        return new ResponseEntity<>(
                cardMapper.convertToCardDto(maskCardService.maskCardById(id, administratorService::findCardById, isMask)),
                HttpStatus.OK
        );
    }

    // OK
    @GetMapping("/cards")
    public ResponseEntity<Set<CardDto>> findAllCards(@RequestParam(defaultValue = "true") boolean isMask) {
        return new ResponseEntity<>(
                cardMapper.convertToCardDtoSet(maskCardService.maskCardsSet(administratorService::findAllCards, isMask)),
                HttpStatus.OK
        );
    }

    // OK
    @GetMapping("/cards/{page}/{size}")
    public ResponseEntity<Page<CardDto>> findAllCards(
            @PathVariable int page,
            @PathVariable int size,
            @RequestParam boolean isMask
    ) {
        return new ResponseEntity<>(
                maskCardService.maskCardsOfPageable(
                        PageRequest.of(page, size),
                        administratorService::findAllCards,
                        isMask
                ).map(cardMapper::convertToCardDto),
                HttpStatus.OK
        );
    }

    @PatchMapping("/cards/{id}/unlock")
    public ResponseEntity<HttpStatus> unlockCard(@PathVariable int id) {
        administratorService.unblockCardById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/cards/{id}")
    public ResponseEntity<HttpStatus> destroyCard(@PathVariable int id) {
        administratorService.deleteCardById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // OK
    @PostMapping("/users")
    public ResponseEntity<HttpStatus> addUser(
            @RequestBody @Valid RegistrationUserDto user,
            @RequestParam(defaultValue = "false") boolean isAdmin,
            BindingResult bindingResult) {
        log.info("BINDING RESULT: {}", bindingResult.getAllErrors());
        administratorService.createUser(user, bindingResult, isAdmin);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // OK
    @GetMapping("/user/{id}")
    public ResponseEntity<UserDto> findUserById(@PathVariable int id) {
        return new ResponseEntity<>(administratorService.findUserById(id), HttpStatus.OK);
    }

    // OK
    @GetMapping("/users")
    public ResponseEntity<Set<UserDto>> findAllUsers() {
        return new ResponseEntity<>(administratorService.findAllUsers(), HttpStatus.OK);
    }

    // OK
    @GetMapping("/users/{page}/{size}")
    public ResponseEntity<Page<UserDto>> findAllUsers(@PathVariable int page, @PathVariable int size) {
        return new ResponseEntity<>(administratorService.findAllUsers(PageRequest.of(page, size)), HttpStatus.OK);
    }

    // OK
    @PatchMapping("/users/{id}")
    public ResponseEntity<HttpStatus> updateUser(
            @PathVariable int id,
            @Valid @RequestBody UserDto user,
            BindingResult bindingResult
    ) {
        administratorService.updateUserById(user, id, bindingResult);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // OK
    @DeleteMapping("/users/{id}")
    public ResponseEntity<HttpStatus> destroyUser(@PathVariable int id) {
        administratorService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // OK
    @PatchMapping("/cards/{id}/block")
    public ResponseEntity<HttpStatus> blockCard(@PathVariable int id) {
        administratorService.blockCard(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
