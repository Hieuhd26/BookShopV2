package Hieu.demo.repository;

import Hieu.demo.dto.response.UserResponse;
import Hieu.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    public boolean existsByUsername(String name);

    Optional<User> findByUsername(String username);
}
