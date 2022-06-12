import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .headers()
        .xssProtection()
        .and()
        .contentSecurityPolicy(
            "script-src 'self' https://dz-dialect-app.herokuapp.com/; object-src https://dz-dialect-app.herokuapp.com/; report-uri /csp-report-endpoint/");
  }
}