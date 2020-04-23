package hr.fer.genericframeworkforautomaticdictationcorrection.Services;

import hr.fer.genericframeworkforautomaticdictationcorrection.Models.Role;

import java.util.List;

public interface RoleService {

    Role findByName(String name);

    List<Role> findAll();

    Role updateRole(Role role);

    Role saveRole(Role role);

    void deleteRole(Role role);
}
