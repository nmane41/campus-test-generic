package com.campusplacement.controller;

import com.campusplacement.dto.QuestionDto;
import com.campusplacement.dto.SubmitTestRequest;
import com.campusplacement.dto.TestResultDto;
import com.campusplacement.entity.User;
import com.campusplacement.repository.UserRepository;
import com.campusplacement.service.QuestionService;
import com.campusplacement.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/questions")
    public ResponseEntity<?> getQuestions(Authentication authentication) {
        try {
            User user = getCurrentUser(authentication);
            
            if (testService.hasUserAttemptedTest(user)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "You have already attempted the test");
                return ResponseEntity.badRequest().body(error);
            }

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

