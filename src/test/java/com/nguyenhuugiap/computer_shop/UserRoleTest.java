package com.nguyenhuugiap.computer_shop;

import com.nguyenhuugiap.computer_shop.entity.Role;
import com.nguyenhuugiap.computer_shop.entity.User;
import com.nguyenhuugiap.computer_shop.enums.RoleType;
import com.nguyenhuugiap.computer_shop.repository.RoleRepository;
import com.nguyenhuugiap.computer_shop.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserRoleTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @Transactional // Test xong tự động rollback data
    public void testCreateUserWithRoles() { // Đã sửa tên hàm (With)
        System.out.println("----- START TEST: USER - ROLE RELATIONSHIP -----");

        // 1. ARRANGE: Chuẩn bị dữ liệu
        // Tạo Role từ Enum để tránh sai chính tả
        Role adminRole = Role.builder()
                .name(RoleType.ADMIN.name()) // "ADMIN"
                .description("System Administrator")
                .build();
        adminRole = roleRepository.save(adminRole);
        System.out.println("✅ (1) Created Role: " + adminRole.getName());

        // Tạo User giả lập
        String testEmail = "test_enum_admin@gmail.com";
        User newUser = User.builder()
                .email(testEmail)
                .passwordHash("secret_password")
                .fullName("Nguyen Van A")
                .phone("0123456789")
                .build();

        // 2. ACT: Hành động cần test (Gán quyền & Lưu)
        newUser.addRole(adminRole); // Sử dụng helper method
        userRepository.save(newUser);
        System.out.println("✅ (2) Saved User with Role to DB. ID: " + newUser.getId());

        // 3. ASSERT: Kiểm tra kết quả từ Database
        // Tìm lại User từ DB (Sửa lỗi chính tả email ở đây)
        User userInDb = userRepository.findByEmail(testEmail)
                .orElseThrow(() -> new RuntimeException("❌ Failed: User not found with email " + testEmail));

        System.out.println("✅ (3) Found User in DB: " + userInDb.getFullName());

        // Kiểm tra thông tin cơ bản
        assertEquals("Nguyen Van A", userInDb.getFullName());

        // Kiểm tra danh sách Role
        Set<UserRole> roles = userInDb.getUserRoles();
        assertFalse(roles.isEmpty(), "❌ Failed: User roles list is empty!");
        assertEquals(1, roles.size(), "❌ Failed: User should have exactly 1 role!");

        // Kiểm tra chi tiết Role
        UserRole link = roles.iterator().next();

        // So sánh tên Role: "ADMIN" vs "ADMIN"
        assertEquals(RoleType.ADMIN.name(), link.getRole().getName(), "❌ Failed: Role name mismatch!");

        // Kiểm tra ngày cấp quyền (assignedAt)
        assertNotNull(link.getAssignedAt(), "❌ Failed: AssignedAt timestamp is null!");

        System.out.println("🎉 TEST PASSED: User-Role relationship works perfectly!");
    }
}