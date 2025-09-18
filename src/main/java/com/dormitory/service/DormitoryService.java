package com.dormitory.service;

import com.dormitory.entity.Dormitory;
import com.dormitory.repository.DormitoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 宿舍服务类
 */
@Service
@Transactional
public class DormitoryService {
    
    @Autowired
    private DormitoryRepository dormitoryRepository;
    
    /**
     * 创建宿舍
     */
    public Dormitory createDormitory(Dormitory dormitory) {
        // 检查宿舍楼和房间号是否已存在
        if (dormitoryRepository.existsByBuildingNameAndRoomNumber(
                dormitory.getBuildingName(), dormitory.getRoomNumber())) {
            throw new RuntimeException("该宿舍已存在");
        }
        
        dormitory.setCreatedAt(LocalDateTime.now());
        dormitory.setUpdatedAt(LocalDateTime.now());
        dormitory.setCurrentOccupancy(0);
        
        return dormitoryRepository.save(dormitory);
    }
    
    /**
     * 更新宿舍信息
     */
    public Dormitory updateDormitory(Long id, Dormitory dormitoryDetails) {
        Dormitory dormitory = dormitoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("宿舍不存在"));
        
        // 检查宿舍楼和房间号是否被其他宿舍使用
        if (!dormitory.getBuildingName().equals(dormitoryDetails.getBuildingName()) 
            || !dormitory.getRoomNumber().equals(dormitoryDetails.getRoomNumber())) {
            if (dormitoryRepository.existsByBuildingNameAndRoomNumber(
                    dormitoryDetails.getBuildingName(), dormitoryDetails.getRoomNumber())) {
                throw new RuntimeException("该宿舍已存在");
            }
        }
        
        // 更新宿舍信息
        dormitory.setBuildingName(dormitoryDetails.getBuildingName());
        dormitory.setRoomNumber(dormitoryDetails.getRoomNumber());
        dormitory.setCapacity(dormitoryDetails.getCapacity());
        dormitory.setType(dormitoryDetails.getType());
        dormitory.setMonthlyRent(dormitoryDetails.getMonthlyRent());
        dormitory.setDescription(dormitoryDetails.getDescription());
        dormitory.setStatus(dormitoryDetails.getStatus());
        dormitory.setUpdatedAt(LocalDateTime.now());
        
        return dormitoryRepository.save(dormitory);
    }
    
    /**
     * 根据ID查找宿舍
     */
    @Transactional(readOnly = true)
    public Optional<Dormitory> findById(Long id) {
        return dormitoryRepository.findById(id);
    }
    
    /**
     * 根据宿舍楼和房间号查找宿舍
     */
    @Transactional(readOnly = true)
    public Optional<Dormitory> findByBuildingNameAndRoomNumber(String buildingName, String roomNumber) {
        return dormitoryRepository.findByBuildingNameAndRoomNumber(buildingName, roomNumber);
    }
    
    /**
     * 获取所有宿舍
     */
    @Transactional(readOnly = true)
    public List<Dormitory> findAll() {
        return dormitoryRepository.findAll();
    }
    
    /**
     * 根据宿舍楼查找宿舍
     */
    @Transactional(readOnly = true)
    public List<Dormitory> findByBuildingName(String buildingName) {
        return dormitoryRepository.findByBuildingName(buildingName);
    }
    
    /**
     * 根据宿舍类型查找宿舍
     */
    @Transactional(readOnly = true)
    public List<Dormitory> findByType(Dormitory.DormitoryType type) {
        return dormitoryRepository.findByType(type);
    }
    
    /**
     * 根据状态查找宿舍
     */
    @Transactional(readOnly = true)
    public List<Dormitory> findByStatus(Dormitory.DormitoryStatus status) {
        return dormitoryRepository.findByStatus(status);
    }
    
    /**
     * 查找可用的宿舍
     */
    @Transactional(readOnly = true)
    public List<Dormitory> findAvailableDormitories() {
        return dormitoryRepository.findAvailableDormitories();
    }
    
    /**
     * 根据宿舍楼和类型查找可用宿舍
     */
    @Transactional(readOnly = true)
    public List<Dormitory> findAvailableDormitoriesByBuildingAndType(String buildingName, Dormitory.DormitoryType type) {
        return dormitoryRepository.findAvailableDormitoriesByBuildingAndType(buildingName, type);
    }
    
    /**
     * 查找已满的宿舍
     */
    @Transactional(readOnly = true)
    public List<Dormitory> findFullDormitories() {
        return dormitoryRepository.findFullDormitories();
    }
    
    /**
     * 根据容量范围查找宿舍
     */
    @Transactional(readOnly = true)
    public List<Dormitory> findByCapacityRange(Integer minCapacity, Integer maxCapacity) {
        return dormitoryRepository.findByCapacityRange(minCapacity, maxCapacity);
    }
    
    /**
     * 根据租金范围查找宿舍
     */
    @Transactional(readOnly = true)
    public List<Dormitory> findByRentRange(Double minRent, Double maxRent) {
        return dormitoryRepository.findByRentRange(minRent, maxRent);
    }
    
    /**
     * 搜索宿舍
     */
    @Transactional(readOnly = true)
    public List<Dormitory> searchDormitories(String keyword) {
        return dormitoryRepository.searchDormitories(keyword);
    }
    
    /**
     * 统计各宿舍楼的宿舍数量
     */
    @Transactional(readOnly = true)
    public List<Object[]> countDormitoriesByBuilding() {
        return dormitoryRepository.countDormitoriesByBuilding();
    }
    
    /**
     * 统计各宿舍楼的入住率
     */
    @Transactional(readOnly = true)
    public List<Object[]> getOccupancyRateByBuilding() {
        return dormitoryRepository.getOccupancyRateByBuilding();
    }
    
    /**
     * 更新宿舍入住人数
     */
    public void updateOccupancy(Long dormitoryId, int change) {
        Dormitory dormitory = dormitoryRepository.findById(dormitoryId)
                .orElseThrow(() -> new RuntimeException("宿舍不存在"));
        
        int newOccupancy = dormitory.getCurrentOccupancy() + change;
        if (newOccupancy < 0 || newOccupancy > dormitory.getCapacity()) {
            throw new RuntimeException("入住人数超出范围");
        }
        
        dormitory.setCurrentOccupancy(newOccupancy);
        
        // 更新宿舍状态
        if (newOccupancy >= dormitory.getCapacity()) {
            dormitory.setStatus(Dormitory.DormitoryStatus.FULL);
        } else if (dormitory.getStatus() == Dormitory.DormitoryStatus.FULL) {
            dormitory.setStatus(Dormitory.DormitoryStatus.AVAILABLE);
        }
        
        dormitory.setUpdatedAt(LocalDateTime.now());
        dormitoryRepository.save(dormitory);
    }
    
    /**
     * 删除宿舍
     */
    public void deleteDormitory(Long id) {
        Dormitory dormitory = dormitoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("宿舍不存在"));
        
        // 检查宿舍是否有学生入住
        if (dormitory.getCurrentOccupancy() > 0) {
            throw new RuntimeException("宿舍仍有学生入住，无法删除");
        }
        
        dormitoryRepository.deleteById(id);
    }
    
    /**
     * 检查宿舍是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return dormitoryRepository.existsById(id);
    }
}
