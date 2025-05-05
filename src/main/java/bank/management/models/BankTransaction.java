package bank.management.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "Модель денежной транзакции между банковскими картами.")
public class BankTransaction {

    @Size(min = 0, message = "id карты не может быть отрицательным!")
    @Schema(description = "Уникальный идентификатор карты отправителя.")
    private int fromId;

    @Size(min = 0, message = "id карты не может быть отрицательным!")
    @Schema(description = "Уникальный идентификатор карты получателя.")
    private int toId;

    @Schema(description = "Сумма перевода.")
    @NotNull(message = "Сумма перевода не может не существовать!")
    @DecimalMin(value = "0.0", message = "Сумма перевода не может быть отрицательной!")
    @Digits(
            integer = 8, // По последним законом можно переводить десятки миллионов максимум. (вроде как)
            fraction = 2,
            message = "Сумма перевода может исчисляться максимум в десятках миллионов! " +
                    "Кол-во дробных цифр после запятой у суммы перевода не может быть больше 2, " +
                    "так как 100 копеек = 1р рублю!!"
    )
    private BigDecimal amount;

}
