package school.hei.klioba.service;

import static school.hei.klioba.model.PaymentStatus.CONFIRMED;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.klioba.model.Club;
import school.hei.klioba.model.Event;
import school.hei.klioba.model.MembershipFee;
import school.hei.klioba.repository.ClubRepository;

@Service
@AllArgsConstructor
public class ClubService {

  private final ClubRepository clubRepository;
  private final EventService eventService;

  public record ClubStats(
      String id, String name, int totalCotisations, int members, int remainingFund) {}

  public List<ClubStats> getAllClubStats() {
    return clubRepository.findAll().stream().map(this::computeStats).toList();
  }

  private ClubStats computeStats(Club club) {
    var events = eventService.findAllByClubIdWithPaymentResolution(club.getId());
    int totalCotisations =
        events.stream()
            .filter(e -> e instanceof MembershipFee)
            .map(Event::getPayment)
            .filter(p -> CONFIRMED.equals(p.status()))
            .mapToInt(p -> Math.max(0, p.amount() == null ? 0 : p.amount()))
            .sum();
    int expenses =
        events.stream()
            .filter(e -> !(e instanceof MembershipFee))
            .map(Event::getPayment)
            .filter(p -> CONFIRMED.equals(p.status()))
            .mapToInt(p -> Math.abs(Math.min(0, p.amount() == null ? 0 : p.amount())))
            .sum();
    long members = events.stream().map(e -> e.getUser().getEmail()).distinct().count();
    return new ClubStats(
        club.getId(),
        club.getName(),
        totalCotisations,
        Math.toIntExact(members),
        totalCotisations - expenses);
  }
}
