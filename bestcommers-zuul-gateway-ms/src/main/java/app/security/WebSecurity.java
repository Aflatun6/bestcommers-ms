package app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private final Environment env;

    @Autowired
    public WebSecurity(Environment env) {
        this.env = env;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilter(new AuthorizationFilter(authenticationManager(), env))
                .authorizeRequests()
                .antMatchers(env.getProperty("api.h2console.url")).permitAll()
                .antMatchers(HttpMethod.POST, env.getProperty("api.signup.url")).permitAll()
                .antMatchers(env.getProperty("api.status.check.url")).permitAll()
                .antMatchers(HttpMethod.POST, env.getProperty("api.login.url")).permitAll()
                .anyRequest().authenticated()
              .and()
                .csrf().disable()
                .headers().frameOptions().disable()
              .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
