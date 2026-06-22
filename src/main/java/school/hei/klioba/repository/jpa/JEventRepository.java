package school.hei.klioba.repository.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import school.hei.klioba.repository.jpa.model.JEvent;

@Repository
public interface JEventRepository
    extends JpaRepository<JEvent, String>, JpaSpecificationExecutor<JEvent> {
  List<JEvent> findAllByOrderByCreationInstantDesc();

  List<JEvent> findAllByClubIdOrderByCreationInstantDesc(String clubId);

  @Override
  @EntityGraph(attributePaths = {"user", "payment"})
  List<JEvent> findAll(@Nullable Specification<JEvent> spec);

  @Override
  @EntityGraph(attributePaths = {"user", "payment"})
  List<JEvent> findAll(@Nullable Specification<JEvent> spec, Sort sort);

  @Override
  @EntityGraph(attributePaths = {"user", "payment"})
  Page<JEvent> findAll(@Nullable Specification<JEvent> spec, Pageable pageable);

  @Override
  @EntityGraph(attributePaths = {"user", "payment"})
  Optional<JEvent> findOne(@Nullable Specification<JEvent> spec);
}
