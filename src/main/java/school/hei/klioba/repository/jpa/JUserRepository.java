package school.hei.klioba.repository.jpa;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.hei.klioba.repository.jpa.model.JUser;

@Repository
public interface JUserRepository extends JpaRepository<JUser, String> {
  Optional<JUser> findByEmail(String email);
}
