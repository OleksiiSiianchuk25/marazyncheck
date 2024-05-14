package ua.lviv.marazyncheck.service.interfaces;

import ua.lviv.marazyncheck.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User save(User user);
    Optional<User> findById(Integer id);
    List<User> findAll();
    void deleteById(Integer id);
}
