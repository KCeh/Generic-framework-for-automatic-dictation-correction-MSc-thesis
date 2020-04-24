package hr.fer.genericframeworkforautomaticdictationcorrection.Security;

import hr.fer.genericframeworkforautomaticdictationcorrection.Utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Qualifier("userDetailServiceImpl")
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //use csrf
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(Constants.Paths.RESOURCES, Constants.Paths.BASE_PATH, Constants.Paths.HOME, Constants.Paths.WEBJARS, Constants.Paths.JS, Constants.Paths.CSS, Constants.Paths.LOGIN_WITH_PARMS, Constants.Paths.REGISTRATION, Constants.Paths.REGISTRATION_COFIRMATION, Constants.Paths.BAD_USER)
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage(Constants.Paths.LOGIN)
                .defaultSuccessUrl(Constants.Paths.BASE_PATH)
                .permitAll()
                .and()
                .logout()
                .logoutUrl(Constants.Paths.LOGOUT)
                .logoutSuccessUrl(Constants.Paths.BASE_PATH).permitAll();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }
}
