package bank.management.components.tool;

import bank.management.exceptions.encryption.EncryptorProcessException;
import bank.management.models.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CardEncryptor {

    private final KeyHelper keyHelper;

    @Autowired
    public CardEncryptor(KeyHelper keyHelper) {
        this.keyHelper = keyHelper;
    }

    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(KeyHelper.DEFAULT_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keyHelper.getSecretKey());
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    public String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(KeyHelper.DEFAULT_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keyHelper.getSecretKey());
        return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)), StandardCharsets.UTF_8);
    }

    public Card encryptCard(Card card) {
        return processCard(card, true);
    }

    public Card decryptCard(Card card) {
        return processCard(card, false);
    }

    private Card processCard(Card card, boolean isEncrypt) {
        try {
            if (isEncrypt) {
                card.setNumber(encrypt(card.getNumber()));
                card.setCvc(encrypt(card.getCvc()));
            } else {
                card.setNumber(decrypt(card.getNumber()));
                card.setCvc(decrypt(card.getCvc()));
            }
        } catch (Exception e) {
            String operation = isEncrypt ? "Шифрация" : "Дешифрация";
            throw new EncryptorProcessException(operation + " данных", operation + " не удалась.");
        }
        return card;
    }

    public Page<Card> decryptCards(Page<Card> cardsPage, Pageable pageable) {
        Set<Card> decryptedCards = cardsPage.getContent().stream()
                .map(this::decryptCard)
                .collect(Collectors.toSet());
        return new PageImpl<>(new ArrayList<>(decryptedCards), pageable, cardsPage.getTotalElements());
    }

}
