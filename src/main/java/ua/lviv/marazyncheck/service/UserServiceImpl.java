package ua.lviv.marazyncheck.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.lviv.marazyncheck.dto.UserDTO;
import ua.lviv.marazyncheck.dto.UserUpdateDTO;
import ua.lviv.marazyncheck.entity.PasswordResetToken;
import ua.lviv.marazyncheck.entity.User;
import ua.lviv.marazyncheck.dao.PasswordResetTokenRepository;
import ua.lviv.marazyncheck.dao.UserRepository;
import ua.lviv.marazyncheck.service.interfaces.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           EmailService emailService, PasswordResetTokenRepository passwordResetTokenRepository,
                           PasswordResetTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Page<UserDTO> findAllForTable(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> new UserDTO(user.getName(), user.getEmail(), user.getTelegram()));
    }

    @Override
    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByEmail(String email) {
        userRepository.deleteByEmail(email);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void updateUserInfo(User user, UserUpdateDTO userUpdateDTO) {
        if (userUpdateDTO.getName() != null) {
            user.setName(userUpdateDTO.getName());
        }
        if (userUpdateDTO.getEmail() != null) {
            user.setEmail(userUpdateDTO.getEmail());
        }
        if (userUpdateDTO.getTelegram() != null) {
            user.setTelegram(userUpdateDTO.getTelegram());
        }
        if (userUpdateDTO.getNewPassword() != null && !userUpdateDTO.getNewPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(userUpdateDTO.getNewPassword());
            user.setPassword(encodedPassword);
        }
        userRepository.save(user);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public boolean updateUser(UserDTO userDTO) {
        return userRepository.findByEmail(userDTO.getEmail())
                .map(user -> {
                    user.setName(userDTO.getName());
                    user.setEmail(userDTO.getEmail());
                    user.setTelegram(userDTO.getTelegram());
                    userRepository.save(user);
                    return true;
                }).orElse(false);
    }

    @Override
    public void sendResetCode(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String code = String.format("%06d", (int) (Math.random() * 1000000));
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setCode(code);
            resetToken.setEmail(email);
            resetToken.setExpirationDate(LocalDateTime.now().plusMinutes(30));
            tokenRepository.save(resetToken);

            emailService.sendSimpleMessage(email, "Password Reset Request", "Your reset code is: " + code);
        }
    }

    @Override
    public boolean verifyResetCode(String email, String code) {
        Optional<PasswordResetToken> tokenOptional = passwordResetTokenRepository.findByCode(code);
        if (tokenOptional.isPresent()) {
            PasswordResetToken token = tokenOptional.get();
            return token.getEmail().equals(email) && token.getExpirationDate().isAfter(LocalDateTime.now());
        }
        return false;
    }

    @Override
    public void resetPassword(String email, String code, String newPassword) {
        Optional<PasswordResetToken> tokenOptional = passwordResetTokenRepository.findByCode(code);
        if (tokenOptional.isPresent()) {
            PasswordResetToken token = tokenOptional.get();
            if (token.getEmail().equals(email) && token.getExpirationDate().isAfter(LocalDateTime.now())) {
                Optional<User> userOptional = userRepository.findByEmail(email);
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userRepository.save(user);
                    passwordResetTokenRepository.delete(token);
                }
            }
        }
    }
}
