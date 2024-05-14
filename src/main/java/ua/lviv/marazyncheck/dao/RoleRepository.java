package ua.lviv.marazyncheck.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.lviv.marazyncheck.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}
