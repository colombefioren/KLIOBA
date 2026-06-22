package school.hei.klioba.service;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static school.hei.klioba.model.PaymentStatus.CONFIRMED;
import static school.hei.klioba.model.psp.vola.api.gen.client.model.Payment.VerificationStatusEnum.SUCCEEDED;
import static school.hei.klioba.model.psp.vola.api.gen.client.model.Payment.VerificationStatusEnum.VERIFYING;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import school.hei.klioba.conf.FacadeIT;
import school.hei.klioba.conf.VolaTestUtils;
import school.hei.klioba.endpoint.http.model.MembershipFeeCreationForm;
import school.hei.klioba.model.PaymentStatus;
import school.hei.klioba.model.psp.vola.api.VolaClient;
import school.hei.klioba.repository.jpa.JClubRepository;
import school.hei.klioba.repository.jpa.model.JClub;

class EventServiceIT extends FacadeIT {
  @Autowired MembershipFeeCreationFormConsumer membershipCreationFormConsumer;
  @Autowired EventService eventService;
  @Autowired JClubRepository jClubRepository;
  @MockBean VolaClient volaClientMock;

  @BeforeEach
  void setUp() {
    if (jClubRepository.findById("cuisine").isEmpty()) {
      jClubRepository.save(new JClub("cuisine", "Club Cuisine", new ArrayList<>()));
    }
  }

  private String generateValidPspId() {
    return "MP250811.1103.C" + String.format("%05d", (int) (Math.random() * 99999));
  }

  @Transactional
  @Rollback
  @Test
  void create_then_confirm() {
    var ref1 = generateValidPspId();
    var newEmail = randomUUID() + "@cute.dev";

    when(volaClientMock.create(any(), eq(ref1), eq(newEmail)))
        .thenReturn(VolaTestUtils.aVolaPayment(VERIFYING));
    membershipCreationFormConsumer.accept(
        new MembershipFeeCreationForm("Lou", "Andria", ref1), newEmail, "cuisine");

    // Just after creation, we simulate that Vola still replies with VERIFYING
    when(volaClientMock.get(any(), any(), any())).thenReturn(VolaTestUtils.aVolaPayment(VERIFYING));
    var events = eventService.findAllWithPaymentResolution();
    assertTrue(events.size() == 4 || events.size() == 1);
    assertEquals(PaymentStatus.VERIFYING, events.get(0).getPayment().status());

    // Now we simulate Vola replies with SUCCEEDED
    when(volaClientMock.get(any(), any(), any())).thenReturn(VolaTestUtils.aVolaPayment(SUCCEEDED));
    events = eventService.findAllWithPaymentResolution();
    assertTrue(events.size() == 4 || events.size() == 1);
    assertEquals(CONFIRMED, events.get(0).getPayment().status());
  }
}
