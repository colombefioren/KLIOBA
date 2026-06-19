package school.hei.klioba.endpoint.http.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static school.hei.klioba.model.PaymentStatus.CONFIRMED;
import static school.hei.klioba.model.PaymentStatus.REFUSED;
import static school.hei.klioba.model.PaymentStatus.VERIFYING;
import static school.hei.klioba.model.psp.PspType.ORANGE_MONEY;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import school.hei.klioba.model.Club;
import school.hei.klioba.model.Event;
import school.hei.klioba.model.MembershipFee;
import school.hei.klioba.model.Payment;
import school.hei.klioba.model.PaymentStatus;
import school.hei.klioba.model.User;

class ThFundTest {
  @Test
  void empty_thFunds_ok() {
    assertEquals(
        "Cotisations confirmées: 0 Ar. Retraits confirmés: 0 Ar. Fonds restants confirmés: 0 Ar.",
        new ThFund(List.of()).toString());
  }

  @Test
  void non_empty_thFunds_ok() {
    var user = new User("userId", "Lou", "Andria", "lou@hei.school");

    assertEquals(
        "Cotisations confirmées: 15 Ar. Retraits confirmés: -11 Ar. Fonds restants confirmés: 4 Ar.",
        new ThFund(
                List.of(
                    anEvent(user, null, VERIFYING),
                    anEvent(user, 10, CONFIRMED),
                    anEvent(user, 5, CONFIRMED),
                    anEvent(user, null, REFUSED),
                    anEvent(user, -7, CONFIRMED),
                    anEvent(user, -4, CONFIRMED)))
            .toString());
  }

  private static Event anEvent(User user, Integer amount, PaymentStatus paymentStatus) {
    var payment =
        new Payment(
            "paymentId",
            amount,
            ORANGE_MONEY,
            "pspId",
            paymentStatus,
            Instant.parse("2025-08-11T13:51:26.165532Z"),
            Instant.parse("2025-08-11T13:51:36.165532Z"));
    var club = new Club("c1", "Club 1");
    var event =
        new MembershipFee(
            "eventId", payment, user, club, Instant.parse("2025-08-11T13:51:16.165532Z"));
    return event;
  }
}
