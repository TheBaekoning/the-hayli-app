package app.asclepius.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "userProfiles")
public class UserProfileEntity {
    @Id
    @Column(name = "userId", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity users;

    @Size(max = 64)
    @Column(name = "firstName", length = 64)
    private String firstName;

    @Size(max = 64)
    @Column(name = "lastName", length = 64)
    private String lastName;

    @Size(max = 320)
    @Column(name = "email", length = 64)
    private String email;

    @Setter(AccessLevel.NONE)
    @CreationTimestamp
    @Column(name = "createdAt", updatable = false)
    private Instant createdAt;

    @Setter(AccessLevel.NONE)
    @Column(name = "updatedAt", insertable = false)
    @UpdateTimestamp
    private Instant updatedAt;
}
