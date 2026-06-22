package school.hei.klioba.model;

import java.time.Instant;

public final class Withdrawal extends Event {
  public Withdrawal(
      String id, Payment payment, User user, Club club, Instant creationInstant, String comment) {
    super(id, payment, user, club, creationInstant, comment);
  }

  @Override
  public Event withPayment(Payment newPayment) {
    return new Withdrawal(id, newPayment, user, club, creationInstant, comment);
  }
}
