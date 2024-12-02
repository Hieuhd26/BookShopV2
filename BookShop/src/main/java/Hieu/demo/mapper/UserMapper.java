package Hieu.demo.mapper;

import Hieu.demo.dto.request.UserCreationRequest;
import Hieu.demo.dto.request.UserUpdateRequest;
import Hieu.demo.dto.response.UserResponse;
import Hieu.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring") // gen mapper cho spring theo kieu DI
public interface UserMapper {
    User toUser(UserCreationRequest request);
    @Mapping(target = "roles",ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    UserResponse toUserResponse(User user);
}
