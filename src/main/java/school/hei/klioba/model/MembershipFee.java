package school.hei.klioba.model;

import java.time.Instant;

public final class MembershipFee extends Event {
  public MembershipFee(String id, Payment payment, User user, Club club, Instant creationInstant) {
    super(id, payment, user, club, creationInstant, "");
  }

  @Override
  public Event withPayment(Payment newPayment) {
    return new MembershipFee(id, newPayment, user, club, creationInstant);
  }
}
