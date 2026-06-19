package school.hei.klioba.model.psp.vola;

import static school.hei.klioba.model.psp.PspType.ORANGE_MONEY;
import static school.hei.klioba.model.psp.vola.api.gen.client.model.Payment.VerificationStatusEnum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import school.hei.klioba.model.Payment;
import school.hei.klioba.model.PaymentStatus;
import school.hei.klioba.model.psp.Psp;
import school.hei.klioba.model.psp.PspType;
import school.hei.klioba.model.psp.vola.api.VolaClient;
import school.hei.klioba.model.psp.vola.api.gen.client.model.PspPayment;

@Slf4j
@AllArgsConstructor
public class VolaPsp implements Psp {
  private final VolaClient volaClient;

  @Override
  public Payment create(String kliobaId, PspType pspType, String pspId, String email) {
    var volaPayment = volaClient.create(pspType, pspId, email);
    return toPayment(kliobaId, volaPayment);
  }

  @Override
  public Payment get(String kliobaId, PspType pspType, String pspId, String email) {
    var volaPayment = volaClient.get(pspType, pspId, email);
    return toPayment(kliobaId, volaPayment);
  }

  private Payment toPayment(
      String kliobaId, school.hei.klioba.model.psp.vola.api.gen.client.model.Payment volaPayment) {
    if (kliobaId == null) {
      throw new IllegalArgumentException("kliobaId cannot be null");
    }
    if (volaPayment == null) {
      throw new IllegalArgumentException("Vola payment is null for kliobaId: " + kliobaId);
    }

    var volaPspPayment = volaPayment.getPspPayment();

    var lastVerificationInstant =
        volaPayment.getLastPspVerificationInstant() != null
            ? volaPayment.getLastPspVerificationInstant().toInstant()
            : null;

    var creationInstant =
        volaPspPayment == null || volaPspPayment.getCreationInstant() == null
            ? null
            : volaPspPayment.getCreationInstant().toInstant();

    var status = toPaymentStatus(volaPayment.getVerificationStatus());

    return volaPspPayment == null
        ? Payment.builder()
            .id(kliobaId)
            .status(status)
            .pspLastVerificationInstant(lastVerificationInstant)
            .creationInstant(null)
            .build()
        : Payment.builder()
            .id(kliobaId)
            .amount(volaPspPayment.getAmount())
            .pspType(toPspType(volaPspPayment.getPspType()))
            .pspId(volaPspPayment.getId())
            .status(status)
            .pspLastVerificationInstant(lastVerificationInstant)
            .creationInstant(creationInstant)
            .build();
  }

  private PspType toPspType(PspPayment.PspTypeEnum volaPspType) {
    return switch (volaPspType) {
      case ORANGE_MONEY -> ORANGE_MONEY;
    };
  }

  private PaymentStatus toPaymentStatus(VerificationStatusEnum volaPaymentStatus) {
    if (volaPaymentStatus == null) {
      return PaymentStatus.UNKNOWN;
    }

    return switch (volaPaymentStatus) {
      case VERIFYING -> PaymentStatus.VERIFYING;
      case SUCCEEDED -> PaymentStatus.CONFIRMED;
      case FAILED -> PaymentStatus.REFUSED;
    };
  }
}
