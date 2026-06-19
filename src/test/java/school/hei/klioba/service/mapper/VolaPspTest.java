package school.hei.klioba.service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static school.hei.klioba.model.psp.PspType.ORANGE_MONEY;

import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.Test;
import school.hei.klioba.model.Payment;
import school.hei.klioba.model.psp.vola.VolaPsp;
import school.hei.klioba.model.psp.vola.api.VolaClient;
import school.hei.klioba.model.psp.vola.api.gen.client.model.PspPayment;

public class VolaPspTest {

  @Test
  void creationInstant_should_be_mapped_from_pspPayment() {
    String kliobaId = "klioba-test";
    String pspId = "MP250805.0922.B95953";
    String email = "ninah@mail.hei.school";

    var rawPayment = createRawPaymentWithPspPayment(pspId);
    VolaClient volaClient = mock(VolaClient.class);
    when(volaClient.get(ORANGE_MONEY, pspId, email)).thenReturn(rawPayment);

    VolaPsp volaPsp = new VolaPsp(volaClient);
    Payment mapped = volaPsp.get(kliobaId, ORANGE_MONEY, pspId, email);

    assertPaymentMappedFromPspPayment(mapped, pspId);
  }

  private school.hei.klioba.model.psp.vola.api.gen.client.model.Payment
      createRawPaymentWithPspPayment(String pspId) {
    var raw = new school.hei.klioba.model.psp.vola.api.gen.client.model.Payment();
    raw.setId("vola-raw-id-1");

    var psp = createPspPayment(pspId);
    raw.setPspPayment(psp);

    raw.setCreationInstant(Date.from(Instant.parse("2025-09-04T14:45:44.142Z")));
    raw.setLastPspVerificationInstant(Date.from(Instant.parse("2025-09-05T08:22:02.683Z")));
    raw.setVerificationStatus(
        school.hei.klioba.model.psp.vola.api.gen.client.model.Payment.VerificationStatusEnum
            .SUCCEEDED);

    return raw;
  }

  private PspPayment createPspPayment(String pspId) {
    var psp = new PspPayment();
    psp.setPspType(PspPayment.PspTypeEnum.ORANGE_MONEY);
    psp.setId(pspId);
    psp.setAmount(3000);
    var expectedPspCreation = Instant.parse("2025-09-04T17:41:55Z");
    psp.setCreationInstant(Date.from(expectedPspCreation));
    return psp;
  }

  private void assertPaymentMappedFromPspPayment(Payment mapped, String pspId) {
    assertNotNull(mapped, "Mapped Payment should not be null");
    assertNotNull(mapped.creationInstant(), "Mapped creationInstant should not be null");

    var expectedPspCreation = Instant.parse("2025-09-04T17:41:55Z");
    assertEquals(
        expectedPspCreation,
        mapped.creationInstant(),
        "creationInstant must be taken from pspPayment.creationInstant when present");

    assertEquals(3000, mapped.amount(), "amount should be mapped from pspPayment.amount");
    assertEquals(pspId, mapped.pspId(), "pspId should be mapped");
  }
}
