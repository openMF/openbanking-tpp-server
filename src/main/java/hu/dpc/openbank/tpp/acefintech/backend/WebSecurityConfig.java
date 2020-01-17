/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableWebMvc
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {
    private final DataSource dataSource;

    public WebSecurityConfig(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected void configure(final @NotNull HttpSecurity http) throws Exception {
//@formatter:off
        http.csrf().disable() // enable it for support localhost develop
            .httpBasic().and().authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll() // enable it for support localhost develop
            .anyRequest().authenticated()
            .and().formLogin().loginPage("/login")
            .and().logout().permitAll();
//@formatter:on
    }

    @Autowired
    public void configureGlobal(final @NotNull AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource);
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        // Enable support localhost develop
        registry.addMapping("/**").exposedHeaders("x-tpp-consentid", "x-fapi-interaction-id");
    }

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        final CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true); loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true); loggingFilter.setIncludeHeaders(true); return loggingFilter;
    }

}
