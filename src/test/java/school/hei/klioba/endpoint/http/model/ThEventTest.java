package school.hei.klioba.endpoint.http.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static school.hei.klioba.model.PaymentStatus.CONFIRMED;
import static school.hei.klioba.model.PaymentStatus.REFUSED;
import static school.hei.klioba.model.PaymentStatus.VERIFYING;
import static school.hei.klioba.model.psp.PspType.ORANGE_MONEY;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import school.hei.klioba.model.Club;
import school.hei.klioba.model.MembershipFee;
import school.hei.klioba.model.Payment;
import school.hei.klioba.model.User;
import school.hei.klioba.model.Withdrawal;

class ThEventTest {

  @Test
  void confirmed_donation_to_string() {
    var payment =
        new Payment(
            "paymentId",
            17,
            ORANGE_MONEY,
            "pspId",
            CONFIRMED,
            Instant.parse("2025-08-11T13:51:26.165532Z"),
            Instant.parse("2025-08-11T13:51:36.165532Z"));
    var user = new User("userId", "Lou", "Andria", "lou@hei.school");

    var club = new Club("c1", "Club 1");
    var thEvent =
        new ThEvent(
            new MembershipFee(
                "eventId", payment, user, club, Instant.parse("2025-08-11T13:51:16.165532Z")));

    assertEquals(
        "2025-08-11 16:51:16, 17 Ar. Par Lou Andria<lou@hei.school>. Statut: en succès, récupéré le"
            + " 2025-08-11 16:51:26. Payé via ORANGE_MONEY, réf: pspId.",
        thEvent.toString());
    assertEquals("black", thEvent.color());
  }

  @Test
  void confirmed_help_to_string() {
    var payment =
        new Payment(
            "paymentId",
            -17,
            ORANGE_MONEY,
            "pspId",
            CONFIRMED,
            Instant.parse("2025-08-11T13:51:36.165532Z"),
            Instant.parse("2025-08-11T13:51:36.165532Z"));

    var user = new User("userId", "Lou", "Andria", "lou@hei.school");

    var club = new Club("c1", "Club 1");
    var thEvent =
        new ThEvent(
            new Withdrawal(
                "eventId", payment, user, club, Instant.parse("2025-08-11T13:51:16.165532Z"), ""));

    assertEquals(
        "2025-08-11 16:51:16, -17 Ar. Pour Lou Andria<lou@hei.school>. ", thEvent.toString());
    assertEquals("blue", thEvent.color());
  }

  @Test
  void verifying_withNo_lastPspVerificationInstant_to_string() {
    var payment =
        new Payment(
            "paymentId",
            null,
            ORANGE_MONEY,
            "pspId",
            VERIFYING,
            null,
            Instant.parse("2025-08-11T13:51:36.165532Z"));
    var user = new User("userId", "Lou", "Andria", "lou@hei.school");

    var club = new Club("c1", "Club 1");
    var thEvent =
        new ThEvent(
            new MembershipFee(
                "eventId", payment, user, club, Instant.parse("2025-08-11T13:51:16.165532Z")));

    assertEquals(
        "2025-08-11 16:51:16. Par Lou Andria<lou@hei.school>. Statut: en vérification, récupéré le"
            + " --. Payé via ORANGE_MONEY, réf: pspId.",
        thEvent.toString());
    assertEquals("lightgray", thEvent.color());
  }

  @Test
  void refused_with_lastPspVerificationInstant_to_string() {
    var payment =
        new Payment(
            "paymentId",
            null,
            ORANGE_MONEY,
            "pspId",
            REFUSED,
            Instant.parse("2025-08-11T13:51:36.165532Z"),
            Instant.parse("2025-08-11T13:51:36.165532Z"));

    var user = new User("userId", "Lou", "Andria", "lou@hei.school");

    var club = new Club("c1", "Club 1");
    var thEvent =
        new ThEvent(
            new MembershipFee(
                "eventId", payment, user, club, Instant.parse("2025-08-11T13:51:16.165532Z")));

    assertEquals(
        "2025-08-11 16:51:16. Par Lou Andria<lou@hei.school>. Statut: en échec, récupéré le"
            + " 2025-08-11 16:51:36. Payé via ORANGE_MONEY, réf: pspId.",
        thEvent.toString());
    assertEquals("red", thEvent.color());
  }
}
