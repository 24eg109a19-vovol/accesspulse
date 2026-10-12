package com.accesspulse.service;

import com.accesspulse.dto.RegistrationRequest;
import com.accesspulse.entity.User;
import com.accesspulse.enums.Role;
import com.accesspulse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("An account with email " + request.getEmail() + " already exists.");
        }

        User user = User.builder()
                .fullName(request.getFullName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole() != null ? request.getRole() : Role.ROLE_STUDENT)
                .departmentOrId(request.getDepartmentOrId())
                .active(true)
                .build();

        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getMaintenanceStaff() {
        return userRepository.findByRoleAndActiveTrue(Role.ROLE_MAINTENANCE);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void toggleUserActive(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setActive(!user.isActive());
            userRepository.save(user);
        });
    }

    public long countByRole(Role role) {
        return userRepository.countByRole(role);
    }
}
