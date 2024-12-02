package Hieu.demo.mapper;

import Hieu.demo.dto.request.PermissionRequest;
import Hieu.demo.dto.request.RoleRequest;
import Hieu.demo.dto.response.PermissionResponse;
import Hieu.demo.dto.response.RoleResponse;
import Hieu.demo.entity.Permission;
import Hieu.demo.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
