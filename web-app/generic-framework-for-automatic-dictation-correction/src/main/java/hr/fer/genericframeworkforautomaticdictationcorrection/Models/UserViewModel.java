package hr.fer.genericframeworkforautomaticdictationcorrection.Models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserViewModel {
    private String email;

    private String firstName;

    private String lastName;

    private List<String> roles;

    public UserViewModel(User user){
        email=user.getEmail();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        Collection<Role> userRoles=user.getRoles();

        roles=new ArrayList<>();
        for(Role role : userRoles){
            roles.add(role.getName());
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
