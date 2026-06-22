package school.hei.klioba.service;

import static java.time.Instant.now;
import static java.util.UUID.randomUUID;
import static school.hei.klioba.model.psp.PspType.ORANGE_MONEY;

import jakarta.transaction.Transactional;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.hei.klioba.endpoint.http.model.MembershipFeeCreationForm;
import school.hei.klioba.model.Event;
import school.hei.klioba.model.User;
import school.hei.klioba.model.psp.PspType;
import school.hei.klioba.model.psp.vola.VolaPsp;
import school.hei.klioba.repository.ClubRepository;
import school.hei.klioba.repository.EventRepository;
import school.hei.klioba.repository.PaymentRepository;
import school.hei.klioba.repository.UserRepository;

@Service
@AllArgsConstructor
@Slf4j
public class MembershipFeeCreationFormConsumer {
  private final UserRepository userRepository;
  private final ClubRepository clubRepository;
  private final PaymentRepository paymentRepository;
  private final EventRepository eventRepository;

  private final VolaPsp volaPsp;

  @Transactional
  public void accept(
      MembershipFeeCreationForm membershipFeeCreationForm, String email, String clubId) {
    if (paymentRepository.findByPspId(membershipFeeCreationForm.pspId()).isPresent()) {
      throw new IllegalArgumentException("pspId already exists");
    } else if (!isPspIdFormat(membershipFeeCreationForm.pspId())) {
      throw new IllegalArgumentException("pspId format incorrect format");
    }

    var paymentCreatedInVola =
        volaPsp.create(
            randomUUID().toString(), pspType(), membershipFeeCreationForm.pspId(), email);
    var payment = paymentRepository.save(paymentCreatedInVola);
    var user = userFrom(membershipFeeCreationForm, email);
    var club =
        clubRepository
            .findById(clubId)
            .orElseThrow(() -> new NoSuchElementException("Club not found: " + clubId));
    eventRepository.save(Event.from(randomUUID().toString(), payment, user, club, now(), ""));
    assignUserToClub(user, clubId);
  }

  private static PspType pspType() {
    return switch (PspType.values()[0]) {
      case ORANGE_MONEY -> ORANGE_MONEY;
    };
  }

  private void assignUserToClub(User user, String clubId) {
    userRepository.addClubToUser(user.getId(), clubId);
  }

  private User userFrom(MembershipFeeCreationForm membershipFeeCreationForm, String email) {
    return userRepository.saveIfEmailNotExist(
        membershipFeeCreationForm.firstName(), membershipFeeCreationForm.lastName(), email);
  }

  public boolean isPspIdFormat(String pspId) {
    if (pspId == null) {
      return false;
    }
    return pspId.matches("^MP\\d{6}\\.\\d{4}\\.[A-Z]\\d{5}$");
  }
}
