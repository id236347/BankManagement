package bank.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Schema(description = "DTO модель роли пользователя")
public class RoleDto {

    @Schema(description = "Название роли.")
    @Pattern(
            regexp = "^(ROLE_ADMIN|ROLE_USER|USER|ADMIN)$",
            message = "Роль пользователя может быть только: ROLE_ADMIN, ROLE_USER, USER, ADMIN"
    )
    private String name;

    @Schema(description = "Уникальные идентификаторы владельцев роли.")
    private Set<Integer> ownersIds;
}
