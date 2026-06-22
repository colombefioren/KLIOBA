package school.hei.klioba.model.psp.vola.api.gen.client;

import com.fasterxml.jackson.databind.util.StdDateFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class RFC3339DateFormat extends StdDateFormat {

  public RFC3339DateFormat() {
    this.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  @Override
  public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
    String value = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(date);
    toAppendTo.append(value);
    return toAppendTo;
  }
}
