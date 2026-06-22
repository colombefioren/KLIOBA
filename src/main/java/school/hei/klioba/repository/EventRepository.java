package school.hei.klioba.repository;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import school.hei.klioba.model.Event;
import school.hei.klioba.repository.jpa.EventSpecification;
import school.hei.klioba.repository.jpa.JEventRepository;
import school.hei.klioba.repository.jpa.model.JEvent;
import school.hei.klioba.repository.mapper.JEventMapper;

@Repository
@AllArgsConstructor
public class EventRepository {

  private final JEventRepository jEventRepository;
  private final JEventMapper jEventMapper;

  private final PaymentRepository paymentRepository;
  private final UserRepository userRepository;

  public Event save(Event event) {
    paymentRepository.save(event.getPayment());
    userRepository.save(event.getUser());
    return jEventMapper.toDomain(jEventRepository.save(jEventMapper.toEntity(event)));
  }

  public List<Event> findAllByOrderByCreationInstantDesc() {
    return jEventRepository.findAllByOrderByCreationInstantDesc().stream()
        .map(jEventMapper::toDomain)
        .toList();
  }

  public List<Event> findAllByClubIdOrderByCreationInstantDesc(String clubId) {
    return jEventRepository.findAllByClubIdOrderByCreationInstantDesc(clubId).stream()
        .map(jEventMapper::toDomain)
        .toList();
  }

  public Page<Event> findByClubIdWithFilters(
      String clubId, String search, Instant dateFrom, Instant dateTo, Pageable pageable) {
    var spec = buildFilterSpec(clubId, search, dateFrom, dateTo);
    return jEventRepository.findAll(spec, pageable).map(jEventMapper::toDomain);
  }

  public List<Event> findAllByClubIdWithFilters(
      String clubId, String search, Instant dateFrom, Instant dateTo) {
    var spec = buildFilterSpec(clubId, search, dateFrom, dateTo);
    return jEventRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "creationInstant")).stream()
        .map(jEventMapper::toDomain)
        .toList();
  }

  private Specification<JEvent> buildFilterSpec(
      String clubId, String search, Instant dateFrom, Instant dateTo) {
    return Specification.where(EventSpecification.clubIdEquals(clubId))
        .and(EventSpecification.searchMatches(search))
        .and(EventSpecification.dateFromAfter(dateFrom))
        .and(EventSpecification.dateToBefore(dateTo));
  }
}
