package ua.lviv.marazyncheck.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.lviv.marazyncheck.dto.UserDTO;
import ua.lviv.marazyncheck.dto.UserUpdateDTO;
import ua.lviv.marazyncheck.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User save(User user);
    Optional<User> findById(Integer id);
    List<User> findAll();
    Page<UserDTO> findAllForTable(Pageable pageable);
    void deleteById(Integer id);
    void deleteByEmail(String email);
    Optional<User> findByEmail(String email);

    void updateUserInfo(User user, UserUpdateDTO userUpdateDTO);
    boolean emailExists(String email);
    boolean updateUser(UserDTO userDTO);
    void sendResetCode(String email);
    boolean verifyResetCode(String email, String code);
    void resetPassword(String email, String code, String newPassword);
}
