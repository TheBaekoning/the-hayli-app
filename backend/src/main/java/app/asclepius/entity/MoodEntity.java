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
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "moods")
public class MoodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "moodId", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;

    @Setter(AccessLevel.NONE)
    @CreationTimestamp
    @Column(name = "createdAt", updatable = false)
    private Instant createdAt;

    @Setter(AccessLevel.NONE)
    @Column(name = "updatedAt", insertable = false)
    @UpdateTimestamp
    private Instant updatedAt;

    @NotNull
    @Column(name = "moodDate", nullable = false)
    private LocalDate moodDate;

    @NotNull
    @Column(name = "moodRating", nullable = false)
    private Integer moodRating;

    @Size(max = 280)
    @Column(name = "notes", length = 280)
    private String notes;

}
