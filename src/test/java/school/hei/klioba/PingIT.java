package school.hei.klioba;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.OK;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import school.hei.klioba.conf.FacadeIT;

class PingIT extends FacadeIT {
  @Autowired private TestRestTemplate restTemplate;

  @Test
  void ping_ok() {
    var response = restTemplate.getForEntity("/ping", String.class);

    assertEquals(OK, response.getStatusCode());
    assertEquals("pong", response.getBody());
  }
}
