package bank.management.util;

import bank.management.models.Card;

public final class MaskiratorCardsUtil {

    public static Card maskNumberAndCvcOfCard(Card card) {
        var rawData = card.getNumber().split(" ");
        int lastIndex = rawData.length - 1;

        for (int i = 0; i < lastIndex; i++)
            rawData[i] = "*".repeat(rawData[i].length());

        card.setNumber(String.join(" ", rawData));
        card.setCvc("*".repeat(card.getCvc().length()));
        return card;
    }

}
