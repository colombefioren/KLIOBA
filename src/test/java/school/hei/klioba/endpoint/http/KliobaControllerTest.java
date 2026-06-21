package school.hei.klioba.endpoint.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.ui.Model;
import school.hei.klioba.endpoint.http.model.MembershipFeeCreationForm;
import school.hei.klioba.model.Club;
import school.hei.klioba.model.MembershipFee;
import school.hei.klioba.model.Payment;
import school.hei.klioba.model.PaymentStatus;
import school.hei.klioba.model.User;
import school.hei.klioba.model.psp.PspType;
import school.hei.klioba.repository.ClubRepository;
import school.hei.klioba.service.ClubService;
import school.hei.klioba.service.EventService;
import school.hei.klioba.service.MembershipFeeCreationFormConsumer;
import school.hei.klioba.service.MembershipFormService;

class KliobaControllerTest {

  private KliobaController controller;
  private EventService eventService;
  private MembershipFeeCreationFormConsumer membershipCreationFormConsumer;
  private ClubRepository clubRepository;
  private MembershipFormService membershipFormService;
  private ClubService clubService;
  private Model model;
  private Authentication authentication;

  @BeforeEach
  void setUp() {
    eventService = mock(EventService.class);
    membershipCreationFormConsumer = mock(MembershipFeeCreationFormConsumer.class);
    clubRepository = mock(ClubRepository.class);
    membershipFormService = mock(MembershipFormService.class);
    clubService = mock(ClubService.class);
    model = mock(Model.class);
    authentication = mock(Authentication.class);

    controller =
        new KliobaController(
            eventService,
            membershipCreationFormConsumer,
            clubRepository,
            membershipFormService,
            clubService);
  }

  @Test
  void home_returnsHomeView() {
    String result = controller.home();

    assertEquals("home", result);
  }

  @Test
  void dashboard_returnsDashboardViewWithStatistics() {
    var clubs =
        List.of(
            new ClubService.ClubStats("c1", "Club 1", 1000, 5, 800),
            new ClubService.ClubStats("c2", "Club 2", 2000, 10, 1500));

    when(clubService.getAllClubStats()).thenReturn(clubs);

    String result = controller.dashboard(authentication, model);

    assertEquals("dashboard", result);

    verify(model).addAttribute("clubs", clubs);
    verify(model).addAttribute("totalCotisations", 3000);
    verify(model).addAttribute("totalDepenses", 700); // (1000-800)+(2000-1500)
    verify(model).addAttribute("totalRemaining", 2300);
    verify(model).addAttribute("totalMembers", 15);
  }

  @Test
  void historyByClub_withDefaultPagination_returnsHistoryView() {
    var club = new Club("c1", "Club 1");
    var user = new User("1", "John", "Doe", "john@example.com");
    var payment =
        new Payment(
            "p1",
            1000,
            PspType.ORANGE_MONEY,
            "PSP123",
            PaymentStatus.CONFIRMED,
            Instant.now(),
            Instant.now());
    MembershipFee fee = new MembershipFee("d1", payment, user, club, Instant.now());

    when(clubRepository.findById("c1")).thenReturn(java.util.Optional.of(club));
    when(eventService.findAllByClubIdWithPaymentResolution("c1")).thenReturn(List.of(fee));

    String result = controller.historyByClub("c1", model, 0, 50);

    assertEquals("history", result);
    verify(model).addAttribute(eq("events"), anyList());
    verify(model).addAttribute(eq("fund"), any());
    verify(model).addAttribute("currentPage", 0);
    verify(model).addAttribute("totalPages", 1);
  }

  @Test
  void historyByClub_withCustomPagination_returnsPagedEvents() {
    var club = new Club("c1", "Club 1");
    var user = new User("1", "John", "Doe", "john@example.com");
    var payment =
        new Payment(
            "p1",
            1000,
            PspType.ORANGE_MONEY,
            "PSP123",
            PaymentStatus.CONFIRMED,
            Instant.now(),
            Instant.now());

    List<school.hei.klioba.model.Event> events =
        List.of(
            new MembershipFee("d1", payment, user, club, Instant.now()),
            new MembershipFee("d2", payment, user, club, Instant.now()),
            new MembershipFee("d3", payment, user, club, Instant.now()),
            new MembershipFee("d4", payment, user, club, Instant.now()),
            new MembershipFee("d5", payment, user, club, Instant.now()));

    when(clubRepository.findById("c1")).thenReturn(java.util.Optional.of(club));
    when(eventService.findAllByClubIdWithPaymentResolution("c1")).thenReturn(events);

    var result = controller.historyByClub("c1", model, 1, 2);

    assertEquals("history", result);
    verify(model).addAttribute(eq("events"), anyList());
    verify(model).addAttribute("currentPage", 1);
    verify(model).addAttribute("totalPages", 3);
  }

  @Test
  void membershipFee_get_returnsPrefilledMembershipForm() {
    var email = "test@example.com";
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("email", email);

    DefaultOAuth2User oAuth2User = mock(DefaultOAuth2User.class);
    when(oAuth2User.getAttributes()).thenReturn(attributes);
    when(authentication.getPrincipal()).thenReturn(oAuth2User);

    var club = new Club("cuisine", "Club Cuisine");
    when(clubRepository.findById("cuisine")).thenReturn(java.util.Optional.of(club));

    var prefilledForm = new MembershipFeeCreationForm("John", "Doe", "");
    when(membershipFormService.getPrefilledMembershipForm(email)).thenReturn(prefilledForm);

    var result = controller.membershipFee("cuisine", authentication, model);

    assertEquals("membership-fee", result);
    verify(membershipFormService).getPrefilledMembershipForm(email);
    verify(model).addAttribute("membershipForm", prefilledForm);
  }

  @Test
  void logout_showsLogoutConfirmation() {
    String result = controller.showLogoutConfirmation();
    assertEquals("logout-confirm", result);
  }
}
