package school.hei.klioba.repository;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import school.hei.klioba.model.Payment;
import school.hei.klioba.repository.jpa.JPaymentRepository;
import school.hei.klioba.repository.mapper.JPaymentMapper;

@Repository
@AllArgsConstructor
public class PaymentRepository {

  private final JPaymentRepository jPaymentRepository;
  private final JPaymentMapper jPaymentMapper;

  public Payment save(Payment payment) {
    var jPaymentToSave = jPaymentMapper.toEntity(payment);
    var jPaymentSaved = jPaymentRepository.save(jPaymentToSave);
    return jPaymentMapper.toDomain(jPaymentSaved);
  }

  public Optional<Payment> findByPspId(String pspId) {
    return jPaymentRepository.findByPspId(pspId).map(jPaymentMapper::toDomain);
  }
}
