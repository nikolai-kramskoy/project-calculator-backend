package org.example.projectcalculator.model;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "team_member")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TeamMember {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "team_member_id_sequence")
  @SequenceGenerator(name = "team_member_id_sequence", allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false, updatable = false)
  private long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "team_member_position", nullable = false)
  private Position position;

  @Column(name = "number_of_team_members", nullable = false)
  private BigDecimal numberOfTeamMembers;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "project_id", nullable = false, updatable = false)
  @ToString.Exclude
  private Project project;

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof TeamMember teamMember)) {
      return false;
    }
    return getId() != 0 && getId() == teamMember.getId();
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
