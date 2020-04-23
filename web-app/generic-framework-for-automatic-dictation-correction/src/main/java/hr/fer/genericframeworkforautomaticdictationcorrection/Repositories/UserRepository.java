package hr.fer.genericframeworkforautomaticdictationcorrection.Repositories;

import hr.fer.genericframeworkforautomaticdictationcorrection.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
    User findByEmail(String email);

    //User findById(Long id);

    List<User> findAll();
}