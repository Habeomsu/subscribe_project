package main.AuthTemplate.user.repository;

import main.AuthTemplate.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUsername(String username);
    User findByUsername(String username);
    void deleteByUsername(String username);
}
