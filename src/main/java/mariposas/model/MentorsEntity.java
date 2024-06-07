package mariposas.model;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Introspected
@Serdeable
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mentors", schema = "public")
public class MentorsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(referencedColumnName = "id", name = "user_id")
    private UserEntity userId;

    @Column(name = "education")
    private String education;

    @Column(name = "mentoring_capacity")
    private BigDecimal mentoringCapacity;
}