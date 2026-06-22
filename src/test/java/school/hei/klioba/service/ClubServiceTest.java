package school.hei.klioba.service;

import static java.time.Instant.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static school.hei.klioba.model.PaymentStatus.CONFIRMED;
import static school.hei.klioba.model.PaymentStatus.VERIFYING;
import static school.hei.klioba.model.psp.PspType.ORANGE_MONEY;

import java.util.List;
import org.junit.jupiter.api.Test;
import school.hei.klioba.model.Club;
import school.hei.klioba.model.MembershipFee;
import school.hei.klioba.model.Payment;
import school.hei.klioba.model.User;
import school.hei.klioba.model.Withdrawal;
import school.hei.klioba.repository.ClubRepository;

class ClubServiceTest {

  ClubRepository clubRepository = mock(ClubRepository.class);
  EventService eventService = mock(EventService.class);
  ClubService clubService = new ClubService(clubRepository, eventService);

  Club club = new Club("c1", "Club 1");
  Club club2 = new Club("c2", "Club 2");
  User user1 = new User("u1", "John", "Doe", "john@email.com");
  User user2 = new User("u2", "Jane", "Smith", "jane@email.com");

  @Test
  void getAllClubStats_with_only_membershipFees() {
    var payment = new Payment("p1", 4000, ORANGE_MONEY, "PSP1", CONFIRMED, now(), now());
    var membershipFee = new MembershipFee("e1", payment, user1, club, now());

    when(clubRepository.findAll()).thenReturn(List.of(club));
    when(eventService.findAllByClubIdWithPaymentResolution("c1"))
        .thenReturn(List.of(membershipFee));

    var stats = clubService.getAllClubStats();

    assertEquals(1, stats.size());
    assertEquals(4000, stats.getFirst().totalCotisations());
    assertEquals(4000, stats.getFirst().remainingFund());
    assertEquals(1, stats.getFirst().members());
  }

  @Test
  void getAllClubStats_with_only_expenses() {
    var payment = new Payment("p2", -500, ORANGE_MONEY, "PSP2", CONFIRMED, now(), now());
    var withdrawal = new Withdrawal("e2", payment, user1, club, now(), "achat fournitures");

    when(clubRepository.findAll()).thenReturn(List.of(club));
    when(eventService.findAllByClubIdWithPaymentResolution("c1")).thenReturn(List.of(withdrawal));

    var stats = clubService.getAllClubStats();

    assertEquals(1, stats.size());
    assertEquals(0, stats.getFirst().totalCotisations());
    assertEquals(-500, stats.getFirst().remainingFund());
    assertEquals(1, stats.getFirst().members());
  }

  @Test
  void getAllClubStats_with_mixed_fees_and_withdrawals() {
    var payment1 = new Payment("p1", 1000, ORANGE_MONEY, "PSP1", CONFIRMED, now(), now());
    var fee = new MembershipFee("e1", payment1, user1, club, now());

    var payment2 = new Payment("p2", -300, ORANGE_MONEY, "PSP2", CONFIRMED, now(), now());
    var withdrawal = new Withdrawal("e2", payment2, user1, club, now(), "achat fournitures");

    when(clubRepository.findAll()).thenReturn(List.of(club));
    when(eventService.findAllByClubIdWithPaymentResolution("c1"))
        .thenReturn(List.of(fee, withdrawal));

    var stats = clubService.getAllClubStats();

    assertEquals(1, stats.size());
    assertEquals(1000, stats.getFirst().totalCotisations());
    assertEquals(700, stats.getFirst().remainingFund());
    assertEquals(1, stats.getFirst().members());
  }

  @Test
  void getAllClubStats_with_unconfirmed_payment_ignored() {
    var payment = new Payment("p1", 1000, ORANGE_MONEY, "PSP1", VERIFYING, now(), now());
    var membershipFee = new MembershipFee("e1", payment, user1, club, now());

    when(clubRepository.findAll()).thenReturn(List.of(club));
    when(eventService.findAllByClubIdWithPaymentResolution("c1"))
        .thenReturn(List.of(membershipFee));

    var stats = clubService.getAllClubStats();

    assertEquals(0, stats.getFirst().totalCotisations());
    assertEquals(0, stats.getFirst().remainingFund());
  }

  @Test
  void getAllClubStats_with_null_amount_treated_as_zero() {
    var payment = new Payment("p1", null, ORANGE_MONEY, "PSP1", CONFIRMED, now(), now());
    var membershipFee = new MembershipFee("e1", payment, user1, club, now());

    when(clubRepository.findAll()).thenReturn(List.of(club));
    when(eventService.findAllByClubIdWithPaymentResolution("c1"))
        .thenReturn(List.of(membershipFee));

    var stats = clubService.getAllClubStats();

    assertEquals(0, stats.getFirst().totalCotisations());
    assertEquals(0, stats.getFirst().remainingFund());
  }

  @Test
  void getAllClubStats_distinct_members_by_email() {
    var payment1 = new Payment("p1", 500, ORANGE_MONEY, "PSP1", CONFIRMED, now(), now());
    var fee1 = new MembershipFee("e1", payment1, user1, club, now());

    var payment2 = new Payment("p2", 600, ORANGE_MONEY, "PSP2", CONFIRMED, now(), now());
    var fee2 = new MembershipFee("e2", payment2, user1, club, now()); // same user

    when(clubRepository.findAll()).thenReturn(List.of(club));
    when(eventService.findAllByClubIdWithPaymentResolution("c1")).thenReturn(List.of(fee1, fee2));

    var stats = clubService.getAllClubStats();

    assertEquals(1, stats.getFirst().members());
    assertEquals(1100, stats.getFirst().totalCotisations());
  }

  @Test
  void getAllClubStats_multiple_clubs() {
    var payment1 = new Payment("p1", 1000, ORANGE_MONEY, "PSP1", CONFIRMED, now(), now());
    var fee = new MembershipFee("e1", payment1, user1, club, now());

    var payment2 = new Payment("p2", -200, ORANGE_MONEY, "PSP2", CONFIRMED, now(), now());
    var withdrawal = new Withdrawal("e2", payment2, user2, club2, now(), "dépense");

    when(clubRepository.findAll()).thenReturn(List.of(club, club2));
    when(eventService.findAllByClubIdWithPaymentResolution("c1")).thenReturn(List.of(fee));
    when(eventService.findAllByClubIdWithPaymentResolution("c2")).thenReturn(List.of(withdrawal));

    var stats = clubService.getAllClubStats();

    assertEquals(2, stats.size());

    var stat1 = stats.stream().filter(s -> s.id().equals("c1")).findFirst().orElseThrow();
    assertEquals(1000, stat1.totalCotisations());
    assertEquals(1000, stat1.remainingFund());
    assertEquals(1, stat1.members());

    var stat2 = stats.stream().filter(s -> s.id().equals("c2")).findFirst().orElseThrow();
    assertEquals(0, stat2.totalCotisations());
    assertEquals(-200, stat2.remainingFund());
    assertEquals(1, stat2.members());
  }

  @Test
  void getAllClubStats_with_empty_events() {
    when(clubRepository.findAll()).thenReturn(List.of(club));
    when(eventService.findAllByClubIdWithPaymentResolution("c1")).thenReturn(List.of());

    var stats = clubService.getAllClubStats();

    assertEquals(1, stats.size());
    assertEquals(0, stats.getFirst().totalCotisations());
    assertEquals(0, stats.getFirst().remainingFund());
    assertEquals(0, stats.getFirst().members());
  }
}
