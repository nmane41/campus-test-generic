package com.campusplacement.controller;

import com.campusplacement.dto.*;
import com.campusplacement.entity.User;
import com.campusplacement.repository.UserRepository;
import com.campusplacement.service.AdminService;
import com.campusplacement.service.PdfExportService;
import com.campusplacement.service.QuestionService;
import com.campusplacement.service.TestStatusService;
import com.campusplacement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@Validated
public class AdminController {
    @Autowired
    private AdminService adminService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PdfExportService pdfExportService;

    @Autowired
    private TestStatusService testStatusService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {
        DashboardStatsDto stats = adminService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/questions")
    public ResponseEntity<List<QuestionDto>> getAllQuestions() {
        List<QuestionDto> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/questions/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable Long id) {
        try {
            QuestionDto question = questionService.getQuestionById(id);
            return ResponseEntity.ok(question);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/questions")
    public ResponseEntity<?> createQuestion(@Valid @RequestBody QuestionRequest request) {
        try {
            QuestionDto question = questionService.createQuestion(request);
            return ResponseEntity.ok(question);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long id, @Valid @RequestBody QuestionRequest request) {
        try {
            QuestionDto question = questionService.updateQuestion(id, request);
            return ResponseEntity.ok(question);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        try {
            questionService.deleteQuestion(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Question deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/results")
    public ResponseEntity<List<StudentResultDto>> getAllStudentResults() {
        List<StudentResultDto> results = adminService.getAllStudentResults();
        return ResponseEntity.ok(results);
    }

    @PostMapping("/users/{userId}/reset-password")
    public ResponseEntity<?> resetUserPassword(@PathVariable Long userId) {
        try {
            PasswordResetResponse response = adminService.resetUserPassword(userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/users/{userId}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long userId, @Valid @RequestBody UserStatusRequest request) {
        try {
            User user = userService.updateUserStatus(userId, request.getEnabled());
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User status updated successfully");
            response.put("userId", user.getId());
            response.put("enabled", user.getEnabled());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/results/export/pdf")
    public ResponseEntity<byte[]> exportResultsAsPdf() {
        try {
            byte[] pdfBytes = pdfExportService.generateResultsPdf();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "test-results-" + 
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")) + ".pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to generate PDF: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/start-test")
    public ResponseEntity<?> startTest() {
        try {
            testStatusService.startTest();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Test started successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

