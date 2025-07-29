package app.asclepius.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "friendsAndFamily")
public class FriendsAndFamilyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friendsAndFamilyId", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;

    @Size(max = 64)
    @Column(name = "name", length = 64)
    private String name;

    @Size(max = 16)
    @Column(name = "phoneNumber", length = 16)
    private String phoneNumber;

    @Setter(AccessLevel.NONE)
    @CreationTimestamp
    @Column(name = "createdAt", updatable = false)
    private Instant createdAt;

    @Setter(AccessLevel.NONE)
    @Column(name = "updatedAt", insertable = false)
    @UpdateTimestamp
    private Instant updatedAt;

    @Column(name = "lastNotified")
    private Instant lastNotified;

}
