package com.dormitory.service;

import com.dormitory.entity.RepairRequest;
import com.dormitory.entity.User;
import com.dormitory.entity.Dormitory;
import com.dormitory.repository.RepairRequestRepository;
import com.dormitory.repository.UserRepository;
import com.dormitory.repository.DormitoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 维修申请服务类
 */
@Service
@Transactional
public class RepairRequestService {
    
    @Autowired
    private RepairRequestRepository repairRequestRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DormitoryRepository dormitoryRepository;
    
    /**
     * 创建维修申请
     */
    public RepairRequest createRepairRequest(RepairRequest repairRequest) {
        // 检查学生是否存在
        User student = userRepository.findById(repairRequest.getStudent().getId())
                .orElseThrow(() -> new RuntimeException("学生不存在"));
        
        if (student.getRole() != User.Role.STUDENT) {
            throw new RuntimeException("该用户不是学生");
        }
        
        // 检查宿舍是否存在
        Dormitory dormitory = dormitoryRepository.findById(repairRequest.getDormitory().getId())
                .orElseThrow(() -> new RuntimeException("宿舍不存在"));
        
        // 设置申请时间
        repairRequest.setRequestedAt(LocalDateTime.now());
        repairRequest.setCreatedAt(LocalDateTime.now());
        repairRequest.setUpdatedAt(LocalDateTime.now());
        
        return repairRequestRepository.save(repairRequest);
    }
    
    /**
     * 分配维修申请给管理员
     */
    public void assignRepairRequest(Long requestId, Long adminId) {
        RepairRequest repairRequest = repairRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("维修申请不存在"));
        
        if (repairRequest.getStatus() != RepairRequest.RepairStatus.PENDING) {
            throw new RuntimeException("只能分配待处理的维修申请");
        }
        
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("管理员不存在"));
        
        if (admin.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("该用户不是管理员");
        }
        
        repairRequest.assignToAdmin(admin);
        repairRequestRepository.save(repairRequest);
    }
    
    /**
     * 完成维修申请
     */
    public void completeRepairRequest(Long requestId, String adminNotes, Double repairCost) {
        RepairRequest repairRequest = repairRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("维修申请不存在"));
        
        if (repairRequest.getStatus() != RepairRequest.RepairStatus.IN_PROGRESS) {
            throw new RuntimeException("只能完成进行中的维修申请");
        }
        
        repairRequest.complete(adminNotes, repairCost);
        repairRequestRepository.save(repairRequest);
    }
    
    /**
     * 取消维修申请
     */
    public void cancelRepairRequest(Long requestId, String reason) {
        RepairRequest repairRequest = repairRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("维修申请不存在"));
        
        if (repairRequest.getStatus() == RepairRequest.RepairStatus.COMPLETED) {
            throw new RuntimeException("已完成的维修申请不能取消");
        }
        
        repairRequest.cancel(reason);
        repairRequestRepository.save(repairRequest);
    }
    
    /**
     * 根据ID查找维修申请
     */
    @Transactional(readOnly = true)
    public Optional<RepairRequest> findById(Long id) {
        return repairRequestRepository.findById(id);
    }
    
    /**
     * 根据学生查找维修申请
     */
    @Transactional(readOnly = true)
    public List<RepairRequest> findByStudent(User student) {
        return repairRequestRepository.findByStudentOrderByRequestedAtDesc(student);
    }
    
    /**
     * 根据宿舍查找维修申请
     */
    @Transactional(readOnly = true)
    public List<RepairRequest> findByDormitory(Dormitory dormitory) {
        return repairRequestRepository.findByDormitoryOrderByRequestedAtDesc(dormitory);
    }
    
    /**
     * 根据状态查找维修申请
     */
    @Transactional(readOnly = true)
    public List<RepairRequest> findByStatus(RepairRequest.RepairStatus status) {
        return repairRequestRepository.findByStatusOrderByRequestedAtDesc(status);
    }
    
    /**
     * 根据类型查找维修申请
     */
    @Transactional(readOnly = true)
    public List<RepairRequest> findByType(RepairRequest.RepairType type) {
        return repairRequestRepository.findByTypeOrderByRequestedAtDesc(type);
    }
    
    /**
     * 根据优先级查找维修申请
     */
    @Transactional(readOnly = true)
    public List<RepairRequest> findByPriority(RepairRequest.RepairPriority priority) {
        return repairRequestRepository.findByPriorityOrderByRequestedAtDesc(priority);
    }
    
    /**
     * 查找待处理的维修申请（按优先级排序）
     */
    @Transactional(readOnly = true)
    public List<RepairRequest> findPendingRequestsOrderByPriority() {
        return repairRequestRepository.findPendingRequestsOrderByPriority();
    }
    
    /**
     * 查找进行中的维修申请
     */
    @Transactional(readOnly = true)
    public List<RepairRequest> findInProgressRequests() {
        return repairRequestRepository.findByStatusOrderByAssignedAtDesc(RepairRequest.RepairStatus.IN_PROGRESS);
    }
    
    /**
     * 查找高优先级的待处理申请
     */
    @Transactional(readOnly = true)
    public List<RepairRequest> findHighPriorityPendingRequests() {
        return repairRequestRepository.findHighPriorityPendingRequests();
    }
    
    /**
     * 查找超时的维修申请
     */
    @Transactional(readOnly = true)
    public List<RepairRequest> findOverdueRequests(Long days) {
        return repairRequestRepository.findOverdueRequests(days);
    }
    
    /**
     * 查找指定日期范围内的维修申请
     */
    @Transactional(readOnly = true)
    public List<RepairRequest> findByRequestedAtRange(LocalDateTime startDate, LocalDateTime endDate) {
        return repairRequestRepository.findByRequestedAtRange(startDate, endDate);
    }
    
    /**
     * 搜索维修申请
     */
    @Transactional(readOnly = true)
    public List<RepairRequest> searchRepairRequests(String keyword) {
        return repairRequestRepository.searchRepairRequests(keyword);
    }
    
    /**
     * 统计各状态的维修申请数量
     */
    @Transactional(readOnly = true)
    public List<Object[]> countByStatus() {
        return repairRequestRepository.countByStatus();
    }
    
    /**
     * 统计各类型的维修申请数量
     */
    @Transactional(readOnly = true)
    public List<Object[]> countByType() {
        return repairRequestRepository.countByType();
    }
    
    /**
     * 统计各优先级的维修申请数量
     */
    @Transactional(readOnly = true)
    public List<Object[]> countByPriority() {
        return repairRequestRepository.countByPriority();
    }
    
    /**
     * 统计各宿舍楼的维修申请数量
     */
    @Transactional(readOnly = true)
    public List<Object[]> countByBuilding() {
        return repairRequestRepository.countByBuilding();
    }
    
    /**
     * 统计各管理员的维修申请处理数量
     */
    @Transactional(readOnly = true)
    public List<Object[]> countByAssignedAdmin() {
        return repairRequestRepository.countByAssignedAdmin();
    }
    
    /**
     * 获取所有维修申请
     */
    @Transactional(readOnly = true)
    public List<RepairRequest> findAll() {
        return repairRequestRepository.findAll();
    }
    
    /**
     * 更新维修申请
     */
    public RepairRequest updateRepairRequest(Long id, RepairRequest repairRequestDetails) {
        RepairRequest repairRequest = repairRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("维修申请不存在"));
        
        // 只能更新待处理的申请
        if (repairRequest.getStatus() != RepairRequest.RepairStatus.PENDING) {
            throw new RuntimeException("只能更新待处理的维修申请");
        }
        
        repairRequest.setDescription(repairRequestDetails.getDescription());
        repairRequest.setType(repairRequestDetails.getType());
        repairRequest.setPriority(repairRequestDetails.getPriority());
        repairRequest.setUpdatedAt(LocalDateTime.now());
        
        return repairRequestRepository.save(repairRequest);
    }
    
    /**
     * 删除维修申请
     */
    public void deleteRepairRequest(Long id) {
        RepairRequest repairRequest = repairRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("维修申请不存在"));
        
        // 只能删除待处理的申请
        if (repairRequest.getStatus() != RepairRequest.RepairStatus.PENDING) {
            throw new RuntimeException("只能删除待处理的维修申请");
        }
        
        repairRequestRepository.deleteById(id);
    }
}
