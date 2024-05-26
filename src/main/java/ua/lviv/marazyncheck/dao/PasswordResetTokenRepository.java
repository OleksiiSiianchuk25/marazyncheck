package ua.lviv.marazyncheck.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.lviv.marazyncheck.entity.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByCode(String code);
    void deleteByEmail(String email);
}
