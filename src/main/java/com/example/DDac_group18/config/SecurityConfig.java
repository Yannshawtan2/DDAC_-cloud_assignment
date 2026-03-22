package com.example.DDac_group18.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

@Configuration
@Profile("!dev")
public class SecurityConfig {

        @Autowired
        private MicroserviceAuthenticationProvider authenticationProvider;

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
        
        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) {
                auth.authenticationProvider(authenticationProvider);
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(authz -> authz
                                                .requestMatchers("/register", "/login", "/css/**", "/js/**", "/images/**","/", "/qualification/**", "/create-test-user", "/check-user/**", "/authenticate-test", "/test-user", "/test-auth", "/patient/test-health-check/**")
                                                .permitAll()
                                                .requestMatchers("/admin/**", "/admindashboard/**").hasRole("ADMIN")
                                                .requestMatchers("/doctor/**", "/doctor/appointmentChecking.html")
                                                .hasRole("DOCTOR")
                                                .requestMatchers("/dietician/**", "/dieticiandashboard/**")
                                                .hasRole("DIETICIAN")
                                                .requestMatchers("/patient/**").hasRole("PATIENT")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/redirectDashboard", true)
                                                .failureUrl("/login?error=true")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutSuccessUrl("/login?logout=true")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll())
                                .sessionManagement(session -> session
                                                .maximumSessions(1)
                                                .maxSessionsPreventsLogin(false))
                                .csrf(csrf -> csrf.disable());
                return http.build();
        }
}