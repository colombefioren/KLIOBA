package school.hei.klioba.repository.jpa.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "\"user\"")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class JUser {
  @Id private String id;

  private String email;
  private String firstName;
  private String lastName;

  @ManyToMany
  @JoinTable(
      name = "club_membership",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "club_id"))
  private List<JClub> clubs = new ArrayList<>();
}
