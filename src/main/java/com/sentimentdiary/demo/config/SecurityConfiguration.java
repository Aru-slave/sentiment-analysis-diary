package com.sentimentdiary.demo.config;


import com.sentimentdiary.demo.auth.RefreshToken.RefreshTokenRepository;
import com.sentimentdiary.demo.auth.filter.JwtAuthenticationFilter;
import com.sentimentdiary.demo.auth.filter.JwtVerificationFilter;
import com.sentimentdiary.demo.auth.handler.*;
import com.sentimentdiary.demo.auth.jwt.JwtTokenizer;
import com.sentimentdiary.demo.auth.utils.CustomAuthorityUtils;
import com.sentimentdiary.demo.auth.utils.CustomOauth2UserService;
import com.sentimentdiary.demo.member.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfiguration {
    private final JwtTokenizer jwtTokenizer;
    private final CustomAuthorityUtils authorityUtils;

    private final CustomOauth2UserService customOAuth2UserService;
    private final Oauth2MemberSuccessHandler oauth2MemberSuccessHandler;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public SecurityConfiguration(JwtTokenizer jwtTokenizer,
                                 CustomAuthorityUtils authorityUtils,
                                 CustomOauth2UserService customOAuth2UserService,
                                 Oauth2MemberSuccessHandler oauth2MemberSuccessHandler,
                                 MemberRepository memberRepository,
                                 RefreshTokenRepository refreshTokenRepository) {
        this.jwtTokenizer = jwtTokenizer;
        this.authorityUtils = authorityUtils;
        this.customOAuth2UserService = customOAuth2UserService;
        this.oauth2MemberSuccessHandler = oauth2MemberSuccessHandler;
        this.memberRepository = memberRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .headers().frameOptions().sameOrigin()
                .and()
                .csrf().disable()
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .exceptionHandling()  // 추가
                .authenticationEntryPoint(new MemberAuthenticationEntryPoint())  // 추가
                .accessDeniedHandler(new MemberAccessDeniedHandler())            // 추가
                .and()
                .apply(new CustomFilterConfigurer())
                .and()
                .logout()
                .logoutUrl("/auth/logout") //logout 처리 url
                .and()
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .oauth2Login()//OAuth2 로그인 시작
                .userInfoEndpoint()//로그인 성공시 사용자 정보를 가져옴
                .userService(customOAuth2UserService); //로그인 성공 후 oauth2userservice 호출
        http
                .oauth2Login()
                .successHandler(new Oauth2MemberSuccessHandler(jwtTokenizer, authorityUtils, memberRepository, refreshTokenRepository));//oauth2 인증 성공 후처리 handler 호출
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token", "Authorization", "Refresh", "heart-beat"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);

            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtTokenizer, refreshTokenRepository);
            jwtAuthenticationFilter.setFilterProcessesUrl("/auth/login");          // login url

            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new MemberAuthenticationSuccessHandler());
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new MemberAuthenticationFailureHandler());

            JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtTokenizer, authorityUtils);

            builder
                    .addFilter(jwtAuthenticationFilter)
                    .addFilterAfter(jwtVerificationFilter, JwtAuthenticationFilter.class);
        }

    }


}