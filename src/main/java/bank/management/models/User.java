package bank.management.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Schema(description = "Модель пользователя.")
public class User implements UserDetails {

    /**
     * Уникальный идентификатор пользователя в БД.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор пользователя.")
    private int id;

    /**
     * Адрес почтового ящика пользователя.
     */
    @Email
    @NotEmpty(message = "Название почты не может быть пустой!")
    @NotNull(message = "У пользователя не может НЕ быть почты!")
    @Schema(description = "Адрес электронной почты пользователя.")
    @NotBlank(message = "Почта не может состоять только из пробелов!")
    @Size(min = 5, max = 254, message = "Кол-во символов в адресе почты должно быть от 3 до 254!")
    private String email;

    /**
     * Пароль пользователя.
     */

    @NotEmpty(message = "Пароль не может быть пустым")
    @Schema(description = "Пароль пользователя.")
    @NotNull(message = "У пользователя не может НЕ быть пароля!")
    @NotBlank(message = "Пароль не может состоять только из пробелов!")
    @Size(min = 6, max = 64, message = "Кол-во символов в пароле почты должно быть от 6 до 64!")
    private String password;

    /**
     * ФИО пользователя.
     */
    @Valid
    @Embedded
    @Schema(description = "ФИО пользователя.")
    @AttributeOverrides({
            @AttributeOverride(name = "firstName", column = @Column(name = "first_name")),
            @AttributeOverride(name = "lastName", column = @Column(name = "last_name")),
            @AttributeOverride(name = "patronymic", column = @Column(name = "patronymic"))
    })
    private FullName fullName;

    /**
     * Банковские карты пользователя.
     */
    @OneToMany(
            mappedBy = "owner",
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @Schema(description = "Банковские карты пользователя.")
    private Set<Card> cards;

    /**
     * Роли пользователя.
     */
    @Valid
    @NotNull
    @ManyToMany(
            cascade = {CascadeType.MERGE},
            fetch = FetchType.LAZY
    )
    @Schema(description = "Роли пользователя.")
    @NotNull(message = "Пользователь не может быть без роли!")
    @JoinTable(
            name = "users_and_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                email.equals(user.email) &&
                password.equals(user.password) &&
                fullName.equals(user.fullName) &&
                cards.equals(user.cards) &&
                roles.equals(user.roles);
    }

}
