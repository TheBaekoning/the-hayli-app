package app.asclepius.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId", nullable = false)
    private Integer id;

    @Size(max = 36)
    @NotNull
    @Column(name = "uuid", nullable = false, length = 36)
    private String uuid;

    @Size(max = 64)
    @NotNull
    @Column(name = "password", nullable = false, length = 64)
    private String password;

    @Setter(AccessLevel.NONE)
    @CreationTimestamp
    @Column(name = "createdAt", updatable = false)
    private Instant createdAt;

    @Setter(AccessLevel.NONE)
    @Column(name = "updatedAt", insertable = false)
    @UpdateTimestamp
    private Instant updatedAt;

    @NotNull
    @Column(name = "isVerified", nullable = false)
    private Byte isVerified;

    @Size(max = 8)
    @Column(name = "verifyToken", length = 8)
    private String verifyToken;

}
