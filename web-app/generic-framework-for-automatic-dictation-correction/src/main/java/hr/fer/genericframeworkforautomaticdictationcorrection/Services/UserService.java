package hr.fer.genericframeworkforautomaticdictationcorrection.Services;

import hr.fer.genericframeworkforautomaticdictationcorrection.Exceptions.EmailExistsException;
import hr.fer.genericframeworkforautomaticdictationcorrection.Forms.NewUserForm;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.User;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.VerificationToken;

import java.util.List;

public interface UserService {

    //find by id provjetiti

    User findByEmail(String email);

    User updateUser(User user);

    User saveUser(User user);

    void deleteUser(User user);

    List<User> findAll();

    User registerNewUserAccount(NewUserForm accountDto) throws EmailExistsException;

    void createVerificationToken(User user, String token);

    VerificationToken getVerificationToken(String VerificationToken);

    User getUser(String verificationToken);

}
