package com.dormitory.repository;

import com.dormitory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问层
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 根据学号查找学生
     */
    Optional<User> findByStudentId(String studentId);
    
    /**
     * 根据角色查找用户
     */
    List<User> findByRole(User.Role role);
    
    /**
     * 根据班级查找学生
     */
    List<User> findByClassName(String className);
    
    /**
     * 根据专业查找学生
     */
    List<User> findByMajor(String major);
    
    /**
     * 根据部门查找管理员
     */
    List<User> findByDepartment(String department);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 检查学号是否存在
     */
    boolean existsByStudentId(String studentId);
    
    /**
     * 根据姓名模糊查询
     */
    @Query("SELECT u FROM User u WHERE u.realName LIKE %:name%")
    List<User> findByRealNameContaining(@Param("name") String name);
    
    /**
     * 查找未分配宿舍的学生
     */
    @Query("SELECT u FROM User u WHERE u.role = 'STUDENT' AND u.id NOT IN " +
           "(SELECT sd.student.id FROM StudentDormitory sd WHERE sd.status = 'ACTIVE')")
    List<User> findStudentsWithoutDormitory();
    
    /**
     * 根据关键词搜索用户
     */
    @Query("SELECT u FROM User u WHERE " +
           "u.username LIKE %:keyword% OR " +
           "u.realName LIKE %:keyword% OR " +
           "u.email LIKE %:keyword% OR " +
           "u.phone LIKE %:keyword% OR " +
           "u.studentId LIKE %:keyword%")
    List<User> searchUsers(@Param("keyword") String keyword);
}
