package com.dormitory.repository;

import com.dormitory.entity.StudentDormitory;
import com.dormitory.entity.User;
import com.dormitory.entity.Dormitory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 学生宿舍关联数据访问层
 */
@Repository
public interface StudentDormitoryRepository extends JpaRepository<StudentDormitory, Long> {
    
    /**
     * 根据学生查找当前活跃的宿舍分配
     */
    @Query("SELECT sd FROM StudentDormitory sd WHERE sd.student = :student AND sd.status = 'ACTIVE'")
    Optional<StudentDormitory> findActiveByStudent(@Param("student") User student);
    
    /**
     * 根据学生ID查找当前活跃的宿舍分配
     */
    @Query("SELECT sd FROM StudentDormitory sd WHERE sd.student.id = :studentId AND sd.status = 'ACTIVE'")
    Optional<StudentDormitory> findActiveByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 根据宿舍查找所有活跃的分配
     */
    @Query("SELECT sd FROM StudentDormitory sd WHERE sd.dormitory = :dormitory AND sd.status = 'ACTIVE'")
    List<StudentDormitory> findActiveByDormitory(@Param("dormitory") Dormitory dormitory);
    
    /**
     * 根据宿舍ID查找所有活跃的分配
     */
    @Query("SELECT sd FROM StudentDormitory sd WHERE sd.dormitory.id = :dormitoryId AND sd.status = 'ACTIVE'")
    List<StudentDormitory> findActiveByDormitoryId(@Param("dormitoryId") Long dormitoryId);
    
    /**
     * 根据状态查找分配记录
     */
    List<StudentDormitory> findByStatus(StudentDormitory.AssignmentStatus status);
    
    /**
     * 根据学生查找所有分配记录
     */
    List<StudentDormitory> findByStudentOrderByCheckInDateDesc(User student);
    
    /**
     * 根据宿舍查找所有分配记录
     */
    List<StudentDormitory> findByDormitoryOrderByCheckInDateDesc(Dormitory dormitory);
    
    /**
     * 查找指定日期范围内的分配记录
     */
    @Query("SELECT sd FROM StudentDormitory sd WHERE sd.checkInDate BETWEEN :startDate AND :endDate")
    List<StudentDormitory> findByCheckInDateRange(@Param("startDate") LocalDate startDate, 
                                                  @Param("endDate") LocalDate endDate);
    
    /**
     * 查找指定日期范围内的退宿记录
     */
    @Query("SELECT sd FROM StudentDormitory sd WHERE sd.checkOutDate BETWEEN :startDate AND :endDate")
    List<StudentDormitory> findByCheckOutDateRange(@Param("startDate") LocalDate startDate, 
                                                   @Param("endDate") LocalDate endDate);
    
    /**
     * 统计各宿舍楼的入住学生数量
     */
    @Query("SELECT sd.dormitory.buildingName, COUNT(sd) FROM StudentDormitory sd " +
           "WHERE sd.status = 'ACTIVE' GROUP BY sd.dormitory.buildingName")
    List<Object[]> countActiveStudentsByBuilding();
    
    /**
     * 统计各专业的入住学生数量
     */
    @Query("SELECT sd.student.major, COUNT(sd) FROM StudentDormitory sd " +
           "WHERE sd.status = 'ACTIVE' GROUP BY sd.student.major")
    List<Object[]> countActiveStudentsByMajor();
    
    /**
     * 统计各班级的入住学生数量
     */
    @Query("SELECT sd.student.className, COUNT(sd) FROM StudentDormitory sd " +
           "WHERE sd.status = 'ACTIVE' GROUP BY sd.student.className")
    List<Object[]> countActiveStudentsByClass();
    
    /**
     * 查找即将到期的分配记录（入住超过指定天数）
     */
    @Query("SELECT sd FROM StudentDormitory sd WHERE sd.status = 'ACTIVE' AND " +
           "DATEDIFF(CURRENT_DATE, sd.checkInDate) >= :days")
    List<StudentDormitory> findLongTermAssignments(@Param("days") Long days);
    
    /**
     * 检查学生是否已有活跃的宿舍分配
     */
    @Query("SELECT COUNT(sd) > 0 FROM StudentDormitory sd WHERE sd.student.id = :studentId AND sd.status = 'ACTIVE'")
    boolean existsActiveAssignmentByStudentId(@Param("studentId") Long studentId);
    
    /**
     * 检查宿舍是否已满
     */
    @Query("SELECT COUNT(sd) >= d.capacity FROM StudentDormitory sd JOIN sd.dormitory d " +
           "WHERE sd.dormitory.id = :dormitoryId AND sd.status = 'ACTIVE'")
    boolean isDormitoryFull(@Param("dormitoryId") Long dormitoryId);
}
