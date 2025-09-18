package com.dormitory.repository;

import com.dormitory.entity.Dormitory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 宿舍数据访问层
 */
@Repository
public interface DormitoryRepository extends JpaRepository<Dormitory, Long> {
    
    /**
     * 根据宿舍楼和房间号查找宿舍
     */
    Optional<Dormitory> findByBuildingNameAndRoomNumber(String buildingName, String roomNumber);
    
    /**
     * 根据宿舍楼查找宿舍
     */
    List<Dormitory> findByBuildingName(String buildingName);
    
    /**
     * 根据宿舍类型查找宿舍
     */
    List<Dormitory> findByType(Dormitory.DormitoryType type);
    
    /**
     * 根据状态查找宿舍
     */
    List<Dormitory> findByStatus(Dormitory.DormitoryStatus status);
    
    /**
     * 查找可用的宿舍
     */
    @Query("SELECT d FROM Dormitory d WHERE d.status = 'AVAILABLE' AND d.currentOccupancy < d.capacity")
    List<Dormitory> findAvailableDormitories();
    
    /**
     * 根据宿舍楼和类型查找可用宿舍
     */
    @Query("SELECT d FROM Dormitory d WHERE d.buildingName = :buildingName AND d.type = :type " +
           "AND d.status = 'AVAILABLE' AND d.currentOccupancy < d.capacity")
    List<Dormitory> findAvailableDormitoriesByBuildingAndType(
            @Param("buildingName") String buildingName, 
            @Param("type") Dormitory.DormitoryType type);
    
    /**
     * 查找已满的宿舍
     */
    @Query("SELECT d FROM Dormitory d WHERE d.currentOccupancy >= d.capacity")
    List<Dormitory> findFullDormitories();
    
    /**
     * 根据容量范围查找宿舍
     */
    @Query("SELECT d FROM Dormitory d WHERE d.capacity BETWEEN :minCapacity AND :maxCapacity")
    List<Dormitory> findByCapacityRange(@Param("minCapacity") Integer minCapacity, 
                                       @Param("maxCapacity") Integer maxCapacity);
    
    /**
     * 根据租金范围查找宿舍
     */
    @Query("SELECT d FROM Dormitory d WHERE d.monthlyRent BETWEEN :minRent AND :maxRent")
    List<Dormitory> findByRentRange(@Param("minRent") Double minRent, 
                                   @Param("maxRent") Double maxRent);
    
    /**
     * 统计各宿舍楼的宿舍数量
     */
    @Query("SELECT d.buildingName, COUNT(d) FROM Dormitory d GROUP BY d.buildingName")
    List<Object[]> countDormitoriesByBuilding();
    
    /**
     * 统计各宿舍楼的入住率
     */
    @Query("SELECT d.buildingName, " +
           "SUM(d.currentOccupancy) as totalOccupancy, " +
           "SUM(d.capacity) as totalCapacity, " +
           "CAST(SUM(d.currentOccupancy) AS DOUBLE) / SUM(d.capacity) * 100 as occupancyRate " +
           "FROM Dormitory d GROUP BY d.buildingName")
    List<Object[]> getOccupancyRateByBuilding();
    
    /**
     * 根据关键词搜索宿舍
     */
    @Query("SELECT d FROM Dormitory d WHERE " +
           "d.buildingName LIKE %:keyword% OR " +
           "d.roomNumber LIKE %:keyword% OR " +
           "d.description LIKE %:keyword%")
    List<Dormitory> searchDormitories(@Param("keyword") String keyword);
    
    /**
     * 检查宿舍楼和房间号是否存在
     */
    boolean existsByBuildingNameAndRoomNumber(String buildingName, String roomNumber);
}
