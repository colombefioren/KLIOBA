package school.hei.klioba.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class UserTest {

  @Test
  void member_creation_succeeds() {
    var member = new Member("d1", "John", "Doe", "john@example.com");

    assertNotNull(member);
    assertEquals("d1", member.getId());
    assertEquals("John", member.getFirstName());
    assertEquals("Doe", member.getLastName());
    assertEquals("john@example.com", member.getEmail());
  }

  @Test
  void withdrawer_creation_succeeds() {
    Withdrawer withdrawer = new Withdrawer("b1", "Jane", "Smith", "jane@example.com");

    assertNotNull(withdrawer);
    assertEquals("b1", withdrawer.getId());
    assertEquals("Jane", withdrawer.getFirstName());
    assertEquals("Smith", withdrawer.getLastName());
    assertEquals("jane@example.com", withdrawer.getEmail());
  }

  @Test
  void user_toString_works() {
    var user = new User("u1", "Alice", "Wonder", "alice@example.com");

    var result = user.toString();

    assertNotNull(result);
    assertEquals("User(id=u1, firstName=Alice, lastName=Wonder, email=alice@example.com)", result);
  }

  @Test
  void member_toString_works() {
    var member = new Member("d1", "Bob", "Builder", "bob@example.com");

    var result = member.toString();

    assertNotNull(result);
    assertEquals("User(id=d1, firstName=Bob, lastName=Builder, email=bob@example.com)", result);
  }
}
