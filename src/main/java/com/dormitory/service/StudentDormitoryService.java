package com.dormitory.service;

import com.dormitory.entity.StudentDormitory;
import com.dormitory.entity.User;
import com.dormitory.entity.Dormitory;
import com.dormitory.repository.StudentDormitoryRepository;
import com.dormitory.repository.UserRepository;
import com.dormitory.repository.DormitoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 学生宿舍分配服务类
 */
@Service
@Transactional
public class StudentDormitoryService {
    
    @Autowired
    private StudentDormitoryRepository studentDormitoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DormitoryRepository dormitoryRepository;
    
    @Autowired
    private DormitoryService dormitoryService;
    
    /**
     * 分配学生到宿舍
     */
    public StudentDormitory assignStudentToDormitory(Long studentId, Long dormitoryId, LocalDate checkInDate) {
        // 检查学生是否存在且为学生角色
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
        
        if (student.getRole() != User.Role.STUDENT) {
            throw new RuntimeException("该用户不是学生");
        }
        
        // 检查学生是否已有活跃的宿舍分配
        if (studentDormitoryRepository.existsActiveAssignmentByStudentId(studentId)) {
            throw new RuntimeException("该学生已有宿舍分配");
        }
        
        // 检查宿舍是否存在且可用
        Dormitory dormitory = dormitoryRepository.findById(dormitoryId)
                .orElseThrow(() -> new RuntimeException("宿舍不存在"));
        
        if (!dormitory.isAvailable()) {
            throw new RuntimeException("宿舍不可用");
        }
        
        // 检查宿舍是否已满
        if (studentDormitoryRepository.isDormitoryFull(dormitoryId)) {
            throw new RuntimeException("宿舍已满");
        }
        
        // 创建分配记录
        StudentDormitory assignment = new StudentDormitory(student, dormitory, checkInDate);
        assignment = studentDormitoryRepository.save(assignment);
        
        // 更新宿舍入住人数
        dormitoryService.updateOccupancy(dormitoryId, 1);
        
        return assignment;
    }
    
    /**
     * 学生退宿
     */
    public void checkOutStudent(Long studentId, LocalDate checkOutDate) {
        // 查找学生的活跃分配记录
        StudentDormitory assignment = studentDormitoryRepository.findActiveByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("该学生没有活跃的宿舍分配"));
        
        // 执行退宿
        assignment.checkOut(checkOutDate);
        studentDormitoryRepository.save(assignment);
        
        // 更新宿舍入住人数
        dormitoryService.updateOccupancy(assignment.getDormitory().getId(), -1);
    }
    
    /**
     * 根据学生ID查找当前活跃的分配
     */
    @Transactional(readOnly = true)
    public Optional<StudentDormitory> findActiveByStudentId(Long studentId) {
        return studentDormitoryRepository.findActiveByStudentId(studentId);
    }
    
    /**
     * 根据学生查找当前活跃的分配
     */
    @Transactional(readOnly = true)
    public Optional<StudentDormitory> findActiveByStudent(User student) {
        return studentDormitoryRepository.findActiveByStudent(student);
    }
    
    /**
     * 根据宿舍ID查找所有活跃的分配
     */
    @Transactional(readOnly = true)
    public List<StudentDormitory> findActiveByDormitoryId(Long dormitoryId) {
        return studentDormitoryRepository.findActiveByDormitoryId(dormitoryId);
    }
    
    /**
     * 根据宿舍查找所有活跃的分配
     */
    @Transactional(readOnly = true)
    public List<StudentDormitory> findActiveByDormitory(Dormitory dormitory) {
        return studentDormitoryRepository.findActiveByDormitory(dormitory);
    }
    
    /**
     * 根据状态查找分配记录
     */
    @Transactional(readOnly = true)
    public List<StudentDormitory> findByStatus(StudentDormitory.AssignmentStatus status) {
        return studentDormitoryRepository.findByStatus(status);
    }
    
    /**
     * 根据学生查找所有分配记录
     */
    @Transactional(readOnly = true)
    public List<StudentDormitory> findByStudent(User student) {
        return studentDormitoryRepository.findByStudentOrderByCheckInDateDesc(student);
    }
    
    /**
     * 根据宿舍查找所有分配记录
     */
    @Transactional(readOnly = true)
    public List<StudentDormitory> findByDormitory(Dormitory dormitory) {
        return studentDormitoryRepository.findByDormitoryOrderByCheckInDateDesc(dormitory);
    }
    
    /**
     * 查找指定日期范围内的分配记录
     */
    @Transactional(readOnly = true)
    public List<StudentDormitory> findByCheckInDateRange(LocalDate startDate, LocalDate endDate) {
        return studentDormitoryRepository.findByCheckInDateRange(startDate, endDate);
    }
    
    /**
     * 查找指定日期范围内的退宿记录
     */
    @Transactional(readOnly = true)
    public List<StudentDormitory> findByCheckOutDateRange(LocalDate startDate, LocalDate endDate) {
        return studentDormitoryRepository.findByCheckOutDateRange(startDate, endDate);
    }
    
    /**
     * 统计各宿舍楼的入住学生数量
     */
    @Transactional(readOnly = true)
    public List<Object[]> countActiveStudentsByBuilding() {
        return studentDormitoryRepository.countActiveStudentsByBuilding();
    }
    
    /**
     * 统计各专业的入住学生数量
     */
    @Transactional(readOnly = true)
    public List<Object[]> countActiveStudentsByMajor() {
        return studentDormitoryRepository.countActiveStudentsByMajor();
    }
    
    /**
     * 统计各班级的入住学生数量
     */
    @Transactional(readOnly = true)
    public List<Object[]> countActiveStudentsByClass() {
        return studentDormitoryRepository.countActiveStudentsByClass();
    }
    
    /**
     * 查找即将到期的分配记录
     */
    @Transactional(readOnly = true)
    public List<StudentDormitory> findLongTermAssignments(Long days) {
        return studentDormitoryRepository.findLongTermAssignments(days);
    }
    
    /**
     * 获取所有分配记录
     */
    @Transactional(readOnly = true)
    public List<StudentDormitory> findAll() {
        return studentDormitoryRepository.findAll();
    }
    
    /**
     * 根据ID查找分配记录
     */
    @Transactional(readOnly = true)
    public Optional<StudentDormitory> findById(Long id) {
        return studentDormitoryRepository.findById(id);
    }
    
    /**
     * 取消分配
     */
    public void cancelAssignment(Long assignmentId, String reason) {
        StudentDormitory assignment = studentDormitoryRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("分配记录不存在"));
        
        if (assignment.getStatus() != StudentDormitory.AssignmentStatus.ACTIVE) {
            throw new RuntimeException("只能取消活跃的分配记录");
        }
        
        assignment.setStatus(StudentDormitory.AssignmentStatus.CANCELLED);
        assignment.setNotes(reason);
        assignment.setUpdatedAt(LocalDateTime.now());
        studentDormitoryRepository.save(assignment);
        
        // 更新宿舍入住人数
        dormitoryService.updateOccupancy(assignment.getDormitory().getId(), -1);
    }
}
