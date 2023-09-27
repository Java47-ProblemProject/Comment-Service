package telran.comment.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfiguration {
    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.requiresChannel(chanel -> chanel.anyRequest().requiresSecure());
        http.httpBasic(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new CustomAuthenticationEntryPoint()));
        http.cors(Customizer.withDefaults());
        http.sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        http.authorizeRequests(authorize -> authorize
//                       //User section//
                        .requestMatchers(HttpMethod.PUT, "/comment/addcomment/{problemId}")
                        .access("@customSecurity.checkProblemId(#problemId)")
                        .requestMatchers(HttpMethod.PUT, "/comment/editcoment/{profileId}/{problemId}/{commentId}")
                        .access("@customSecurity.checkCommentAuthorAndProblemId(#problemId, #commentId, #profileId)")
                        .requestMatchers(HttpMethod.DELETE, "/comment/deletecomment/{profileId}/{problemId}/{commentId}")
                        .access("@customSecurity.checkCommentAuthorAndProblemId(#problemId, #commentId, #profileId)")
                        .anyRequest()
                        .authenticated()
        );
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://127.0.0.1:5173/", "http://127.0.0.1:5173", "https://j-a-n.netlify.app/", "https://j-a-n.netlify.app"));
        configuration.setAllowedMethods(List.of("POST", "PUT", "GET", "OPTIONS", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
