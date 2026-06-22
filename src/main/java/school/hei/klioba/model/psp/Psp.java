package school.hei.klioba.model.psp;

import school.hei.klioba.model.Payment;

public interface Psp {
  Payment create(String kliobaId, PspType pspType, String pspId, String email);

  Payment get(String kliobaId, PspType pspType, String pspId, String email);
}
