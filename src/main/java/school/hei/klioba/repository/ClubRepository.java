package school.hei.klioba.repository;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import school.hei.klioba.model.Club;
import school.hei.klioba.repository.jpa.JClubRepository;
import school.hei.klioba.repository.mapper.JClubMapper;

@Repository
@AllArgsConstructor
public class ClubRepository {

  private final JClubRepository jClubRepository;
  private final JClubMapper jClubMapper;

  public Optional<Club> findById(String id) {
    return jClubRepository.findById(id).map(jClubMapper::toDomain);
  }

  public List<Club> findAll() {
    return jClubRepository.findAll().stream().map(jClubMapper::toDomain).toList();
  }
}
