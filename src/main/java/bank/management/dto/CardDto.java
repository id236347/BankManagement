package bank.management.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(description = "DTO модель банковской карты.")
public class CardDto {

    @Schema(description = "Номер банковской карты.")
    private String number;

    @Schema(description = "CVC код банковской карты.")
    @NotBlank(message = "CVC не может быть пустым или состоять из пробелов!")
    @Pattern(regexp = "^\\d+$", message = "СVC должен состоять только из цифр!")
    private String cvc;

    @Min(value = 1, message = "id владельца не найдено в БД!")
    @Schema(description = "Уникальный идентификатор владельца банковской карты.")
    private int ownerId;

    @Schema(description = "Дата окончания срока действия.")
    private String expiryDate;

    @Schema(description = "Статус состояния банковской карты: -1 : заблокирована; 0 : истек срок действия; 1 : активна", example = "1")
    @Max(value = 1, message = "Статус пользователя может быть только: -1 : заблокирована; 0 : просрочена; 1 : активна")
    @Min(value = -1, message = "Статус пользователя может быть только: -1 : заблокирована; 0 : просрочена; 1 : активна")
    private short status;

    @Schema(description = "Баланс банковской карты.")
    @PositiveOrZero(message = "Баланс не может быть меньше 0!")
    @Digits(integer = 9, fraction = 2, message = "Баланс может иметь не более двух знаков после запятой!")
    private double balance;
}
