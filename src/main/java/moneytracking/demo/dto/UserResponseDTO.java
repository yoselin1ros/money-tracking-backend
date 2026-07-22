package moneytracking.demo.dto;

public class UserResponseDTO {
    private Long id;
    private String email;
    private String displayName;
    private String preferredCurrency;
    private boolean emailVerified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    @Override
    public String toString() {
        return "UserResponseDTO [id=" + id + ", email=" + email + ", displayName=" + displayName
                + ", preferredCurrency=" + preferredCurrency + ", emailVerified=" + emailVerified + "]";
    }

}
