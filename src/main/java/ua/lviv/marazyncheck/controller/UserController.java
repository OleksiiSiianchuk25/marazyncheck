package ua.lviv.marazyncheck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ua.lviv.marazyncheck.dto.UserDTO;
import ua.lviv.marazyncheck.dto.UserUpdateDTO;
import ua.lviv.marazyncheck.entity.User;
import ua.lviv.marazyncheck.security.JwtTokenUtil;
import ua.lviv.marazyncheck.service.UserDetailsServiceImplementation;
import ua.lviv.marazyncheck.service.interfaces.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsServiceImplementation userDetailsService;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager,
                          JwtTokenUtil jwtTokenUtil, UserDetailsServiceImplementation userDetailsService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.save(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.findAllForTable(pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/by-email/{email}")
    public ResponseEntity<Void> deleteUserByEmail(@PathVariable String email) {
        userService.deleteByEmail(email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/info")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        User user = userService.findByEmail(authentication.getName()).get();
        UserDTO userDTO = new UserDTO(user.getName(), user.getEmail(), user.getTelegram());
        return ResponseEntity.ok(userDTO);
    }

    @PatchMapping("/update-user")
    public ResponseEntity<?> updateUser(@RequestBody UserDTO userDTO) {
        boolean updateStatus = userService.updateUser(userDTO);
        if (updateStatus) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateUserInfo(@RequestBody UserUpdateDTO userUpdateDTO, Authentication currentAuth) {
        User currentUser = userService.findByEmail(currentAuth.getName()).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.notFound().build();
        }

        if (!currentUser.getEmail().equals(userUpdateDTO.getEmail()) && userService.emailExists(userUpdateDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Така електронна пошта вже використовується");
        }

        boolean isEmailUpdated = !currentUser.getEmail().equals(userUpdateDTO.getEmail());
        userService.updateUserInfo(currentUser, userUpdateDTO);

        if (isEmailUpdated) {
            UserDetails userDetails = userDetailsService.createUserDetails(currentUser);
            String newJwtToken = jwtTokenUtil.generateToken(userDetails);
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    userUpdateDTO.getEmail(), currentUser.getPassword(), currentAuth.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            Map<String, Object> response = new HashMap<>();
            response.put("user", new UserDTO(currentUser.getName(), currentUser.getEmail(), currentUser.getTelegram()));
            response.put("jwtToken", newJwtToken);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok(new UserDTO(currentUser.getName(), currentUser.getEmail(), currentUser.getTelegram()));
    }
}
