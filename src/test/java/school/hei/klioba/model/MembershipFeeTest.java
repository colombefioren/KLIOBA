package school.hei.klioba.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import school.hei.klioba.model.psp.PspType;

class MembershipFeeTest {

  @Test
  void membershipFee_creation_succeeds() {
    var payment =
        new Payment(
            "p1",
            1000,
            PspType.ORANGE_MONEY,
            "PSP123",
            PaymentStatus.CONFIRMED,
            Instant.now(),
            Instant.now());
    var user = new User("u1", "John", "Doe", "john@example.com");
    var creationInstant = Instant.now();

    var club = new Club("c1", "Club 1");
    var membershipFee = new MembershipFee("d1", payment, user, club, creationInstant);

    assertNotNull(membershipFee);
    assertEquals("d1", membershipFee.getId());
    assertEquals(payment, membershipFee.getPayment());
    assertEquals(user, membershipFee.getUser());
    assertEquals(creationInstant, membershipFee.getCreationInstant());
  }

  @Test
  void membershipFee_toString_works() {
    // Arrange
    var payment =
        new Payment(
            "p1",
            1000,
            PspType.ORANGE_MONEY,
            "PSP123",
            PaymentStatus.CONFIRMED,
            Instant.parse("2025-08-11T13:51:26.165532Z"),
            Instant.parse("2025-08-11T13:51:36.165532Z"));
    var user = new User("u1", "John", "Doe", "john@example.com");
    var club = new Club("c1", "Club 1");
    var membershipFee = new MembershipFee("d1", payment, user, club, Instant.now());

    assertNotNull(membershipFee.toString());
  }
}
