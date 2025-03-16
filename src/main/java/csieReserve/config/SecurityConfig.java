package csieReserve.config;

import csieReserve.Repository.RefreshRepository;
import csieReserve.exception.CustomAccessDeniedHandler;
import csieReserve.exception.ExceptionHandlerFilter;
import csieReserve.jwt.CustomLogoutFilter;
import csieReserve.jwt.JWTFilter;
import csieReserve.jwt.JWTUtil;
import csieReserve.jwt.LoginFilter;
import csieReserve.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig{
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final CorsConfigurationSource corsConfigurationSource;
    private final RefreshRepository refreshRepository;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;


//    /** 세션 공유를 위한 repository **/
//    @Bean
//    public SecurityContextRepository securityContextRepository() {
//        return new HttpSessionSecurityContextRepository(); // 세션 기반 SecurityContext 저장소 사용
//    }

    /** 인증매니저의 흐름 오버라이딩 **/
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//        AuthenticationManagerBuilder authenticationManagerBuilder =
//                http.getSharedObject(AuthenticationManagerBuilder.class);
//        authenticationManagerBuilder.userDetailsService(customUserDetailsService)
//                .passwordEncoder(bCryptPasswordEncoder());
//        return authenticationManagerBuilder.build();
//    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Bean
    public SecurityFilterChain FilterChain(HttpSecurity http) throws Exception{
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource)); // ✅ CORS 설정 추가

//        토큰 비활성화
        http
                .csrf((auth) -> auth.disable());

        http
                .formLogin((auth) -> auth.disable());

        http
                .httpBasic((auth) -> auth.disable());



//        JWTFilter 등록
        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshRepository), UsernamePasswordAuthenticationFilter.class);
        http
                .csrf((auth) -> auth.disable());

        http
                .formLogin((auth) -> auth.disable());

        http
                .httpBasic((auth) -> auth.disable());

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                        .requestMatchers("/login", "/", "/api/signup").permitAll()
                        .requestMatchers("/api/reissue").permitAll()
                        .requestMatchers("/api/signup/**").permitAll()//
                        .requestMatchers("/api/faq/getAll").permitAll()
                        .requestMatchers("/api/reservation/**").permitAll()
                        .requestMatchers("/api/admin/studentFeePayer/verify").permitAll()
//                        .requestMatchers("/api/logout").authenticated()
                        .requestMatchers("/api/admin/faq/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/notice/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/studentFeePayer/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/reservation/**").hasRole("ADMIN")
                        .anyRequest().authenticated());

        http
                .exceptionHandling()
                    .accessDeniedHandler(customAccessDeniedHandler);

//        JWTFilter 등록
        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshRepository), UsernamePasswordAuthenticationFilter.class);
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);
        http
                .addFilterBefore(new ExceptionHandlerFilter(), CustomLogoutFilter.class);
//                        .logout()
//                        .disable();

        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));


//                .addFilterBefore(new SecurityContextPersistenceFilter(), UsernamePasswordAuthenticationFilter.class);
//
//        http
//                .sessionManagement((auth) -> auth
//                        .maximumSessions(1)
////                        .maxSessionsPreventsLogin(false)
//////                        true : 새로운 로그인 차단
//////                        false : 기존 세션 하나 삭제
//                );
//        http
//                .sessionManagement((auth) -> auth
//                        .sessionFixation().changeSessionId()
//                );


        return http.build();
    }
}
