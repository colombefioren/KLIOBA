package school.hei.klioba.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.hei.klioba.endpoint.http.model.MembershipFeeCreationForm;
import school.hei.klioba.model.Club;
import school.hei.klioba.model.MembershipFee;
import school.hei.klioba.model.Payment;
import school.hei.klioba.model.PaymentStatus;
import school.hei.klioba.model.User;
import school.hei.klioba.model.psp.PspType;

public class MembershipFormServiceTest {

  private EventService eventService;
  private MembershipFormService membershipFormService;

  @BeforeEach
  void setUp() {
    eventService = mock(EventService.class);
    membershipFormService = new MembershipFormService(eventService);
  }

  @Test
  void getPrefilledDonationForm_noPreviousEvent_returnsEmptyForm() {
    String email = "user@example.com";
    when(eventService.findAllWithPaymentResolution()).thenReturn(List.of());

    MembershipFeeCreationForm form = membershipFormService.getPrefilledDonationForm(email);

    assertEquals("", form.firstName());
    assertEquals("", form.lastName());
    assertEquals("", form.pspId());
  }

  @Test
  void getPrefilledDonationForm_hasPreviousEvent_returnsPrefilledForm() {
    String email = "user@example.com";

    Club club = new Club("c1", "Club 1");
    User user = new User("1", "Tiavina", "Andriamamivony", email);
    Payment payment =
        new Payment(
            "p1",
            1000,
            PspType.ORANGE_MONEY,
            "MP240201.1234.A12345",
            PaymentStatus.CONFIRMED,
            Instant.now(),
            Instant.parse("2025-08-11T13:51:36.165532Z"));
    MembershipFee donation = new MembershipFee("d1", payment, user, club, Instant.now());

    when(eventService.findAllWithPaymentResolution()).thenReturn(List.of(donation));

    MembershipFeeCreationForm form = membershipFormService.getPrefilledDonationForm(email);

    assertEquals("Tiavina", form.firstName());
    assertEquals("Andriamamivony", form.lastName());
    assertEquals("", form.pspId());
  }

  @Test
  void getPrefilledDonationForm_multipleEvents_returnsLatestForUser() {
    String email = "user@example.com";

    Club club = new Club("c1", "Club 1");
    User user1 = new User("1", "Alice", "Smith", email);
    Payment payment1 =
        new Payment(
            "p1",
            500,
            PspType.ORANGE_MONEY,
            "PSP1",
            PaymentStatus.CONFIRMED,
            Instant.now().minusSeconds(3600),
            Instant.parse("2025-08-11T13:51:36.165532Z"));
    MembershipFee donation1 =
        new MembershipFee("d1", payment1, user1, club, Instant.now().minusSeconds(3600));

    User user2 = new User("2", "Tiavina", "Andriamamivony", email);
    Payment payment2 =
        new Payment(
            "p2",
            1000,
            PspType.ORANGE_MONEY,
            "PSP2",
            PaymentStatus.CONFIRMED,
            Instant.now(),
            Instant.parse("2025-08-11T13:51:36.165532Z"));
    MembershipFee donation2 = new MembershipFee("d2", payment2, user2, club, Instant.now());

    when(eventService.findAllWithPaymentResolution()).thenReturn(List.of(donation1, donation2));

    MembershipFeeCreationForm form = membershipFormService.getPrefilledDonationForm(email);

    assertEquals("Tiavina", form.firstName());
    assertEquals("Andriamamivony", form.lastName());
    assertEquals("", form.pspId());
  }
}
