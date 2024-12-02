package Hieu.demo.service;

import Hieu.demo.dto.request.PermissionRequest;
import Hieu.demo.dto.request.RoleRequest;
import Hieu.demo.dto.response.PermissionResponse;
import Hieu.demo.dto.response.RoleResponse;
import Hieu.demo.entity.Permission;
import Hieu.demo.mapper.PermissionMapper;
import Hieu.demo.mapper.RoleMapper;
import Hieu.demo.repository.PermissionRepository;
import Hieu.demo.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;
    PermissionRepository permissionRepository;

    public RoleResponse create(RoleRequest request) {
        var role = roleMapper.toRole(request);
        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));
        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);

    }

    public List<RoleResponse> getAll() {
        var roles = roleRepository.findAll();
        return roles.stream().map(roleMapper::toRoleResponse).toList();
    }

    public void delete(String role) {
        roleRepository.deleteById(role);
    }

}
