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
@Table(name = "mentees", schema = "public")
public class MenteesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(referencedColumnName = "id", name = "user_id")
    private UserEntity userId;

    @Column(name = "is_sponsored")
    private Boolean isSponsored;

    @Column(name = "mentee_level_id")
    private BigDecimal menteeLevelId;
}