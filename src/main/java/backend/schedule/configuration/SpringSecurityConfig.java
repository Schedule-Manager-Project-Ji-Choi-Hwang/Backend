package backend.schedule.configuration;

import backend.schedule.jwt.JwtTokenFilter;
import backend.schedule.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final MemberService memberService;
//    private static String secretKey = "secret-key-456456";
    @Value("${spring.jwt.secretkey}")
    private String secretKey;


    //CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:19006","http://192.168.0.4:8081", "exp://192.168.0.7:8081"));
//        config.setAllowedOrigins(Arrays.asList("exp://192.168.0.7:8081"));
        config.setAllowedMethods(Arrays.asList("HEAD", "POST", "GET", "DELETE", "PUT", "PATCH"));
        config.setAllowedHeaders(Arrays.asList("*"));

        config.setExposedHeaders(Arrays.asList("Your-Custom-Header-1", "Your-Custom-Header-2"));
        //응답 헤더 노출
        config.setExposedHeaders(Arrays.asList(HttpHeaders.AUTHORIZATION, "Refresh-Token"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        RequestMatcher notRefreshEndpoint = new NegatedRequestMatcher(new AntPathRequestMatcher("/member/refresh"));
        return http
                .httpBasic().disable()
                .csrf().disable()
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(new JwtTokenFilter(secretKey), UsernamePasswordAuthenticationFilter.class)
                .requestMatcher(notRefreshEndpoint)
                .authorizeRequests()
                .antMatchers("/member/sign-up", "/member/log-in", "/member/refresh", "/member/findLoginId", "/member/findPassword", "/study-board").permitAll()
                .anyRequest().authenticated()
                .and().build();
    }
}
