package school.hei.klioba.endpoint.http.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConf {

  private final String casdoorClientId;
  private final String casdoorLogoutUrl;
  private final String tsinjoLogoutUrl;
  private final Oauth2StatePaddingFixFilter statePaddingFixFilter;

  public SecurityConf(
      @Value("${spring.security.oauth2.client.registration.casdoor.clientid}")
          String casdoorClientId,
      @Value("${casdoor.logout.url}") String casdoorLogoutUrl,
      @Value("${tsinjo.logout.url}") String tsinjoLogoutUrl,
      Oauth2StatePaddingFixFilter statePaddingFixFilter) {
    this.casdoorClientId = casdoorClientId;
    this.casdoorLogoutUrl = casdoorLogoutUrl;
    this.tsinjoLogoutUrl = tsinjoLogoutUrl;
    this.statePaddingFixFilter = statePaddingFixFilter;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(Customizer.withDefaults())
        .authorizeHttpRequests(
            authorization ->
                authorization
                    .requestMatchers(HttpMethod.GET, "/ping")
                    .permitAll()
                    .requestMatchers("**")
                    .authenticated())
        .addFilterBefore(statePaddingFixFilter, BasicAuthenticationFilter.class)
        .oauth2Login(
            oauth2 ->
                oauth2
                    .loginPage("/oauth2/authorization/casdoor")
                    .successHandler(
                        (request, response, authentication) -> {
                          log.info("✅ OAuth2 login SUCCESS");
                          log.info("User: {}", authentication.getName());
                          log.info("Authorities: {}", authentication.getAuthorities());
                          response.sendRedirect("/");
                        })
                    .failureHandler(
                        (request, response, exception) -> {
                          // On success redirection from Casdoor URL instead of
                          // custom domain URL
                          // so it is incorrectly interpreted as authorization_request_not_found.
                          // Redo the call and it will be Ok.
                          log.error("❌ OAuth2 login FAILURE", exception);
                          log.error("Message: {}", exception.getMessage());
                          new SimpleUrlAuthenticationFailureHandler("/oauth2/authorization/casdoor")
                              .onAuthenticationFailure(request, response, exception);
                          log.info("🔄 Forced redirect to /oauth2/authorization/casdoor executed");
                        }))
        .logout(
            logout ->
                logout
                    .logoutUrl("/perform-logout")
                    .logoutSuccessHandler(
                        (request, response, authentication) -> {
                          if (authentication != null) {
                            var principal = (DefaultOidcUser) authentication.getPrincipal();
                            String accessToken = principal.getIdToken().getTokenValue();
                            response.sendRedirect(
                                casdoorLogoutUrl
                                    + "?id_token_hint="
                                    + accessToken
                                    + "&post_logout_redirect_uri="
                                    + tsinjoLogoutUrl);
                          } else {
                            response.sendRedirect(tsinjoLogoutUrl);
                          }
                        }));
    return http.build();
  }
}
