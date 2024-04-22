package org.example.projectcalculator.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "project")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Project {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "project_id_sequence")
  @SequenceGenerator(name = "project_id_sequence", allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false, updatable = false)
  private long id;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "description", nullable = false)
  private String description;

  @Column(name = "client", nullable = false)
  private String client;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "creator_id", nullable = false, updatable = false)
  @ToString.Exclude
  private User creator;

  @Column(name = "estimate_in_days", nullable = false)
  private BigDecimal estimateInDays;

  @OneToMany(mappedBy = "project")
  @ToString.Exclude
  private List<Rate> rates;

  @OneToMany(mappedBy = "project")
  @ToString.Exclude
  private List<TeamMember> teamMembers;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "last_updated_at", nullable = false)
  private LocalDateTime lastUpdatedAt;

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Project project)) {
      return false;
    }
    return getId() != 0 && getId() == project.getId();
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
