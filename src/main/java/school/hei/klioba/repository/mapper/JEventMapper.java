package school.hei.klioba.repository.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.klioba.model.Event;
import school.hei.klioba.repository.jpa.model.JEvent;

@Component
@AllArgsConstructor
public class JEventMapper {

  private final JPaymentMapper jPaymentMapper;
  private final JUserMapper jUserMapper;
  private final JClubMapper jClubMapper;

  public Event toDomain(JEvent jEvent) {
    var payment = jPaymentMapper.toDomain(jEvent.getPayment());
    var user = jUserMapper.toDomain(jEvent.getUser());
    var club = jClubMapper.toDomain(jEvent.getClub());
    var creationInstant = jEvent.getCreationInstant();

    return Event.from(jEvent.getId(), payment, user, club, creationInstant, jEvent.getComment());
  }

  public JEvent toEntity(Event event) {
    var jUser = jUserMapper.toEntity(event.getUser());
    var jClub = jClubMapper.toEntity(event.getClub());
    var jPayment = jPaymentMapper.toEntity(event.getPayment());

    return new JEvent(
        event.getId(), jClub, jUser, jPayment, event.getCreationInstant(), event.getComment());
  }
}
