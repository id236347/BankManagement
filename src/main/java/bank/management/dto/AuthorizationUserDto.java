package bank.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO модель пользователя для авторизации/аутентификации.")
public class AuthorizationUserDto {

    @Email
    @Schema(description = "Почта пользователя.", example = "some@mail.ru")
    @NotEmpty(message = "Название почты не может быть пустой!")
    @NotNull(message = "У пользователя не может НЕ быть почты!")
    @NotBlank(message = "Почта не может состоять только из пробелов!")
    @Size(min = 5, max = 254, message = "Кол-во символов в адресе почты должно быть от 3 до 254!")
    private String email;

    @Schema(description = "Пароль пользователя.", example = "StrongPassword2323")
    @NotEmpty(message = "Пароль не может быть пустым")
    @NotNull(message = "У пользователя не может НЕ быть пароля!")
    @NotBlank(message = "Пароль не может состоять только из пробелов!")
    @Size(min = 6, max = 64, message = "Кол-во символов в пароле почты должно быть от 6 до 64!")
    private String password;
}
