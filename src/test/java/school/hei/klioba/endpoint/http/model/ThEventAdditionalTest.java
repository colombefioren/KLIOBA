package school.hei.klioba.endpoint.http.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static school.hei.klioba.model.PaymentStatus.UNKNOWN;
import static school.hei.klioba.model.psp.PspType.ORANGE_MONEY;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import school.hei.klioba.model.Club;
import school.hei.klioba.model.MembershipFee;
import school.hei.klioba.model.Payment;
import school.hei.klioba.model.User;
import school.hei.klioba.model.Withdrawal;

class ThEventAdditionalTest {

  @Test
  void unknown_status_to_string() {
    var payment =
        new Payment(
            "paymentId",
            null,
            ORANGE_MONEY,
            "pspId",
            UNKNOWN,
            Instant.parse("2025-08-11T13:51:36.165532Z"),
            Instant.parse("2025-08-11T13:51:36.165532Z"));

    var user = new User("userId", "Lou", "Andria", "lou@hei.school");

    var club = new Club("c1", "Club 1");
    var thEvent =
        new ThEvent(
            new MembershipFee(
                "eventId", payment, user, club, Instant.parse("2025-08-11T13:51:16.165532Z")));

    assertEquals(
        "2025-08-11 16:51:16. Par Lou Andria<lou@hei.school>. Statut: inconnu, récupéré le"
            + " 2025-08-11 16:51:36. Payé via ORANGE_MONEY, réf: pspId.",
        thEvent.toString());
    assertEquals("yellow", thEvent.color());
  }

  @Test
  void unknown_status_with_negativeAmount_hasBlueColor() {
    var payment =
        new Payment(
            "paymentId",
            -100,
            ORANGE_MONEY,
            "pspId",
            UNKNOWN,
            Instant.parse("2025-08-11T13:51:36.165532Z"),
            Instant.parse("2025-08-11T13:51:36.165532Z"));

    var user = new User("userId", "Lou", "Andria", "lou@hei.school");

    var club = new Club("c1", "Club 1");
    var thEvent =
        new ThEvent(
            new Withdrawal(
                "eventId", payment, user, club, Instant.parse("2025-08-11T13:51:16.165532Z"), ""));

    assertEquals("yellow", thEvent.color());
  }

  @Test
  void confirmed_status_with_zeroAmount_hasBlackColor() {
    var payment =
        new Payment(
            "paymentId",
            0,
            ORANGE_MONEY,
            "pspId",
            school.hei.klioba.model.PaymentStatus.CONFIRMED,
            Instant.parse("2025-08-11T13:51:36.165532Z"),
            Instant.parse("2025-08-11T13:51:36.165532Z"));

    var user = new User("userId", "Lou", "Andria", "lou@hei.school");

    var club = new Club("c1", "Club 1");
    var thEvent =
        new ThEvent(
            new MembershipFee(
                "eventId", payment, user, club, Instant.parse("2025-08-11T13:51:16.165532Z")));

    assertEquals("black", thEvent.color());
  }

  @Test
  void withdrawal_without_statusDetails_hasNoPaymentInfo() {
    var payment =
        new Payment(
            "paymentId",
            -500,
            ORANGE_MONEY,
            "pspId",
            school.hei.klioba.model.PaymentStatus.CONFIRMED,
            Instant.parse("2025-08-11T13:51:36.165532Z"),
            Instant.parse("2025-08-11T13:51:36.165532Z"));

    var user = new User("userId", "Alice", "Smith", "alice@example.com");

    var club = new Club("c1", "Club 1");
    var thEvent =
        new ThEvent(
            new Withdrawal(
                "eventId",
                payment,
                user,
                club,
                Instant.parse("2025-08-11T13:51:16.165532Z"),
                "OUT"));

    assertEquals(
        "2025-08-11 16:51:16, -500 Ar. Pour Alice Smith<alice@example.com>. ", thEvent.toString());
  }
}
