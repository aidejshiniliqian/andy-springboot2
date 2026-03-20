package com.warehouse.management.controller;

import com.warehouse.management.common.Result;
import com.warehouse.management.entity.User;
import com.warehouse.management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public Result<User> create(@RequestBody User user) {
        if (userService.existsByUsername(user.getUsername())) {
            return Result.error("用户名已存在");
        }
        return Result.success(userService.save(user));
    }

    @PutMapping("/{id}")
    public Result<User> update(@PathVariable Long id, @RequestBody User user) {
        return userService.findById(id)
                .map(existing -> {
                    user.setId(id);
                    return Result.success(userService.save(user));
                })
                .orElse(Result.error("用户不存在"));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (userService.findById(id).isEmpty()) {
            return Result.error("用户不存在");
        }
        userService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<User> findById(@PathVariable Long id) {
        return userService.findById(id)
                .map(Result::success)
                .orElse(Result.error("用户不存在"));
    }

    @GetMapping
    public Result<List<User>> findAll() {
        return Result.success(userService.findAll());
    }

    @GetMapping("/page")
    public Result<Page<User>> findPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Result.success(userService.findAll(pageable));
    }
}
