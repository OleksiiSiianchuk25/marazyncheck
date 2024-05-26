package ua.lviv.marazyncheck.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.lviv.marazyncheck.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    void deleteByEmail(String email);
}
