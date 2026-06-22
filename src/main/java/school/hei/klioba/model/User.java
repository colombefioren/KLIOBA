package school.hei.klioba.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public sealed class User permits Member, Withdrawer {
  private final String id;
  private final String firstName;
  private final String lastName;
  private final String email;
}
