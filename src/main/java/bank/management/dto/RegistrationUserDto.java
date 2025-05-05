package bank.management.dto;

import bank.management.models.FullName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "DTO модель пользователя, который будет зарегистрирован.")
public class RegistrationUserDto implements Serializable {

    @Valid
    @Schema(description = "ФИО будущего пользователя.")
    @NotNull(message = "ФИО не может НЕ быть у пользователя!")
    private FullName fullName;

    @Email
    @NotEmpty(message = "Название почты не может быть пустой!")
    @NotNull(message = "У пользователя не может НЕ быть почты!")
    @NotBlank(message = "Почта не может состоять только из пробелов!")
    @Size(min = 5, max = 254, message = "Кол-во символов в адресе почты должно быть от 3 до 254!")
    @Schema(description = "Адрес электронной почты будущего пользователя.", example = "some@mail.ru")
    private String email;

    @NotEmpty(message = "Пароль не может быть пустым")
    @NotNull(message = "У пользователя не может НЕ быть пароля!")
    @NotBlank(message = "Пароль не может состоять только из пробелов!")
    @Schema(description = "Пароль будущего пользователя.", example = "StrongPassword3000")
    @Size(min = 6, max = 64, message = "Кол-во символов в пароле почты должно быть от 6 до 64!")
    private String password;

}
