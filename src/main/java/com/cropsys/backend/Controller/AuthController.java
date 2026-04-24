package com.cropsys.backend.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cropsys.backend.Security.JWT.JwtUtils;
import com.cropsys.backend.Security.Requset.LoginRequest;
import com.cropsys.backend.Security.Requset.SignupRequest;
import com.cropsys.backend.Security.Responce.UserInfoResponse;
import com.cropsys.backend.Security.ServiceSecurity.UserDetailsImpl;
import com.cropsys.backend.model.AppRole;
import com.cropsys.backend.model.Role;
import com.cropsys.backend.model.User;
import com.cropsys.backend.repository.RoleRepository;
import com.cropsys.backend.repository.UserRepository;

import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthenticationManager authenticationManager;


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
    
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );
        } catch (AuthenticationException exception) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Bad credentials"));
        }
    
        SecurityContextHolder.getContext().setAuthentication(authentication);
    
        UserDetailsImpl userDetails =
            (UserDetailsImpl) authentication.getPrincipal();
    
        ResponseCookie jwtCookie =
            jwtUtils.ganrateJwtCookie(userDetails);
    
        List<String> roles = userDetails.getAuthorities()
            .stream()
            .map(a -> a.getAuthority())
            .toList();
    
        UserInfoResponse response = new UserInfoResponse(
            userDetails.getId(),
                    userDetails.getName(),
                    userDetails.getEmail(),
                    userDetails.getState(),  // enum → string
                    userDetails.getPhoneNumber(),
                    roles
        );

    
        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails.getEmail());
        return ResponseEntity.ok(Map.of("token", jwtToken));
        
            
    }
    
    @PostMapping("/create-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody SignupRequest signupRequest) {
    
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }
    
        Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));
    
        User admin = new User();
        admin.setName(signupRequest.getName());
        admin.setEmail(signupRequest.getEmail());
        admin.setPassword(encoder.encode(signupRequest.getPassword()));
        admin.getRoles().add(adminRole);
    
        userRepository.save(admin);
    
        return ResponseEntity.ok("Admin created successfully!");
    }
    

@PostMapping("/signup")
public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {

    if (userRepository.existsByEmail(signupRequest.getEmail())) {
        return ResponseEntity.badRequest().body("Error: Email is already in use!");
    }

    Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
            .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));

    User user = new User();
    user.setName(signupRequest.getName());
    user.setEmail(signupRequest.getEmail());
    user.setPassword(encoder.encode(signupRequest.getPassword()));
    user.setState(signupRequest.getState());
    user.setPhoneNumber(signupRequest.getPhoneNumber());
    user.getRoles().add(userRole);

    userRepository.save(user);

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(Map.of("message", "User registered successfully"));
}

@GetMapping("/me")
public ResponseEntity<?> getCurrentUser(
        @RequestHeader(value = "Authorization", required = false) String authHeader) {

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String token = authHeader.substring(7);

    if (!jwtUtils.validateJwtToken(token)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String email = jwtUtils.getUserNameFromJwtToken(token);

    Optional<User> optionalUser = userRepository.findByEmail(email);

    if (optionalUser.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("User not registered");
    }

    User user = optionalUser.get();

    Map<String, Object> response = new HashMap<>();
    response.put("id", user.getId());
    response.put("name", user.getName());
    response.put("email", user.getEmail());
    response.put("location", user.getState());
    response.put("phoneNumber", user.getPhoneNumber());

    return ResponseEntity.ok(response);
}






    
    @GetMapping("/userDetails")
    public ResponseEntity<?> getUserDetails(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.ganrateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(r -> r.getAuthority())
                .collect(Collectors.toList());
     
                UserInfoResponse response = new UserInfoResponse(
                    userDetails.getId(),
                    userDetails.getName(),
                    userDetails.getEmail(),
                    userDetails.getState(),  // enum → string
                    userDetails.getPhoneNumber(),
                    roles

            );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, jwtUtils.getCleanJwtCookie().toString())
            .build();
    }
    
}
