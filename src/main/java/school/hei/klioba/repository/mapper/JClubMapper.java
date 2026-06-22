package school.hei.klioba.repository.mapper;

import org.springframework.stereotype.Component;
import school.hei.klioba.model.Club;
import school.hei.klioba.repository.jpa.model.JClub;

@Component
public class JClubMapper {

  public Club toDomain(JClub jClub) {
    return new Club(jClub.getId(), jClub.getName());
  }

  public JClub toEntity(Club club) {
    JClub jClub = new JClub();
    jClub.setId(club.getId());
    jClub.setName(club.getName());
    return jClub;
  }
}
