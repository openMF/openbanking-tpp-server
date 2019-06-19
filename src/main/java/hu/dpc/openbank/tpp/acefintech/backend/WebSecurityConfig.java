package hu.dpc.openbank.tpp.acefintech.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
/*
        http //
                .csrf().disable()//
                .authorizeRequests().antMatchers("/", "/home").permitAll() //
                .anyRequest().authenticated() //
                .and().formLogin().loginPage("/login").permitAll() //
                .and().logout().permitAll().
        and().httpBasic();
 */
        http
                .csrf().disable()
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .httpBasic();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception {
//        User.UserBuilder users = User.withDefaultPasswordEncoder();
        auth.jdbcAuthentication()
                .dataSource(dataSource);
//                .withDefaultSchema().withUser(users.username("tppuser").password("password").roles("USER"));
/*
        auth.inMemoryAuthentication()
                .withUser("admin")
                .password("{noop}password")
                .roles("USER");
 */
    }

}
