package com.dormitory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学生宿舍关联实体类
 */
@Entity
@Table(name = "student_dormitories")
public class StudentDormitory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dormitory_id", nullable = false)
    private Dormitory dormitory;
    
    @NotNull(message = "入住日期不能为空")
    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;
    
    @Column(name = "check_out_date")
    private LocalDate checkOutDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status = AssignmentStatus.ACTIVE;
    
    @Column(name = "monthly_fee")
    private Double monthlyFee;
    
    @Column(name = "notes", length = 500)
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public StudentDormitory() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public StudentDormitory(User student, Dormitory dormitory, LocalDate checkInDate) {
        this();
        this.student = student;
        this.dormitory = dormitory;
        this.checkInDate = checkInDate;
        this.monthlyFee = dormitory.getMonthlyRent();
    }
    
    // 业务方法
    public boolean isActive() {
        return status == AssignmentStatus.ACTIVE;
    }
    
    public void checkOut(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
        this.status = AssignmentStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public long getDaysStayed() {
        LocalDate endDate = checkOutDate != null ? checkOutDate : LocalDate.now();
        return java.time.temporal.ChronoUnit.DAYS.between(checkInDate, endDate);
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
    
    public LocalDate getCheckInDate() {
        return checkInDate;
    }
    
    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }
    
    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }
    
    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }
    
    public AssignmentStatus getStatus() {
        return status;
    }
    
    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }
    
    public Double getMonthlyFee() {
        return monthlyFee;
    }
    
    public void setMonthlyFee(Double monthlyFee) {
        this.monthlyFee = monthlyFee;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
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
    
    // 分配状态枚举
    public enum AssignmentStatus {
        ACTIVE, COMPLETED, CANCELLED
    }
}
