package school.hei.klioba.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import school.hei.klioba.model.psp.PspType;

class PaymentTest {

  @Test
  void payment_builder_createsPayment() {
    var now = Instant.now();

    var payment =
        Payment.builder()
            .id("p1")
            .amount(1000)
            .pspType(PspType.ORANGE_MONEY)
            .pspId("PSP123")
            .status(PaymentStatus.CONFIRMED)
            .pspLastVerificationInstant(now)
            .creationInstant(now)
            .build();

    assertNotNull(payment);
    assertEquals("p1", payment.id());
    assertEquals(1000, payment.amount());
    assertEquals(PspType.ORANGE_MONEY, payment.pspType());
    assertEquals("PSP123", payment.pspId());
    assertEquals(PaymentStatus.CONFIRMED, payment.status());
    assertEquals(now, payment.pspLastVerificationInstant());
    assertEquals(now, payment.creationInstant());
  }

  @Test
  void payment_withNullAmount_works() {
    var now = Instant.now();

    var payment =
        Payment.builder()
            .id("p1")
            .amount(null)
            .pspType(PspType.ORANGE_MONEY)
            .pspId("PSP123")
            .status(PaymentStatus.VERIFYING)
            .pspLastVerificationInstant(null)
            .creationInstant(now)
            .build();

    assertNotNull(payment);
    assertNull(payment.amount());
    assertNull(payment.pspLastVerificationInstant());
  }

  @Test
  void payment_record_equality_works() {
    Instant now = Instant.now();
    Payment payment1 =
        new Payment("p1", 1000, PspType.ORANGE_MONEY, "PSP123", PaymentStatus.CONFIRMED, now, now);
    Payment payment2 =
        new Payment("p1", 1000, PspType.ORANGE_MONEY, "PSP123", PaymentStatus.CONFIRMED, now, now);

    assertEquals(payment1, payment2);
    assertEquals(payment1.hashCode(), payment2.hashCode());
  }
}
