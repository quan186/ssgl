package com.dormitory.service;

import com.dormitory.entity.User;
import com.dormitory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户服务类
 */
@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 创建用户
     */
    public User createUser(User user) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        
        // 检查学号是否已存在（仅学生）
        if (user.getRole() == User.Role.STUDENT && user.getStudentId() != null 
            && userRepository.existsByStudentId(user.getStudentId())) {
            throw new RuntimeException("学号已存在");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    /**
     * 更新用户信息
     */
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查用户名是否被其他用户使用
        if (!user.getUsername().equals(userDetails.getUsername()) 
            && userRepository.existsByUsername(userDetails.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否被其他用户使用
        if (userDetails.getEmail() != null 
            && !userDetails.getEmail().equals(user.getEmail()) 
            && userRepository.existsByEmail(userDetails.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        
        // 检查学号是否被其他用户使用
        if (user.getRole() == User.Role.STUDENT && userDetails.getStudentId() != null 
            && !userDetails.getStudentId().equals(user.getStudentId()) 
            && userRepository.existsByStudentId(userDetails.getStudentId())) {
            throw new RuntimeException("学号已存在");
        }
        
        // 更新用户信息
        user.setUsername(userDetails.getUsername());
        user.setRealName(userDetails.getRealName());
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());
        
        if (user.getRole() == User.Role.STUDENT) {
            user.setStudentId(userDetails.getStudentId());
            user.setClassName(userDetails.getClassName());
            user.setMajor(userDetails.getMajor());
        } else {
            user.setDepartment(userDetails.getDepartment());
            user.setPosition(userDetails.getPosition());
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    /**
     * 修改密码
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        // 设置新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    
    /**
     * 根据ID查找用户
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * 根据用户名查找用户
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * 根据学号查找学生
     */
    @Transactional(readOnly = true)
    public Optional<User> findByStudentId(String studentId) {
        return userRepository.findByStudentId(studentId);
    }
    
    /**
     * 获取所有用户
     */
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    /**
     * 根据角色查找用户
     */
    @Transactional(readOnly = true)
    public List<User> findByRole(User.Role role) {
        return userRepository.findByRole(role);
    }
    
    /**
     * 根据班级查找学生
     */
    @Transactional(readOnly = true)
    public List<User> findByClassName(String className) {
        return userRepository.findByClassName(className);
    }
    
    /**
     * 根据专业查找学生
     */
    @Transactional(readOnly = true)
    public List<User> findByMajor(String major) {
        return userRepository.findByMajor(major);
    }
    
    /**
     * 查找未分配宿舍的学生
     */
    @Transactional(readOnly = true)
    public List<User> findStudentsWithoutDormitory() {
        return userRepository.findStudentsWithoutDormitory();
    }
    
    /**
     * 搜索用户
     */
    @Transactional(readOnly = true)
    public List<User> searchUsers(String keyword) {
        return userRepository.searchUsers(keyword);
    }
    
    /**
     * 删除用户
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("用户不存在");
        }
        userRepository.deleteById(id);
    }
    
    /**
     * 检查用户是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
}
