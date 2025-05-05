package bank.management.models;

import bank.management.components.tool.YearMonthAttributeConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cards")
@Schema(description = "Модель банковской карты.")
public class Card implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор банковской карты.")
    private int id;

    @Schema(description = "Номер банковской карты.")
    private String number;

    @Schema(description = "CVC код банковской карты.")
    @NotBlank(message = "CVC не может быть пустым или состоять из пробелов!")
    private String cvc;

    @Valid
    @ManyToOne
    @Schema(description = "Владелец банковской карты.")
    @NotNull(message = "У карты не может не быть владельца")
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User owner;

    @Column(name = "expiry_date")
    @Schema(description = "Дата окончания срока действия.")
    @Convert(converter = YearMonthAttributeConverter.class)
    @Future(message = "Дата окончания срока использования карты должна быть в будущем!")
    private YearMonth expiryDate;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Статус состояния банковской карты.")
    private Status status;

    @Schema(description = "Баланс банковской карты.")
    @PositiveOrZero(message = "Баланс не может быть меньше 0!")
    @Digits(integer = 9, fraction = 2, message = "Баланс может иметь не более двух знаков после запятой!")
    private BigDecimal balance;

}
