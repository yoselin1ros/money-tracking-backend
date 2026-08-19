package moneytracking.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PasswordChangeRequestDTO {
    @NotNull(message = "Current password is a required field")
    private String currentPassword;

    @Size(min = 8, max = 100, message = "New password must be between 8 and 100 characters")
    @NotNull(message = "New password is a required field")
    private String newPassword;

    @NotNull(message = "Repeat new password is a required field")
    private String repeatNewPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRepeatNewPassword() {
        return repeatNewPassword;
    }

    public void setRepeatNewPassword(String repeatNewPassword) {
        this.repeatNewPassword = repeatNewPassword;
    }

    @Override
    public String toString() {
        return "PasswordChangeRequestDTO [currentPassword=" + currentPassword + ", newPassword=" + newPassword
                + ", repeatNewPassword=" + repeatNewPassword + "]";
    }

}
