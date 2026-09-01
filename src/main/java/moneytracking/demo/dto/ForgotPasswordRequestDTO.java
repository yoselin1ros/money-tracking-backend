package moneytracking.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class ForgotPasswordRequestDTO {
    @Email(message = "Email should be valid")
    @NotNull(message = "Email is a required field")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "ForgotPasswordRequestDTO [email=" + email + "]";
    }

}
