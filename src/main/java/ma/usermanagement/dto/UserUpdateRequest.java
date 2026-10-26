package ma.usermanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserUpdateRequest {

    @NotBlank(message = "name is mandatory")
    @Size(max = 100, message = "name must be under 100 characters")
    private String name;

    @NotBlank(message = "email is mandatory")
    @Email(message = "address invalid")
    @Size(max = 150, message = "email must be under 150 characters")
    private String email;

    public UserUpdateRequest() {
    }

    public UserUpdateRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
