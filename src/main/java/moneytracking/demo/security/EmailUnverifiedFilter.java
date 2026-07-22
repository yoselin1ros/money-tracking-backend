package moneytracking.demo.security;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import moneytracking.demo.dto.CustomUserDetails;

@Component
public class EmailUnverifiedFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // 1. Bypass public or verification endpoints to avoid infinite loops
        if (path.equals("/verify-email") || path.equals("/login") || path.equals("/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() 
            && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            if (!userDetails.isEmailVerified()) {
                sendForbiddenResponse(response);
                return; // Break the filter chain execution immediately
            }
        }
        filterChain.doFilter(request, response);
    }

    private void sendForbiddenResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Build the structured REST error response
        Map<String, String> errorDetails = Map.of(
            "status", "403",
            "error", "Forbidden",
            "message", "Email unverified"
        );

        objectMapper.writeValue(response.getWriter(), errorDetails);
    }
}
