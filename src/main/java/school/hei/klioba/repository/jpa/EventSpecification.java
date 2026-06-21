package school.hei.klioba.repository.jpa;

import jakarta.persistence.criteria.JoinType;
import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;
import school.hei.klioba.repository.jpa.model.JEvent;

public class EventSpecification {

  public static Specification<JEvent> clubIdEquals(String clubId) {
    return (root, query, cb) -> cb.equal(root.get("club").get("id"), clubId);
  }

  public static Specification<JEvent> searchMatches(String search) {
    if (search == null || search.isBlank()) return Specification.where(null);
    return (root, query, cb) -> {
      var user = root.join("user", JoinType.INNER);
      var pattern = "%" + search.toLowerCase() + "%";
      return cb.or(
          cb.like(cb.lower(user.get("firstName")), pattern),
          cb.like(cb.lower(user.get("lastName")), pattern),
          cb.like(cb.lower(user.get("email")), pattern));
    };
  }

  public static Specification<JEvent> dateFromAfter(Instant dateFrom) {
    if (dateFrom == null) return Specification.where(null);
    return (root, query, cb) ->
        cb.greaterThanOrEqualTo(root.get("creationInstant"), dateFrom);
  }

  public static Specification<JEvent> dateToBefore(Instant dateTo) {
    if (dateTo == null) return Specification.where(null);
    return (root, query, cb) -> cb.lessThan(root.get("creationInstant"), dateTo);
  }
}
