package com.dormitory.controller;

import com.dormitory.entity.Dormitory;
import com.dormitory.service.DormitoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 宿舍控制器
 */
@Controller
@RequestMapping("/dormitories")
public class DormitoryController {
    
    @Autowired
    private DormitoryService dormitoryService;
    
    /**
     * 宿舍列表页面
     */
    @GetMapping
    public String dormitoryList(@RequestParam(required = false) String building,
                                @RequestParam(required = false) String type,
                                @RequestParam(required = false) String status,
                                @RequestParam(required = false) String keyword,
                                Model model) {
        List<Dormitory> dormitories;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            dormitories = dormitoryService.searchDormitories(keyword);
        } else if (building != null && !building.trim().isEmpty()) {
            dormitories = dormitoryService.findByBuildingName(building);
        } else if (type != null && !type.trim().isEmpty()) {
            dormitories = dormitoryService.findByType(Dormitory.DormitoryType.valueOf(type.toUpperCase()));
        } else if (status != null && !status.trim().isEmpty()) {
            dormitories = dormitoryService.findByStatus(Dormitory.DormitoryStatus.valueOf(status.toUpperCase()));
        } else {
            dormitories = dormitoryService.findAll();
        }
        
        model.addAttribute("dormitories", dormitories);
        model.addAttribute("currentBuilding", building);
        model.addAttribute("currentType", type);
        model.addAttribute("currentStatus", status);
        model.addAttribute("keyword", keyword);
        
        // 获取所有宿舍楼名称用于筛选
        List<Object[]> buildingStats = dormitoryService.countDormitoriesByBuilding();
        model.addAttribute("buildings", buildingStats);
        
        return "admin/dormitories/list";
    }
    
    /**
     * 宿舍详情页面
     */
    @GetMapping("/{id}")
    public String dormitoryDetail(@PathVariable Long id, Model model) {
        Dormitory dormitory = dormitoryService.findById(id)
                .orElseThrow(() -> new RuntimeException("宿舍不存在"));
        
        model.addAttribute("dormitory", dormitory);
        return "admin/dormitories/detail";
    }
    
    /**
     * 创建宿舍页面
     */
    @GetMapping("/create")
    public String createDormitoryForm(Model model) {
        model.addAttribute("dormitory", new Dormitory());
        return "admin/dormitories/form";
    }
    
    /**
     * 编辑宿舍页面
     */
    @GetMapping("/{id}/edit")
    public String editDormitoryForm(@PathVariable Long id, Model model) {
        Dormitory dormitory = dormitoryService.findById(id)
                .orElseThrow(() -> new RuntimeException("宿舍不存在"));
        
        model.addAttribute("dormitory", dormitory);
        return "admin/dormitories/form";
    }
    
    /**
     * 保存宿舍
     */
    @PostMapping("/save")
    public String saveDormitory(@ModelAttribute Dormitory dormitory, RedirectAttributes redirectAttributes) {
        try {
            if (dormitory.getId() == null) {
                dormitoryService.createDormitory(dormitory);
                redirectAttributes.addFlashAttribute("successMessage", "宿舍创建成功");
            } else {
                dormitoryService.updateDormitory(dormitory.getId(), dormitory);
                redirectAttributes.addFlashAttribute("successMessage", "宿舍更新成功");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return dormitory.getId() == null ? "redirect:/dormitories/create" : "redirect:/dormitories/" + dormitory.getId() + "/edit";
        }
        
        return "redirect:/dormitories";
    }
    
    /**
     * 删除宿舍
     */
    @PostMapping("/{id}/delete")
    public String deleteDormitory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            dormitoryService.deleteDormitory(id);
            redirectAttributes.addFlashAttribute("successMessage", "宿舍删除成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/dormitories";
    }
    
    /**
     * 可用宿舍列表（学生查看）
     */
    @GetMapping("/available")
    public String availableDormitories(@RequestParam(required = false) String building,
                                      @RequestParam(required = false) String type,
                                      Model model) {
        List<Dormitory> dormitories;
        
        if (building != null && !building.trim().isEmpty() && type != null && !type.trim().isEmpty()) {
            dormitories = dormitoryService.findAvailableDormitoriesByBuildingAndType(
                    building, Dormitory.DormitoryType.valueOf(type.toUpperCase()));
        } else if (building != null && !building.trim().isEmpty()) {
            dormitories = dormitoryService.findByBuildingName(building);
            dormitories.removeIf(d -> !d.isAvailable());
        } else if (type != null && !type.trim().isEmpty()) {
            dormitories = dormitoryService.findByType(Dormitory.DormitoryType.valueOf(type.toUpperCase()));
            dormitories.removeIf(d -> !d.isAvailable());
        } else {
            dormitories = dormitoryService.findAvailableDormitories();
        }
        
        model.addAttribute("dormitories", dormitories);
        model.addAttribute("currentBuilding", building);
        model.addAttribute("currentType", type);
        
        // 获取所有宿舍楼名称用于筛选
        List<Object[]> buildingStats = dormitoryService.countDormitoriesByBuilding();
        model.addAttribute("buildings", buildingStats);
        
        return "student/dormitories/available";
    }
    
    /**
     * 宿舍统计页面
     */
    @GetMapping("/statistics")
    public String dormitoryStatistics(Model model) {
        // 各宿舍楼统计
        model.addAttribute("buildingStats", dormitoryService.countDormitoriesByBuilding());
        
        // 入住率统计
        model.addAttribute("occupancyStats", dormitoryService.getOccupancyRateByBuilding());
        
        // 宿舍状态统计
        model.addAttribute("availableCount", dormitoryService.findByStatus(Dormitory.DormitoryStatus.AVAILABLE).size());
        model.addAttribute("fullCount", dormitoryService.findByStatus(Dormitory.DormitoryStatus.FULL).size());
        model.addAttribute("maintenanceCount", dormitoryService.findByStatus(Dormitory.DormitoryStatus.MAINTENANCE).size());
        model.addAttribute("closedCount", dormitoryService.findByStatus(Dormitory.DormitoryStatus.CLOSED).size());
        
        return "admin/dormitories/statistics";
    }
}
