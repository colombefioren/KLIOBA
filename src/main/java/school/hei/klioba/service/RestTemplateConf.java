package school.hei.klioba.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConf {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
}
