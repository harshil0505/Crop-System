

package com.cropsys.backend.Security.JWT;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthEnteryPointJwt implements AuthenticationEntryPoint {

 
            
                private static final Logger logger =
                        LoggerFactory.getLogger(AuthEnteryPointJwt.class);
            
                @Override
                public void commence(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        AuthenticationException authException)
                        throws IOException {
            
                    String path = request.getServletPath();
            
                    // ✅ DO NOT RETURN 401 FOR PUBLIC ENDPOINTS
                    if (path.startsWith("/api/crop/")
                        || path.startsWith("/api/auth/")
                        || path.startsWith("/api/public/")
                        || path.startsWith("/swagger")
                        || path.startsWith("/v3/api-docs")) {
                        return;
                    }
            
                    logger.error("Unauthorized error: {}", authException.getMessage());
            
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            
                    final Map<String, Object> body = new HashMap<>();
                    body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
                    body.put("error", "Unauthorized");
                    body.put("message", authException.getMessage());
                    body.put("path", path);
            
                    new ObjectMapper().writeValue(response.getOutputStream(), body);
                }
            }
            