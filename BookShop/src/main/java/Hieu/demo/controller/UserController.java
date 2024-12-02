package Hieu.demo.controller;

import Hieu.demo.dto.request.UserCreationRequest;
import Hieu.demo.dto.request.UserUpdateRequest;
import Hieu.demo.dto.response.ApiResponse;
import Hieu.demo.dto.response.UserResponse;
import Hieu.demo.entity.User;
import Hieu.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping()
    // map data cua ng dung vao object
    public ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest request) {
        ApiResponse<User> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createUser(request));
        return apiResponse;
    }

    @GetMapping()
    public ApiResponse<List<UserResponse>> getAllUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("Username: {} ",authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> {
            log.info(grantedAuthority.getAuthority());
        });

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setResult(userService.getAll());
        return apiResponse;
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable int userId) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setResult(userService.getUser(userId));
        return apiResponse;

    }

    @GetMapping("/myInfor")
    public ApiResponse<UserResponse> getMyInfor() {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setResult(userService.getMyInfor());
        return apiResponse;

    }

    @PutMapping("/{userId}")
    public ApiResponse<UserResponse> updateUser(@RequestBody UserUpdateRequest request, @PathVariable int userId) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setResult(userService.updateUser(request, userId));
        return apiResponse;
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<String> deleteUser(@PathVariable int userId) {
        ApiResponse apiResponse = new ApiResponse();
        userService.deleteUser(userId);
        apiResponse.setResult("User has been deleted");
        return apiResponse;
    }
}
