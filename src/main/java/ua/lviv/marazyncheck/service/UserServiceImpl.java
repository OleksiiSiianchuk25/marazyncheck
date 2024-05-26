package ua.lviv.marazyncheck.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.lviv.marazyncheck.dto.UserDTO;
import ua.lviv.marazyncheck.dto.UserUpdateDTO;
import ua.lviv.marazyncheck.entity.User;
import ua.lviv.marazyncheck.dao.UserRepository;
import ua.lviv.marazyncheck.service.interfaces.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
}
