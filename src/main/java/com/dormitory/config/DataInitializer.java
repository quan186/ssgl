package com.dormitory.config;

import com.dormitory.entity.User;
import com.dormitory.entity.Dormitory;
import com.dormitory.entity.StudentDormitory;
import com.dormitory.service.UserService;
import com.dormitory.service.DormitoryService;
import com.dormitory.service.StudentDormitoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 数据初始化器
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DormitoryService dormitoryService;
    
    @Autowired
    private StudentDormitoryService studentDormitoryService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // 检查是否已有数据
        if (userService.findAll().isEmpty()) {
            initializeData();
        }
    }
    
    private void initializeData() {
        System.out.println("开始初始化测试数据...");
        
        // 创建管理员用户
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin123");
        admin.setRealName("系统管理员");
        admin.setEmail("admin@dormitory.com");
        admin.setPhone("13800138000");
        admin.setRole(User.Role.ADMIN);
        admin.setDepartment("后勤管理处");
        admin.setPosition("系统管理员");
        userService.createUser(admin);
        
        // 创建学生用户
        User student1 = new User();
        student1.setUsername("student");
        student1.setPassword("student123");
        student1.setRealName("张三");
        student1.setEmail("zhangsan@student.com");
        student1.setPhone("13800138001");
        student1.setRole(User.Role.STUDENT);
        student1.setStudentId("2024001");
        student1.setClassName("计算机科学与技术1班");
        student1.setMajor("计算机科学与技术");
        userService.createUser(student1);
        
        User student2 = new User();
        student2.setUsername("student2");
        student2.setPassword("student123");
        student2.setRealName("李四");
        student2.setEmail("lisi@student.com");
        student2.setPhone("13800138002");
        student2.setRole(User.Role.STUDENT);
        student2.setStudentId("2024002");
        student2.setClassName("软件工程1班");
        student2.setMajor("软件工程");
        userService.createUser(student2);
        
        User student3 = new User();
        student3.setUsername("student3");
        student3.setPassword("student123");
        student3.setRealName("王五");
        student3.setEmail("wangwu@student.com");
        student3.setPhone("13800138003");
        student3.setRole(User.Role.STUDENT);
        student3.setStudentId("2024003");
        student3.setClassName("网络工程1班");
        student3.setMajor("网络工程");
        userService.createUser(student3);
        
        User student4 = new User();
        student4.setUsername("student4");
        student4.setPassword("student123");
        student4.setRealName("赵六");
        student4.setEmail("zhaoliu@student.com");
        student4.setPhone("13800138004");
        student4.setRole(User.Role.STUDENT);
        student4.setStudentId("2024004");
        student4.setClassName("计算机科学与技术1班");
        student4.setMajor("计算机科学与技术");
        userService.createUser(student4);
        
        // 创建宿舍
        Dormitory dormitory1 = new Dormitory();
        dormitory1.setBuildingName("A栋");
        dormitory1.setRoomNumber("101");
        dormitory1.setCapacity(4);
        dormitory1.setType(Dormitory.DormitoryType.MALE);
        dormitory1.setMonthlyRent(500.0);
        dormitory1.setDescription("四人间，配备空调、热水器、独立卫生间");
        dormitory1.setStatus(Dormitory.DormitoryStatus.AVAILABLE);
        dormitoryService.createDormitory(dormitory1);
        
        Dormitory dormitory2 = new Dormitory();
        dormitory2.setBuildingName("A栋");
        dormitory2.setRoomNumber("102");
        dormitory2.setCapacity(4);
        dormitory2.setType(Dormitory.DormitoryType.MALE);
        dormitory2.setMonthlyRent(500.0);
        dormitory2.setDescription("四人间，配备空调、热水器、独立卫生间");
        dormitory2.setStatus(Dormitory.DormitoryStatus.AVAILABLE);
        dormitoryService.createDormitory(dormitory2);
        
        Dormitory dormitory3 = new Dormitory();
        dormitory3.setBuildingName("A栋");
        dormitory3.setRoomNumber("103");
        dormitory3.setCapacity(4);
        dormitory3.setType(Dormitory.DormitoryType.MALE);
        dormitory3.setMonthlyRent(500.0);
        dormitory3.setDescription("四人间，配备空调、热水器、独立卫生间");
        dormitory3.setStatus(Dormitory.DormitoryStatus.AVAILABLE);
        dormitoryService.createDormitory(dormitory3);
        
        Dormitory dormitory4 = new Dormitory();
        dormitory4.setBuildingName("B栋");
        dormitory4.setRoomNumber("201");
        dormitory4.setCapacity(4);
        dormitory4.setType(Dormitory.DormitoryType.FEMALE);
        dormitory4.setMonthlyRent(500.0);
        dormitory4.setDescription("四人间，配备空调、热水器、独立卫生间");
        dormitory4.setStatus(Dormitory.DormitoryStatus.AVAILABLE);
        dormitoryService.createDormitory(dormitory4);
        
        Dormitory dormitory5 = new Dormitory();
        dormitory5.setBuildingName("B栋");
        dormitory5.setRoomNumber("202");
        dormitory5.setCapacity(4);
        dormitory5.setType(Dormitory.DormitoryType.FEMALE);
        dormitory5.setMonthlyRent(500.0);
        dormitory5.setDescription("四人间，配备空调、热水器、独立卫生间");
        dormitory5.setStatus(Dormitory.DormitoryStatus.AVAILABLE);
        dormitoryService.createDormitory(dormitory5);
        
        Dormitory dormitory6 = new Dormitory();
        dormitory6.setBuildingName("C栋");
        dormitory6.setRoomNumber("301");
        dormitory6.setCapacity(2);
        dormitory6.setType(Dormitory.DormitoryType.MALE);
        dormitory6.setMonthlyRent(800.0);
        dormitory6.setDescription("双人间，配备空调、热水器、独立卫生间、阳台");
        dormitory6.setStatus(Dormitory.DormitoryStatus.AVAILABLE);
        dormitoryService.createDormitory(dormitory6);
        
        // 分配学生到宿舍
        studentDormitoryService.assignStudentToDormitory(student1.getId(), dormitory1.getId(), LocalDate.now().minusDays(30));
        studentDormitoryService.assignStudentToDormitory(student2.getId(), dormitory1.getId(), LocalDate.now().minusDays(25));
        studentDormitoryService.assignStudentToDormitory(student3.getId(), dormitory2.getId(), LocalDate.now().minusDays(20));
        studentDormitoryService.assignStudentToDormitory(student4.getId(), dormitory6.getId(), LocalDate.now().minusDays(15));
        
        System.out.println("测试数据初始化完成！");
        System.out.println("管理员账户: admin / admin123");
        System.out.println("学生账户: student / student123");
        System.out.println("学生账户: student2 / student123");
        System.out.println("学生账户: student3 / student123");
        System.out.println("学生账户: student4 / student123");
    }
}
