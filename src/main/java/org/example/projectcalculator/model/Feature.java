package org.example.projectcalculator.model;

import static org.example.projectcalculator.configuration.ApplicationConfiguration.ESTIMATE_FRACTIONAL_PART;
import static org.example.projectcalculator.configuration.ApplicationConfiguration.ESTIMATE_ROUNDING_MODE;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "feature")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Feature {

  private static final MathContext mc =
      new MathContext(ESTIMATE_FRACTIONAL_PART, ESTIMATE_ROUNDING_MODE);

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "feature_id_sequence")
  @SequenceGenerator(name = "feature_id_sequence", allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false, updatable = false)
  private long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "project_id", nullable = false, updatable = false)
  @ToString.Exclude
  private Project project;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "milestone_id")
  @ToString.Exclude
  private Milestone milestone;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(name = "best_case_estimate_in_days", nullable = false)
  private BigDecimal bestCaseEstimateInDays;

  @Column(name = "most_likely_estimate_in_days", nullable = false)
  private BigDecimal mostLikelyEstimateInDays;

  @Column(name = "worst_case_estimate_in_days", nullable = false)
  private BigDecimal worstCaseEstimateInDays;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "last_updated_at", nullable = false)
  private LocalDateTime lastUpdatedAt;

  public BigDecimal getEstimateInDays() {
    // E = (a + 4m + b) / 6

    var sum = BigDecimal.ZERO;
    sum = sum.add(bestCaseEstimateInDays);
    sum = sum.add(new BigDecimal(4).multiply(mostLikelyEstimateInDays));
    sum = sum.add(worstCaseEstimateInDays);

    return sum.divide(new BigDecimal(6), mc);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Feature feature)) {
      return false;
    }
    return getId() != 0 && getId() == feature.getId();
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
