package school.hei.klioba.model.psp.vola;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static school.hei.klioba.model.PaymentStatus.CONFIRMED;
import static school.hei.klioba.model.psp.PspType.ORANGE_MONEY;
import static school.hei.klioba.model.psp.vola.api.gen.client.model.Payment.VerificationStatusEnum;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.hei.klioba.conf.FacadeIT;
import school.hei.klioba.conf.VolaTestUtils;
import school.hei.klioba.model.psp.vola.api.VolaClient;

class VolaPspIT extends FacadeIT {
  @Autowired VolaPsp volaPsp;
  @MockBean VolaClient volaClientMock;

  @Test
  void read_succeeded_payment() {
    var volaPaymentMock =
        VolaTestUtils.aVolaPayment(
            VerificationStatusEnum.SUCCEEDED, 324_000, "MP250729.1216.D77954");
    when(volaClientMock.get(any(), any(), any())).thenReturn(volaPaymentMock);

    var volaPayment =
        volaPsp.get(
            "d1b7f126-677f-4dfb-b871-87b5efcd70e7",
            ORANGE_MONEY,
            "MP250729.1216.D77954",
            "lou@hei.school");

    assertEquals(324_000, volaPayment.amount());
    assertEquals(CONFIRMED, volaPayment.status());
  }
}
