package moneytracking.demo.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequestDTO {
    private String token;

    @Size(min = 8, max = 100, message = "New password must be between 8 and 100 characters")
    @NotNull(message = "New password is a required field")
    private String newPassword;

    @NotNull(message = "Repeat new password is a required field")
    private String repeatNewPassword;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
        return "ResetPasswordRequestDTO [token=" + token + ", newPassword=" + newPassword + ", repeatNewPassword="
                + repeatNewPassword + "]";
    }

}
