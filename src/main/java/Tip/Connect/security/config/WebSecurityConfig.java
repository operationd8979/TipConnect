package Tip.Connect.security.config;

import Tip.Connect.service.JwtFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtFilterService jwtFilterService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf->csrf.disable())
                .authorizeHttpRequests(requests ->
                        requests.requestMatchers("/api/v1/registration/**").permitAll()
                                .requestMatchers("/api/v1/auth/**").permitAll()
                                .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilterService, UsernamePasswordAuthenticationFilter.class)
        ;
        return http.build();
    }


}
