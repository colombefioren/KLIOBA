package school.hei.klioba.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ClubTest {

  @Test
  void club_creation_succeeds() {
    var club = new Club("cuisine", "Club Cuisine");

    assertNotNull(club);
    assertEquals("cuisine", club.getId());
    assertEquals("Club Cuisine", club.getName());
  }

  @Test
  void club_with_different_id_and_name() {
    var club = new Club("sport", "Club Sport");

    assertEquals("sport", club.getId());
    assertEquals("Club Sport", club.getName());
  }

  @Test
  void club_toString_contains_id_and_name() {
    var club = new Club("cuisine", "Club Cuisine");
    var str = club.toString();

    assertNotNull(str);
    assertTrue(str.contains("cuisine"));
    assertTrue(str.contains("Club Cuisine"));
  }

  @Test
  void club_with_null_id() {
    var club = new Club(null, "Club Null");

    assertNotNull(club);
    assertNull(club.getId());
    assertEquals("Club Null", club.getName());
  }

  @Test
  void club_with_null_name() {
    var club = new Club("null-name", null);

    assertNotNull(club);
    assertEquals("null-name", club.getId());
    assertNull(club.getName());
  }

  @Test
  void club_with_empty_id() {
    var club = new Club("", "Club Empty");

    assertEquals("", club.getId());
  }

  @Test
  void club_two_instances_with_same_values_are_distinct_objects() {
    var club1 = new Club("cuisine", "Club Cuisine");
    var club2 = new Club("cuisine", "Club Cuisine");

    assertNotSame(club1, club2);
    assertEquals("cuisine", club1.getId());
    assertEquals("cuisine", club2.getId());
  }
}
