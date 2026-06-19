package school.hei.klioba.service;

import org.springframework.stereotype.Service;
import school.hei.klioba.endpoint.http.model.MembershipFeeCreationForm;
import school.hei.klioba.endpoint.http.model.ThEvent;

@Service
public class MembershipFormService {

  private final EventService eventService;

  public MembershipFormService(EventService eventService) {
    this.eventService = eventService;
  }

  public MembershipFeeCreationForm getPrefilledMembershipForm(String email) {
    var events = eventService.findAllWithPaymentResolution();
    var thEvents = events.stream().map(ThEvent::new).toList();

    var lastEvent =
        thEvents.stream()
            .filter(e -> e.event().getUser().getEmail().equals(email))
            .reduce((first, second) -> second)
            .orElse(null);

    if (lastEvent != null) {
      return new MembershipFeeCreationForm(
          lastEvent.event().getUser().getFirstName(),
          lastEvent.event().getUser().getLastName(),
          "");
    } else {
      return new MembershipFeeCreationForm("", "", "");
    }
  }
}
