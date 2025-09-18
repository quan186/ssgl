package com.dormitory.controller;

import com.dormitory.entity.StudentDormitory;
import com.dormitory.entity.User;
import com.dormitory.service.StudentDormitoryService;
import com.dormitory.service.UserService;
import com.dormitory.service.DormitoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

/**
 * 学生宿舍分配控制器
 */
@Controller
@RequestMapping("/assignments")
public class StudentDormitoryController {
    
    @Autowired
    private StudentDormitoryService studentDormitoryService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DormitoryService dormitoryService;
    
    /**
     * 分配记录列表页面
     */
    @GetMapping
    public String assignmentList(@RequestParam(required = false) String status,
                                @RequestParam(required = false) String building,
                                Model model) {
        List<StudentDormitory> assignments;
        
        if (status != null && !status.trim().isEmpty()) {
            assignments = studentDormitoryService.findByStatus(
                    StudentDormitory.AssignmentStatus.valueOf(status.toUpperCase()));
        } else if (building != null && !building.trim().isEmpty()) {
            assignments = studentDormitoryService.findAll();
            assignments.removeIf(a -> !a.getDormitory().getBuildingName().equals(building));
        } else {
            assignments = studentDormitoryService.findAll();
        }
        
        model.addAttribute("assignments", assignments);
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentBuilding", building);
        
        // 获取所有宿舍楼名称用于筛选
        List<Object[]> buildingStats = dormitoryService.countDormitoriesByBuilding();
        model.addAttribute("buildings", buildingStats);
        
        return "admin/assignments/list";
    }
    
    /**
     * 分配宿舍页面
     */
    @GetMapping("/create")
    public String createAssignmentForm(Model model) {
        model.addAttribute("assignment", new StudentDormitory());
        model.addAttribute("students", userService.findByRole(User.Role.STUDENT));
        model.addAttribute("dormitories", dormitoryService.findAvailableDormitories());
        
        return "admin/assignments/form";
    }
    
    /**
     * 保存分配记录
     */
    @PostMapping("/save")
    public String saveAssignment(@RequestParam Long studentId,
                                @RequestParam Long dormitoryId,
                                @RequestParam String checkInDate,
                                RedirectAttributes redirectAttributes) {
        try {
            LocalDate checkIn = LocalDate.parse(checkInDate);
            studentDormitoryService.assignStudentToDormitory(studentId, dormitoryId, checkIn);
            redirectAttributes.addFlashAttribute("successMessage", "宿舍分配成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/assignments";
    }
    
    /**
     * 学生退宿
     */
    @PostMapping("/{id}/checkout")
    public String checkoutStudent(@PathVariable Long id,
                                 @RequestParam String checkOutDate,
                                 RedirectAttributes redirectAttributes) {
        try {
            LocalDate checkOut = LocalDate.parse(checkOutDate);
            StudentDormitory assignment = studentDormitoryService.findById(id)
                    .orElseThrow(() -> new RuntimeException("分配记录不存在"));
            
            studentDormitoryService.checkOutStudent(assignment.getStudent().getId(), checkOut);
            redirectAttributes.addFlashAttribute("successMessage", "学生退宿成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/assignments";
    }
    
    /**
     * 取消分配
     */
    @PostMapping("/{id}/cancel")
    public String cancelAssignment(@PathVariable Long id,
                                  @RequestParam String reason,
                                  RedirectAttributes redirectAttributes) {
        try {
            studentDormitoryService.cancelAssignment(id, reason);
            redirectAttributes.addFlashAttribute("successMessage", "分配已取消");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/assignments";
    }
    
    /**
     * 我的宿舍页面（学生）
     */
    @GetMapping("/my-dormitory")
    public String myDormitory(Authentication authentication, Model model) {
        User currentUser = (User) authentication.getPrincipal();
        StudentDormitory assignment = studentDormitoryService.findActiveByStudent(currentUser)
                .orElse(null);
        
        model.addAttribute("assignment", assignment);
        return "student/assignments/my-dormitory";
    }
    
    /**
     * 申请退宿页面（学生）
     */
    @GetMapping("/apply-checkout")
    public String applyCheckoutForm(Authentication authentication, Model model) {
        User currentUser = (User) authentication.getPrincipal();
        StudentDormitory assignment = studentDormitoryService.findActiveByStudent(currentUser)
                .orElseThrow(() -> new RuntimeException("您当前没有宿舍分配"));
        
        model.addAttribute("assignment", assignment);
        return "student/assignments/apply-checkout";
    }
    
    /**
     * 提交退宿申请
     */
    @PostMapping("/apply-checkout")
    public String submitCheckoutApplication(@RequestParam String checkOutDate,
                                          Authentication authentication,
                                          RedirectAttributes redirectAttributes) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            LocalDate checkOut = LocalDate.parse(checkOutDate);
            
            studentDormitoryService.checkOutStudent(currentUser.getId(), checkOut);
            redirectAttributes.addFlashAttribute("successMessage", "退宿申请提交成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/assignments/my-dormitory";
    }
    
    /**
     * 分配统计页面
     */
    @GetMapping("/statistics")
    public String assignmentStatistics(Model model) {
        // 各宿舍楼入住学生统计
        model.addAttribute("buildingStats", studentDormitoryService.countActiveStudentsByBuilding());
        
        // 各专业入住学生统计
        model.addAttribute("majorStats", studentDormitoryService.countActiveStudentsByMajor());
        
        // 各班级入住学生统计
        model.addAttribute("classStats", studentDormitoryService.countActiveStudentsByClass());
        
        // 分配状态统计
        model.addAttribute("activeCount", studentDormitoryService.findByStatus(StudentDormitory.AssignmentStatus.ACTIVE).size());
        model.addAttribute("completedCount", studentDormitoryService.findByStatus(StudentDormitory.AssignmentStatus.COMPLETED).size());
        model.addAttribute("cancelledCount", studentDormitoryService.findByStatus(StudentDormitory.AssignmentStatus.CANCELLED).size());
        
        return "admin/assignments/statistics";
    }
}
