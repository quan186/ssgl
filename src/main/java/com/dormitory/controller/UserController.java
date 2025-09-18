package com.dormitory.controller;

import com.dormitory.entity.User;
import com.dormitory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 用户控制器
 */
@Controller
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户列表页面
     */
    @GetMapping
    public String userList(@RequestParam(required = false) String role,
                          @RequestParam(required = false) String keyword,
                          Model model) {
        List<User> users;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            users = userService.searchUsers(keyword);
        } else if (role != null && !role.trim().isEmpty()) {
            users = userService.findByRole(User.Role.valueOf(role.toUpperCase()));
        } else {
            users = userService.findAll();
        }
        
        model.addAttribute("users", users);
        model.addAttribute("currentRole", role);
        model.addAttribute("keyword", keyword);
        
        return "admin/users/list";
    }
    
    /**
     * 用户详情页面
     */
    @GetMapping("/{id}")
    public String userDetail(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        model.addAttribute("user", user);
        return "admin/users/detail";
    }
    
    /**
     * 创建用户页面
     */
    @GetMapping("/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/users/form";
    }
    
    /**
     * 编辑用户页面
     */
    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        model.addAttribute("user", user);
        return "admin/users/form";
    }
    
    /**
     * 保存用户
     */
    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            if (user.getId() == null) {
                userService.createUser(user);
                redirectAttributes.addFlashAttribute("successMessage", "用户创建成功");
            } else {
                userService.updateUser(user.getId(), user);
                redirectAttributes.addFlashAttribute("successMessage", "用户更新成功");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/users/create";
        }
        
        return "redirect:/users";
    }
    
    /**
     * 删除用户
     */
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "用户删除成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/users";
    }
    
    /**
     * 个人资料页面
     */
    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        User currentUser = (User) authentication.getPrincipal();
        User user = userService.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        model.addAttribute("user", user);
        return "profile";
    }
    
    /**
     * 更新个人资料
     */
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User userDetails, 
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            userService.updateUser(currentUser.getId(), userDetails);
            redirectAttributes.addFlashAttribute("successMessage", "个人资料更新成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/users/profile";
    }
    
    /**
     * 修改密码页面
     */
    @GetMapping("/change-password")
    public String changePasswordForm() {
        return "change-password";
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                @RequestParam String newPassword,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            userService.changePassword(currentUser.getId(), oldPassword, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "密码修改成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/users/change-password";
    }
}
