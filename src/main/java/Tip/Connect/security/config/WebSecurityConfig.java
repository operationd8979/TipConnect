package Tip.Connect.security.config;

import Tip.Connect.model.Auth.AppUserRole;
import Tip.Connect.service.JwtFilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.List;


@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtFilterService jwtFilterService;

    private static final String[] DOMAIN_FRIEND = {
            "http://localhost:3000",
            "http://192.168.1.18:3000"
    };

    private static final String[] WHITE_LIST_URL = {
            "/api/v1/registration/**",
            "/api/v1/auth/**",
            "/ws/**",
            "/api/user/live/**"
    };

    private static final String[] USER_LIST_URL = {
            "/api/user/**",
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf->csrf.disable())
                .cors(cors->cors.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(Arrays.stream(DOMAIN_FRIEND).toList());
                    configuration.setAllowedMethods(List.of("HEAD","GET","POST","PUT","DELETE","PATCH","OPTIONS"));
                    configuration.setAllowCredentials(true);
                    configuration.addExposedHeader("Message");
                    configuration.setAllowedHeaders(List.of("Authorization","Cache-Control","Content-Type"));
                    return configuration;
                }))
                .authorizeHttpRequests(requests ->
                        requests.requestMatchers(WHITE_LIST_URL).permitAll()
                                .requestMatchers(USER_LIST_URL).hasAnyAuthority(AppUserRole.USER.toString())
                                .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilterService, UsernamePasswordAuthenticationFilter.class)
        ;
        return http.build();
    }


    @Bean
    public SessionRegistry sessionRegistry(){
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher(){
        return new HttpSessionEventPublisher();
    }


}
