package sg.edu.nus.iss.vttp5a_mini_project_a.model;

import java.io.Serializable;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class Login implements Serializable{
    
    @NotEmpty(message = "User ID is required")
    @Size(min = 1, max = 30, message = "User ID should have between 1 and 30 characters")
    private String userId;

    @NotEmpty(message = "Password is required")
    @Size(min = 5, message = "Password should have a minimum of 5 characters")
    private String password;


    public Login() {
    }

    public Login(
            @NotEmpty(message = "User ID is required") @Size(min = 1, max = 30, message = "User ID should have between 1 and 30 characters") String userId,
            @NotEmpty(message = "Password is required") @Size(min = 5, message = "Password should have a minimum of 5 characters") String password) {
        this.userId = userId;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    
}
