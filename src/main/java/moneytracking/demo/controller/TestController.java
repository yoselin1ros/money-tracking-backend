package moneytracking.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import moneytracking.demo.dto.CustomUserDetails;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }
    @GetMapping("/user")
    public String userAccess() {
        return "User Content.";
    }

    @GetMapping("/me")
    public String getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // Returns the username stored in the token
        return "Logged in user: " + userDetails.getUsername() + " - Email Verified: " + userDetails.isEmailVerified() + " - ID: " + userDetails.getId();
    }
}
