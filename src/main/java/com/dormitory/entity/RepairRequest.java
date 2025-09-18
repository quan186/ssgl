package com.dormitory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * 维修申请实体类
 */
@Entity
@Table(name = "repair_requests")
public class RepairRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dormitory_id", nullable = false)
    private Dormitory dormitory;
    
    @NotBlank(message = "问题描述不能为空")
    @Column(name = "description", nullable = false, length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepairType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepairStatus status = RepairStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepairPriority priority = RepairPriority.MEDIUM;
    
    @Column(name = "admin_notes", length = 1000)
    private String adminNotes;
    
    @Column(name = "repair_cost")
    private Double repairCost;
    
    @Column(name = "requested_at")
    private LocalDateTime requestedAt;
    
    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_admin_id")
    private User assignedAdmin;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public RepairRequest() {
        this.requestedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // 业务方法
    public void assignToAdmin(User admin) {
        this.assignedAdmin = admin;
        this.assignedAt = LocalDateTime.now();
        this.status = RepairStatus.IN_PROGRESS;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void complete(String adminNotes, Double repairCost) {
        this.status = RepairStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.adminNotes = adminNotes;
        this.repairCost = repairCost;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void cancel(String reason) {
        this.status = RepairStatus.CANCELLED;
        this.adminNotes = reason;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getStudent() {
        return student;
    }
    
    public void setStudent(User student) {
        this.student = student;
    }
    
    public Dormitory getDormitory() {
        return dormitory;
    }
    
    public void setDormitory(Dormitory dormitory) {
        this.dormitory = dormitory;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public RepairType getType() {
        return type;
    }
    
    public void setType(RepairType type) {
        this.type = type;
    }
    
    public RepairStatus getStatus() {
        return status;
    }
    
    public void setStatus(RepairStatus status) {
        this.status = status;
    }
    
    public RepairPriority getPriority() {
        return priority;
    }
    
    public void setPriority(RepairPriority priority) {
        this.priority = priority;
    }
    
    public String getAdminNotes() {
        return adminNotes;
    }
    
    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }
    
    public Double getRepairCost() {
        return repairCost;
    }
    
    public void setRepairCost(Double repairCost) {
        this.repairCost = repairCost;
    }
    
    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }
    
    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }
    
    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }
    
    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public User getAssignedAdmin() {
        return assignedAdmin;
    }
    
    public void setAssignedAdmin(User assignedAdmin) {
        this.assignedAdmin = assignedAdmin;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // 维修类型枚举
    public enum RepairType {
        PLUMBING, ELECTRICAL, FURNITURE, APPLIANCE, OTHER
    }
    
    // 维修状态枚举
    public enum RepairStatus {
        PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    }
    
    // 维修优先级枚举
    public enum RepairPriority {
        LOW, MEDIUM, HIGH, URGENT
    }
}
