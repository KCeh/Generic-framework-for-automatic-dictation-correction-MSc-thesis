package hr.fer.genericframeworkforautomaticdictationcorrection.Services;

import hr.fer.genericframeworkforautomaticdictationcorrection.Models.User;

import java.util.List;

public interface UserService {

    //find by id provjetiti

    User findByEmail(String email);

    User updateUser(User user);

    User saveUser(User user);

    void deleteUser(User user);

    List<User> findAll();
}
