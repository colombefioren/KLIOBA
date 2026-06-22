package school.hei.klioba.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.hei.klioba.repository.jpa.model.JClub;

@Repository
public interface JClubRepository extends JpaRepository<JClub, String> {}
