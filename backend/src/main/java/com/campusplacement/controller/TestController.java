package com.campusplacement.controller;

import com.campusplacement.dto.QuestionDto;
import com.campusplacement.dto.SubmitTestRequest;
import com.campusplacement.dto.TestResultDto;
import com.campusplacement.entity.User;
import com.campusplacement.repository.UserRepository;
import com.campusplacement.service.QuestionService;
import com.campusplacement.service.TestService;
import com.campusplacement.service.TestStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
@Validated
public class TestController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private TestService testService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestStatusService testStatusService;

    @GetMapping("/status")
    public ResponseEntity<?> getTestStatus(Authentication authentication) {
        try {
            Map<String, Object> response = new HashMap<>();
            boolean isStarted = testStatusService.isTestStarted();
            response.put("status", isStarted ? "STARTED" : "NOT_STARTED");
            if (isStarted) {
                LocalDateTime startTime = testStatusService.getTestStartTime();
                response.put("startTime", startTime);
                
                // Also include user's test attempt start time if available
                try {
                    User user = getCurrentUser(authentication);
                    java.util.Optional<com.campusplacement.entity.TestAttempt> attempt = 
                        testService.getUserTestAttempt(user);
                    if (attempt.isPresent() && attempt.get().getStartTime() != null) {
                        response.put("userStartTime", attempt.get().getStartTime());
                    }
                } catch (Exception e) {
                    // User may not have started test yet
                }
            }
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/questions")
    public ResponseEntity<?> getQuestions(Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            
            // Check if test has been started by admin
            if (!testStatusService.isTestStarted()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Test has not been started yet. Please wait for the administrator to start the test.");
                return ResponseEntity.badRequest().body(error);
            }
            
            if (testService.hasUserAttemptedTest(user)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "You have already attempted the test");
                return ResponseEntity.badRequest().body(error);
            }

            // Start test attempt for user (or get existing if already started)
            testService.startTest(user);
            List<QuestionDto> questions = questionService.getAllQuestionsForTest();
            
            if (questions.size() < 50) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Not enough questions in the database. Please contact administrator.");
                return ResponseEntity.badRequest().body(error);
            }

            return ResponseEntity.ok(questions.subList(0, 50));
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitTest(@Valid @RequestBody SubmitTestRequest request, Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            testService.submitTest(user, request);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Test submitted successfully. Results will be available to administrators.");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    private User getCurrentUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

