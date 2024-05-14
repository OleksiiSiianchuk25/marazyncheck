package ua.lviv.marazyncheck.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.lviv.marazyncheck.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}
