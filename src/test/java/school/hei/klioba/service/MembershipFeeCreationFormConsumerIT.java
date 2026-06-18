package school.hei.klioba.service;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static school.hei.klioba.model.PaymentStatus.VERIFYING;
import static school.hei.klioba.model.psp.vola.api.gen.client.model.Payment.VerificationStatusEnum;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.hei.klioba.conf.FacadeIT;
import school.hei.klioba.conf.VolaTestUtils;
import school.hei.klioba.endpoint.http.model.MembershipFeeCreationForm;
import school.hei.klioba.model.Event;
import school.hei.klioba.model.psp.vola.api.VolaClient;
import school.hei.klioba.repository.jpa.JClubRepository;
import school.hei.klioba.repository.jpa.model.JClub;

class MembershipFeeCreationFormConsumerIT extends FacadeIT {

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

  @Test
  void donate_then_read_donations() {
    var ref1 = generateValidPspId();
    var ref2 = generateValidPspId();
    var newEmail = randomUUID() + "@cute.dev";

    when(volaClientMock.create(any(), eq(ref1), eq(newEmail)))
        .thenReturn(VolaTestUtils.aVolaPayment(VerificationStatusEnum.VERIFYING, null, ref1));
    when(volaClientMock.create(any(), eq(ref2), eq(newEmail)))
        .thenReturn(VolaTestUtils.aVolaPayment(VerificationStatusEnum.VERIFYING, null, ref2));

    membershipCreationFormConsumer.accept(
        new MembershipFeeCreationForm("Lou", "Andria", ref1), newEmail, "cuisine");
    membershipCreationFormConsumer.accept(
        new MembershipFeeCreationForm(null, null, ref2), newEmail, "cuisine");

    var events = eventService.findAllWithPaymentResolution();
    assertEquals(2, events.size());

    var payment1 =
        events.stream()
            .map(Event::getPayment)
            .filter(p -> ref1.equals(p.pspId()))
            .findFirst()
            .orElseThrow();
    assertEquals(VERIFYING, payment1.status());
    assertNull(payment1.amount());
    assertNull(payment1.pspLastVerificationInstant());

    var user =
        events.stream()
            .filter(e -> ref2.equals(e.getPayment().pspId()))
            .map(Event::getUser)
            .findFirst()
            .orElseThrow();
    assertEquals("Lou", user.getFirstName());
    assertEquals("Andria", user.getLastName());
  }

  @Test
  void donations_cannot_have_same_pspId() {
    String pspId = generateValidPspId();

    when(volaClientMock.create(any(), any(), any()))
        .thenReturn(VolaTestUtils.aVolaPayment(VerificationStatusEnum.VERIFYING, null, pspId));

    membershipCreationFormConsumer.accept(
        new MembershipFeeCreationForm("Lou", "Andria", pspId), "lou@cute.dev", "cuisine");

    assertThrows(
        IllegalArgumentException.class,
        () ->
            membershipCreationFormConsumer.accept(
                new MembershipFeeCreationForm(null, null, pspId), "lou@cute.dev", "cuisine"));
  }

  @Test
  void donation_with_invalid_pspId_shouldFail() {
    String invalidPspId = randomUUID().toString();

    assertThrows(
        IllegalArgumentException.class,
        () ->
            membershipCreationFormConsumer.accept(
                new MembershipFeeCreationForm("Lou", "Andria", invalidPspId),
                "lou@cute.dev",
                "cuisine"));
  }
}
