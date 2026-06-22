package school.hei.klioba.conf;

import static school.hei.klioba.model.psp.vola.api.gen.client.model.PspPayment.PspTypeEnum.ORANGE_MONEY;

import java.util.UUID;
import school.hei.klioba.model.psp.vola.api.gen.client.model.Payment;
import school.hei.klioba.model.psp.vola.api.gen.client.model.Payment.VerificationStatusEnum;
import school.hei.klioba.model.psp.vola.api.gen.client.model.PspPayment;

public class VolaTestUtils {

  public static Payment aVolaPayment(VerificationStatusEnum status) {
    return aVolaPayment(status, null, UUID.randomUUID().toString());
  }

  public static Payment aVolaPayment(VerificationStatusEnum status, Integer amount, String pspId) {
    var pspPayment = new PspPayment();
    pspPayment.setId(pspId);
    pspPayment.setPspType(ORANGE_MONEY);
    if (amount != null) {
      pspPayment.setAmount(amount);
    }

    var volaPayment = new Payment();
    volaPayment.setVerificationStatus(status);
    volaPayment.setPspPayment(pspPayment);
    return volaPayment;
  }
}
