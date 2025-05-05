package bank.management.services;

@FunctionalInterface
public interface BlockingCardService {
    void blockCard(int cardId);
}
