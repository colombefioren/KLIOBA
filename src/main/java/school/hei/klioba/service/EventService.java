package school.hei.klioba.service;

import static school.hei.klioba.model.PaymentStatus.VERIFYING;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import school.hei.klioba.model.Event;
import school.hei.klioba.model.psp.vola.VolaPsp;
import school.hei.klioba.repository.EventRepository;

@Component
@AllArgsConstructor
@Slf4j
public class EventService {

  private final EventRepository eventRepository;
  private final VolaPsp volaPsp;

  public List<Event> findAllWithPaymentResolution() {
    return eventRepository.findAllByOrderByCreationInstantDesc().stream()
        .map(this::resolvePayment)
        .toList();
  }

  public List<Event> findAllByClubIdWithPaymentResolution(String clubId) {
    return eventRepository.findAllByClubIdOrderByCreationInstantDesc(clubId).stream()
        .map(this::resolvePayment)
        .toList();
  }

  public Page<Event> findPageByClubIdWithPaymentResolution(
      String clubId, String search, Instant dateFrom, Instant dateTo, int page, int size) {
    return eventRepository
        .findByClubIdWithFilters(clubId, search, dateFrom, dateTo, PageRequest.of(page, size))
        .map(this::resolvePayment);
  }

  public List<Event> findAllByClubIdWithPaymentResolution(
      String clubId, String search, Instant dateFrom, Instant dateTo) {
    return eventRepository.findAllByClubIdWithFilters(clubId, search, dateFrom, dateTo).stream()
        .map(this::resolvePayment)
        .toList();
  }

  private Event resolvePayment(Event event) {
    var payment = event.getPayment();
    if (!VERIFYING.equals(payment.status())) {
      return event;
    }

    var resolvedPayment = payment;
    try {
      resolvedPayment =
          volaPsp.get(payment.id(), payment.pspType(), payment.pspId(), event.getUser().getEmail());
    } catch (Exception e) {
      log.error("Could not resolve payment for event: {}", event);
      return event;
    }
    return eventRepository.save(event.withPayment(resolvedPayment));
  }
}
