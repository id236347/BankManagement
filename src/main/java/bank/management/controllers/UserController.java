package bank.management.controllers;

import bank.management.components.mappers.CardMapper;
import bank.management.dto.CardDto;
import bank.management.models.BankTransaction;
import bank.management.services.MaskCardService;
import bank.management.services.UserCardsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserCardsService userCardsService;
    private final MaskCardService maskCardService;
    private final CardMapper cardMapper;

    @Autowired
    public UserController(UserCardsService userCardsService, MaskCardService maskCardService, CardMapper cardMapper) {
        this.userCardsService = userCardsService;
        this.maskCardService = maskCardService;
        this.cardMapper = cardMapper;
    }

    @GetMapping("/cards")
    public ResponseEntity<Page<CardDto>> getMyCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size,
            @RequestParam(defaultValue = "true") boolean isMask
    ) {
        return new ResponseEntity<>(
                maskCardService
                        .maskCardsOfPageable(
                                PageRequest.of(page, size),
                                userCardsService::getMyCards,
                                isMask
                        ).map(cardMapper::convertToCardDto),
                HttpStatus.OK
        );
    }

    @GetMapping("/cards/balances/total")
    public ResponseEntity<BigDecimal> getMyCardsBalances() {
        return new ResponseEntity<>(userCardsService.getTotalBalance(), HttpStatus.OK);
    }

    @GetMapping("/cards/balances")
    public ResponseEntity<List<BigDecimal>> getCardBalances() {
        return new ResponseEntity<>(userCardsService.getCardBalances(), HttpStatus.OK);
    }

    @GetMapping("/cards/{id}")
    public ResponseEntity<CardDto> getCardById(
            @PathVariable int id,
            @RequestParam(defaultValue = "true") boolean isMask
    ) {
        return new ResponseEntity<>(cardMapper.convertToCardDto(maskCardService.maskCardById(id, userCardsService::getCardById, isMask)), HttpStatus.OK);
    }

    @GetMapping("/cards/balances/{id}")
    public ResponseEntity<BigDecimal> getCardBalanceById(@PathVariable int id) {
        return new ResponseEntity<>(userCardsService.getCardBalanceById(id), HttpStatus.OK);
    }

    @PostMapping("/cards")
    public ResponseEntity<HttpStatus> transferMoney(@Valid @RequestBody BankTransaction transaction) {
        userCardsService.transferMoney(transaction.getFromId(), transaction.getToId(), transaction.getAmount());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PatchMapping("/cards/{id}")
    public ResponseEntity<HttpStatus> blockCard(@PathVariable int id) {
        userCardsService.blockCard(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}

