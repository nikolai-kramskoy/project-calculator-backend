package org.example.projectcalculator.model;

import java.math.BigDecimal;
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
@Table(name = "milestone")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Milestone {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "milestone_id_sequence")
  @SequenceGenerator(name = "milestone_id_sequence", allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false, updatable = false)
  private long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "project_id", nullable = false, updatable = false)
  @ToString.Exclude
  private Project project;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(name = "start_timestamp", nullable = false)
  private LocalDateTime startDateTime;

  @Column(name = "end_timestamp", nullable = false)
  private LocalDateTime endDateTime;

  @Column(name = "estimate_in_days", nullable = false)
  private BigDecimal estimateInDays;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "last_updated_at", nullable = false)
  private LocalDateTime lastUpdatedAt;

  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Milestone milestone)) {
      return false;
    }
    return getId() != 0 && getId() == milestone.getId();
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
