
package ma.usermanagement.dto;

import ma.usermanagement.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserCreateRequest {

    @NotBlank(message = "name is mandatory")
    @Size(max = 100, message = "name must be unde 100 character")
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "adress invalid")
    @Size(max = 150, message = "mail must be unde 150 character")
    private String email;

    @NotBlank(message = "password mandatory")
    @Size(min = 8, max = 72,
            message = "password must be at least 8 character")
    private String password;

    @NotNull(message = "role is obligatory")
    private Role role;

    public UserCreateRequest() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
