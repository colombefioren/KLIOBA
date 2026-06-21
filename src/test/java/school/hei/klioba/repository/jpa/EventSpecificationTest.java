package school.hei.klioba.repository.jpa;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class EventSpecificationTest {

  @Test
  void clubIdEquals_returnsSpecification() {
    assertNotNull(EventSpecification.clubIdEquals("c1"));
  }

  @Test
  void searchMatches_null_isSafe() {
    assertDoesNotThrow(() -> EventSpecification.searchMatches(null));
  }

  @Test
  void searchMatches_blank_isSafe() {
    assertDoesNotThrow(() -> EventSpecification.searchMatches(""));
  }

  @Test
  void searchMatches_nonBlank_returnsSpecification() {
    assertNotNull(EventSpecification.searchMatches("john"));
  }

  @Test
  void dateFromAfter_null_isSafe() {
    assertDoesNotThrow(() -> EventSpecification.dateFromAfter(null));
  }

  @Test
  void dateToBefore_null_isSafe() {
    assertDoesNotThrow(() -> EventSpecification.dateToBefore(null));
  }

  @Test
  void dateFromAfter_nonNull_returnsSpecification() {
    assertNotNull(EventSpecification.dateFromAfter(Instant.parse("2024-01-01T00:00:00Z")));
  }

  @Test
  void dateToBefore_nonNull_returnsSpecification() {
    assertNotNull(EventSpecification.dateToBefore(Instant.parse("2024-12-31T23:59:59Z")));
  }
}
