package ua.lviv.marazyncheck.service.interfaces;

import ua.lviv.marazyncheck.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Role save(Role role);
    Optional<Role> findById(Integer id);
    List<Role> findAll();
    void deleteById(Integer id);
}
