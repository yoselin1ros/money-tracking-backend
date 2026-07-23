package moneytracking.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserRequestDTO {
    @Email(message = "Email should be valid")
    @NotNull(message = "Email is a required field")
    private String email;

    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @NotNull(message = "Password is a required field")
    private String passwordHash;
    
    @Size(min = 3, max = 50, message = "Display name must be between 3 and 50 characters")
    @NotNull(message = "Display name is a required field")
    private String displayName;
    
    private String preferredCurrency;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPreferredCurrency() {
        return preferredCurrency;
    }

    public void setPreferredCurrency(String preferredCurrency) {
        this.preferredCurrency = preferredCurrency;
    }

    @Override
    public String toString() {
        return "UserRequestDTO [email=" + email + ", passwordHash=" + passwordHash + ", displayName=" + displayName
                + ", preferredCurrency=" + preferredCurrency + "]";
    }

}
