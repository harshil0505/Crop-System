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

import com.cropsys.backend.Security.JWT.AuthEnteryPointJwt;
import com.cropsys.backend.Security.JWT.AuthTokenFilter;

import com.cropsys.backend.Security.Requset.SignupRequest;
import com.cropsys.backend.model.AppRole;
import com.cropsys.backend.model.Role;
import com.cropsys.backend.model.State;
import com.cropsys.backend.model.User;
import com.cropsys.backend.repository.RoleRepository;
import com.cropsys.backend.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Slf4j
public class WebSecurityConfig {

    @Autowired
    private AuthEnteryPointJwt unauthorizedHandler;

    @Autowired
    private AuthTokenFilter authTokenFilter;
    
    SignupRequest signupRequest;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


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
                .cors(Customizer.withDefaults())
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
                        admin.setName("Admin");
                        admin.setEmail("admin@example.com");
                        admin.setPassword(passwordEncoder.encode("admin123"));
                        admin.setState(State.GOA);   // adjust enum value
                        admin.setPhoneNumber("9999999999");
                        admin.addRole(adminRole);
            
                        userRepository.save(admin);
                    }
            
                    // 3️⃣ Create NORMAL user
                    if (!userRepository.existsByEmail("user@example.com")) {
                        User user = new User();
                        user.setName("User");
                        user.setEmail("user@example.com");
                        user.setPassword(passwordEncoder.encode("user123"));
                        user.setState(State.GUJARAT);   // adjust enum value
                        user.setPhoneNumber("8888888888");
                        user.addRole(userRole);
            
                        userRepository.save(user);
                    }
                };
}    
}