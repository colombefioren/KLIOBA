package school.hei.klioba.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@AllArgsConstructor
@Getter
public abstract sealed class Event permits MembershipFee, Withdrawal {
  protected final String id;
  protected final Payment payment;
  protected final User user;
  protected final Club club;
  protected final Instant creationInstant;
  protected final String comment;

  public static Event from(
      String id, Payment payment, User user, Club club, Instant creationInstant, String comment) {
    return payment.amount() == null || payment.amount() >= 0
        ? new MembershipFee(id, payment, user, club, creationInstant)
        : new Withdrawal(id, payment, user, club, creationInstant, comment);
  }

  public abstract Event withPayment(Payment newPayment);
}
