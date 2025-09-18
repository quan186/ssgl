package com.dormitory.controller;

import com.dormitory.entity.RepairRequest;
import com.dormitory.entity.User;
import com.dormitory.entity.Dormitory;
import com.dormitory.service.RepairRequestService;
import com.dormitory.service.UserService;
import com.dormitory.service.DormitoryService;
import com.dormitory.service.StudentDormitoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 维修申请控制器
 */
@Controller
@RequestMapping("/repair-requests")
public class RepairRequestController {
    
    @Autowired
    private RepairRequestService repairRequestService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DormitoryService dormitoryService;
    
    @Autowired
    private StudentDormitoryService studentDormitoryService;
    
    /**
     * 维修申请列表页面
     */
    @GetMapping
    public String repairRequestList(@RequestParam(required = false) String status,
                                   @RequestParam(required = false) String type,
                                   @RequestParam(required = false) String priority,
                                   @RequestParam(required = false) String keyword,
                                   Model model) {
        List<RepairRequest> repairRequests;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            repairRequests = repairRequestService.searchRepairRequests(keyword);
        } else if (status != null && !status.trim().isEmpty()) {
            repairRequests = repairRequestService.findByStatus(RepairRequest.RepairStatus.valueOf(status.toUpperCase()));
        } else if (type != null && !type.trim().isEmpty()) {
            repairRequests = repairRequestService.findByType(RepairRequest.RepairType.valueOf(type.toUpperCase()));
        } else if (priority != null && !priority.trim().isEmpty()) {
            repairRequests = repairRequestService.findByPriority(RepairRequest.RepairPriority.valueOf(priority.toUpperCase()));
        } else {
            repairRequests = repairRequestService.findAll();
        }
        
        model.addAttribute("repairRequests", repairRequests);
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentType", type);
        model.addAttribute("currentPriority", priority);
        model.addAttribute("keyword", keyword);
        
        return "admin/repair-requests/list";
    }
    
    /**
     * 维修申请详情页面
     */
    @GetMapping("/{id}")
    public String repairRequestDetail(@PathVariable Long id, Model model) {
        RepairRequest repairRequest = repairRequestService.findById(id)
                .orElseThrow(() -> new RuntimeException("维修申请不存在"));
        
        model.addAttribute("repairRequest", repairRequest);
        return "admin/repair-requests/detail";
    }
    
    /**
     * 创建维修申请页面（学生）
     */
    @GetMapping("/create")
    public String createRepairRequestForm(Authentication authentication, Model model) {
        User currentUser = (User) authentication.getPrincipal();
        
        // 获取学生当前宿舍
        StudentDormitory assignment = studentDormitoryService.findActiveByStudent(currentUser)
                .orElseThrow(() -> new RuntimeException("您当前没有宿舍分配"));
        
        RepairRequest repairRequest = new RepairRequest();
        repairRequest.setStudent(currentUser);
        repairRequest.setDormitory(assignment.getDormitory());
        
        model.addAttribute("repairRequest", repairRequest);
        return "student/repair-requests/form";
    }
    
    /**
     * 保存维修申请
     */
    @PostMapping("/save")
    public String saveRepairRequest(@ModelAttribute RepairRequest repairRequest,
                                   RedirectAttributes redirectAttributes) {
        try {
            repairRequestService.createRepairRequest(repairRequest);
            redirectAttributes.addFlashAttribute("successMessage", "维修申请提交成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/repair-requests/my-requests";
    }
    
    /**
     * 分配维修申请给管理员
     */
    @PostMapping("/{id}/assign")
    public String assignRepairRequest(@PathVariable Long id,
                                    @RequestParam Long adminId,
                                    RedirectAttributes redirectAttributes) {
        try {
            repairRequestService.assignRepairRequest(id, adminId);
            redirectAttributes.addFlashAttribute("successMessage", "维修申请分配成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/repair-requests/" + id;
    }
    
    /**
     * 完成维修申请
     */
    @PostMapping("/{id}/complete")
    public String completeRepairRequest(@PathVariable Long id,
                                      @RequestParam String adminNotes,
                                      @RequestParam(required = false) Double repairCost,
                                      RedirectAttributes redirectAttributes) {
        try {
            repairRequestService.completeRepairRequest(id, adminNotes, repairCost);
            redirectAttributes.addFlashAttribute("successMessage", "维修申请已完成");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/repair-requests/" + id;
    }
    
    /**
     * 取消维修申请
     */
    @PostMapping("/{id}/cancel")
    public String cancelRepairRequest(@PathVariable Long id,
                                    @RequestParam String reason,
                                    RedirectAttributes redirectAttributes) {
        try {
            repairRequestService.cancelRepairRequest(id, reason);
            redirectAttributes.addFlashAttribute("successMessage", "维修申请已取消");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/repair-requests/" + id;
    }
    
    /**
     * 我的维修申请页面（学生）
     */
    @GetMapping("/my-requests")
    public String myRepairRequests(Authentication authentication, Model model) {
        User currentUser = (User) authentication.getPrincipal();
        List<RepairRequest> repairRequests = repairRequestService.findByStudent(currentUser);
        
        model.addAttribute("repairRequests", repairRequests);
        return "student/repair-requests/my-requests";
    }
    
    /**
     * 待处理维修申请页面
     */
    @GetMapping("/pending")
    public String pendingRepairRequests(Model model) {
        List<RepairRequest> repairRequests = repairRequestService.findPendingRequestsOrderByPriority();
        model.addAttribute("repairRequests", repairRequests);
        return "admin/repair-requests/pending";
    }
    
    /**
     * 高优先级维修申请页面
     */
    @GetMapping("/high-priority")
    public String highPriorityRepairRequests(Model model) {
        List<RepairRequest> repairRequests = repairRequestService.findHighPriorityPendingRequests();
        model.addAttribute("repairRequests", repairRequests);
        return "admin/repair-requests/high-priority";
    }
    
    /**
     * 维修统计页面
     */
    @GetMapping("/statistics")
    public String repairStatistics(Model model) {
        // 状态统计
        model.addAttribute("statusStats", repairRequestService.countByStatus());
        
        // 类型统计
        model.addAttribute("typeStats", repairRequestService.countByType());
        
        // 优先级统计
        model.addAttribute("priorityStats", repairRequestService.countByPriority());
        
        // 宿舍楼统计
        model.addAttribute("buildingStats", repairRequestService.countByBuilding());
        
        // 管理员处理统计
        model.addAttribute("adminStats", repairRequestService.countByAssignedAdmin());
        
        return "admin/repair-requests/statistics";
    }
}
