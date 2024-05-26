package ua.lviv.marazyncheck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ua.lviv.marazyncheck.dto.LoginRequest;
import ua.lviv.marazyncheck.dto.RegisterRequest;
import ua.lviv.marazyncheck.entity.User;
import ua.lviv.marazyncheck.security.JwtRequest;
import ua.lviv.marazyncheck.security.JwtResponse;
import ua.lviv.marazyncheck.security.JwtTokenUtil;
import ua.lviv.marazyncheck.service.interfaces.RoleService;
import ua.lviv.marazyncheck.service.interfaces.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final RoleService roleService;

    @Autowired
    public AuthController(UserService userService,
                          PasswordEncoder passwordEncoder,
                          UserDetailsService userDetailsService,
                          AuthenticationManager authenticationManager,
                          JwtTokenUtil jwtTokenUtil,
                          RoleService roleService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.roleService = roleService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        if (userService.findByEmail(registerRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Така електронна пошта вже використовується!");
        }
        User newUser = new User();
        newUser.setName(registerRequest.getName());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setRole(roleService.findById(2).orElseThrow(() -> new RuntimeException("Role not found"))); // Adjust as necessary
        newUser.setTelegram(registerRequest.getTelegram());
        userService.save(newUser);
        return ResponseEntity.ok("Користувач успішно зареєстрований!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
