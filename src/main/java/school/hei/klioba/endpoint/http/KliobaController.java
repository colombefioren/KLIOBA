package school.hei.klioba.endpoint.http;

import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import school.hei.klioba.endpoint.http.model.MembershipFeeCreationForm;
import school.hei.klioba.endpoint.http.model.ThEvent;
import school.hei.klioba.endpoint.http.model.ThFund;
import school.hei.klioba.repository.ClubRepository;
import school.hei.klioba.service.ClubService;
import school.hei.klioba.service.EventService;
import school.hei.klioba.service.MembershipFeeCreationFormConsumer;
import school.hei.klioba.service.MembershipFormService;

@Controller
@AllArgsConstructor
public class KliobaController {

  private final EventService eventService;
  private final MembershipFeeCreationFormConsumer membershipCreationFormConsumer;
  private final ClubRepository clubRepository;
  private final MembershipFormService membershipFormService;
  private final ClubService clubService;

  @GetMapping("/")
  public String home() {
    return "home";
  }

  @GetMapping("/dashboard")
  public String dashboard(Authentication authentication, Model model) {
    var clubs = clubService.getAllClubStats();
    int totalCotisations = clubs.stream().mapToInt(ClubService.ClubStats::totalCotisations).sum();
    int totalDepenses =
        clubs.stream().mapToInt(c -> c.totalCotisations() - c.remainingFund()).sum();
    int totalRemaining = clubs.stream().mapToInt(ClubService.ClubStats::remainingFund).sum();
    int totalMembers = clubs.stream().mapToInt(ClubService.ClubStats::members).sum();
    model.addAttribute("clubs", clubs);
    model.addAttribute("totalCotisations", totalCotisations);
    model.addAttribute("totalDepenses", totalDepenses);
    model.addAttribute("totalRemaining", totalRemaining);
    model.addAttribute("totalMembers", totalMembers);
    return "dashboard";
  }

  @GetMapping("/history/{clubId}")
  public String historyByClub(
      @PathVariable String clubId,
      Model model,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "50") int size) {
    var events = eventService.findAllByClubIdWithPaymentResolution(clubId);
    var thEvents = events.stream().map(ThEvent::new).toList();
    int total = thEvents.size();
    int fromIndex = Math.min(page * size, total);
    int toIndex = Math.min(fromIndex + size, total);
    var pagedEvents = thEvents.subList(fromIndex, toIndex);
    model.addAttribute("events", pagedEvents);
    model.addAttribute("fund", new ThFund(events));
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", (int) Math.ceil((double) total / size));
    model.addAttribute("size", size);
    model.addAttribute("clubId", clubId);
    model.addAttribute(
        "clubName",
        clubRepository
            .findById(clubId)
            .orElseThrow(() -> new NoSuchElementException("This doesn't exist"))
            .getName());
    return "history";
  }

  @PostMapping("/club/{clubId}/membershipFee")
  public String membershipFee(
      @PathVariable String clubId,
      Authentication authentication,
      MembershipFeeCreationForm membershipCreationForm) {
    var defaultOAuth2User = ((DefaultOAuth2User) authentication.getPrincipal());
    var email = defaultOAuth2User.getAttributes().get("email").toString();
    membershipCreationFormConsumer.accept(membershipCreationForm, email, clubId);
    return "redirect:/history/" + clubId;
  }

  @GetMapping("/club/{clubId}/membershipFee")
  public String membershipFee(
      @PathVariable String clubId, Authentication authentication, Model model) {
    var defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
    var email = defaultOAuth2User.getAttributes().get("email").toString();
    MembershipFeeCreationForm membershipForm =
        membershipFormService.getPrefilledMembershipForm(email);
    model.addAttribute("clubId", clubId);
    model.addAttribute("membershipForm", membershipForm);
    model.addAttribute(
        "clubName",
        clubRepository
            .findById(clubId)
            .orElseThrow(() -> new NoSuchElementException("This doesn't exist"))
            .getName());
    return "membership-fee";
  }

  @GetMapping("/logout")
  public String showLogoutConfirmation() {
    return "logout-confirm";
  }
}
