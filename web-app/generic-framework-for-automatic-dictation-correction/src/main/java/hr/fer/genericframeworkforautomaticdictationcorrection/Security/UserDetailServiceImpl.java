package hr.fer.genericframeworkforautomaticdictationcorrection.Security;

import hr.fer.genericframeworkforautomaticdictationcorrection.Models.Role;
import hr.fer.genericframeworkforautomaticdictationcorrection.Models.User;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.HashSet;

@Service
@Transactional
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userService.findByEmail(s);

        if (ObjectUtils.isEmpty(user)) {
            return new org.springframework.security.core.userdetails.User(" ", " ", new HashSet<>());
        }

        Collection<Role> roles = user.getRoles();
        //process names
        //add to new list
        //send to userdetalis

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getFirstName()+" "+user.getLastName()).disabled(!user.isActive()).password(user.getPassword()).roles("USER").build();
        //check roles stuff
    }
}
