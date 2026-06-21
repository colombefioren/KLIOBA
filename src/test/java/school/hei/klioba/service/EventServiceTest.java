package school.hei.klioba.service;

import static java.time.Instant.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static school.hei.klioba.model.PaymentStatus.CONFIRMED;
import static school.hei.klioba.model.PaymentStatus.VERIFYING;
import static school.hei.klioba.model.psp.PspType.ORANGE_MONEY;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import school.hei.klioba.model.Club;
import school.hei.klioba.model.Event;
import school.hei.klioba.model.MembershipFee;
import school.hei.klioba.model.Payment;
import school.hei.klioba.model.User;
import school.hei.klioba.model.psp.vola.VolaPsp;
import school.hei.klioba.repository.EventRepository;

class EventServiceTest {

  EventRepository eventRepository = mock(EventRepository.class);
  VolaPsp volaPsp = mock(VolaPsp.class);
  EventService eventService = new EventService(eventRepository, volaPsp);

  Club club = new Club("c1", "Club 1");
  User user = new User("u1", "John", "Doe", "john@test.com");
  Instant now = now();

  @Test
  void findPageByClubIdWithFilters_returnsPage() {
    var payment = new Payment("p1", 1000, ORANGE_MONEY, "PSP1", CONFIRMED, null, now);
    var event = new MembershipFee("e1", payment, user, club, now);
    var page = new PageImpl<>(List.of((Event) event));

    when(eventRepository.findByClubIdWithFilters(
            eq("c1"), isNull(), isNull(), isNull(), any(PageRequest.class)))
        .thenReturn(page);

    var result = eventService.findPageByClubIdWithPaymentResolution("c1", null, null, null, 0, 50);

    assertEquals(1, result.getContent().size());
    assertEquals("e1", result.getContent().getFirst().getId());
  }

  @Test
  void findPageByClubIdWithFilters_withSearch_returnsFilteredPage() {
    var payment = new Payment("p1", 1000, ORANGE_MONEY, "PSP1", CONFIRMED, null, now);
    var event = new MembershipFee("e1", payment, user, club, now);
    var page = new PageImpl<>(List.of((Event) event));

    when(eventRepository.findByClubIdWithFilters(
            eq("c1"), eq("John"), isNull(), isNull(), any(PageRequest.class)))
        .thenReturn(page);

    var result =
        eventService.findPageByClubIdWithPaymentResolution("c1", "John", null, null, 0, 50);

    assertEquals(1, result.getContent().size());
  }

  @Test
  void findPageByClubIdWithFilters_withDateRange_returnsFilteredPage() {
    var payment = new Payment("p1", 1000, ORANGE_MONEY, "PSP1", CONFIRMED, null, now);
    var event = new MembershipFee("e1", payment, user, club, now);
    var page = new PageImpl<>(List.of((Event) event));
    var from = Instant.parse("2024-01-01T00:00:00Z");
    var to = Instant.parse("2024-12-31T23:59:59Z");

    when(eventRepository.findByClubIdWithFilters(
            eq("c1"), isNull(), eq(from), eq(to), any(PageRequest.class)))
        .thenReturn(page);

    var result = eventService.findPageByClubIdWithPaymentResolution("c1", null, from, to, 0, 50);

    assertEquals(1, result.getContent().size());
  }

  @Test
  void findAllByClubIdWithFilters_returnsList() {
    var payment = new Payment("p1", 1000, ORANGE_MONEY, "PSP1", CONFIRMED, null, now);
    var event = new MembershipFee("e1", payment, user, club, now);

    when(eventRepository.findAllByClubIdWithFilters(eq("c1"), isNull(), isNull(), isNull()))
        .thenReturn(List.of(event));

    var result = eventService.findAllByClubIdWithPaymentResolution("c1", null, null, null);

    assertEquals(1, result.size());
    assertEquals("e1", result.getFirst().getId());
  }

  @Test
  void findAllByClubIdWithFilters_withSearchAndDate_returnsFilteredList() {
    var payment = new Payment("p1", 1000, ORANGE_MONEY, "PSP1", CONFIRMED, null, now);
    var event = new MembershipFee("e1", payment, user, club, now);
    var from = Instant.parse("2024-01-01T00:00:00Z");

    when(eventRepository.findAllByClubIdWithFilters(eq("c1"), eq("john"), eq(from), isNull()))
        .thenReturn(List.of(event));

    var result = eventService.findAllByClubIdWithPaymentResolution("c1", "john", from, null);

    assertEquals(1, result.size());
  }

  @Test
  void resolvePayment_keepsNonVerifyingPaymentUnchanged() {
    var payment = new Payment("p1", 1000, ORANGE_MONEY, "PSP1", CONFIRMED, null, now);
    var event = new MembershipFee("e1", payment, user, club, now);
    var page = new PageImpl<>(List.of((Event) event));

    when(eventRepository.findByClubIdWithFilters(
            eq("c1"), isNull(), isNull(), isNull(), any(PageRequest.class)))
        .thenReturn(page);

    var result = eventService.findPageByClubIdWithPaymentResolution("c1", null, null, null, 0, 50);

    assertEquals(CONFIRMED, result.getContent().getFirst().getPayment().status());
    verify(volaPsp, never()).get(any(), any(), any(), any());
  }

  @Test
  void resolvePayment_resolvesVerifyingPayment() {
    var payment = new Payment("p1", 1000, ORANGE_MONEY, "PSP1", VERIFYING, null, now);
    var event = new MembershipFee("e1", payment, user, club, now);
    var resolvedPayment = new Payment("p1", 1000, ORANGE_MONEY, "PSP1", CONFIRMED, now, now);
    var resolvedEvent = new MembershipFee("e1", resolvedPayment, user, club, now);

    when(eventRepository.findByClubIdWithFilters(
            eq("c1"), isNull(), isNull(), isNull(), any(PageRequest.class)))
        .thenReturn(new PageImpl<>(List.of((Event) event)));
    when(volaPsp.get(any(), any(), any(), any())).thenReturn(resolvedPayment);
    when(eventRepository.save(any())).thenReturn(resolvedEvent);

    var result = eventService.findPageByClubIdWithPaymentResolution("c1", null, null, null, 0, 50);

    assertEquals(CONFIRMED, result.getContent().getFirst().getPayment().status());
    verify(volaPsp).get(eq("p1"), any(), any(), any());
    verify(eventRepository).save(any());
  }

  @Test
  void resolvePayment_handlesVerifyingPaymentError() {
    var payment = new Payment("p1", 1000, ORANGE_MONEY, "PSP1", VERIFYING, null, now);
    var event = new MembershipFee("e1", payment, user, club, now);

    when(eventRepository.findByClubIdWithFilters(
            eq("c1"), isNull(), isNull(), isNull(), any(PageRequest.class)))
        .thenReturn(new PageImpl<>(List.of((Event) event)));
    when(volaPsp.get(any(), any(), any(), any()))
        .thenThrow(new RuntimeException("PSP unavailable"));

    var result = eventService.findPageByClubIdWithPaymentResolution("c1", null, null, null, 0, 50);

    assertEquals(VERIFYING, result.getContent().getFirst().getPayment().status());
    verify(eventRepository, never()).save(any());
  }

  @Test
  void findAllByClubIdWithFilters_resolvesPayments() {
    var payment = new Payment("p1", 1000, ORANGE_MONEY, "PSP1", VERIFYING, null, now);
    var event = new MembershipFee("e1", payment, user, club, now);
    var resolvedPayment = new Payment("p1", 1000, ORANGE_MONEY, "PSP1", CONFIRMED, now, now);
    var resolvedEvent = new MembershipFee("e1", resolvedPayment, user, club, now);

    when(eventRepository.findAllByClubIdWithFilters(eq("c1"), isNull(), isNull(), isNull()))
        .thenReturn(List.of(event));
    when(volaPsp.get(any(), any(), any(), any())).thenReturn(resolvedPayment);
    when(eventRepository.save(any())).thenReturn(resolvedEvent);

    var result = eventService.findAllByClubIdWithPaymentResolution("c1", null, null, null);

    assertEquals(CONFIRMED, result.getFirst().getPayment().status());
  }
}
