package bank.management.validation;


import bank.management.dto.CardDto;
import bank.management.components.tool.YearMonthAttributeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.stream.IntStream;

@Component
public class CardDtoValidator implements Validator {

    private final YearMonthAttributeConverter yearMonthAttributeConverter;

    public static final String DEFAULT_ERROR_MSG = "Ошибка валидации банковской карты";

    @Autowired
    public CardDtoValidator(YearMonthAttributeConverter yearMonthAttributeConverter) {
        this.yearMonthAttributeConverter = yearMonthAttributeConverter;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return CardDto.class.equals(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {


        CardDto cardDto = (CardDto) target;
        YearMonth date;
        try {
            date = yearMonthAttributeConverter.convertToEntityAttribute(cardDto.getExpiryDate());
        } catch (Exception e) {
            errors.rejectValue("expiryDate", "", "Некорректный синтаксис даты!");
            return;
        }

        if (date.isBefore(YearMonth.now())) {
            errors.rejectValue("expiryDate", "", "Дата окончания срока использования карты должна быть в будущем!");
            return;
        }


        if (isValidCvc(cardDto.getCvc()))
            errors.rejectValue("cvc", "", "Кол-во символов CVC должно быть от 3 до 4!");

        if (!isValidCardNumberByLuhnAlgorithm(cardDto.getNumber())) {
            errors.rejectValue(
                    "number",
                    "",
                    "Номер карты не прошел проверку алгоритмом Луна. " +
                    "\nАлгоритм Луна: https://ru.wikipedia.org/wiki/%D0%90%D0%BB%D0%B3%D0%BE%D1%80%D0%B8%D1%82%D0%BC_%D0%9B%D1%83%D0%BD%D0%B0"
            );
        }

    }

    /**
     * 1. Если количество цифр нечетное, удваиваются цифры на нечетных позициях. </br>
     * 2. Если количество цифр четное, удваиваются цифры на четных позициях. </br>
     * Потом делается проверка: если 2·x > 9, то из произведения вычитается 9, иначе произведение 2·x оставляем без изменения, где x — текущая цифра. </br>
     * Затем все числа, полученные на предыдущем этапе, складываются. </br>
     * Полученная сумма должна быть кратна 10 (то есть равна 40, 50, 60, 70, …). </br> </br>
     * Пример: </br>
     * 4--5--6--1-----2--6--1--2-----1--2--3--4-----5--4--6--4- </br>
     * 8-----12-------4-----2--------2-----6--------10----12--- </br>
     * 8-----3--------4-----2--------2-----6--------1-----3---- </br> </br>
     * Затем все числа, полученные на предыдущем этапе, складываются: </br>
     * 8+5+3+1 + 4+6+2+2 + 2+2+6+4 + 1+4+3+4 = 57
     *
     * @param cardNumber номер карты представленный в строковом виде.
     * @return проходит ли номер карты алгоритм корректности.
     */
    public static boolean isValidCardNumberByLuhnAlgorithm(String cardNumber) {

        cardNumber = cardNumber.replaceAll("\\s+", "");
        var rawData = new ArrayList<>(IntStream.range(0, cardNumber.length())
                .mapToObj(cardNumber::charAt)
                .map(Character::getNumericValue).toList());

        // Может быть нечитаемо.
        // Прохожусь по четным/нечетным позициям в зависимости от четности суммы цифр номера карты.
        // Удваиваю на 2 те элементы, по которым прохожусь.
        // Если после удвоения элемент > 9, то вычитаю из него 9.
        for (int i = rawData.size() % 2 == 0 ? 0 : 1; i < rawData.size(); i += 2) {
            rawData.set(i, rawData.get(i) * 2);
            if (rawData.get(i) > 9)
                rawData.set(i, rawData.get(i) - 9);
        }

        return rawData.stream().mapToInt(Integer::intValue).sum() % 10 == 0;
    }

    public static boolean isValidCvc(String cvc) {
        int cvcLength = cvc.length();
        return cvcLength <= 4 && cvcLength >= 3;
    }

}
