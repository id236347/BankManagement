package bank.management.dto;

import bank.management.models.FullName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
@Schema(description = "DTO Модель пользователя.")
public class UserDto {

    @Email
    @NotEmpty(message = "Название почты не может быть пустой!")
    @NotNull(message = "У пользователя не может НЕ быть почты!")
    @Schema(description = "Адрес электронной почты пользователя.")
    @NotBlank(message = "Почта не может состоять только из пробелов!")
    @Size(min = 5, max = 254, message = "Кол-во символов в адресе почты должно быть от 3 до 254!")
    private String email;

    @NotEmpty(message = "Пароль не может быть пустым")
    @Schema(description = "Пароль пользователя.")
    @NotNull(message = "У пользователя не может НЕ быть пароля!")
    @NotBlank(message = "Пароль не может состоять только из пробелов!")
    @Size(min = 6, max = 64, message = "Кол-во символов в пароле почты должно быть от 6 до 64!")
    private String password;

    @Valid
    @Embedded
    @Schema(description = "ФИО пользователя.")
    @AttributeOverrides({
            @AttributeOverride(name = "firstName", column = @Column(name = "first_name")),
            @AttributeOverride(name = "lastName", column = @Column(name = "last_name")),
            @AttributeOverride(name = "patronymic", column = @Column(name = "patronymic"))
    })
    private FullName fullName;


    @Schema(description = "Уникальные идентификаторы банковских карт пользователя.")
    private Set<Integer> cardIds;

    @NotNull(message = "У пользователя не может НЕ быть ролей!")
    @NotEmpty(message = "Роли пользователя не могут быть пустыми!")
    @Schema(description = "Уникальные идентификаторы ролей пользователя.")
    private Set<Integer> rolesIds;

}
