package com.cropsys.backend.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.cropsys.backend.Repository.RoleRepository;
import com.cropsys.backend.Repository.UserRepository;
import com.cropsys.backend.Security.JWT.AuthEnteryPointJwt;
import com.cropsys.backend.Security.JWT.AuthTokenFilter;
import com.cropsys.backend.Security.Requset.SignupRequest;
import com.cropsys.backend.model.AppRole;
import com.cropsys.backend.model.Role;
import com.cropsys.backend.model.User;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Autowired
    private AuthEnteryPointJwt unauthorizedHandler;

    @Autowired
    private AuthTokenFilter authTokenFilter;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


   

    private SignupRequest signupRequest;
    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider) throws Exception {

        http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                        .csrf(csrf -> csrf.disable())
                            .exceptionHandling(exception ->
                                    exception.authenticationEntryPoint(unauthorizedHandler))
                            .sessionManagement(session ->
                                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                            .authorizeHttpRequests(auth -> auth
                                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                    .requestMatchers("/api/auth/**").permitAll()
                                    .requestMatchers("/api/public/**").permitAll()
                                    .requestMatchers("/api/test/**").permitAll()
                                    .requestMatchers("/images/**").permitAll()
                                    .requestMatchers("/v3/api-docs/**").permitAll()
                                    .requestMatchers("/swagger-ui/**").permitAll()
                                    .requestMatchers("/actuator/**").permitAll()
                                    .requestMatchers("/h2-console/**").permitAll()
                                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                    .requestMatchers("/api/crop/**").permitAll()
                                  
                                   
                                    .anyRequest().authenticated()
                            );
                
                        
                        http.authenticationProvider(authenticationProvider);
                        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
                        http.headers(headers ->
                                headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));
                
                        return http.build();
                    }
                
                
                 
                
                    
                
                    private CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                                return corsConfigurationSource;
            }
        
                    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                "/v2/api-docs",
                "/swagger-resources/**",
                "/swagger-ui.html",
                "/webjars/**"
        );
    }

    
    @Bean
    CommandLineRunner initData(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
    
        return args -> {
    
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("ROLE_USER missing in DB"));
    
            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("ROLE_ADMIN missing in DB"));
    
            if (!userRepository.existsByEmail("admin@example.com")) {
                User admin = new User();
                admin.setName(signupRequest.getName());
                admin.setEmail(signupRequest.getEmail());
                admin.setPassword(signupRequest.getPassword());
                
                admin.setState(signupRequest.getState());
                admin.setPhoneNumber(signupRequest.getPhoneNumber());
                userRepository.save(admin);
            }
    
            if (!userRepository.existsByEmail("user1@example.com")) {
                
                  User user = new User();
                   user.setName(signupRequest.getName());
                   user.setEmail(signupRequest.getEmail());
                   user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
                   user.setState(signupRequest.getState());
                 user.setPhoneNumber(signupRequest.getPhoneNumber());

                userRepository.save(user);
            }
        };
    }
}    
