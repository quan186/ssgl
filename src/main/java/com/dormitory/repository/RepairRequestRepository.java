package com.dormitory.repository;

import com.dormitory.entity.RepairRequest;
import com.dormitory.entity.User;
import com.dormitory.entity.Dormitory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 维修申请数据访问层
 */
@Repository
public interface RepairRequestRepository extends JpaRepository<RepairRequest, Long> {
    
    /**
     * 根据学生查找维修申请
     */
    List<RepairRequest> findByStudentOrderByRequestedAtDesc(User student);
    
    /**
     * 根据宿舍查找维修申请
     */
    List<RepairRequest> findByDormitoryOrderByRequestedAtDesc(Dormitory dormitory);
    
    /**
     * 根据状态查找维修申请
     */
    List<RepairRequest> findByStatusOrderByRequestedAtDesc(RepairRequest.RepairStatus status);
    
    /**
     * 根据类型查找维修申请
     */
    List<RepairRequest> findByTypeOrderByRequestedAtDesc(RepairRequest.RepairType type);
    
    /**
     * 根据优先级查找维修申请
     */
    List<RepairRequest> findByPriorityOrderByRequestedAtDesc(RepairRequest.RepairPriority priority);
    
    /**
     * 根据分配的管理员查找维修申请
     */
    List<RepairRequest> findByAssignedAdminOrderByRequestedAtDesc(User assignedAdmin);
    
    /**
     * 查找待处理的维修申请
     */
    @Query("SELECT rr FROM RepairRequest rr WHERE rr.status = 'PENDING' ORDER BY " +
           "CASE rr.priority " +
           "WHEN 'URGENT' THEN 1 " +
           "WHEN 'HIGH' THEN 2 " +
           "WHEN 'MEDIUM' THEN 3 " +
           "WHEN 'LOW' THEN 4 " +
           "END, rr.requestedAt ASC")
    List<RepairRequest> findPendingRequestsOrderByPriority();
    
    /**
     * 查找进行中的维修申请
     */
    List<RepairRequest> findByStatusOrderByAssignedAtDesc(RepairRequest.RepairStatus status);
    
    /**
     * 查找指定日期范围内的维修申请
     */
    @Query("SELECT rr FROM RepairRequest rr WHERE rr.requestedAt BETWEEN :startDate AND :endDate")
    List<RepairRequest> findByRequestedAtRange(@Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);
    
    /**
     * 统计各状态的维修申请数量
     */
    @Query("SELECT rr.status, COUNT(rr) FROM RepairRequest rr GROUP BY rr.status")
    List<Object[]> countByStatus();
    
    /**
     * 统计各类型的维修申请数量
     */
    @Query("SELECT rr.type, COUNT(rr) FROM RepairRequest rr GROUP BY rr.type")
    List<Object[]> countByType();
    
    /**
     * 统计各优先级的维修申请数量
     */
    @Query("SELECT rr.priority, COUNT(rr) FROM RepairRequest rr GROUP BY rr.priority")
    List<Object[]> countByPriority();
    
    /**
     * 统计各宿舍楼的维修申请数量
     */
    @Query("SELECT rr.dormitory.buildingName, COUNT(rr) FROM RepairRequest rr GROUP BY rr.dormitory.buildingName")
    List<Object[]> countByBuilding();
    
    /**
     * 统计各管理员的维修申请处理数量
     */
    @Query("SELECT rr.assignedAdmin.realName, COUNT(rr) FROM RepairRequest rr " +
           "WHERE rr.assignedAdmin IS NOT NULL GROUP BY rr.assignedAdmin.realName")
    List<Object[]> countByAssignedAdmin();
    
    /**
     * 查找超时的维修申请（超过指定天数未处理）
     */
    @Query("SELECT rr FROM RepairRequest rr WHERE rr.status IN ('PENDING', 'IN_PROGRESS') AND " +
           "DATEDIFF(CURRENT_TIMESTAMP, rr.requestedAt) >= :days")
    List<RepairRequest> findOverdueRequests(@Param("days") Long days);
    
    /**
     * 查找高优先级的待处理申请
     */
    @Query("SELECT rr FROM RepairRequest rr WHERE rr.status = 'PENDING' AND " +
           "rr.priority IN ('URGENT', 'HIGH') ORDER BY rr.priority, rr.requestedAt ASC")
    List<RepairRequest> findHighPriorityPendingRequests();
    
    /**
     * 根据关键词搜索维修申请
     */
    @Query("SELECT rr FROM RepairRequest rr WHERE " +
           "rr.description LIKE %:keyword% OR " +
           "rr.adminNotes LIKE %:keyword% OR " +
           "rr.student.realName LIKE %:keyword% OR " +
           "rr.dormitory.buildingName LIKE %:keyword% OR " +
           "rr.dormitory.roomNumber LIKE %:keyword%")
    List<RepairRequest> searchRepairRequests(@Param("keyword") String keyword);
}
