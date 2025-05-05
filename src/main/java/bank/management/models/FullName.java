package bank.management.models;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Модель ФИО.")
public class FullName implements Serializable {

    @Schema(description = "Имя.", example = "Артем")
    @NotEmpty(message = "Имя пользователя не может быть пустым!")
    @NotNull(message = "У пользователя не может НЕ быть имени!")
    @Pattern(regexp = "[a-zA-Zа-яА-ЯёЁ]+", message = "В имени допустимы только буквы!")
    @NotBlank(message = "Имя пользователя не может состоять только из пробелов!")
    @Size(min = 1, max = 32, message = "Кол-во символов в имени пользователя должно быть от 1 до 32!")
    private String firstName;

    @Schema(description = "Фамилия.", example = "Артемов")
    @NotEmpty(message = "Фамилия пользователя не может быть пустой!")
    @NotNull(message = "У пользователя не может НЕ быть фамилии!")
    @Pattern(regexp = "[a-zA-Zа-яА-ЯёЁ]+", message = "В фамилии допустимы только буквы!")
    @NotBlank(message = "Фамилия пользователя не может состоять только из пробелов!")
    @Size(min = 1, max = 32, message = "Кол-во символов в фамилии пользователя должно быть от 1 до 32!")
    private String lastName;

    @Schema(description = "Отчество.", example = "Артемович")
    @Pattern(regexp = "[a-zA-Zа-яА-ЯёЁ]+", message = "В фамилии допустимы только буквы! Если фамилии нет, то передавайте в это поле null!")
    @Size(max = 32, message = "Кол-во символов в фамилии пользователя должно быть до 32!")
    private String patronymic;

    public boolean fieldsIsBlank() {
        return firstName.isBlank() && lastName.isBlank() && patronymic.isBlank();
    }
}