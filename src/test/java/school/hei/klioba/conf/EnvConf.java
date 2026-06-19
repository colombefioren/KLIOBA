package school.hei.klioba.conf;

import org.springframework.test.context.DynamicPropertyRegistry;

public class EnvConf {

  void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("env", () -> "test");
    registry.add(
        "spring.datasource.url", () -> "jdbc:h2:mem:testdb;CASE_INSENSITIVE_IDENTIFIERS=TRUE");
    registry.add("spring.datasource.driverClassName", () -> "org.h2.Driver");
    registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.H2Dialect");

    registry.add("spring.security.oauth2.client.provider.casdoor.authorization-uri", () -> "dummy");
    registry.add("spring.security.oauth2.client.provider.casdoor.token-uri", () -> "dummy");
    registry.add("spring.security.oauth2.client.registration.casdoor.provider", () -> "casdoor");
    registry.add("spring.security.oauth2.client.registration.casdoor.client-id", () -> "dummy");
    registry.add(
        "spring.security.oauth2.client.registration.casdoor.redirect-uri",
        () -> "{baseUrl}/login/oauth2/code/casdoor");
    registry.add(
        "spring.security.oauth2.client.registration.casdoor.authorization-grant-type",
        () -> "authorization_code");
    registry.add("casdoor.logout.url", () -> "dummy");
    registry.add("klioba.logout.url", () -> "dummy");
    registry.add("vola.api.url", () -> "http://dummy.vola.api");
    registry.add("vola.api.key", () -> "dummy-key");
  }
}
