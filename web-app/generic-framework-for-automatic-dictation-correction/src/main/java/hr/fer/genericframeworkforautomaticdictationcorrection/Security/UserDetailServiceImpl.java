package hr.fer.genericframeworkforautomaticdictationcorrection.Security;

import hr.fer.genericframeworkforautomaticdictationcorrection.Models.User;
import hr.fer.genericframeworkforautomaticdictationcorrection.Services.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userService.findByEmail(s);

        if (ObjectUtils.isEmpty(user)) {
            return new org.springframework.security.core.userdetails.User(" ", " ", new HashSet<>());
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getFirstName()+" "+user.getLastName()).disabled(!user.isActive()).password(user.getPassword()).roles("USER").build();
        //check roles stuff
    }
}
