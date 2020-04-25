package hr.fer.genericframeworkforautomaticdictationcorrection.Services.Implementations;

import hr.fer.genericframeworkforautomaticdictationcorrection.Exceptions.EmailExistsException;
import hr.fer.genericframeworkforautomaticdictationcorrection.Forms.NewUserForm;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.PasswordResetToken;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.Role;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.User;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.VerificationToken;
import hr.fer.genericframeworkforautomaticdictationcorrection.Repositories.PasswordResetTokenRepository;
import hr.fer.genericframeworkforautomaticdictationcorrection.Repositories.UserRepository;
import hr.fer.genericframeworkforautomaticdictationcorrection.Repositories.VerificationTokenRepository;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.RoleService;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.UserService;
import hr.fer.genericframeworkforautomaticdictationcorrection.Utils.Constants;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    RoleService roleService;

    @Autowired
    VerificationTokenRepository verificationTokenRepository;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User registerNewUserAccount(NewUserForm accountDto) throws EmailExistsException {
        if (emailExist(accountDto.getEmail())) {
            throw new EmailExistsException("There is already an account with that email address: " + accountDto.getEmail());
        }

        User user = new User();
        user.setEmail(accountDto.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(accountDto.getPassword()));
        user.setFirstName(accountDto.getFirstName());
        user.setLastName(accountDto.getLastName());
        user.setActive(false);

        Role role = roleService.findByName("ROLE_USER");
        user.setRoles(Arrays.asList(role));
        return saveUser(user);
    }

    @Override
    @Transactional
    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        verificationTokenRepository.save(myToken);
    }

    @Override
    public VerificationToken getVerificationToken(String VerificationToken) {
        return verificationTokenRepository.findByToken(VerificationToken);
    }

    @Override
    public User getUser(String verificationToken) {
        return verificationTokenRepository.findByToken(verificationToken).getUser();
    }

    @Override
    @Transactional
    public void createPasswordResetTokenForUser(User user, String token) {
        final PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(myToken);
    }

    @Override
    @Transactional
    public void changeUserPassword(User user, String password) {
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
    }

    public String validatePasswordResetToken(int id, String token) {
        PasswordResetToken passToken = passwordResetTokenRepository.findByToken(token);
        if ((ObjectUtils.isEmpty(passToken)) || (passToken.getUser().getId() != id)) {
            return "invalidToken";
        }

        Calendar cal = Calendar.getInstance();
        if ((passToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            return "expired";
        }

        User user = passToken.getUser();
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, Arrays.asList(new SimpleGrantedAuthority(Constants.Authority.CHANGE_PASSWORD)));
        SecurityContextHolder.getContext().setAuthentication(auth);
        return null;
    }

    private boolean emailExist(String email) {
        User user = userRepository.findByEmail(email);
        return !ObjectUtils.isEmpty(user);
    }
}
