package school.hei.klioba.model;

import java.time.Instant;
import lombok.Builder;
import school.hei.klioba.model.psp.PspType;

@Builder
public record Payment(
    String id,
    Integer amount,
    PspType pspType,
    String pspId,
    PaymentStatus status,
    Instant pspLastVerificationInstant,
    Instant creationInstant) {}
