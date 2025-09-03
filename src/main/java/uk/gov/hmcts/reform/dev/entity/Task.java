package uk.gov.hmcts.reform.dev.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tasks")
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @SequenceGenerator(
        name = "tasks_id_seq",
        sequenceName = "tasks_id_seq",
        allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tasks_id_seq")
    private long id;

    @Column(name="system_id", nullable = false)
    private UUID systemId;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name= "status", nullable =false)
    private Status status;

    @Column(name="due_date", nullable = false)
    private LocalDateTime dueDate;

    @PrePersist
    protected void onCreate() {
        if (systemId == null) {
            systemId = UUID.randomUUID();
        }
    }
}
