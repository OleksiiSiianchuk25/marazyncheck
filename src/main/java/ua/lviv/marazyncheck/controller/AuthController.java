package ua.lviv.marazyncheck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private UserDetailsService userDetailsService;
    private AuthenticationManager authenticationManager;
    private JwtTokenUtil jwtTokenUtil;
    private RoleService roleService;

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
            return ResponseEntity.badRequest().body("Email is already in use!");
        }
        User newUser = new User();
        newUser.setName(registerRequest.getName());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setTelegram(registerRequest.getTelegram());
        newUser.setRole(roleService.findById(2).get());
        userService.save(newUser);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        final String token = jwtTokenUtil.generateToken(authenticate.getName());
        return ResponseEntity.ok(new JwtResponse(token));
    }

}
