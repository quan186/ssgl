package com.dormitory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 宿舍实体类
 */
@Entity
@Table(name = "dormitories")
public class Dormitory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "宿舍楼不能为空")
    @Column(name = "building_name", nullable = false)
    private String buildingName;
    
    @NotBlank(message = "房间号不能为空")
    @Column(name = "room_number", nullable = false)
    private String roomNumber;
    
    @NotNull(message = "容量不能为空")
    @Positive(message = "容量必须大于0")
    @Column(nullable = false)
    private Integer capacity;
    
    @Column(name = "current_occupancy")
    private Integer currentOccupancy = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DormitoryType type;
    
    @Column(name = "monthly_rent")
    private Double monthlyRent;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DormitoryStatus status = DormitoryStatus.AVAILABLE;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 关联关系
    @OneToMany(mappedBy = "dormitory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StudentDormitory> studentDormitories = new ArrayList<>();
    
    // 构造函数
    public Dormitory() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // 业务方法
    public boolean isAvailable() {
        return status == DormitoryStatus.AVAILABLE && currentOccupancy < capacity;
    }
    
    public boolean isFull() {
        return currentOccupancy >= capacity;
    }
    
    public void addOccupant() {
        if (currentOccupancy < capacity) {
            currentOccupancy++;
            if (currentOccupancy >= capacity) {
                status = DormitoryStatus.FULL;
            }
        }
    }
    
    public void removeOccupant() {
        if (currentOccupancy > 0) {
            currentOccupancy--;
            if (status == DormitoryStatus.FULL) {
                status = DormitoryStatus.AVAILABLE;
            }
        }
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getBuildingName() {
        return buildingName;
    }
    
    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }
    
    public String getRoomNumber() {
        return roomNumber;
    }
    
    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
    
    public Integer getCapacity() {
        return capacity;
    }
    
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    
    public Integer getCurrentOccupancy() {
        return currentOccupancy;
    }
    
    public void setCurrentOccupancy(Integer currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
    }
    
    public DormitoryType getType() {
        return type;
    }
    
    public void setType(DormitoryType type) {
        this.type = type;
    }
    
    public Double getMonthlyRent() {
        return monthlyRent;
    }
    
    public void setMonthlyRent(Double monthlyRent) {
        this.monthlyRent = monthlyRent;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public DormitoryStatus getStatus() {
        return status;
    }
    
    public void setStatus(DormitoryStatus status) {
        this.status = status;
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
    
    public List<StudentDormitory> getStudentDormitories() {
        return studentDormitories;
    }
    
    public void setStudentDormitories(List<StudentDormitory> studentDormitories) {
        this.studentDormitories = studentDormitories;
    }
    
    // 宿舍类型枚举
    public enum DormitoryType {
        MALE, FEMALE, MIXED
    }
    
    // 宿舍状态枚举
    public enum DormitoryStatus {
        AVAILABLE, FULL, MAINTENANCE, CLOSED
    }
}
