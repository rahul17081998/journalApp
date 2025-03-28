package com.rahul.journal_app.config;

import com.rahul.journal_app.entity.User;
import com.rahul.journal_app.filter.JwtFilter;
import com.rahul.journal_app.repository.UserRepository;
import com.rahul.journal_app.service.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
//@Profile("dev")
public class SpringSecurity implements AuthenticationProvider, AuthenticationManager {

    /**
     * Below is the custom security configurations
     */

    private static final Logger logger = LoggerFactory.getLogger(SpringSecurity.class);

    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;

    @Autowired
    private JwtFilter jwtFilter;


    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf->csrf.disable())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/journal/**", "/user/**", "/attachment/**", "/pdf/**").authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll());

//        http.sessionManagement(session->{
//            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//        });

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    // User authentication validation
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        logger.info("> Starting authentication process...");
        String username = authentication.getName().toLowerCase();
        logger.info("> Attempting authentication for username: {}", username);
        String pwd = authentication.getCredentials().toString();

        try {
            User user = userRepository.findByUserName(username);
            if (user != null) {
                logger.info("> User information retrieved successfully for username: {}", username);
                if (passwordEncoder.matches(pwd, user.getPassword())) {
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    for (String role : user.getRoles()) {
                        authorities.add(new SimpleGrantedAuthority(role));
                    }
                    logger.info("> Authentication successful for username: {}", username);
                    return new UsernamePasswordAuthenticationToken(username, pwd, authorities);
                } else {
                    logger.warn("--> Password validation failed for username: {} ", username);
                    throw new BadCredentialsException("Invalid password");
                }
            } else {
                logger.warn("> No user found in the database for username: {}", username);
                throw new BadCredentialsException("No user register with this details");
            }
        }catch (Exception e) {
            logger.error("An error occurred during authentication for username: {}", username, e);
            throw new AuthenticationException("Authentication failed due to an internal error") {};
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

}
