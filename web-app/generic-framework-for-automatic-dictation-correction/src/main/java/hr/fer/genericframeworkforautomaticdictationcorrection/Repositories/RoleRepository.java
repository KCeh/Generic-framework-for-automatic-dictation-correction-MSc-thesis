package hr.fer.genericframeworkforautomaticdictationcorrection.Repositories;

import hr.fer.genericframeworkforautomaticdictationcorrection.Models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);

    //Role findById(Long id);
    List<Role> findAll();
}
