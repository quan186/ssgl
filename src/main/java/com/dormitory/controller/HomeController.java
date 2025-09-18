package com.dormitory.controller;

import com.dormitory.entity.User;
import com.dormitory.service.UserService;
import com.dormitory.service.DormitoryService;
import com.dormitory.service.StudentDormitoryService;
import com.dormitory.service.RepairRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 首页控制器
 */
@Controller
public class HomeController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DormitoryService dormitoryService;
    
    @Autowired
    private StudentDormitoryService studentDormitoryService;
    
    @Autowired
    private RepairRequestService repairRequestService;
    
    /**
     * 首页
     */
    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            model.addAttribute("currentUser", user);
            
            // 根据用户角色显示不同的统计信息
            if (user.getRole() == User.Role.ADMIN) {
                // 管理员统计信息
                model.addAttribute("totalStudents", userService.findByRole(User.Role.STUDENT).size());
                model.addAttribute("totalDormitories", dormitoryService.findAll().size());
                model.addAttribute("availableDormitories", dormitoryService.findAvailableDormitories().size());
                model.addAttribute("pendingRepairRequests", repairRequestService.findByStatus(RepairRequest.RepairStatus.PENDING).size());
                
                // 各宿舍楼入住率统计
                model.addAttribute("occupancyStats", dormitoryService.getOccupancyRateByBuilding());
                
                // 维修申请统计
                model.addAttribute("repairStats", repairRequestService.countByStatus());
                
                return "admin/dashboard";
            } else {
                // 学生统计信息
                model.addAttribute("myDormitory", studentDormitoryService.findActiveByStudent(user).orElse(null));
                model.addAttribute("myRepairRequests", repairRequestService.findByStudent(user));
                
                return "student/dashboard";
            }
        }
        
        return "index";
    }
    
    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    /**
     * 注册页面
     */
    @GetMapping("/register")
    public String register() {
        return "register";
    }
    
    /**
     * 关于页面
     */
    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
