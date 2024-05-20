package ua.lviv.marazyncheck.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.lviv.marazyncheck.entity.User;
import ua.lviv.marazyncheck.service.interfaces.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImplementation implements UserDetailsService {
    private UserService userService;

    @Autowired
    public UserDetailsServiceImplementation(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userService.findByEmail(username);
        System.out.println(user);

        if(user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with this email"+username);
        }


        System.out.println("Loaded user: " + user.get().getEmail() + ", Role: " + user.get().getRole());
        List<GrantedAuthority> authorities = new ArrayList<>();
        return new org.springframework.security.core.userdetails.User(
                user.get().getEmail(),
                user.get().getPassword(),
                authorities);
    }
}
