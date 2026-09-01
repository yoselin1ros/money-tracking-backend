package moneytracking.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProfileRequestDTO {
    @Size(min = 3, max = 50, message = "Display name must be between 3 and 50 characters")
    @NotNull(message = "Display name is a required field")
    private String displayName;

    private String preferredCurrency;

    private String themePreference;

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

    public String getThemePreference() {
        return themePreference;
    }

    public void setThemePreference(String themePreference) {
        this.themePreference = themePreference;
    }

    @Override
    public String toString() {
        return "ProfileRequestDTO [displayName=" + displayName + ", preferredCurrency="
                + preferredCurrency + ", themePreference=" + themePreference + "]";
    }

}
