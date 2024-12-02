package Hieu.demo.repository;

import Hieu.demo.entity.Permission;
import Hieu.demo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role,String> {
}
