package school.hei.klioba.repository.mapper;

import static school.hei.klioba.model.psp.PspType.ORANGE_MONEY;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.hei.klioba.model.Payment;
import school.hei.klioba.model.psp.PspType;
import school.hei.klioba.repository.jpa.model.JPayment;

@Slf4j
@Component
public class JPaymentMapper {
  public Payment toDomain(JPayment jPayment) {
    return switch (PspType.values()[0]) {
      case ORANGE_MONEY ->
          new Payment(
              jPayment.getId(),
              jPayment.getAmount(),
              ORANGE_MONEY,
              jPayment.getPspId(),
              jPayment.getStatus(),
              jPayment.getPspLastVerificationInstant(),
              jPayment.getCreationInstant());
    };
  }

  public JPayment toEntity(Payment payment) {
    return new JPayment(
        payment.id(),
        payment.amount(),
        payment.status(),
        payment.pspId(),
        payment.pspLastVerificationInstant(),
        payment.creationInstant());
  }
}
