package com.campusplacement.service;

import com.campusplacement.dto.DashboardStatsDto;
import com.campusplacement.dto.PasswordResetResponse;
import com.campusplacement.dto.StudentResultDto;
import com.campusplacement.entity.TestAttempt;
import com.campusplacement.entity.User;
import com.campusplacement.repository.TestAttemptRepository;
import com.campusplacement.repository.UserRepository;
import com.campusplacement.util.TimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestAttemptRepository testAttemptRepository;

    @Autowired
    private TestService testService;

    @Autowired
    private UserService userService;

    public DashboardStatsDto getDashboardStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByEnabledTrue();
        long totalTestAttempts = testService.getTotalTestAttempts();
        double averageScore = testService.getAverageScore();

        return new DashboardStatsDto(totalUsers, activeUsers, totalTestAttempts, averageScore);
    }

    public List<StudentResultDto> getAllStudentResults() {
        List<TestAttempt> attempts = testAttemptRepository.findAllByOrderByEndTimeDesc();
        return attempts.stream()
                .filter(attempt -> attempt.getEndTime() != null)
                .map(attempt -> {
                    StudentResultDto dto = new StudentResultDto();
                    dto.setUserId(attempt.getUser().getId());
                    dto.setUsername(attempt.getUser().getUsername());
                    dto.setEmail(attempt.getUser().getEmail());
                    dto.setAttemptId(attempt.getId());
                    dto.setScore(attempt.getScore());
                    dto.setStartTime(attempt.getStartTime());
                    dto.setEndTime(attempt.getEndTime());
                    
                    // Calculate time taken in seconds
                    if (attempt.getStartTime() != null && attempt.getEndTime() != null) {
                        Duration duration = Duration.between(attempt.getStartTime(), attempt.getEndTime());
                        dto.setTimeTakenSeconds(duration.getSeconds());
                        dto.setTimeTakenFormatted(TimeFormatter.formatTimeTaken(duration.getSeconds()));
                    }
                    
                    // Format times in IST 12-hour format
                    dto.setStartTimeIST(TimeFormatter.formatDateTimeToIST(attempt.getStartTime()));
                    dto.setEndTimeIST(TimeFormatter.formatDateTimeToIST(attempt.getEndTime()));
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public PasswordResetResponse resetUserPassword(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String temporaryPassword = generateTemporaryPassword();
        userService.resetPassword(userId, temporaryPassword);

        return new PasswordResetResponse(temporaryPassword, "Password reset successfully");
    }

    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            password.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return password.toString();
    }
}

