package Hieu.demo.mapper;

import Hieu.demo.dto.request.PermissionRequest;
import Hieu.demo.dto.request.UserCreationRequest;
import Hieu.demo.dto.request.UserUpdateRequest;
import Hieu.demo.dto.response.PermissionResponse;
import Hieu.demo.dto.response.UserResponse;
import Hieu.demo.entity.Permission;
import Hieu.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission user);
}
