package school.hei.klioba.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import school.hei.klioba.model.psp.PspType;

class WithdrawalTest {

  @Test
  void withdrawal_creation_succeeds() {
    var payment =
        new Payment(
            "p1",
            -1000,
            PspType.ORANGE_MONEY,
            "PSP123",
            PaymentStatus.CONFIRMED,
            Instant.now(),
            Instant.now());
    var user = new User("u1", "Jane", "Doe", "jane@example.com");
    Instant creationInstant = Instant.now();

    var club = new Club("c1", "Club 1");
    var withdrawal = new Withdrawal("h1", payment, user, club, creationInstant, "");

    assertNotNull(withdrawal);
    assertEquals("h1", withdrawal.getId());
    assertEquals(payment, withdrawal.getPayment());
    assertEquals(user, withdrawal.getUser());
    assertEquals(creationInstant, withdrawal.getCreationInstant());
  }

  @Test
  void withdrawal_toString_works() {
    // Arrange
    var payment =
        new Payment(
            "p1",
            -1000,
            PspType.ORANGE_MONEY,
            "PSP123",
            PaymentStatus.CONFIRMED,
            Instant.parse("2025-08-11T13:51:26.165532Z"),
            Instant.parse("2025-08-11T13:51:36.165532Z"));
    var user = new User("u1", "Jane", "Doe", "jane@example.com");
    var club = new Club("c1", "Club 1");
    var withdrawal = new Withdrawal("h1", payment, user, club, Instant.now(), "");

    assertNotNull(withdrawal.toString());
  }
}
