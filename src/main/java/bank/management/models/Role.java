package bank.management.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Set;

/**
 * Модель роли пользователя.
 */
@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
@Schema(description = "Модель роли пользователя.")
public class Role implements GrantedAuthority, Serializable {

    /**
     * Уникальный идентификатор роли.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор роли.")
    private int id;

    /**
     * Название роли.
     */
    @Schema(description = "Название роли.")
    @Pattern(
            regexp = "^(ROLE_ADMIN|ROLE_USER|USER|ADMIN)$",
            message = "Роль пользователя может быть только: ROLE_ADMIN, ROLE_USER, USER, ADMIN"
    )
    private String name;

    /**
     * Пользователи, кому принадлежит роль.
     */
    @Schema(description = "Пользователи обладающие данной ролью.")
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> owners;

    @Override
    public String getAuthority() {
        return name;
    }
}
